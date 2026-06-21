package br.com.fiap.vinheriaagnello.data.repository

import br.com.fiap.vinheriaagnello.data.local.WineDao
import br.com.fiap.vinheriaagnello.data.local.WineEntity
import kotlinx.coroutines.flow.Flow

/**
 * Centralizes inventory data access and enforces domain validation before persistence.
 *
 * UI-facing layers depend on this repository rather than communicating with Room directly.
 */
class WineRepository(private val wineDao: WineDao) {
    /** Reactive source of truth for the complete wine inventory. */
    val wines: Flow<List<WineEntity>> = wineDao.observeAllWines()

    /** Validates and inserts [wine], returning the generated identifier. */
    suspend fun insertWine(wine: WineEntity): Long {
        validate(wine)
        return wineDao.insertWine(wine.copy(id = 0))
    }

    /** Returns the wine identified by [id], or `null` when it does not exist. */
    suspend fun findWineById(id: Long): WineEntity? = wineDao.findWineById(id)

    /** Validates and updates [wine], failing when the target row no longer exists. */
    suspend fun updateWine(wine: WineEntity) {
        require(wine.id > 0) { "Wine ID must be valid." }
        validate(wine)
        if (wineDao.updateWine(wine) != 1) throw WineNotFoundException()
    }

    /** Deletes [wine], failing when the target row no longer exists. */
    suspend fun deleteWine(wine: WineEntity) {
        require(wine.id > 0) { "Wine ID must be valid." }
        if (wineDao.deleteWine(wine) != 1) throw WineNotFoundException()
    }

    /** Returns a reactive name-filtered inventory query. */
    fun searchWinesByName(query: String): Flow<List<WineEntity>> =
        wineDao.searchWinesByName(query.trim())

    /** Returns a reactive query for wines at or below [threshold] units. */
    fun observeLowStockWines(threshold: Int): Flow<List<WineEntity>> =
        wineDao.observeLowStockWines(threshold.coerceAtLeast(0))

    private fun validate(wine: WineEntity) {
        require(wine.name.isNotBlank()) { "Wine name cannot be blank." }
        require(wine.stockQuantity >= 0) { "Stock quantity cannot be negative." }
    }
}

/** Signals that a requested wine was removed before an operation completed. */
class WineNotFoundException : IllegalStateException()
