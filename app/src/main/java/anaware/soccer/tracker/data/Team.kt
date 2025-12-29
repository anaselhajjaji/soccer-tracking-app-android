package anaware.soccer.tracker.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a soccer team that a player belongs to.
 *
 * @property id Unique identifier (UUID)
 * @property name Team name
 * @property color Team color in hex format (e.g., "#FF5722")
 * @property league Optional league name
 * @property season Optional season identifier (e.g., "2024-2025")
 */
data class Team(
    val id: String = "",
    val name: String = "",
    val color: String = "#2196F3", // Default blue color
    val league: String = "",
    val season: String = ""
) {
    /**
     * Returns display name with season if present (e.g., "FC United (2024-2025)").
     */
    fun getDisplayName(): String {
        return if (season.isNotBlank()) {
            "$name ($season)"
        } else {
            name
        }
    }

    /**
     * Converts hex color string to Compose Color.
     */
    fun getColorInt(): Color {
        return try {
            val colorString = color.removePrefix("#")
            val colorLong = colorString.toLong(16)
            Color(colorLong or 0xFF000000)
        } catch (e: Exception) {
            Color(0xFF2196F3) // Default blue if parsing fails
        }
    }
}
