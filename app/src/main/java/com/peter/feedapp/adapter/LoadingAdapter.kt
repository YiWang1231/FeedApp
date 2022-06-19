package com.peter.feedapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.peter.feedapp.R

class LoadingAdapter(val context: Context,private val failedCallback: FailedCallback): RecyclerView.Adapter<LoadingAdapter.BaseViewHolder>(){
    @LayoutRes
    var loadingStyleResId = R.layout.loading_more
    @LayoutRes
    var loadingFailedResId = R.layout.load_more_failed
    @LayoutRes
    var loadingFinishedResId = R.layout.loading_no_more

    private var loadingState = LoadingStatusEnum.EMPTY_VIEW

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder{
        return when(loadingState) {
            LoadingStatusEnum.STATUS_FAILED -> createLoadFailedHolder(parent)
            LoadingStatusEnum.STATUS_FINISHED -> createLoadingFinished(parent)
            LoadingStatusEnum.EMPTY_VIEW -> createEmptyHolder()
            else -> createLoadMoreHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (loadingState == LoadingStatusEnum.STATUS_FAILED) {
            holder.itemView.setOnClickListener {
                failedCallback.onClick()
            }
        }
    }

    fun setState(state: LoadingStatusEnum) {
        this.loadingState = state
        notifyItemChanged(0)
    }

    private fun createLoadMoreHolder(parent: ViewGroup): LoadingMoreViewHolder {
        val view = LayoutInflater.from(context).inflate(loadingStyleResId, parent, false)
        return LoadingMoreViewHolder(view)
    }

    private fun createLoadFailedHolder(parent: ViewGroup): LoadingFailedViewHolder {
        val view = LayoutInflater.from(context).inflate(loadingFailedResId, parent, false)
        return LoadingFailedViewHolder(view)
    }

    private fun createLoadingFinished(parent: ViewGroup): LoadingFinishedViewHolder {
        val view = LayoutInflater.from(context).inflate(loadingFinishedResId, parent, false)
        return LoadingFinishedViewHolder(view)
    }

    private fun createEmptyHolder():EmptyViewHolder {
        val view = LinearLayout(context)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        view.layoutParams = layoutParams
        return EmptyViewHolder(view)
    }

    open class BaseViewHolder(view: View): RecyclerView.ViewHolder(view)

    class LoadingMoreViewHolder(view: View): BaseViewHolder(view)

    class LoadingFailedViewHolder(view: View): BaseViewHolder(view) {
        lateinit var textView: TextView
    }

    class LoadingFinishedViewHolder(view: View): BaseViewHolder(view)

    class EmptyViewHolder(view: View): BaseViewHolder(view)

    interface FailedCallback {
        fun onClick()
    }
}