package com.github.john.tstatusdemo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.john.tstatus.TStatus
import com.github.john.tstatus.ViewType
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val s = TStatus.Builder(this,scrollView).build()
        btn.setOnClickListener { btn.open() }
        btn.addActionItem(
            SpeedDialActionItem.Builder(R.id.status_loading, R.drawable.ic_loading)
                .setFabBackgroundColor(Color.parseColor("#346CF2"))
                .setLabel(R.string.loading_alert).create()
        )
        btn.addActionItem(
            SpeedDialActionItem.Builder(R.id.status_empty, R.drawable.ic_empty)
                .setFabBackgroundColor(Color.parseColor("#3FA440"))
                .setLabel(R.string.empty_alert).create()
        )
        btn.addActionItem(
            SpeedDialActionItem.Builder(R.id.status_network_error, R.drawable.ic_network_error)
                .setFabBackgroundColor(Color.parseColor("#8B03A0"))
                .setLabel(R.string.network_alert).create()
        )
        btn.addActionItem(
            SpeedDialActionItem.Builder(R.id.status_server_error, R.drawable.ic_server_error)
                .setFabBackgroundColor(Color.parseColor("#FC4042"))
                .setLabel(R.string.server_alert).create()
        )
        btn.addActionItem(
            SpeedDialActionItem.Builder(R.id.status_content, R.drawable.ic_launcher_foreground)
                .setFabBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setLabel(R.string.app_name).create()
        )
        btn.setOnActionSelectedListener { item ->
            when (item.id) {
                R.id.status_loading ->
                    s.showLoading()
                R.id.status_empty ->
                    s.showEmpty()
                R.id.status_network_error ->
                    s.showNetError()
                R.id.status_server_error ->
                    s.showServerError()
                else ->
                    s.showContent()
            }
            false
        }
    }
}
