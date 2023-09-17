package com.siyeon.haniumproject

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class GalleryActivity : AppCompatActivity() {

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_layout)

        gridView = findViewById(R.id.gridView)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 권한 허용 안됨
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 이전에 이미 권한이 거부되었을 때 설명
                AlertDialog.Builder(this)
                    .setMessage("사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다")
                    .setTitle("권한이 필요한 이유")
                    .setPositiveButton("권한 요청") { _, _ ->
                        // 권한 요청
                        ActivityCompat.requestPermissions(
                            this@GalleryActivity,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    .setNegativeButton("취소") { _, _ -> }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            // 권한이 이미 허용됨
            getAllPhotos()
        }
    }

    private fun getAllPhotos(){
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")
        val uriArr = ArrayList<String>()
        if(cursor!=null){
            while(cursor.moveToNext()){
                // 사진 경로 Uri 가져오기
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                uriArr.add(uri)
            }
            cursor.close()
        }
        val adapter = GalleryAdapter(this,uriArr)
        gridView.numColumns=3 // 한 줄에 3개씩
        gridView.adapter = adapter

    }

}