package com.siyeon.haniumproject

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide

class GalleryAdapter(val context: Context, uriArr:ArrayList<String>) : BaseAdapter(){

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
        val display = context.resources.displayMetrics // getResources()는 필요 없음
        imageView.setPadding(2, 2, 2, 2) // padding 설정
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.layoutParams = LinearLayout.LayoutParams(display.widthPixels/3,display.widthPixels/3)
        Glide.with(context).load(items[p]).into(imageView)
        return imageView
    }

}