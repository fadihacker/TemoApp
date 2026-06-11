package com.eloop.mobileapp.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import java.util.UUID
import androidx.compose.ui.graphics.Color
import com.eloop.mobileapp.ui.theme.PrimaryDark


data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val ecoPoints: Int = 0,
    val devicesRecycled: Int = 0,
    val co2Saved: Double = 0.0,
    val profileImageUrl: String? = null
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val points: Int,
    val devices: Int,
    val initials: String,
    val color: Color,
    val isCurrentUser: Boolean = false
)

data class ScanResult(
    val id: String = UUID.randomUUID().toString(),
    val deviceName: String = "",
    val price: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class NotificationModel(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)

data class PickupOrder(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val userName: String = "",
    val deviceName: String = "",
    val devicePrice: String = "",
    val pickupDate: String = "",
    val pickupTime: String = "",
    val status: String = "Scheduled",
    val ecoPointsEarned: Int = 50,
    val deviceImageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class DevicePrice(
    val id: String = "",
    val modelName: String = "",
    val basePrice: Double = 0.0,
    val category: String = "",
    val points: Int = 0
)

object FirebaseRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUser get() = auth.currentUser

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun createUserProfile(name: String, email: String) {
        val uid = auth.currentUser?.uid ?: return
        val profile = UserProfile(uid = uid, name = name, email = email)
        db.collection("users").document(uid).set(profile, SetOptions.merge()).await()
    }

    suspend fun getUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("users").document(uid).get().await().toObject(UserProfile::class.java)
    }

    fun listenToUserProfile(): Flow<UserProfile?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        
        val registration = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(UserProfile::class.java))
                } else {
                    trySend(null)
                }
            }
        
        awaitClose { registration.remove() }
    }

    suspend fun saveScanResult(deviceName: String, price: String, category: String) {
        val uid = auth.currentUser?.uid ?: return
        val scan = ScanResult(deviceName = deviceName, price = price, category = category)
        db.collection("users").document(uid).collection("scans").document(scan.id).set(scan).await()
    }

    suspend fun getScanHistory(): List<ScanResult> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return db.collection("users").document(uid).collection("scans")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await().toObjects(ScanResult::class.java)
    }

    suspend fun savePickupOrder(context: Context, deviceName: String, price: String, date: String, time: String, address: String = "", pickupType: String = "Home Pickup") {
        val uid = auth.currentUser?.uid
        val profile = if (uid != null) getUserProfile() else null
        val order = PickupOrder(
            id = UUID.randomUUID().toString(),
            userId = uid ?: "local_user",
            userName = profile?.name ?: SessionManager(context).getUserName().ifEmpty { "Eco Warrior" },
            deviceName = deviceName,
            devicePrice = price,
            pickupDate = date,
            pickupTime = time,
            status = "Scheduled",
            ecoPointsEarned = 50,
            timestamp = System.currentTimeMillis()
        )
        
        // Save locally to SharedPreferences first as a guarantee
        saveOrderLocally(context, order)
        
        // Try saving to Firebase if user is logged in
        if (uid != null) {
            try {
                // Save to user's orders
                db.collection("users").document(uid).collection("orders").document(order.id).set(order).await()
                
                // Save to global orders for admin
                db.collection("orders").document(order.id).set(order).await()
                
                // Update user points
                val currentProfile = getUserProfile()
                if (currentProfile != null) {
                    val updatedProfile = currentProfile.copy(
                        ecoPoints = currentProfile.ecoPoints + order.ecoPointsEarned,
                        devicesRecycled = currentProfile.devicesRecycled + 1,
                        co2Saved = currentProfile.co2Saved + 2.5
                    )
                    db.collection("users").document(uid).set(updatedProfile).await()
                }
            } catch (e: Exception) {
                // Ignore Firestore failures and fallback to local success
                e.printStackTrace()
            }
        }
    }

    private fun saveOrderLocally(context: Context, order: PickupOrder) {
        try {
            val sharedPreferences = context.getSharedPreferences("ELoopLocalOrders", Context.MODE_PRIVATE)
            val gson = com.google.gson.Gson()
            val ordersList = getLocalOrders(context).toMutableList()
            ordersList.add(0, order) // Add new order at the beginning
            val json = gson.toJson(ordersList)
            sharedPreferences.edit().putString("orders_list", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLocalOrders(context: Context): List<PickupOrder> {
        return try {
            val sharedPreferences = context.getSharedPreferences("ELoopLocalOrders", Context.MODE_PRIVATE)
            val json = sharedPreferences.getString("orders_list", null) ?: return emptyList()
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<PickupOrder>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPickupOrders(context: Context): List<PickupOrder> {
        val localOrders = getLocalOrders(context)
        val uid = auth.currentUser?.uid ?: return localOrders.ifEmpty {
            // Seed a sample order if empty to keep UI populated
            val sample = PickupOrder(
                id = "sample-1",
                userId = "local_user",
                userName = SessionManager(context).getUserName().ifEmpty { "Eco Warrior" },
                deviceName = "iPhone 12 Pro (Sample Entry)",
                devicePrice = "EGP 10,200",
                pickupDate = "Tomorrow",
                pickupTime = "11-1 PM",
                status = "Scheduled"
            )
            listOf(sample)
        }

        return try {
            val remoteOrders = db.collection("users").document(uid).collection("orders")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await().toObjects(PickupOrder::class.java)
            
            // Merge local and remote orders, filtering duplicates by ID
            val allOrders = (localOrders + remoteOrders).distinctBy { it.id }
            allOrders.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            localOrders
        }
    }

    suspend fun getLeaderboard(): List<LeaderboardEntry> {
        val currentUid = auth.currentUser?.uid
        return try {
            val snapshots = db.collection("users")
                .orderBy("ecoPoints", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()

            val list = snapshots.documents.mapIndexed { index, doc ->
                val name = doc.getString("name") ?: "Unknown"
                val points = doc.getLong("ecoPoints")?.toInt() ?: 0
                val devices = doc.getLong("devicesRecycled")?.toInt() ?: 0
                val uid = doc.id

                LeaderboardEntry(
                    rank = index + 1,
                    name = name,
                    points = points,
                    devices = devices,
                    initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                    color = when (index + 1) {
                        1 -> Color(0xFFFFD700) // Gold
                        2 -> Color(0xFFC0C0C0) // Silver
                        3 -> Color(0xFFCD7F32) // Bronze
                        else -> PrimaryDark
                    },
                    isCurrentUser = uid == currentUid
                )
            }
            if (list.isEmpty()) {
                getMockLeaderboard(currentUid)
            } else {
                list
            }
        } catch (e: Exception) {
            getMockLeaderboard(currentUid)
        }
    }

    private fun getMockLeaderboard(currentUid: String?): List<LeaderboardEntry> {
        val mockUsers = listOf(
            Triple("Ahmed Khaled", 1520, 14),
            Triple("Sara Aly", 1250, 11),
            Triple("Youssef Omar", 980, 9),
            Triple("Mariam Mahmoud", 810, 7),
            Triple("John Smith", 640, 5),
            Triple("Yasmine Tarek", 420, 3)
        )

        val leaderboard = mockUsers.mapIndexed { index, triple ->
            LeaderboardEntry(
                rank = index + 1,
                name = triple.first,
                points = triple.second,
                devices = triple.third,
                initials = triple.first.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                color = when (index + 1) {
                    1 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> Color(0xFF4CAF50)
                },
                isCurrentUser = false
            )
        }.toMutableList()

        val currentUserName = if (currentUid != null) "You" else "Eco Warrior"
        val currentUserPoints = 300

        val currentUserEntry = LeaderboardEntry(
            rank = 0,
            name = currentUserName,
            points = currentUserPoints,
            devices = 2,
            initials = "ME",
            color = Color(0xFF2E7D32),
            isCurrentUser = true
        )

        leaderboard.add(currentUserEntry)
        
        val sortedList = leaderboard.sortedByDescending { it.points }
        return sortedList.mapIndexed { index, entry ->
            entry.copy(
                rank = index + 1,
                color = when (index + 1) {
                    1 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> entry.color
                }
            )
        }
    }

    fun listenToNotifications(): Flow<List<NotificationModel>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = db.collection("users").document(uid).collection("notifications")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(NotificationModel::class.java))
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { registration.remove() }
    }

    suspend fun redeemVoucher(voucherCost: Int, voucherTitle: String): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        val userRef = db.collection("users").document(uid)
        val voucherId = UUID.randomUUID().toString()
        val voucherRef = userRef.collection("vouchers").document(voucherId)

        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentPoints = snapshot.getLong("ecoPoints")?.toInt() ?: 0

                if (currentPoints < voucherCost) {
                    throw Exception("Insufficient ecoPoints")
                }

                transaction.update(userRef, "ecoPoints", currentPoints - voucherCost)

                val voucherData = mapOf(
                    "id" to voucherId,
                    "title" to voucherTitle,
                    "cost" to voucherCost,
                    "redeemedAt" to System.currentTimeMillis(),
                    "isUsed" to false
                )
                transaction.set(voucherRef, voucherData)
                voucherId
            }.await()
            Result.success(voucherId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Admin & Catalog Methods ---

    suspend fun getDevicePrice(modelName: String): DevicePrice? {
        return db.collection("prices")
            .whereEqualTo("modelName", modelName)
            .get().await().toObjects(DevicePrice::class.java).firstOrNull()
    }

    /**
     * Fetches ALL device prices from Firestore as a map keyed by modelName (lowercased).
     * Returns an empty map if Firestore has no data or on error.
     * This enables updating prices remotely without shipping a new app version.
     */
    suspend fun fetchDevicePrices(): Map<String, Double> {
        return try {
            val snapshot = db.collection("prices").get().await()
            val result = mutableMapOf<String, Double>()
            snapshot.documents.forEach { doc ->
                val key = (doc.getString("modelName") ?: "").lowercase().trim()
                val price = doc.getDouble("basePrice") ?: 0.0
                if (key.isNotEmpty() && price > 0) result[key] = price
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun getAllOrders(): List<PickupOrder> {
        return db.collection("orders")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await().toObjects(PickupOrder::class.java)
    }

    suspend fun updateOrderStatus(orderId: String, userId: String, newStatus: String) {
        // Update global order
        db.collection("orders").document(orderId).update("status", newStatus).await()
        // Update user's order copy
        db.collection("users").document(userId).collection("orders").document(orderId).update("status", newStatus).await()
    }

    suspend fun seedSampleData() {
        val samplePrices = listOf(
            DevicePrice(id = "p1", modelName = "iPhone 15 Pro, 256GB, Excellent Condition", basePrice = 48000.0, category = "Phones", points = 1500),
            DevicePrice(id = "p2", modelName = "Samsung Galaxy S23 Ultra, 256GB, Good Condition", basePrice = 32000.0, category = "Phones", points = 1200),
            DevicePrice(id = "p3", modelName = "MacBook Air M2, 512GB, Excellent Condition", basePrice = 55000.0, category = "Laptops", points = 2500),
            DevicePrice(id = "p4", modelName = "Sony A7 IV, Good Condition", basePrice = 65000.0, category = "Cameras", points = 3000),
            DevicePrice(id = "p5", modelName = "iPad Pro 11, 128GB, Excellent Condition", basePrice = 28000.0, category = "Tablets", points = 1000)
        )
        
        samplePrices.forEach { price ->
            db.collection("prices").document(price.id).set(price).await()
        }
        
        // Also add a sample order if not exists
        val uid = auth.currentUser?.uid ?: return
        val profile = getUserProfile()
        val sampleOrder = PickupOrder(
            userId = uid,
            userName = profile?.name ?: "Demo User",
            deviceName = "iPhone 12 Pro (Sample Entry)",
            pickupDate = "Oct 30",
            pickupTime = "10:00 AM",
            status = "Scheduled"
        )
        db.collection("orders").document(sampleOrder.id).set(sampleOrder).await()
        db.collection("users").document(uid).collection("orders").document(sampleOrder.id).set(sampleOrder).await()
    }

    fun logout() {
        auth.signOut()
    }
}

