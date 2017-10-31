package com.github.kotlin.android

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import com.github.kotlin.R
import com.github.kotlin.R.layout.activity_main
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    val a = 0;
    val b = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        initView()

    }

    private fun initView() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        btn_login.text = "计算成功"
        btn_login.setOnClickListener {
            this@MainActivity
        }
        btn_add.setOnClickListener {
            this@MainActivity
            onClick(btn_add)
        }
        btn_sub.setOnClickListener {
            this@MainActivity
            onClick(btn_sub)
        }
        btn_pro.setOnClickListener {
            this@MainActivity
            onClick(btn_pro)
        }
        btn_div.setOnClickListener {
            this@MainActivity
            onClick(btn_div)
        }
    }

    fun onClick(view: View): Unit {
        val id = view.id;
        var a=num_a.text
        var b=(num_b.text)
        var i=Integer.parseInt(a.toString())
        var j=Integer.parseInt(b.toString())
        when (id) {

            R.id.btn_add->tv_num.text=getAdd(i,j).toString()
            R.id.btn_sub ->tv_num.text=getSub(i,j).toString()
            R.id.btn_pro ->tv_num.text=getPro(i,j).toString()
            R.id.btn_div ->tv_num.text=getDiv(i,j).toString()
        }
    }

    fun jump() {
        val intent = Intent()
        //获取intent对象
        intent.setClass(this, LoginActivity::class.java)
        // 获取class是使用::反射
        startActivity(intent)
    }

    private fun getName(name: String): String = name
    fun getAdd(a: Int, b: Int): Int = a + b
    fun getSub(a: Int, b: Int): Int = a - b
    fun getPro(a: Int, b: Int): Int = a * b
    fun getDiv(a: Int, b: Int): Int = a / b
}
