package br.com.fiap.vinheriaagnello

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.fiap.vinheriaagnello.ui.navigation.AppNavigation
import br.com.fiap.vinheriaagnello.ui.theme.VinheriaAgnelloTheme

/**
 * Hosts the Compose UI and connects application-scoped dependencies to navigation.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VinheriaAgnelloTheme {
                val application = application as VinheriaApplication
                AppNavigation(repository = application.container.wineRepository)
            }
        }
    }
}
