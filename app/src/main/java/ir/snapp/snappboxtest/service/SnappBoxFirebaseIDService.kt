package ir.snapp.snappboxtest.service

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ir.snapp.snappboxtest.data.Offer
import ir.snapp.snappboxtest.util.Constants.OFFER
import ir.snapp.snappboxtest.view.MainActivity

class SnappBoxFirebaseIDService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            Log.d("FCM Message", "${message.data}")

            try {
                val offer = Gson().fromJson(message.data.toString(), Offer::class.java)

                startActivity(
                    Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(OFFER, offer)
                    }
                )
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.i("FCM token", token)
    }
}