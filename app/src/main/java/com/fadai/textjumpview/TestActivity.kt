package com.fadai.textjumpview

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import com.gz.goodneighbor.widget.loading.TextBean
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        tcl.setTextList(
            arrayListOf(
                TextBean("中", 0xFF009925.toInt()),
                TextBean("国", 0xFFEEB211.toInt()),
                TextBean("好", 0xFF3369E8.toInt()),
                TextBean("邻", 0xFFD50F25.toInt()),
                TextBean("居", 0xFF9932CD.toInt())
            )
        )
        tcl.setTextSize(SizeUtils.dp2px(this, 21F).toFloat())
        tcl.post { tcl.start() }

    }

}
