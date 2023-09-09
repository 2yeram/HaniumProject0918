package com.siyeon.haniumproject

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CctvActivity: AppCompatActivity() {

    private lateinit var webView: WebView
    private var isInitialUrl = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cctv_layout)

        webView = findViewById<WebView>(R.id.webView)
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
        }

        loadInitialUrl()

        title = "CCTV 확인"

        var buttonReturn = findViewById<ImageButton>(R.id.button_return)
        buttonReturn.setOnClickListener {
            finish()
        }

        val buttonTurn = findViewById<Button>(R.id.bt_turn)
        buttonTurn.setOnClickListener {
            // Toggle between InitialUrl and newUrl
            if (isInitialUrl) {
                loadNewUrl("https://www.youtube.com/watch?v=3sL2hceTCU8&list=LL&index=9")
            } else {
                loadInitialUrl()
            }
            // Toggle the flag
            isInitialUrl = !isInitialUrl
        }
    }

    private fun loadInitialUrl() {
        // Load the Initial URL
        webView.loadUrl("https://www.youtube.com/watch?v=6Dbq05hchX4")
    }

    private fun loadNewUrl(url: String) {
        // Load a new URL into the WebView
        webView.loadUrl(url)
    }
}
