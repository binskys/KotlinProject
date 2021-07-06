package com.ruiec.common_library.widget.adapter

/**
 *
 * @author pfm
 * @date 2021/2/3 14:15
 */
interface ItemViewDelegate<T> {
    fun getItemViewLayoutId(viewType: Int): Int
    fun isForViewType(item: T, position: Int): Boolean
    fun convert(holder: RViewHolder, t: T, position: Int)
}