package com.novoda.dungeoncrawlercompanion

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_game
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_game -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, GameFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_about -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, AboutFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
