package anaware.soccer.tracker.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Unit tests for BackupData and BackupAction serialization.
 */
class BackupDataTest {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true // Include fields with default values
    }

    @Test
    fun `BackupAction fromSoccerAction creates correct backup`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 14, 30)
        val action = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        val backupAction = BackupAction.fromSoccerAction(action)

        assertEquals(action.dateTime, backupAction.dateTime)
        assertEquals(action.actionCount, backupAction.actionCount)
        assertEquals(action.actionType, backupAction.actionType)
        assertEquals(action.isMatch, backupAction.match) // Firestore uses "match"
        assertEquals(action.opponent, backupAction.opponent)
    }

    @Test
    fun `BackupAction toSoccerAction creates correct entity`() {
        val backupAction = BackupAction(
            dateTime = "2025-12-19T14:30:00",
            actionCount = 3,
            actionType = "ASSIST",
            match = false, // Firestore uses "match"
            opponent = "Team B"
        )

        val actionId = 123456789L
        val action = backupAction.toSoccerAction(actionId)

        assertEquals(backupAction.dateTime, action.dateTime)
        assertEquals(backupAction.actionCount, action.actionCount)
        assertEquals(backupAction.actionType, action.actionType)
        assertEquals(backupAction.match, action.isMatch) // Convert back to "isMatch"
        assertEquals(backupAction.opponent, action.opponent)
        assertEquals(actionId, action.id) // ID comes from Firestore document ID
    }

    @Test
    fun `BackupAction serialization includes all fields`() {
        val backupAction = BackupAction(
            dateTime = "2025-12-19T14:30:00",
            actionCount = 5,
            actionType = "GOAL",
            match = true, // Firestore uses "match"
            opponent = "Team A"
        )

        val jsonString = json.encodeToString(backupAction)

        assertTrue(jsonString.contains("\"dateTime\""))
        assertTrue(jsonString.contains("\"actionCount\""))
        assertTrue(jsonString.contains("\"actionType\""))
        assertTrue(jsonString.contains("\"match\"")) // Firestore field name
        assertTrue(jsonString.contains("\"opponent\""))
        assertTrue(jsonString.contains("Team A"))
    }

    @Test
    fun `BackupAction deserialization with missing opponent uses default`() {
        val jsonWithoutOpponent = """
            {
                "dateTime": "2025-12-19T14:30:00",
                "actionCount": 5,
                "actionType": "GOAL",
                "match": true
            }
        """.trimIndent()

        val backupAction = json.decodeFromString<BackupAction>(jsonWithoutOpponent)

        assertEquals("", backupAction.opponent)
    }

    @Test
    fun `BackupData has correct version`() {
        val backupData = BackupData(
            exportDate = "2025-12-19T10:00:00",
            actions = emptyList()
        )

        assertEquals(3, backupData.version)
        assertEquals(3, BackupData.CURRENT_VERSION)
    }

    @Test
    fun `BackupData serialization works correctly`() {
        val actions = listOf(
            BackupAction(
                dateTime = "2025-12-19T14:30:00",
                actionCount = 5,
                actionType = "GOAL",
                match = true, // Firestore uses "match"
                opponent = "Team A"
            ),
            BackupAction(
                dateTime = "2025-12-18T16:00:00",
                actionCount = 2,
                actionType = "ASSIST",
                match = false, // Firestore uses "match"
                opponent = ""
            )
        )

        val backupData = BackupData(
            exportDate = "2025-12-19T10:00:00",
            actions = actions
        )

        val jsonString = json.encodeToString(backupData)

        assertTrue("JSON should contain version", jsonString.contains("version"))
        assertTrue("JSON should contain exportDate", jsonString.contains("exportDate"))
        assertTrue("JSON should contain actions", jsonString.contains("actions"))
        assertTrue("JSON should contain Team A", jsonString.contains("Team A"))
    }

    @Test
    fun `BackupData round trip preserves data`() {
        val original = BackupData(
            exportDate = "2025-12-19T10:00:00",
            actions = listOf(
                BackupAction(
                    dateTime = "2025-12-19T14:30:00",
                    actionCount = 5,
                    actionType = "GOAL",
                    match = true, // Firestore uses "match"
                    opponent = "Team A"
                )
            )
        )

        val jsonString = json.encodeToString(original)
        val decoded = json.decodeFromString<BackupData>(jsonString)

        assertEquals(original.version, decoded.version)
        assertEquals(original.exportDate, decoded.exportDate)
        assertEquals(original.actions.size, decoded.actions.size)
        assertEquals(original.actions[0].opponent, decoded.actions[0].opponent)
    }

    // Version 3 tests for player and team fields

    @Test
    fun `BackupAction with playerId and teamId converts correctly`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 14, 30)
        val action = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456"
        )

        val backupAction = BackupAction.fromSoccerAction(action)

        assertEquals("player-123", backupAction.playerId)
        assertEquals("team-456", backupAction.teamId)
    }

    @Test
    fun `BackupAction to SoccerAction preserves player and team`() {
        val backupAction = BackupAction(
            dateTime = "2025-12-19T14:30:00",
            actionCount = 3,
            actionType = "ASSIST",
            match = false,
            opponent = "Team B",
            playerId = "player-789",
            teamId = "team-012"
        )

        val action = backupAction.toSoccerAction(123456789L)

        assertEquals("player-789", action.playerId)
        assertEquals("team-012", action.teamId)
    }

    @Test
    fun `BackupAction serialization includes player and team fields`() {
        val backupAction = BackupAction(
            dateTime = "2025-12-19T14:30:00",
            actionCount = 5,
            actionType = "GOAL",
            match = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456"
        )

        val jsonString = json.encodeToString(backupAction)

        assertTrue(jsonString.contains("\"playerId\""))
        assertTrue(jsonString.contains("\"teamId\""))
        assertTrue(jsonString.contains("player-123"))
        assertTrue(jsonString.contains("team-456"))
    }

    @Test
    fun `BackupAction deserialization with missing player and team uses defaults`() {
        val jsonWithoutPlayerTeam = """
            {
                "dateTime": "2025-12-19T14:30:00",
                "actionCount": 5,
                "actionType": "GOAL",
                "match": true,
                "opponent": "Team A"
            }
        """.trimIndent()

        val backupAction = json.decodeFromString<BackupAction>(jsonWithoutPlayerTeam)

        assertEquals("", backupAction.playerId)
        assertEquals("", backupAction.teamId)
    }

    @Test
    fun `BackupPlayer fromPlayer creates correct backup`() {
        val player = Player(
            id = "player-123",
            name = "John Doe",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1", "team-2")
        )

        val backupPlayer = BackupPlayer.fromPlayer(player)

        assertEquals("player-123", backupPlayer.id)
        assertEquals("John Doe", backupPlayer.name)
        assertEquals("2010-05-15", backupPlayer.birthdate)
        assertEquals(10, backupPlayer.number)
        assertEquals(2, backupPlayer.teams.size)
    }

    @Test
    fun `BackupPlayer toPlayer creates correct entity`() {
        val backupPlayer = BackupPlayer(
            id = "player-456",
            name = "Jane Smith",
            birthdate = "2012-08-20",
            number = 7,
            teams = listOf("team-3")
        )

        val player = backupPlayer.toPlayer()

        assertEquals("player-456", player.id)
        assertEquals("Jane Smith", player.name)
        assertEquals("2012-08-20", player.birthdate)
        assertEquals(7, player.number)
        assertEquals(1, player.teams.size)
    }

    @Test
    fun `BackupTeam fromTeam creates correct backup`() {
        val team = Team(
            id = "team-789",
            name = "Thunder FC",
            color = "#FF0000",
            league = "Premier",
            season = "2024-2025"
        )

        val backupTeam = BackupTeam.fromTeam(team)

        assertEquals("team-789", backupTeam.id)
        assertEquals("Thunder FC", backupTeam.name)
        assertEquals("#FF0000", backupTeam.color)
        assertEquals("Premier", backupTeam.league)
        assertEquals("2024-2025", backupTeam.season)
    }

    @Test
    fun `BackupTeam toTeam creates correct entity`() {
        val backupTeam = BackupTeam(
            id = "team-012",
            name = "Lightning SC",
            color = "#00FF00",
            league = "Division 1",
            season = "2024"
        )

        val team = backupTeam.toTeam()

        assertEquals("team-012", team.id)
        assertEquals("Lightning SC", team.name)
        assertEquals("#00FF00", team.color)
        assertEquals("Division 1", team.league)
        assertEquals("2024", team.season)
    }

    @Test
    fun `BackupData with players and teams serializes correctly`() {
        val backupData = BackupData(
            version = 3,
            exportDate = "2025-12-19T10:00:00",
            actions = listOf(
                BackupAction(
                    dateTime = "2025-12-19T14:30:00",
                    actionCount = 5,
                    actionType = "GOAL",
                    match = true,
                    opponent = "Team A",
                    playerId = "player-1",
                    teamId = "team-1"
                )
            ),
            players = listOf(
                BackupPlayer(
                    id = "player-1",
                    name = "John Doe",
                    birthdate = "2010-05-15",
                    number = 10,
                    teams = listOf("team-1")
                )
            ),
            teams = listOf(
                BackupTeam(
                    id = "team-1",
                    name = "Thunder FC",
                    color = "#2196F3",
                    league = "Premier",
                    season = "2024-2025"
                )
            )
        )

        val jsonString = json.encodeToString(backupData)

        assertTrue(jsonString.contains("\"players\""))
        assertTrue(jsonString.contains("\"teams\""))
        assertTrue(jsonString.contains("John Doe"))
        assertTrue(jsonString.contains("Thunder FC"))
    }

    @Test
    fun `BackupData version 3 round trip preserves all data`() {
        val original = BackupData(
            version = 3,
            exportDate = "2025-12-19T10:00:00",
            actions = listOf(
                BackupAction(
                    dateTime = "2025-12-19T14:30:00",
                    actionCount = 5,
                    actionType = "GOAL",
                    match = true,
                    opponent = "Team A",
                    playerId = "player-1",
                    teamId = "team-1"
                )
            ),
            players = listOf(
                BackupPlayer(
                    id = "player-1",
                    name = "John Doe",
                    birthdate = "2010-05-15",
                    number = 10,
                    teams = listOf("team-1")
                )
            ),
            teams = listOf(
                BackupTeam(
                    id = "team-1",
                    name = "Thunder FC",
                    color = "#2196F3",
                    league = "Premier",
                    season = "2024-2025"
                )
            )
        )

        val jsonString = json.encodeToString(original)
        val decoded = json.decodeFromString<BackupData>(jsonString)

        assertEquals(3, decoded.version)
        assertEquals(original.exportDate, decoded.exportDate)
        assertEquals(1, decoded.actions.size)
        assertEquals("player-1", decoded.actions[0].playerId)
        assertEquals("team-1", decoded.actions[0].teamId)
        assertEquals(1, decoded.players.size)
        assertEquals("John Doe", decoded.players[0].name)
        assertEquals(1, decoded.teams.size)
        assertEquals("Thunder FC", decoded.teams[0].name)
    }

    @Test
    fun `BackupData deserialization without players and teams uses empty lists`() {
        val jsonWithoutPlayerTeam = """
            {
                "version": 3,
                "exportDate": "2025-12-19T10:00:00",
                "actions": []
            }
        """.trimIndent()

        val backupData = json.decodeFromString<BackupData>(jsonWithoutPlayerTeam)

        assertEquals(3, backupData.version)
        assertTrue(backupData.players.isEmpty())
        assertTrue(backupData.teams.isEmpty())
    }

    @Test
    fun `Legacy BackupAction without player team is backward compatible`() {
        val legacyAction = BackupAction(
            dateTime = "2025-12-19T14:30:00",
            actionCount = 5,
            actionType = "GOAL",
            match = true,
            opponent = "Team A"
            // No playerId or teamId
        )

        val action = legacyAction.toSoccerAction(123456789L)

        assertEquals("", action.playerId)
        assertEquals("", action.teamId)
        assertTrue(action.isLegacyAction())
    }
}
