package com.m400companion.phone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vuzix.connectivity.sdk.Connectivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val GLASSES_PACKAGE = "com.m400companion.glasses"
        const val ACTION_PLAY = "com.m400companion.action.PLAY_VIDEO"
        const val EXTRA_URL = "com.m400companion.extra.URL"

        private val YOUTUBE_URL_REGEX =
            Regex("""https?://(?:www\.|m\.)?(?:youtube\.com|youtu\.be)/\S+""")
    }

    private lateinit var urlInput: EditText
    private lateinit var sendButton: Button
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput = findViewById(R.id.url_input)
        sendButton = findViewById(R.id.send_button)
        statusText = findViewById(R.id.status_text)

        sendButton.setOnClickListener { send(urlInput.text.toString()) }

        handleShareIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND || intent.type != "text/plain") return
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
        val url = extractYouTubeUrl(sharedText)
        if (url != null) {
            urlInput.setText(url)
            send(url)
        } else {
            urlInput.setText(sharedText)
            toast(getString(R.string.no_youtube_link))
        }
    }

    private fun extractYouTubeUrl(text: String): String? =
        YOUTUBE_URL_REGEX.find(text)?.value?.trimEnd('.', ',', ')')

    private fun send(rawText: String) {
        val url = extractYouTubeUrl(rawText.trim())
        if (url == null) {
            toast(getString(R.string.invalid_url))
            return
        }

        val connectivity = Connectivity.get(this)
        when {
            !connectivity.isAvailable -> {
                toast(getString(R.string.companion_missing))
            }
            !connectivity.isLinked -> {
                toast(getString(R.string.not_linked))
            }
            !connectivity.isConnected -> {
                toast(getString(R.string.not_connected))
            }
            else -> {
                val play = Intent(ACTION_PLAY).apply {
                    setPackage(GLASSES_PACKAGE)
                    putExtra(EXTRA_URL, url)
                }
                val sent = connectivity.sendBroadcast(play)
                toast(
                    if (sent) getString(R.string.sent_ok)
                    else getString(R.string.sent_fail)
                )
            }
        }
        updateStatus()
    }

    private fun updateStatus() {
        val connectivity = Connectivity.get(this)
        statusText.text = when {
            !connectivity.isAvailable -> getString(R.string.status_unavailable)
            !connectivity.isLinked -> getString(R.string.status_not_linked)
            !connectivity.isConnected -> getString(R.string.status_not_connected)
            else -> getString(R.string.status_connected)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
