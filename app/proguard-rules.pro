# ═══════════════════════════════════════════════
# E-Loop ProGuard Rules
# ═══════════════════════════════════════════════

# ── Kotlin ──────────────────────────────────────
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keepattributes *Annotation*, InnerClasses, Signature, SourceFile, LineNumberTable, EnclosingMethod

# ── Firebase ────────────────────────────────────
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ── Firestore Data Models ────────────────────────
# Keep all data classes used with Firestore/Gson serialization
-keep class com.eloop.mobileapp.data.UserProfile { *; }
-keep class com.eloop.mobileapp.data.ScanResult { *; }
-keep class com.eloop.mobileapp.data.PickupOrder { *; }
-keep class com.eloop.mobileapp.data.NotificationModel { *; }
-keep class com.eloop.mobileapp.data.DevicePrice { *; }
-keep class com.eloop.mobileapp.data.LeaderboardEntry { *; }

# ── Retrofit + Gson ──────────────────────────────
-keep class com.squareup.retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.eloop.mobileapp.data.Gemini** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ── OkHttp ───────────────────────────────────────
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ── Coil (Image Loading) ─────────────────────────
-dontwarn coil.**

# ── Jetpack Compose ──────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Navigation Compose ───────────────────────────
-keep class androidx.navigation.** { *; }

# ── OSMDroid ─────────────────────────────────────
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# ── CameraX ──────────────────────────────────────
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ── ML Kit Image Labeling ─────────────────────────────
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_** { *; }
-dontwarn com.google.mlkit.**
