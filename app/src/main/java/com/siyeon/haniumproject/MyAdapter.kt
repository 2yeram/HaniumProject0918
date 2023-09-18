package com.siyeon.haniumproject

import android.content.Context
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

    override fun getView(p: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(context)
        val display = context.resources.displayMetrics // getResources() 대신 resources 사용
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val layoutParams = LinearLayout.LayoutParams(display.widthPixels / 3, display.widthPixels / 3)
        imageView.layoutParams = layoutParams // 레이아웃 파라미터에 설정
        imageView.setPadding(2, 2, 2, 2) // 패딩 설정
        Glide.with(context).load(items[p]).into(imageView)
        return imageView

    }


}