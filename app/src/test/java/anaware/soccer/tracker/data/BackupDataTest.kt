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
        encodeDefaults = true  // Include fields with default values
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
        assertEquals(action.isMatch, backupAction.match)  // Firestore uses "match"
        assertEquals(action.opponent, backupAction.opponent)
    }

    @Test
    fun `BackupAction toSoccerAction creates correct entity`() {
        val backupAction = BackupAction(
            dateTime = "2025-12-19T14:30:00",
            actionCount = 3,
            actionType = "ASSIST",
            match = false,  // Firestore uses "match"
            opponent = "Team B"
        )

        val actionId = 123456789L
        val action = backupAction.toSoccerAction(actionId)

        assertEquals(backupAction.dateTime, action.dateTime)
        assertEquals(backupAction.actionCount, action.actionCount)
        assertEquals(backupAction.actionType, action.actionType)
        assertEquals(backupAction.match, action.isMatch)  // Convert back to "isMatch"
        assertEquals(backupAction.opponent, action.opponent)
        assertEquals(actionId, action.id)  // ID comes from Firestore document ID
    }

    @Test
    fun `BackupAction serialization includes all fields`() {
        val backupAction = BackupAction(
            dateTime = "2025-12-19T14:30:00",
            actionCount = 5,
            actionType = "GOAL",
            match = true,  // Firestore uses "match"
            opponent = "Team A"
        )

        val jsonString = json.encodeToString(backupAction)

        assertTrue(jsonString.contains("\"dateTime\""))
        assertTrue(jsonString.contains("\"actionCount\""))
        assertTrue(jsonString.contains("\"actionType\""))
        assertTrue(jsonString.contains("\"match\""))  // Firestore field name
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

        assertEquals(2, backupData.version)
        assertEquals(2, BackupData.CURRENT_VERSION)
    }

    @Test
    fun `BackupData serialization works correctly`() {
        val actions = listOf(
            BackupAction(
                dateTime = "2025-12-19T14:30:00",
                actionCount = 5,
                actionType = "GOAL",
                match = true,  // Firestore uses "match"
                opponent = "Team A"
            ),
            BackupAction(
                dateTime = "2025-12-18T16:00:00",
                actionCount = 2,
                actionType = "ASSIST",
                match = false,  // Firestore uses "match"
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
                    match = true,  // Firestore uses "match"
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
}
