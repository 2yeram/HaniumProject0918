package com.siyeon.haniumproject

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        val currentDateTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "webview_capture_$currentDateTime.png"

        // 외부 저장소 경로
        val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        val filePath = "$folderPath/$filename"

        try {
            val outputStream: OutputStream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // 미디어 스토어에 이미지를 추가합니다.
            val contentResolver = applicationContext.contentResolver
            MediaScannerConnection.scanFile(
                applicationContext,
                arrayOf(filePath),
                null
            ) { _, _ ->
                // 스캔이 완료되었을 때 수행할 작업 (여기서는 아무 작업도 하지 않음)
            }

            // 미디어 스토어에 이미지를 추가한 후 갤러리 앱에서 볼 수 있게 됩니다.
            Toast.makeText(this, "캡쳐가 저장되었습니다: $filePath", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "캡쳐 저장 실패", Toast.LENGTH_SHORT).show()
        }
    }
}