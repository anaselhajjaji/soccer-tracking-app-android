package anaware.soccer.tracker.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class DebugBackupTest {
    private val json = Json { prettyPrint = true }

    @Test
    fun `debug json output`() {
        val backupData = BackupData(
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

        val jsonString = json.encodeToString(backupData)
        println("JSON Output:")
        println(jsonString)
        println("\nContains 'version': ${jsonString.contains("version")}")
    }
}
