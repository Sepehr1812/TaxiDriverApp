package ir.snapp.snappboxtest.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class SnappBoxFirebaseIDService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.i("FCM token", token)
    }
}