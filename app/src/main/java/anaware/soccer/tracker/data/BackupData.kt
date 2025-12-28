package anaware.soccer.tracker.data

import kotlinx.serialization.Serializable

/**
 * Data class for backup/restore operations.
 * Contains all soccer action records in a serializable format.
 */
@Serializable
data class BackupData(
    val version: Int = 3,
    val exportDate: String,
    val actions: List<BackupAction>,
    val players: List<BackupPlayer> = emptyList(),
    val teams: List<BackupTeam> = emptyList()
) {
    companion object {
        const val CURRENT_VERSION = 3
    }
}

/**
 * Serializable representation of a SoccerAction for backup.
 * Must have no-arg constructor for Firestore deserialization.
 */
@Serializable
data class BackupAction(
    val dateTime: String = "",
    val actionCount: Int = 0,
    val actionType: String = "",
    val match: Boolean = false, // Firestore uses "match" not "isMatch"
    val opponent: String = "",
    val playerId: String = "",
    val teamId: String = ""
) {
    /**
     * Converts this backup action to a SoccerAction entity.
     * @param id The document ID from Firestore (should be the timestamp)
     */
    fun toSoccerAction(id: Long): SoccerAction {
        return SoccerAction(
            id = id,
            dateTime = dateTime,
            actionCount = actionCount,
            actionType = actionType,
            isMatch = match, // Convert from Firestore "match" to "isMatch"
            opponent = opponent,
            playerId = playerId,
            teamId = teamId
        )
    }

    companion object {
        /**
         * Creates a BackupAction from a SoccerAction entity.
         */
        fun fromSoccerAction(action: SoccerAction): BackupAction {
            return BackupAction(
                dateTime = action.dateTime,
                actionCount = action.actionCount,
                actionType = action.actionType,
                match = action.isMatch, // Convert from "isMatch" to Firestore "match"
                opponent = action.opponent,
                playerId = action.playerId,
                teamId = action.teamId
            )
        }
    }
}

/**
 * Serializable representation of a Player for backup.
 * Must have no-arg constructor for Firestore deserialization.
 */
@Serializable
data class BackupPlayer(
    val id: String = "",
    val name: String = "",
    val birthdate: String = "",
    val number: Int = 0,
    val teams: List<String> = emptyList()
) {
    /**
     * Converts this backup player to a Player entity.
     */
    fun toPlayer(): Player {
        return Player(
            id = id,
            name = name,
            birthdate = birthdate,
            number = number,
            teams = teams
        )
    }

    companion object {
        /**
         * Creates a BackupPlayer from a Player entity.
         */
        fun fromPlayer(player: Player): BackupPlayer {
            return BackupPlayer(
                id = player.id,
                name = player.name,
                birthdate = player.birthdate,
                number = player.number,
                teams = player.teams
            )
        }
    }
}

/**
 * Serializable representation of a Team for backup.
 * Must have no-arg constructor for Firestore deserialization.
 */
@Serializable
data class BackupTeam(
    val id: String = "",
    val name: String = "",
    val color: String = "#2196F3",
    val league: String = "",
    val season: String = ""
) {
    /**
     * Converts this backup team to a Team entity.
     */
    fun toTeam(): Team {
        return Team(
            id = id,
            name = name,
            color = color,
            league = league,
            season = season
        )
    }

    companion object {
        /**
         * Creates a BackupTeam from a Team entity.
         */
        fun fromTeam(team: Team): BackupTeam {
            return BackupTeam(
                id = team.id,
                name = team.name,
                color = team.color,
                league = team.league,
                season = team.season
            )
        }
    }
}
