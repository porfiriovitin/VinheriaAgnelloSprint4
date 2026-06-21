package br.com.fiap.vinheriaagnello.ui.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.vinheriaagnello.data.local.WineEntity
import br.com.fiap.vinheriaagnello.R

/**
 * Displays the reactive inventory and coordinates add, edit, retry, and delete interactions.
 *
 * @param viewModel source of inventory state and business actions.
 * @param onBack invoked when the user leaves the stock module.
 * @param onAddWine invoked when a new wine should be created.
 * @param onEditWine invoked with the identifier of the wine selected for editing.
 * @param modifier optional modifier applied to the screen root.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    viewModel: StockViewModel,
    onBack: () -> Unit,
    onAddWine: () -> Unit,
    onEditWine: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage = uiState.errorMessageResId?.let { stringResource(it) }
    val retryLabel = stringResource(R.string.retry)
    val snackbarHostState = remember { SnackbarHostState() }
    var winePendingDeletion by remember { mutableStateOf<WineEntity?>(null) }

    // Error resources are resolved by Compose before the suspend snackbar API is called.
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (uiState.canRetry) retryLabel else null
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.retryLoad()
            } else {
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stock_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddWine) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_wine))
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.wines.isEmpty() -> {
                EmptyStock(
                    loadFailed = uiState.canRetry,
                    onRetry = viewModel::retryLoad,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.wines, key = { it.id }) { wine ->
                        WineListItem(
                            wine = wine,
                            onEdit = { onEditWine(wine.id) },
                            onDelete = { winePendingDeletion = wine }
                        )
                    }
                }
            }
        }
    }

    winePendingDeletion?.let { wine ->
        AlertDialog(
            onDismissRequest = { winePendingDeletion = null },
            title = { Text(stringResource(R.string.delete_wine_title)) },
            text = { Text(stringResource(R.string.delete_wine_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWine(wine)
                        winePendingDeletion = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { winePendingDeletion = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/** Renders either an empty inventory message or a recoverable load failure state. */
@Composable
private fun EmptyStock(
    loadFailed: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inventory2,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 18.dp)
        )
        Text(
            text = stringResource(
                if (loadFailed) R.string.stock_load_failed_title else R.string.empty_stock_title
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(
                if (loadFailed) R.string.stock_load_failed_message else R.string.empty_stock_message
            ),
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (loadFailed) {
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}
