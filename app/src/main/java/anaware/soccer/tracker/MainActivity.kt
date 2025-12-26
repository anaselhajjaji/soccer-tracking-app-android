package anaware.soccer.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import anaware.soccer.tracker.ui.SoccerTrackerApp
import anaware.soccer.tracker.ui.SoccerViewModel
import anaware.soccer.tracker.ui.SoccerViewModelFactory
import anaware.soccer.tracker.ui.theme.SoccerTrackerTheme

/**
 * Main activity that hosts the Soccer Tracker app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SoccerTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Get repository from application
                    val application = application as anaware.soccer.tracker.SoccerTrackerApp
                    val repository = application.repository

                    // Create ViewModel with factory
                    val viewModel: SoccerViewModel = viewModel(
                        factory = SoccerViewModelFactory(repository)
                    )

                    // Display app
                    SoccerTrackerApp(viewModel = viewModel)
                }
            }
        }
    }
}
