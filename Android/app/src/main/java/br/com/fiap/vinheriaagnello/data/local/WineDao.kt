package br.com.fiap.vinheriaagnello.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room data access contract for wine inventory CRUD and observable queries.
 *
 * All write operations are suspend functions so database work never blocks the UI thread.
 */
@Dao
interface WineDao {
    /** Inserts [wine] and returns its generated local identifier. */
    @Insert
    suspend fun insertWine(wine: WineEntity): Long

    /** Observes the complete inventory ordered alphabetically by wine name. */
    @Query("SELECT * FROM wines ORDER BY name COLLATE NOCASE ASC")
    fun observeAllWines(): Flow<List<WineEntity>>

    /** Returns the wine identified by [id], or `null` when it no longer exists. */
    @Query("SELECT * FROM wines WHERE id = :id LIMIT 1")
    suspend fun findWineById(id: Long): WineEntity?

    /** Observes wines whose names contain [query], ignoring alphabetical case for ordering. */
    @Query("SELECT * FROM wines WHERE name LIKE '%' || :query || '%' ORDER BY name COLLATE NOCASE ASC")
    fun searchWinesByName(query: String): Flow<List<WineEntity>>

    /** Observes wines at or below the supplied stock [threshold]. */
    @Query("SELECT * FROM wines WHERE stockQuantity <= :threshold ORDER BY stockQuantity ASC, name COLLATE NOCASE ASC")
    fun observeLowStockWines(threshold: Int): Flow<List<WineEntity>>

    /** Updates [wine] and returns the number of affected rows. */
    @Update
    suspend fun updateWine(wine: WineEntity): Int

    /** Deletes [wine] and returns the number of affected rows. */
    @Delete
    suspend fun deleteWine(wine: WineEntity): Int
}
