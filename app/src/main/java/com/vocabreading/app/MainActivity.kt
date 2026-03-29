package com.vocabreading.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vocabreading.app.ui.screens.CameraScreen
import com.vocabreading.app.ui.screens.ProcessingScreen
import com.vocabreading.app.ui.screens.ResultsScreen
import com.vocabreading.app.ui.screens.SettingsScreen
import com.vocabreading.app.ui.theme.VocabReadingTheme
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            // Permission denied — user will see the camera error
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OpenCV
        OpenCVLoader.initLocal()

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            VocabReadingTheme {
                val viewModel: MainViewModel = viewModel()
                val screen by viewModel.screen.collectAsState()
                val settings by viewModel.settings.collectAsState()
                val snackbarMessage by viewModel.snackbar.collectAsState()
                val context = LocalContext.current

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(snackbarMessage) {
                    snackbarMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.dismissSnackbar()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    when (val currentScreen = screen) {
                        is AppScreen.Camera -> {
                            CameraScreen(
                                onPhotoCaptured = { bitmap ->
                                    viewModel.onPhotoCaptured(bitmap)
                                },
                                onSettingsClick = {
                                    viewModel.navigateToSettings()
                                }
                            )
                        }

                        is AppScreen.Processing -> {
                            ProcessingScreen(
                                statusMessage = currentScreen.status
                            )
                        }

                        is AppScreen.Results -> {
                            ResultsScreen(
                                words = currentScreen.words,
                                onToggleWord = { index ->
                                    viewModel.toggleWord(index)
                                },
                                onCreateFlashcards = {
                                    viewModel.createFlashcards(context)
                                },
                                onRetake = {
                                    viewModel.navigateToCamera()
                                }
                            )
                        }

                        is AppScreen.Settings -> {
                            SettingsScreen(
                                settings = settings,
                                onSettingsChanged = { newSettings ->
                                    viewModel.updateSettings(newSettings)
                                },
                                onBack = {
                                    viewModel.navigateToCamera()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
