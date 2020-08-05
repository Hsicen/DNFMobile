package com.hsicen.dnfmobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hsicen.dnfmobile.test.MyLocator

class MainActivity : AppCompatActivity() {

    private val myLocator by lazy {
        MyLocator { lat, lng ->
            println("当前位置: $lat , $lng")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //注册监听
        lifecycle.addObserver(myLocator)
    }
}