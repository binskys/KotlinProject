package com.ruiec.common_library.widget.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @author pfm
 * @date 2021/2/3 14:12
 */
open class RvEmptyAdapter<T>(private var mContext: Context, var datas: List<T>) : RecyclerView.Adapter<RViewHolder>() {
    private var itemViewDelegate: ItemViewDelegate<T>? = null
    protected var mOnItemClickListener: OnItemClickListener? = null

    // 头部与底部视图
    private var mHeaderView: View? = null
    private var mFooterView: View? = null

    override fun getItemViewType(position: Int): Int {
        //根据传入adapter来判断是否有数据
        val type : Int
        if(datas.isEmpty()){
            type =
                TYPE_EMPTY
        } else{
            type = when(position){
                0 ->{
                    if(null != mHeaderView){
                        TYPE_HEADER
                    } else{
                        TYPE_NORMAL
                    }
                }
                itemCount - 1 ->{
                    //最后一个ITEM
                    if(null != mFooterView){
                        TYPE_FOOTER
                    } else{
                        TYPE_NORMAL
                    }
                }
                else ->{
                    TYPE_NORMAL
                }
            }
        }
        return type
    }

    /** HeaderView和FooterView的get和set函数  */
    open fun getHeaderView(): View? {
        return mHeaderView
    }

    open fun setHeaderView(headerView: View) {
        mHeaderView = headerView
        notifyItemInserted(0)
    }

    open fun getFooterView(): View? {
        return mFooterView
    }

    open fun setFooterView(footerView: View?) {
        if (null != mFooterView) {
            //移除之前的底部视图
            mFooterView = footerView
            notifyItemChanged(itemCount - 1)
            notifyDataSetChanged()
        } else {
            mFooterView = footerView
            if(null != footerView) {
                notifyItemInserted(itemCount - 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return RViewHolder.createViewHolder(
                mContext,
                mHeaderView!!
            )
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return RViewHolder.createViewHolder(
                mContext,
                mFooterView!!
            )
        }

        val layoutId = itemViewDelegate!!.getItemViewLayoutId(viewType)
        val holder =
            RViewHolder.createViewHolder(
                mContext,
                parent,
                layoutId
            )
        onViewHolderCreated(holder, holder.convertView, viewType)
        setListener(parent, holder, viewType)
        return holder
    }

    private fun onViewHolderCreated(
        holder: RViewHolder?,
        itemView: View?,
        viewType: Int
    ) {
    }

    private fun convert(holder: RViewHolder, t: T) {
        itemViewDelegate!!.convert(holder, t, holder.adapterPosition)
    }

    private fun convertEmptyView(holder: RViewHolder?) {}
    private fun isEnabled(viewType: Int): Boolean {
        return viewType != TYPE_EMPTY
    }

    private fun setListener(
        parent: ViewGroup?,
        viewHolder: RViewHolder,
        viewType: Int
    ) {
        if (!isEnabled(viewType)) return
        viewHolder.convertView.setOnClickListener(object : NoDoubleClickListener() {
            override fun onClick(v: View?) {
                super.onClick(v)
                val position = viewHolder.adapterPosition
                mOnItemClickListener?.onItemClick(v, viewHolder, position)
            }
        })
        viewHolder.convertView.setOnLongClickListener(View.OnLongClickListener { v ->

            mOnItemClickListener?.let {
                val position = viewHolder.adapterPosition
                return@OnLongClickListener it.onItemLongClick(v, viewHolder, position)
            }
            false
        })
    }

    override fun onBindViewHolder(holder: RViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            convert(holder, datas[position])
        } else if (getItemViewType(position) == TYPE_HEADER) {
            //convert(holder, datas[position-1])
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            //convert(holder, datas[position-1])
        } else {
            //空布局
            convertEmptyView(holder)
        }
    }

    override fun getItemCount(): Int {
        return if(datas.isEmpty()){
            if(isEmptyViewShowed) 1 else 0
        } else{
            if (mHeaderView == null && mFooterView == null) {
                datas.size
            } else if (mHeaderView == null) {
                datas.size + 1
            } else if (mFooterView == null) {
                datas.size + 1
            } else {
                datas.size + 2
            }
        }
    }

    fun setItemViewDelegate(delegate: ItemViewDelegate<T>?): RvEmptyAdapter<*> {
        itemViewDelegate = delegate
        return this
    }

    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            holder: RecyclerView.ViewHolder?,
            position: Int
        )

        fun onItemLongClick(
            view: View?,
            holder: RecyclerView.ViewHolder?,
            position: Int
        ): Boolean  {
            //长按时间
            return false
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    open val isEmptyViewShowed: Boolean get() = true

    companion object {
        //空布局
        const val TYPE_EMPTY = 0
        // 不带有header和footer的
        const val TYPE_NORMAL = 1
        // 带有Header的
        const val TYPE_HEADER = 2
        // 带有Footer的
        const val TYPE_FOOTER = 3
    }
}