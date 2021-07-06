package com.ruiec.zxing_library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_zxing.*

class ZXingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zxing)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
        }
    }

}
