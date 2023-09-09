package com.siyeon.haniumproject

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileOutputStream
import java.io.OutputStream

class CctvActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var isInitialUrl = true

    // 캡쳐 권한 요청 코드
    private val REQUEST_WRITE_EXTERNAL_STORAGE = 1

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

        var buttonReturn = findViewById<ImageButton>(R.id.button_return) // 뒤로가기
        buttonReturn.setOnClickListener {
            finish()
        }

        val buttonTurn = findViewById<Button>(R.id.bt_turn) // 영상 전환
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

        val buttonCapture = findViewById<Button>(R.id.bt_capture) // 화면 캡쳐
        buttonCapture.setOnClickListener {
            requestWriteExternalStoragePermission()
        }
    }

    private fun requestWriteExternalStoragePermission() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val requestCode = REQUEST_WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 부여되지 않은 경우 권한 요청 대화 상자 표시
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            // 이미 권한이 부여된 경우 권한이 있는 작업 수행
            captureAndSaveWebView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우 권한이 있는 작업 수행
                captureAndSaveWebView()
            } else {
                // 권한이 거부된 경우 사용자에게 메시지 표시 또는 처리
                Toast.makeText(this, "저장소 쓰기 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
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

    private fun captureAndSaveWebView() {
        // Capture the WebView as a Bitmap
        val bitmap = Bitmap.createBitmap(webView.width, webView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        webView.draw(canvas)

        // Save the captured Bitmap to the device's external storage
        val filename = "webview_capture.png"
        val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        val filePath = "$folderPath/$filename"

        try {
            val outputStream: OutputStream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Add the image to the device's MediaStore (to make it appear in the gallery)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, filename)
                put(MediaStore.Images.Media.DESCRIPTION, "WebView Capture")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.DATA, filePath)
            }

            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            Toast.makeText(this, "캡쳐가 저장되었습니다: $filePath", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "캡쳐 저장 실패", Toast.LENGTH_SHORT).show()
        }
    }
}
