package com.siyeon.haniumproject

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.IBinder
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenCaptureService : Service() {
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection

    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false
    private var videoFilePath: String? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val action = intent.action
        if (action == ACTION_START_CAPTURE) {
            // 화면 녹화 시작 요청을 받았을 때
            startCapture(intent)
        } else if (action == ACTION_STOP_CAPTURE) {
            // 화면 녹화 종료 요청을 받았을 때
            stopCapture()
        }

        return START_NOT_STICKY
    }

    private fun startCapture(intent: Intent) {
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1)
        val projectionData = intent.getParcelableExtra<Intent>(EXTRA_PROJECTION_DATA)

        if (resultCode != -1 && projectionData != null) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, projectionData)

            // MediaRecorder 설정
            val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val currentDateAndTime: String = dateFormat.format(Date())
            val videoFilePath = "${outputDir.absolutePath}/$currentDateAndTime.mp4"

            val mediaRecorder = MediaRecorder()
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mediaRecorder.setOutputFile(videoFilePath)
            mediaRecorder.setVideoSize(1280, 720) // 캡쳐 화면 해상도 설정

            try {
                mediaRecorder.prepare()
                mediaProjection.createVirtualDisplay(
                    "ScreenCapture",
                    1280,
                    720,
                    resources.displayMetrics.densityDpi,
                    0,
                    mediaRecorder.surface,
                    null,
                    null
                )
                mediaRecorder.start()
                isRecording = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopCapture() {
        if (isRecording) {
            try {
                mediaRecorder.stop()
                mediaRecorder.reset()
                mediaRecorder.release()
                mediaProjection.stop()
                isRecording = false // 녹화 중지 시 isRecording을 false로 설정
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val ACTION_START_CAPTURE = "start_capture"
        const val ACTION_STOP_CAPTURE = "stop_capture"
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_PROJECTION_DATA = "projection_data"
    }
}