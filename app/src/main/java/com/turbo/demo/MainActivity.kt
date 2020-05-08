package com.turbo.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.turbo.fsp.FSP
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val SP_NAME = "mainactivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        write_int.setOnClickListener {
            FSP.get(SP_NAME)?.edit()?.putInt("INT_KEY", 5)?.apply()
            Toast.makeText(this, "write int successful", Toast.LENGTH_SHORT).show()
        }

        read_int.setOnClickListener {
            val int = FSP.get(SP_NAME)?.getInt("INT_KEY", 2)
            Toast.makeText(this, "read int from sp->" + int, Toast.LENGTH_SHORT).show()
        }
    }
}
