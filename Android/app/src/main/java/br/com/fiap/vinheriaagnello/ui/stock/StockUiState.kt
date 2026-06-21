package br.com.fiap.vinheriaagnello.ui.stock

import androidx.annotation.StringRes
import br.com.fiap.vinheriaagnello.data.local.WineEntity

/** Immutable state rendered by the stock list screen. */
data class StockUiState(
    val wines: List<WineEntity> = emptyList(),
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageResId: Int? = null,
    val canRetry: Boolean = false
)

/**
 * Editable form values, progress flags, and validation messages for create and update flows.
 *
 * Resource identifiers keep localized text resolution inside the Compose UI layer.
 */
data class WineFormState(
    val wineId: Long? = null,
    val name: String = "",
    val country: String = "",
    val region: String = "",
    val grape: String = "",
    val type: String = "",
    val stockQuantity: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    @param:StringRes val nameErrorResId: Int? = null,
    @param:StringRes val stockErrorResId: Int? = null,
    @param:StringRes val formErrorResId: Int? = null
) {
    /** Indicates whether the form is editing a persisted wine. */
    val isEditMode: Boolean get() = wineId != null
}

/** One-time effects emitted by [StockViewModel] for the active screen to consume. */
sealed interface StockUiEvent {
    /** Signals that persistence succeeded and the form can be dismissed. */
    data object WineSaved : StockUiEvent
}
