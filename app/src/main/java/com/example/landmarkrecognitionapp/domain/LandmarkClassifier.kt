package com.example.landmarkrecognitionapp.domain

import android.graphics.Bitmap

interface LandmarkClassifier {

    fun classify(bitmap: Bitmap, rotationDegrees: Int): List<Classification>
}