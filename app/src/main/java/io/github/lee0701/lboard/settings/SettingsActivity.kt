package io.github.lee0701.lboard.settings

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import io.github.lee0701.lboard.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp)
        }

        navigation.setNavigationItemSelectedListener(this)

        settings.openDrawer(GravityCompat.START)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_activation -> {
                loadFragment(ActivationFragment())
            }
        }
        settings.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                settings.openDrawer(GravityCompat.START)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit()
        collapsing_toolbar.title = resources.getString(fragment.title)
    }

    abstract class Fragment: PreferenceFragmentCompat() {
        abstract val title: Int
    }

    class ActivationFragment: Fragment() {
        override val title = R.string.pref_activation_title
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.lboard_pref_activation)
        }
    }

}
