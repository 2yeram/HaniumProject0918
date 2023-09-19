package com.siyeon.haniumproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView // imageView를 클래스 멤버로 선언

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


        val button = findViewById<Button>(R.id.bt_place_name)
        button.setOnClickListener {
            val bottomSheet = BottomSheetDialog()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        var buttonAlarmActivity = findViewById<Button>(R.id.bt_home_alarm)
        buttonAlarmActivity.setOnClickListener {
            var intent = Intent(applicationContext, AlarmActivity::class.java)
            startActivity(intent)
        }

        var buttonCCTVActivity = findViewById<Button>(R.id.bt_home_cctv)
        buttonCCTVActivity.setOnClickListener {
            var intent = Intent(applicationContext, CctvActivity::class.java)
            startActivity(intent)
        }

        var buttonGalleryActivity = findViewById<Button>(R.id.bt_home_gallery)
        buttonGalleryActivity.setOnClickListener {
            var intent = Intent(applicationContext, GalleryActivity::class.java)
            startActivity(intent)
        }

        var buttonFacilityActivity = findViewById<Button>(R.id.bt_home_facility)
        buttonFacilityActivity.setOnClickListener {
            var intent = Intent(applicationContext, FacilityActivity::class.java)
            startActivity(intent)
        }
    }

}

