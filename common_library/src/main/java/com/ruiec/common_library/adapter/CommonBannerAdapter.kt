package com.ruiec.common_library.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ruiec.common_library.R
import com.youth.banner.adapter.BannerAdapter

/**
 *
 * @author pengfaming
 * @date 2021/2/24 14:50
 */
abstract class CommonBannerAdapter<T>(private var contxt: Context, list: List<T>) :
    BannerAdapter<T, CommonBannerAdapter<T>.BannerViewHolder>(list) {

    inner class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgBanner)
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(contxt).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder?, data: T, position: Int, size: Int) {
        bindView(holder!!.imageView, data, position)
    }

    abstract fun bindView(view: ImageView, data: T, position: Int)
}