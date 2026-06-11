package com.m400companion.glasses

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var overlayButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.status_text)
        overlayButton = findViewById(R.id.overlay_button)

        findViewById<Button>(R.id.open_youtube_button).setOnClickListener {
            startActivity(
                Intent(this, PlayerActivity::class.java)
                    .putExtra(Constants.EXTRA_URL, "https://m.youtube.com")
            )
        }

        overlayButton.setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val canAutoOpen = Settings.canDrawOverlays(this)
        overlayButton.visibility =
            if (canAutoOpen) android.view.View.GONE else android.view.View.VISIBLE
        statusText.text = getString(
            if (canAutoOpen) R.string.status_ready else R.string.status_need_overlay
        )
    }
}
