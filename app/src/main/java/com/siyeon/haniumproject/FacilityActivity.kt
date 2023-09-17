package com.siyeon.haniumproject

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import android.widget.ImageView
import android.view.View

class FacilityActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var dataTextView: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.facility_layout)

        FirebaseApp.initializeApp(this)

        dataTextView = findViewById(R.id.dataTextView) // 레이아웃에서 표시할 TextView
        imageView = findViewById(R.id.test) // 이미지뷰

        // Firebase Realtime Database의 데이터를 가져올 레퍼런스 설정 (test/push 노드)
        databaseReference = FirebaseDatabase.getInstance().getReference("test/push")

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

        // Firebase Realtime Database의 데이터를 가져올 레퍼런스 설정 (test/one 노드)
        val oneReference = FirebaseDatabase.getInstance().getReference("test/one")

        // ValueEventListener를 사용하여 데이터 가져오기 (test/one 노드)
        oneReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터 변경 시 호출됩니다.
                val value = dataSnapshot.getValue()?.toString() // toString()을 사용하여 Long 값을 String으로 변환
                if (value != null) {
                    dataTextView.text = value
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error: ${databaseError.message}")
            }
        })

        // 이하 코드는 onCreate 메서드 내에서 초기화됩니다.
        title = "시설물 현황"

        var buttonReturn = findViewById<ImageButton>(R.id.button_return)
        buttonReturn.setOnClickListener {
            finish()
        }
    }
}
