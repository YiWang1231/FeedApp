package com.peter.feedapp.adapter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peter.feedapp.R

class LoadingAdapter(val context: Context) : RecyclerView.Adapter<LoadingAdapter.LoadingViewHolder>(){
    private val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, PaginationAdapter(context).getPixelFromDp(80F))
    private var loadingStyle: ProgressBar? = null
    private var loadFailedStyle: TextView? = null
    private var loadFinishedStyle: TextView? = null
    private var isShowLoading = false
    var styleView: View? = null
    private set

    private var statusType = LoadingStatusEnum.STATUS_LOADING.statusCode
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingViewHolder{
        setLoadingStatus(statusType, null)
        when(isShowLoading) {
            true -> styleView?.visibility = View.VISIBLE
            false -> styleView?.visibility = View.GONE
        }
        println("again")
        return LoadingViewHolder(styleView!!)
    }



    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, position: Int) {
        //
    }



    fun setLoadingStatus(loadingStatus: Int, view: View?) {
        println("setState")
        statusType = loadingStatus
        if (view != null) {
            styleView = view
        } else {
            layoutParams.gravity = Gravity.CENTER
            styleView = when(statusType) {
                LoadingStatusEnum.STATUS_FINISHED.statusCode -> {
                    initLoadFinished()
                    loadFinishedStyle
                }
                LoadingStatusEnum.STATUS_FAILED.statusCode -> {
                    initLoadFailed()
                    loadFailedStyle
                }
                else -> {
                    initLoadingBar()
                    loadingStyle
                }
            }
        }
    }

    fun setLoadingShowState(isShow: Boolean) {
        isShowLoading = isShow
    }

    private fun initLoadingBar() {
        if (loadingStyle == null) {
            loadingStyle = ProgressBar(context)
            loadingStyle!!.layoutParams =layoutParams
        } else {
            return
        }
    }

    private fun initLoadFailed() {
        if (loadFailedStyle == null) {
            loadFailedStyle = TextView(context)
            loadFailedStyle!!.layoutParams = layoutParams
            loadFailedStyle!!.text = context.getString(R.string.loading_failed)
        } else {
            return
        }
    }

    private fun initLoadFinished() {
        if (loadFinishedStyle == null) {
            loadFinishedStyle = TextView(context)
            loadFinishedStyle!!.layoutParams = layoutParams
            loadFinishedStyle!!.text = context.getString(R.string.loading_finished)
        } else {
            return
        }
    }

    class LoadingViewHolder(view: View): RecyclerView.ViewHolder(view)
}