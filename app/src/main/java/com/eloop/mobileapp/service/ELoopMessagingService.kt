package com.eloop.mobileapp.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class ELoopMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        
        db.collection("users").document(uid).update("fcmToken", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        
        val title = message.notification?.title ?: message.data["title"] ?: "New Notification"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        
        val notificationData = mapOf(
            "id" to UUID.randomUUID().toString(),
            "title" to title,
            "body" to body,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users").document(uid)
                  .collection("notifications")
                  .document(notificationData["id"] as String)
                  .set(notificationData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

