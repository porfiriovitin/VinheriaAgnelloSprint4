package br.com.fiap.vinheriaagnello.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.fiap.vinheriaagnello.data.repository.WineRepository
import br.com.fiap.vinheriaagnello.ui.home.MainScreen
import br.com.fiap.vinheriaagnello.ui.stock.StockScreen
import br.com.fiap.vinheriaagnello.ui.stock.StockViewModel
import br.com.fiap.vinheriaagnello.ui.stock.WineFormScreen

/** Central route definitions used by the application navigation graph. */
private object Routes {
    const val HOME = "home"
    const val STOCK = "stock"
    const val NEW_WINE = "stock/new"
    const val EDIT_WINE = "stock/edit/{wineId}"

    fun editWine(wineId: Long) = "stock/edit/$wineId"
}

/**
 * Declares the application navigation graph and shares one stock ViewModel across its routes.
 *
 * @param repository inventory repository used to create the navigation-scoped ViewModel.
 * @param modifier optional modifier applied to the navigation host.
 */
@Composable
fun AppNavigation(
    repository: WineRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val factory = remember(repository) { StockViewModel.Factory(repository) }
    // The activity-owned instance keeps list and form state consistent across stock destinations.
    val stockViewModel: StockViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            MainScreen(onOpenStock = { navController.navigate(Routes.STOCK) })
        }
        composable(Routes.STOCK) {
            StockScreen(
                viewModel = stockViewModel,
                onBack = { navController.popBackStack() },
                onAddWine = { navController.navigate(Routes.NEW_WINE) },
                onEditWine = { wineId -> navController.navigate(Routes.editWine(wineId)) }
            )
        }
        composable(Routes.NEW_WINE) {
            WineFormScreen(
                wineId = null,
                viewModel = stockViewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.EDIT_WINE,
            arguments = listOf(navArgument("wineId") { type = NavType.LongType })
        ) { backStackEntry ->
            WineFormScreen(
                wineId = backStackEntry.arguments?.getLong("wineId"),
                viewModel = stockViewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
