package com.dario.carrizo.app.videomeeting.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Dario Carrizo on 15/7/2020
 **/
abstract class BaseViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, position: Int)
}