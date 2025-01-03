package com.example.landmarkrecognitionapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.landmarkrecognitionapp.domain.Classification
import com.example.landmarkrecognitionapp.presentation.CameraPreview
import com.example.landmarkrecognitionapp.presentation.LandmarkImageAnalyzer
import com.example.landmarkrecognitionapp.ui.theme.LandmarkRecognitionAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            if (!hasCameraPermission()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    0
                )
            }
            var classifications by remember {
                mutableStateOf(emptyList<Classification>())
            }

            val analyzer = remember {
                LandmarkImageAnalyzer(
                    classifier = com.example.landmarkrecognitionapp.data.TfLiteLandmarkClassifier(applicationContext),
                    onResults = {
                        classifications = it
                    }
                )
            }

            val controller = remember {
                LifecycleCameraController(applicationContext).apply {
                    setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                    setImageAnalysisAnalyzer(
                        ContextCompat.getMainExecutor(applicationContext),
                        analyzer
                    )
                }
            }

            LandmarkRecognitionAppTheme {
                Surface(shadowElevation = 8.dp , shape = RoundedCornerShape(6.dp) , color = MaterialTheme.colorScheme.onBackground) {
                    Box(modifier = Modifier.fillMaxSize()){
                        CameraPreview(controller = controller , modifier = Modifier.fillMaxSize())

                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .align(Alignment.Center)
                        ) {
                            classifications.forEach {
                                Text(text = it.name,
                                    modifier = Modifier.fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

