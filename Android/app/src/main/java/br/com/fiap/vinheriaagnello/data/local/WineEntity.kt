package br.com.fiap.vinheriaagnello.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persistent representation of a wine stored in the local inventory.
 *
 * @property id auto-generated local database identifier.
 * @property name commercial name displayed to the user.
 * @property country country of origin.
 * @property region producing region within the country.
 * @property grape primary grape variety.
 * @property type wine category, such as red, white, or sparkling.
 * @property stockQuantity number of units currently available.
 */
@Entity(tableName = "wines")
data class WineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val country: String,
    val region: String,
    val grape: String,
    val type: String,
    val stockQuantity: Int
)
