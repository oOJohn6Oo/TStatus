package com.github.john.tstatus

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.annotation.Size
import androidx.annotation.StringRes
import androidx.core.view.get
import androidx.fragment.app.Fragment

class TStatus private constructor(private val builder: Builder) {
    private var statusViews: Array<View?>
    var viewStatus:ViewType = ViewType.CONTENT

    init {
        statusViews = Array(builder.builderLayouts.size) { null }
    }

    companion object {
        private var hasInitialized: Boolean = false
        var globalAlerts: Array<String>? = null
        var globalViews: IntArray? = null
        var globalLayouts: IntArray? = null

        private fun init(alerts: Array<String>) {
            check(!hasInitialized) { "T_status only needs to be init once." }
            hasInitialized = true
            this.globalAlerts = alerts
        }

        /**
         * 只设置
         * @alerts 提示语
         */
        fun init(context: Context, @Size(4) alerts: IntArray) {
            init(Array(alerts.size) { pos ->
                context.getString(alerts[pos])
            })
        }

        /**
         * @globalAlerts 各样式的提示语
         * @resourceInt 分别为 empty图片、loading*颜色*、网络出错图片、服务器出错图片 的资源ID
         */
        fun init(context: Context, @Size(4) alerts: IntArray, @Size(4) resourceInt: IntArray) {
            init(context, alerts)
            this.globalViews = resourceInt
        }

        /**
         * 支持通过资源ID设置提示语
         */
        fun init(context: Context, @ArrayRes alerts: Int, @Size(4) resourceInt: IntArray) {
            init(context.resources.getStringArray(alerts))
            this.globalViews = resourceInt
        }

        /**
         * 直接设置view的样式
         * layoutViewIds 分别对应各状态资源ID
         */
        fun init(@Size(4) layoutViewIds: IntArray) {
            check(!hasInitialized) { "T_status only needs to be init once." }
            hasInitialized = true
            globalLayouts = layoutViewIds
        }
    }

    class Builder(val context: Context, val targetView: View) {


        constructor(fragment: Fragment) : this(fragment.context!!, fragment.requireView())

        constructor(activity: Activity) : this(
            activity,
            (activity.findViewById(android.R.id.content) as ViewGroup).getChildAt(0)
        )

        var netErrorClickListener: View.OnClickListener? = null
        var serverErrorClickListener: View.OnClickListener? = null
        val parentView: ViewGroup = targetView.parent as ViewGroup
        val layoutIndex: Int = parentView.indexOfChild(targetView)
        val builderAlerts: Array<String> = if (globalAlerts == null) arrayOf(
            context.getString(R.string.loading_alert)
            , context.getString(R.string.empty_alert)
            , context.getString(R.string.network_alert)
            , context.getString(R.string.server_alert)
        ) else globalAlerts!!
        val builderViews: IntArray = if (globalViews == null) intArrayOf(
            Color.RED
            , R.drawable.ic_status_empty_view
            , R.drawable.ic_status_network_error
            , R.drawable.ic_status_server_error
        ) else globalViews!!
        val builderLayouts: IntArray = if (globalLayouts == null) intArrayOf(
            R.layout.view_status_loading,
            R.layout.view_status_empty
            , R.layout.view_status_net_error, R.layout.view_status_server_error
        ) else globalLayouts!!

        fun setNetErrorClickListener(clickListener: View.OnClickListener): Builder {
            this.netErrorClickListener = clickListener
            return this
        }

        fun setServerErrorClickListener(clickListener: View.OnClickListener): Builder {
            this.serverErrorClickListener = clickListener
            return this
        }


        fun setAlertMsg(viewType: ViewType, msg: String): Builder {
            check(globalLayouts == null) { "Custom layout do not permitted to invoke this func for the security" }
            builderAlerts[viewType.ordinal] = msg
            return this
        }

        fun setAlertMsg(viewType: ViewType, msg: Int): Builder {
            return setAlertMsg(viewType, context.getString(msg))
        }

        fun setAlertView(viewType: ViewType, firstIsColorNextDrawable: Int): Builder {
            check(globalLayouts == null) { "Custom layout do not permitted to invoke this func for the security" }
            builderViews[viewType.ordinal] = firstIsColorNextDrawable
            return this
        }

        fun setLayout(viewType: ViewType, @LayoutRes layoutResId: Int): Builder {
            builderLayouts[viewType.ordinal] = layoutResId
            return this
        }

        fun build(): TStatus = TStatus(this)
    }

    fun showLoading() = stateSwitch(ViewType.LOADING)
    fun showEmpty() = stateSwitch(ViewType.EMPTY)
    fun showServerError() = stateSwitch(ViewType.SERVER_ERROR)
    fun showNetError() = stateSwitch(ViewType.NET_ERROR)
    fun showContent() = stateSwitch(ViewType.CONTENT)


    private fun stateSwitch(viewType: ViewType) {
        duplicateCheck(viewType)
        nullCheck(viewType)
        builder.parentView.removeViewAt(builder.layoutIndex)
        if (viewType == ViewType.CONTENT)
            builder.parentView.addView(builder.targetView, builder.layoutIndex)
        else
            builder.parentView.addView(
                statusViews[viewType.ordinal],
                builder.layoutIndex,
                builder.targetView.layoutParams
            )
    }

    private fun duplicateCheck(viewType: ViewType) {
        if(viewStatus == viewType)
            return
        else
            viewStatus = viewType
    }

    private fun nullCheck(viewType: ViewType) {
        val index = viewType.ordinal
        if (viewType != ViewType.CONTENT && statusViews[index] == null) {
            statusViews[index] = LayoutInflater.from(builder.context).inflate(
                builder.builderLayouts[index],
                builder.parentView,
                false
            )

            // 用户没有自定义
            if (globalLayouts == null) {
                val linearLayout = (statusViews[index] as ViewGroup)[0] as ViewGroup
                if (viewType == ViewType.NET_ERROR)
                    linearLayout.setOnClickListener(builder.netErrorClickListener)
                else if (viewType == ViewType.SERVER_ERROR)
                    linearLayout.setOnClickListener(builder.serverErrorClickListener)
                // 设置loading颜色
                if (index == 0)
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                        (linearLayout[0] as ProgressBar).indeterminateDrawable.setTint(
                            builder.builderViews[index]
                        )
                    else
                        (linearLayout[0] as ProgressBar).indeterminateDrawable.setColorFilter(
                            builder.builderViews[index],
                            PorterDuff.Mode.SRC
                        )
                // 设置图片
                else
                    (linearLayout[0] as ImageView).setImageResource(builder.builderViews[index])
                // 设置文字
                (linearLayout[1] as TextView).text = builder.builderAlerts[index]
            }
        }
    }
}