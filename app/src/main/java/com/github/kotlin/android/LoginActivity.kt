package com.github.kotlin.android


import android.os.Bundle
import android.view.View
import android.widget.Button
import com.github.kotlin.R
import com.github.kotlin.R.layout.login_laoyut
import kotlinx.android.synthetic.main.login_laoyut.*

/**
 * Created by benny
 * on 2017/10/19.
 */

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(login_laoyut)
        val account = et_account.text;
        val pwd = et_pwd.text;
        val login=findViewById(R.id.btn_login)as Button
        login.setOnClickListener(View.OnClickListener { login.text = "qqqqweqweqwe" })

    }
}
