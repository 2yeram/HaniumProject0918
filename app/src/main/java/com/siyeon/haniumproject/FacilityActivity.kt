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

class FacilityActivity: AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var dataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.facility_layout)

        FirebaseApp.initializeApp(this)

        dataTextView = findViewById(R.id.dataTextView) // 레이아웃에서 표시할 TextView

        // Firebase Realtime Database의 데이터를 가져올 레퍼런스 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("test/one") // 노드 이름을 변경해야 합니다.


        // ValueEventListener를 사용하여 데이터 가져오기
        databaseReference.addValueEventListener(object : ValueEventListener {
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

        title = "시설물 현황"

        var buttonReturn = findViewById<ImageButton>(R.id.button_return)
        buttonReturn.setOnClickListener {
            finish()
        }
    }
}
