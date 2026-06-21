package br.com.fiap.vinheriaagnello.ui.stock

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.fiap.vinheriaagnello.data.local.WineEntity
import br.com.fiap.vinheriaagnello.data.repository.WineRepository
import br.com.fiap.vinheriaagnello.data.repository.WineNotFoundException
import br.com.fiap.vinheriaagnello.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Coordinates inventory observation, form validation, and asynchronous CRUD operations.
 *
 * The ViewModel exposes immutable state streams and keeps Android UI objects out of the data flow.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModel(
    private val repository: WineRepository
) : ViewModel() {
    private val errorMessageResId = MutableStateFlow<Int?>(null)
    private val loadFailed = MutableStateFlow(false)
    private val reloadSignal = MutableStateFlow(0L)

    // A changed signal replaces the failed Room subscription with a fresh observable stream.
    private val wines = reloadSignal.flatMapLatest {
        repository.wines
            .retryWhen { cause, attempt ->
                // Cancellation is control flow and must never be converted into a recoverable error.
                if (cause is CancellationException) throw cause
                if (attempt < MAX_LOAD_RETRIES) {
                    delay(RETRY_DELAY_MILLIS * (attempt + 1))
                    true
                } else {
                    false
                }
            }
            .onEach { loadFailed.value = false }
            .catch { error ->
                if (error is CancellationException) throw error
                loadFailed.value = true
                errorMessageResId.value = R.string.error_stock_load
                emit(emptyList())
            }
    }

    /** Lifecycle-aware state consumed by the stock list screen. */
    val uiState: StateFlow<StockUiState> = combine(
        wines,
        errorMessageResId,
        loadFailed
    ) { wineList, error, retryAvailable ->
        StockUiState(
            wines = wineList,
            isLoading = false,
            errorMessageResId = error,
            canRetry = retryAvailable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StockUiState(isLoading = true)
    )

    private val _formState = MutableStateFlow(WineFormState())
    /** Current values, progress flags, and validation results for the wine form. */
    val formState: StateFlow<WineFormState> = _formState

    private val _events = MutableSharedFlow<StockUiEvent>(extraBufferCapacity = 1)
    /** One-time UI effects that should not be represented as persistent screen state. */
    val events = _events.asSharedFlow()

    private var loadWineJob: Job? = null

    /** Resets form state for a new wine and cancels any pending edit load. */
    fun beginCreate() {
        loadWineJob?.cancel()
        _formState.value = WineFormState()
    }

    /**
     * Loads the wine identified by [id] into the form.
     *
     * The previous load is cancelled and the identifier is checked again before applying results,
     * preventing an older request from overwriting a newer form.
     */
    fun loadWine(id: Long) {
        loadWineJob?.cancel()
        _formState.value = WineFormState(wineId = id, isLoading = true)
        loadWineJob = viewModelScope.launch {
            try {
                val wine = repository.findWineById(id)
                if (_formState.value.wineId != id) return@launch

                _formState.value = if (wine == null) {
                    WineFormState(
                        wineId = id,
                        formErrorResId = R.string.error_wine_not_found
                    )
                } else {
                    wine.toFormState()
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                if (_formState.value.wineId == id) {
                    _formState.value = WineFormState(
                        wineId = id,
                        formErrorResId = error.toMessageResId(R.string.error_wine_load)
                    )
                }
            }
        }
    }

    // Field actions also clear stale validation feedback as the user corrects the input.
    fun onNameChanged(value: String) = updateForm {
        copy(name = value, nameErrorResId = null, formErrorResId = null)
    }

    fun onCountryChanged(value: String) = updateForm { copy(country = value, formErrorResId = null) }
    fun onRegionChanged(value: String) = updateForm { copy(region = value, formErrorResId = null) }
    fun onGrapeChanged(value: String) = updateForm { copy(grape = value, formErrorResId = null) }
    fun onTypeChanged(value: String) = updateForm { copy(type = value, formErrorResId = null) }

    fun onStockChanged(value: String) = updateForm {
        copy(stockQuantity = value, stockErrorResId = null, formErrorResId = null)
    }

    /** Validates the active form and dispatches either a create or update operation. */
    fun saveWine() {
        val form = _formState.value
        if (form.isSaving || form.isLoading) return

        if (form.wineId == null) {
            createWine(form)
        } else {
            updateWine(form.wineId, form)
        }
    }

    /** Creates a new inventory record from [form] when validation succeeds. */
    fun createWine(form: WineFormState) {
        val wine = validateAndCreateEntity(form, id = 0) ?: return
        persist { repository.insertWine(wine) }
    }

    /** Updates the wine identified by [id] when form validation succeeds. */
    fun updateWine(id: Long, form: WineFormState) {
        val wine = validateAndCreateEntity(form, id) ?: return
        persist { repository.updateWine(wine) }
    }

    /** Deletes [wine] and exposes a localized error when the operation fails. */
    fun deleteWine(wine: WineEntity) {
        viewModelScope.launch {
            try {
                repository.deleteWine(wine)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                errorMessageResId.value = error.toMessageResId(R.string.error_wine_delete)
            }
        }
    }

    /** Re-subscribes to the inventory after a terminal Room observation failure. */
    fun retryLoad() {
        errorMessageResId.value = null
        loadFailed.value = false
        reloadSignal.value += 1
    }

    /** Clears the transient list-level error after it has been presented. */
    fun clearError() {
        errorMessageResId.value = null
    }

    /** Runs a write operation while maintaining save progress and completion effects. */
    private fun persist(operation: suspend () -> Unit) {
        _formState.value = _formState.value.copy(isSaving = true, formErrorResId = null)
        viewModelScope.launch {
            try {
                operation()
                _formState.value = _formState.value.copy(isSaving = false)
                _events.emit(StockUiEvent.WineSaved)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _formState.value = _formState.value.copy(
                    isSaving = false,
                    formErrorResId = error.toMessageResId(R.string.error_wine_save)
                )
            }
        }
    }

    /** Converts validated form text into the persistence model or updates field errors. */
    private fun validateAndCreateEntity(form: WineFormState, id: Long): WineEntity? {
        val nameErrorResId = if (form.name.isBlank()) R.string.validation_name_required else null
        val stock = form.stockQuantity.trim().toIntOrNull()
        val stockErrorResId = when {
            form.stockQuantity.isBlank() -> R.string.validation_stock_required
            stock == null || stock < 0 -> R.string.validation_stock_non_negative
            else -> null
        }

        if (nameErrorResId != null || stockErrorResId != null) {
            _formState.value = form.copy(
                nameErrorResId = nameErrorResId,
                stockErrorResId = stockErrorResId,
                formErrorResId = R.string.validation_review_fields
            )
            return null
        }

        return WineEntity(
            id = id,
            name = form.name.trim(),
            country = form.country.trim(),
            region = form.region.trim(),
            grape = form.grape.trim(),
            type = form.type.trim(),
            stockQuantity = checkNotNull(stock)
        )
    }

    private fun updateForm(transform: WineFormState.() -> WineFormState) {
        _formState.value = _formState.value.transform()
    }

    private fun WineEntity.toFormState() = WineFormState(
        wineId = id,
        name = name,
        country = country,
        region = region,
        grape = grape,
        type = type,
        stockQuantity = stockQuantity.toString()
    )

    private fun Throwable.toMessageResId(@StringRes fallback: Int): Int =
        if (this is WineNotFoundException) R.string.error_wine_not_found else fallback

    /** Creates [StockViewModel] with its repository dependency. */
    class Factory(
        private val repository: WineRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(StockViewModel::class.java))
            return StockViewModel(repository) as T
        }
    }

    private companion object {
        const val MAX_LOAD_RETRIES = 2L
        const val RETRY_DELAY_MILLIS = 300L
    }
}
