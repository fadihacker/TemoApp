package com.eloop.mobileapp.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Image as ImageIcon
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.DeviceAnalyzer
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage
import android.util.Log
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController, deviceHint: String = "") {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Decode URI → Bitmap safely
            try {
                val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
                if (bitmap != null) {
                    selectedBitmap = bitmap
                    isAnalyzing = true

                    coroutineScope.launch {
                        try {
                            val estimate = DeviceAnalyzer().analyzeImage(bitmap, deviceHint)
                            isAnalyzing = false

                            if (estimate != null) {
                                try {
                                    val categoryEncoded = java.net.URLEncoder.encode(estimate.category, "UTF-8")
                                    val route = "condition_selection?" +
                                            "category=$categoryEncoded" +
                                            "&basePrice=${estimate.basePrice}" +
                                            "&co2Saved=${estimate.co2Saved}" +
                                            "&ecoPoints=${estimate.ecoPoints}"
                                    
                                    navController.navigate(route)
                                } catch (e: Exception) {
                                    Log.e("ScanScreen", "Navigation failed: ${e.message}")
                                    snackbarHostState.showSnackbar(
                                        if (isAr) "عذراً، حدث خطأ في معالجة البيانات" 
                                        else "Error processing device data"
                                    )
                                }
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = if (isAr)
                                        "لم يتم التعرف على جهاز إلكتروني. حاول برفع صورة أوضح."
                                    else
                                        "This doesn't look like an electronic device. Please try a clearer image."
                                )
                            }
                        } catch (e: Exception) {
                            isAnalyzing = false
                            snackbarHostState.showSnackbar(
                                message = if (isAr)
                                    "حدث خطأ أثناء التحليل. حاول مرة أخرى."
                                else
                                    "Analysis failed. Please try again."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isAr) "مسح الجهاز" else "Scan Device",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Instruction Box
            Surface(
                color = PrimaryGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                val instructionText = if (deviceHint.isNotEmpty()) {
                    if (isAr) "ارفع صورة لـ $deviceHint للتقييم" 
                    else "Upload a photo of your $deviceHint"
                } else {
                    if (isAr) "ارفع صورة جهازك لتقييمه بالذكاء الاصطناعي"
                    else "Upload a photo of your device for AI appraisal"
                }
                
                Text(
                    text = instructionText,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDark
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Image Preview Area
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isAnalyzing -> {
                            // Analyzing state
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (selectedBitmap != null) {
                                    Image(
                                        bitmap = selectedBitmap!!.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.6f)
                                            .clip(RoundedCornerShape(24.dp)),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        alpha = 0.4f
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                CircularProgressIndicator(color = PrimaryGreen, strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isAr) "جاري تحليل الجهاز..." else "Analyzing device...",
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryDark,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        selectedBitmap != null -> {
                            // Show selected image
                            Image(
                                bitmap = selectedBitmap!!.asImageBitmap(),
                                contentDescription = "Selected",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }

                        else -> {
                            // Empty state — tap to upload
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { galleryLauncher.launch("image/*") }
                                    .padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Upload,
                                    contentDescription = null,
                                    modifier = Modifier.size(72.dp),
                                    tint = PrimaryGreen.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isAr) "اضغط لرفع صورة" else "Tap to upload image",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isAr) "JPG, PNG مدعوم" else "JPG, PNG supported",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Upload Button
            Button(
                onClick = { 
                    if (!isAnalyzing) galleryLauncher.launch("image/*") 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnalyzing) PrimaryGreen.copy(alpha = 0.5f) else PrimaryGreen
                ),
                enabled = !isAnalyzing
            ) {
                Icon(Icons.Rounded.Upload, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "رفع صورة من المعرض" else "Upload from Gallery",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


