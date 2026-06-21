package br.com.fiap.vinheriaagnello

import android.app.Application
import android.content.Context
import br.com.fiap.vinheriaagnello.data.local.VinheriaDatabase
import br.com.fiap.vinheriaagnello.data.repository.WineRepository

/**
 * Application entry point that owns the dependency container for the entire process.
 */
class VinheriaApplication : Application() {
    /** Lazily creates shared application-level dependencies on first access. */
    val container: AppContainer by lazy { AppContainer(this) }
}

/**
 * Lightweight manual dependency injection container.
 *
 * Dependencies are scoped to the application process and initialized only when needed.
 */
class AppContainer(context: Context) {
    private val database: VinheriaDatabase by lazy {
        VinheriaDatabase.getInstance(context)
    }

    /** Repository used as the single entry point to wine inventory data. */
    val wineRepository: WineRepository by lazy {
        WineRepository(database.wineDao())
    }
}
