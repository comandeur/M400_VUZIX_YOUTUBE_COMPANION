package com.m400companion.glasses

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.vuzix.connectivity.sdk.Connectivity

class PlayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Constants.ACTION_PLAY) return

        // Only accept broadcasts that really came from our phone app via the
        // Vuzix Connectivity framework (rejects locally spoofed intents).
        if (!Connectivity.get(context).verify(intent, Constants.PHONE_PACKAGE)) {
            Log.w("PlayReceiver", "Dropped unverified PLAY_VIDEO broadcast")
            return
        }

        val url = intent.getStringExtra(Constants.EXTRA_URL) ?: return

        val launch = Intent(context, PlayerActivity::class.java).apply {
            putExtra(Constants.EXTRA_URL, url)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
        try {
            context.startActivity(launch)
        } catch (e: Exception) {
            Log.e("PlayReceiver", "Could not start player", e)
        }
    }
}
