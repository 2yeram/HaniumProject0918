package com.siyeon.haniumproject

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class notificationActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        imageView = findViewById(R.id.test) // 이미지뷰

        // Firebase Realtime Database의 데이터를 가져올 레퍼런스 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("test/push")

        // ValueEventListener를 사용하여 데이터 가져오기
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pushValue = dataSnapshot.getValue(Long::class.java)

                if (pushValue == 1L) {
                    // pushValue가 1로 변경되었을 때 이미지를 표시
                    imageView.setImageResource(R.drawable.ring_resize)
                } else {
                    // pushValue가 1이 아닌 경우 이미지를 숨김
                    imageView.setImageDrawable(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val errorMessage = databaseError.message
                Log.e("FirebaseDatabase", "Firebase Database 오류: $errorMessage")
            }
        })
    }
}
