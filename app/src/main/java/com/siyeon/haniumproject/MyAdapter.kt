package com.siyeon.haniumproject

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide

class MyAdapter(val context: Context, uriArr:ArrayList<String>) : BaseAdapter(){
    private var items = ArrayList<String>()
    init {
        this.items = uriArr
    }
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(context)
        val display = context.resources.displayMetrics
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val layoutParams = LinearLayout.LayoutParams(display.widthPixels / 3, display.widthPixels / 3)
        imageView.layoutParams = layoutParams
        imageView.setPadding(2, 2, 2, 2)
        Glide.with(context).load(items[position]).into(imageView)

        // 이미지 클릭 이벤트 처리
        imageView.setOnClickListener {
            val intent = Intent(context, PhotoDetailActivity::class.java)
            intent.putExtra("imageUri", items[position])
            context.startActivity(intent)
        }

        return imageView
    }
}