package com.ruiec.common_library.widget.adapter

import android.content.Context
import com.ruiec.common_library.R

/**
 *
 * @author pfm
 * @date 2021/2/3 14:44
 */
abstract class RVAdapter<T>(mContext: Context, private var mLayoutId: Int,
                            mData: List<T>) : RvEmptyAdapter<T>(mContext, mData) {
    private var emptyId = R.layout.item_empty_layout

    protected abstract fun convert(holder: RViewHolder, itemBean: T, position: Int)

    protected fun getViewLayoutId(viewType: Int): Int {
        return if (viewType == TYPE_EMPTY) { emptyId } else mLayoutId
    }

    fun setEmptyId(emptyId: Int) {
        this.emptyId = emptyId
    }

    init {
        //getItemViewLayoutId重写，则此处layoutId可直接写0
        setItemViewDelegate(object :
            ItemViewDelegate<T> {
            override fun getItemViewLayoutId(viewType: Int): Int {
                return getViewLayoutId(viewType)
            }

            override fun isForViewType(item: T, position: Int): Boolean {
                return true
            }

            override fun convert(holder: RViewHolder, t: T, position: Int) {
                if (isForViewType(t, position)) {
                    this@RVAdapter.convert(holder, t, position)
                }
            }
        })
    }
}