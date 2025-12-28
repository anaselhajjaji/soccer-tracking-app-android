package anaware.soccer.tracker.data

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

/**
 * Represents a soccer player with their personal information.
 *
 * @property id Unique identifier (UUID)
 * @property name Player's full name
 * @property birthdate ISO date format (yyyy-MM-dd)
 * @property number Jersey number
 * @property teams List of team IDs this player belongs to
 */
data class Player(
    val id: String = "",
    val name: String = "",
    val birthdate: String = "",
    val number: Int = 0,
    val teams: List<String> = emptyList()
) {
    /**
     * Converts the ISO date string to a LocalDate object.
     */
    fun getBirthdate(): LocalDate {
        return LocalDate.parse(birthdate, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    /**
     * Returns formatted birthdate for display (e.g., "Dec 19, 2015").
     */
    fun getFormattedBirthdate(): String {
        val date = getBirthdate()
        return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", java.util.Locale.US))
    }

    /**
     * Calculates the player's current age in years.
     */
    fun getAge(): Int {
        val today = LocalDate.now()
        return Period.between(getBirthdate(), today).years
    }

    /**
     * Returns display name with jersey number (e.g., "John Doe #10").
     */
    fun getDisplayName(): String {
        return if (number > 0) {
            "$name #$number"
        } else {
            name
        }
    }
}
