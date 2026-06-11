package com.eloop.mobileapp
 
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.delay

class DeviceAnalyzer {

    companion object {
        private var scanCount = 0
    }

    suspend fun analyzeImage(bitmap: Bitmap, hint: String = ""): DeviceEstimate? {
        // 4-item cycle logic
        val currentCategory = when (scanCount % 4) {
            0 -> "Smartphone"
            1 -> "Laptop"
            2 -> "Headphone"
            3 -> "Smartwatch"
            else -> "Smartphone"
        }

        Log.d("DeviceAnalyzer", "Mock Scan #$scanCount -> Returning $currentCategory")
        
        scanCount++
        
        // Direct return without delay to prevent coroutine issues during navigation
        return DevicePriceTable.getEstimateByCategory(currentCategory)
    }
}

