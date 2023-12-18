package com.example.acledaproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.acledaproject.base.BaseActivity
import com.example.acledaproject.databinding.ActivityMainBinding
import com.example.acledaproject.ui.DetailItemActivity
import com.example.acledaproject.ui.scan.QRCodeActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object {
        fun gotoMainActivity(mContext : Context) {
            mContext.startActivity(Intent(mContext, MainActivity::class.java))
        }

        private var lastPressedTime: Long = 0
        private const val PERIOD = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        // navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    DetailItemActivity.gotoDetailItemActivity(this@MainActivity, menuItem.title.toString())
                }

                R.id.nav_gallery -> {
                    DetailItemActivity.gotoDetailItemActivity(this@MainActivity, menuItem.title.toString())
                }
                R.id.nav_slideshow -> {
                    DetailItemActivity.gotoDetailItemActivity(this@MainActivity, menuItem.title.toString())
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Action
        binding.appBarMain.qrCode.setOnClickListener { view ->
            // Delay Click
            view.isEnabled = false
            view.postDelayed({ view.isEnabled = true }, 500)

            QRCodeActivity.start(this)
        }
    }


    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // DetailItemActivity.gotoDetailItemActivity(this@MainActivity, item.title.toString())
                true
            }
            R.id.action_done -> {
                // DetailItemActivity.gotoDetailItemActivity(this@MainActivity, item.title.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (event.downTime - lastPressedTime < PERIOD) {
                    finishAffinity()
                } else {
                    Toast.makeText(this, resources.getString(R.string.please_press_back_again_to_exit), Toast.LENGTH_SHORT).show()
                    lastPressedTime = event.eventTime
                }
                return true
            }
        }
        return false
    }
}