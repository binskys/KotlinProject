package com.ruiec.common_library.widget.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 可设置空布局的RecyclerView
 * @author pfm
 * @date 2021/2/3 10:29
 */
open class EmptyRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var mEmptyView : View

    inner class EmptyObserver : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            if (this@EmptyRecyclerView::mEmptyView.isInitialized){
                if(adapter?.itemCount == 0){
                    mEmptyView.visibility = View.VISIBLE
                    this@EmptyRecyclerView.visibility = View.GONE
                } else if(adapter?.itemCount!! > 0){
                    mEmptyView.visibility = View.GONE
                    this@EmptyRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    open fun setEmptyView(view: View){
        this.mEmptyView = view
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(EmptyObserver())
        EmptyObserver().onChanged()
    }
}