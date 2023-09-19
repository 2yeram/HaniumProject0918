package com.siyeon.haniumproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class CctvActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView // imageView를 클래스 멤버로 선언

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var surface: Surface
    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private var isRecording = false
    private var videoFilePath: String? = null
    private lateinit var webView: WebView
    private var isInitialUrl = true

    // 캡쳐 권한 요청 코드
    private val REQUEST_WRITE_EXTERNAL_STORAGE = 1
    // 녹화 권한 요청 코드
    private val REQUEST_CODE_CAPTURE_VIDEO = 100
    private val REQUEST_CODE_SCREEN_CAPTURE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cctv_layout)

        mediaRecorder = MediaRecorder()
        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        webView = findViewById<WebView>(R.id.webView)
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
        }

        loadInitialUrl()

        startRecordingButton = findViewById<Button>(R.id.bt_record)
        startRecordingButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
                startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE)
            }
        }
        startRecordingButton.isEnabled = false

        var buttonReturn = findViewById<ImageButton>(R.id.button_return) // 뒤로가기
        buttonReturn.setOnClickListener {
            finish()
        }

        // imageView를 XML 레이아웃에서 찾아서 초기화
        imageView = findViewById(R.id.imageView)

        // Firebase Realtime Database의 데이터를 가져올 레퍼런스 설정 (test/push 노드)
        var databaseReference = FirebaseDatabase.getInstance().getReference("test2/dan")

// ImageView 정의
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.elevation = 100f // 100은 예시입니다. 적절한 값을 선택하세요.

// ValueEventListener를 사용하여 데이터 가져오기 (test/push 노드)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pushValue = dataSnapshot.getValue(Long::class.java)

                if (pushValue == 1L) {
                    // pushValue가 1로 변경되었을 때 ImageView를 표시
                    imageView.visibility = View.VISIBLE
                } else {
                    // pushValue가 1이 아닌 경우 ImageView를 숨김
                    imageView.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val errorMessage = databaseError.message
                Log.e("FirebaseDatabase", "Firebase Database 오류: $errorMessage")
            }
        })

        val buttonTurn = findViewById<Button>(R.id.bt_turn) // 영상 전환
        buttonTurn.setOnClickListener {
            // Toggle between InitialUrl and newUrl
            if (isInitialUrl) {
                loadNewUrl("https://youtu.be/QW7EClanDmA?si=U8gYtOL28-U-oB1B") //두 번째 영상 링크
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

        startRecordingButton = findViewById<Button>(R.id.bt_record)

        startRecordingButton.setOnClickListener { startRecording() }

        if (checkPermissions()) {
            initializeMediaRecorder()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                audioPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS)
    }

    private fun requestPermissionsAndStartRecording() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        val permissionResults = permissions.map {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (permissionResults.all { it }) {
            startRecording()
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    private fun initializeMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setVideoSize(720, 1280)

        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDateAndTime: String = dateFormat.format(Date())
        val videoFileName = "CCTV_$currentDateAndTime.mp4"
        videoFilePath = File(outputDir, videoFileName).absolutePath

        mediaRecorder.setOutputFile(videoFilePath)

        mediaRecorder.prepare()
    }

    private fun startRecording() {
        try {

            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val currentDateAndTime: String = dateFormat.format(Date())
            videoFilePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                    .absolutePath + "/$currentDateAndTime.mp4"

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mediaRecorder.setOutputFile(videoFilePath)
            mediaRecorder.setVideoSize(720, 1280) // 적절한 해상도 설정

            surface = mediaRecorder.surface

            mediaRecorder.prepare()
            mediaRecorder.start()

            isRecording = true
            startRecordingButton.text = "녹화 중지"
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            try {
                mediaRecorder.stop()
                mediaRecorder.reset()
                mediaRecorder.release()
                mediaProjection?.stop()
                mediaProjection = null
                isRecording = false
                startRecordingButton.text = "녹화 시작"

                // 녹화된 동영상 파일의 경로(videoFilePath)를 사용하여 추가 작업을 수행할 수 있습니다.
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadInitialUrl() {
        // Load the Initial URL
        webView.loadUrl("https://www.youtube.com/watch?v=6Dbq05hchX4") //첫 번째 영상 링크
    }

    private fun loadNewUrl(url: String) {
        // Load a new URL into the WebView
        webView.loadUrl(url)
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


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
    }
}