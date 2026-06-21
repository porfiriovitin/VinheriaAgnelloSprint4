package br.com.fiap.vinheriaagnello.ui.stock

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.text.KeyboardOptions
import br.com.fiap.vinheriaagnello.R

/**
 * Shared create and edit screen for wine inventory records.
 *
 * A `null` [wineId] starts a fresh form; an existing identifier loads the persisted record.
 *
 * @param wineId identifier of the wine being edited, or `null` for creation.
 * @param viewModel owner of form state and persistence actions.
 * @param onBack invoked when the user cancels or navigates back.
 * @param onSaved invoked after a successful create or update operation.
 * @param modifier optional modifier applied to the screen root.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineFormScreen(
    wineId: Long?,
    viewModel: StockViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val form by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(wineId) {
        if (wineId == null) viewModel.beginCreate() else viewModel.loadWine(wineId)
    }
    // Navigation is handled by the active screen so the ViewModel never owns a NavController.
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event == StockUiEvent.WineSaved) onSaved()
        }
    }
    // Prevent leaving the form while an asynchronous save may still emit a completion event.
    BackHandler(enabled = form.isSaving) {}

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (wineId == null) R.string.add_wine else R.string.edit_wine))
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !form.isSaving) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (form.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            WineForm(
                form = form,
                viewModel = viewModel,
                onCancel = onBack,
                onSave = viewModel::saveWine,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

/** Renders editable fields and delegates all state mutations to [viewModel]. */
@Composable
private fun WineForm(
    form: WineFormState,
    viewModel: StockViewModel,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FormTextField(
            value = form.name,
            onValueChange = viewModel::onNameChanged,
            labelResId = R.string.field_name,
            errorResId = form.nameErrorResId
        )
        FormTextField(
            value = form.country,
            onValueChange = viewModel::onCountryChanged,
            labelResId = R.string.field_country
        )
        FormTextField(
            value = form.region,
            onValueChange = viewModel::onRegionChanged,
            labelResId = R.string.field_region
        )
        FormTextField(
            value = form.grape,
            onValueChange = viewModel::onGrapeChanged,
            labelResId = R.string.field_grape
        )
        FormTextField(
            value = form.type,
            onValueChange = viewModel::onTypeChanged,
            labelResId = R.string.field_type
        )
        FormTextField(
            value = form.stockQuantity,
            onValueChange = viewModel::onStockChanged,
            labelResId = R.string.field_stock_quantity,
            errorResId = form.stockErrorResId,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )

        form.formErrorResId?.let { messageResId ->
            Text(
                text = stringResource(messageResId),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel, enabled = !form.isSaving) {
                Text(stringResource(R.string.cancel))
            }
            Button(onClick = onSave, enabled = !form.isSaving) {
                if (form.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 10.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                }
                Text(
                    text = stringResource(if (form.isSaving) R.string.saving else R.string.save),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/** Standard single-line form field with optional localized validation feedback. */
@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelResId: Int,
    modifier: Modifier = Modifier,
    @StringRes errorResId: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(labelResId)) },
        singleLine = true,
        isError = errorResId != null,
        supportingText = errorResId?.let { messageResId ->
            { Text(stringResource(messageResId)) }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        )
    )
}
