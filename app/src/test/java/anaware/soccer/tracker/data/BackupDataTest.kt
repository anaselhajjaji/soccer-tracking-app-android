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

        assertEquals(4, backupData.version)
        assertEquals(4, BackupData.CURRENT_VERSION)
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
            version = 4,
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
    fun `BackupData version 4 round trip preserves all data`() {
        val original = BackupData(
            version = 4,
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

        assertEquals(4, decoded.version)
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
    fun `BackupData deserialization without players teams and matches uses empty lists`() {
        val jsonWithoutPlayerTeamMatch = """
            {
                "version": 4,
                "exportDate": "2025-12-19T10:00:00",
                "actions": []
            }
        """.trimIndent()

        val backupData = json.decodeFromString<BackupData>(jsonWithoutPlayerTeamMatch)

        assertEquals(4, backupData.version)
        assertTrue(backupData.players.isEmpty())
        assertTrue(backupData.teams.isEmpty())
        assertTrue(backupData.matches.isEmpty())
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

    // Version 4 tests for Match entity

    @Test
    fun `BackupMatch fromMatch creates correct backup`() {
        val match = Match(
            id = "match-123",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier League",
            playerScore = 3,
            opponentScore = 2
        )

        val backupMatch = BackupMatch.fromMatch(match)

        assertEquals("match-123", backupMatch.id)
        assertEquals("2025-12-28", backupMatch.date)
        assertEquals("team-1", backupMatch.playerTeamId)
        assertEquals("team-2", backupMatch.opponentTeamId)
        assertEquals("Premier League", backupMatch.league)
        assertEquals(3, backupMatch.playerScore)
        assertEquals(2, backupMatch.opponentScore)
    }

    @Test
    fun `BackupMatch toMatch creates correct entity`() {
        val backupMatch = BackupMatch(
            id = "match-456",
            date = "2025-12-29",
            playerTeamId = "team-3",
            opponentTeamId = "team-4",
            league = "Youth League",
            playerScore = 1,
            opponentScore = 1
        )

        val match = backupMatch.toMatch()

        assertEquals("match-456", match.id)
        assertEquals("2025-12-29", match.date)
        assertEquals("team-3", match.playerTeamId)
        assertEquals("team-4", match.opponentTeamId)
        assertEquals("Youth League", match.league)
        assertEquals(1, match.playerScore)
        assertEquals(1, match.opponentScore)
    }

    @Test
    fun `BackupMatch serialization includes all fields`() {
        val backupMatch = BackupMatch(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier",
            playerScore = 3,
            opponentScore = 2
        )

        val jsonString = json.encodeToString(backupMatch)

        assertTrue(jsonString.contains("\"id\""))
        assertTrue(jsonString.contains("\"date\""))
        assertTrue(jsonString.contains("\"playerTeamId\""))
        assertTrue(jsonString.contains("\"opponentTeamId\""))
        assertTrue(jsonString.contains("\"league\""))
        assertTrue(jsonString.contains("\"playerScore\""))
        assertTrue(jsonString.contains("\"opponentScore\""))
        assertTrue(jsonString.contains("match-1"))
        assertTrue(jsonString.contains("Premier"))
    }

    @Test
    fun `BackupMatch deserialization with missing scores uses defaults`() {
        val jsonWithoutScores = """
            {
                "id": "match-1",
                "date": "2025-12-28",
                "playerTeamId": "team-1",
                "opponentTeamId": "team-2",
                "league": "Premier"
            }
        """.trimIndent()

        val backupMatch = json.decodeFromString<BackupMatch>(jsonWithoutScores)

        assertEquals(-1, backupMatch.playerScore)
        assertEquals(-1, backupMatch.opponentScore)
    }

    @Test
    fun `BackupData with matches serializes correctly`() {
        val backupData = BackupData(
            version = 4,
            exportDate = "2025-12-28T10:00:00",
            actions = listOf(
                BackupAction(
                    dateTime = "2025-12-28T14:30:00",
                    actionCount = 5,
                    actionType = "GOAL",
                    match = true,
                    opponent = "Team A",
                    playerId = "player-1",
                    teamId = "team-1",
                    matchId = "match-1"
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
                ),
                BackupTeam(
                    id = "team-2",
                    name = "Lightning SC",
                    color = "#FF5722",
                    league = "Premier",
                    season = "2024-2025"
                )
            ),
            matches = listOf(
                BackupMatch(
                    id = "match-1",
                    date = "2025-12-28",
                    playerTeamId = "team-1",
                    opponentTeamId = "team-2",
                    league = "Premier",
                    playerScore = 3,
                    opponentScore = 2
                )
            )
        )

        val jsonString = json.encodeToString(backupData)

        assertTrue(jsonString.contains("\"matches\""))
        assertTrue(jsonString.contains("\"matchId\""))
        assertTrue(jsonString.contains("match-1"))
        assertTrue(jsonString.contains("Thunder FC"))
        assertTrue(jsonString.contains("Lightning SC"))
    }

    @Test
    fun `BackupData version 4 round trip with matches preserves all data`() {
        val original = BackupData(
            version = 4,
            exportDate = "2025-12-28T10:00:00",
            actions = listOf(
                BackupAction(
                    dateTime = "2025-12-28T14:30:00",
                    actionCount = 5,
                    actionType = "GOAL",
                    match = true,
                    opponent = "Team A",
                    playerId = "player-1",
                    teamId = "team-1",
                    matchId = "match-1"
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
                ),
                BackupTeam(
                    id = "team-2",
                    name = "Lightning SC",
                    color = "#FF5722",
                    league = "Premier",
                    season = "2024-2025"
                )
            ),
            matches = listOf(
                BackupMatch(
                    id = "match-1",
                    date = "2025-12-28",
                    playerTeamId = "team-1",
                    opponentTeamId = "team-2",
                    league = "Premier",
                    playerScore = 3,
                    opponentScore = 2
                )
            )
        )

        val jsonString = json.encodeToString(original)
        val decoded = json.decodeFromString<BackupData>(jsonString)

        assertEquals(4, decoded.version)
        assertEquals(original.exportDate, decoded.exportDate)
        assertEquals(1, decoded.actions.size)
        assertEquals("match-1", decoded.actions[0].matchId)
        assertEquals(1, decoded.players.size)
        assertEquals("John Doe", decoded.players[0].name)
        assertEquals(2, decoded.teams.size)
        assertEquals("Thunder FC", decoded.teams[0].name)
        assertEquals("Lightning SC", decoded.teams[1].name)
        assertEquals(1, decoded.matches.size)
        assertEquals("match-1", decoded.matches[0].id)
        assertEquals(3, decoded.matches[0].playerScore)
        assertEquals(2, decoded.matches[0].opponentScore)
    }

    @Test
    fun `Legacy BackupAction without matchId is backward compatible`() {
        val legacyAction = BackupAction(
            dateTime = "2025-12-28T14:30:00",
            actionCount = 5,
            actionType = "GOAL",
            match = true,
            opponent = "Team A",
            playerId = "player-1",
            teamId = "team-1"
            // No matchId
        )

        val action = legacyAction.toSoccerAction(123456789L)

        assertEquals("", action.matchId)
        assertEquals("player-1", action.playerId)
        assertEquals("team-1", action.teamId)
    }
}
