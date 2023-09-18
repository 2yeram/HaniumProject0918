package com.siyeon.haniumproject

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import android.Manifest
import android.widget.ImageButton
import android.widget.Toast


class GalleryActivity : AppCompatActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_layout)

        var buttonReturn = findViewById<ImageButton>(R.id.button_return) // 뒤로가기
        buttonReturn.setOnClickListener {
            finish()
        }

        // 권한 부여 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(this)
                    .setMessage("사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다")
                    .setTitle("권한이 필요한 이유")
                    .setPositiveButton("권한 요청") { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton("취소") { _, _ ->
                        // 권한 요청을 거부한 경우 처리
                    }
                    .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
            }
        }
        else {
            // 권한이 이미 허용됨

            getAllPhotos()

        }
    }

    // 사용자가 권한 요청 시 호출되는 메서드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getAllPhotos()
                } else {
                    Toast.makeText(this, "권한 거부 됨", Toast.LENGTH_SHORT).show()
                }

                return
            }
        }

    }

    private fun getAllPhotos(){
        val cursor = this.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        val uriArr = ArrayList<String>()
        if(cursor!=null){
            while(cursor.moveToNext()){
                // 사진 경로 Uri 가져오기
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                uriArr.add(uri)
            }
            cursor.close()
        }
        val adapter = MyAdapter(this,uriArr)
        val gridView = findViewById<GridView>(R.id.your_gallery_grid)

        gridView.numColumns=3 // 한 줄에 3개씩
        gridView.adapter = adapter

    }
}

