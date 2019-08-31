package io.github.lee0701.lboard.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceFragmentCompat
import io.github.lee0701.lboard.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity: AppCompatActivity(), AdapterView.OnItemClickListener {

    val fragments = listOf<Fragment>(
            ActivationFragment(),
            CommonFragment(),
            InputMethodFragmentEn(),
            InputMethodFragmentKo(),
            AboutFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setTitle(R.string.settings_name)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp)
        }

        val navigationList = findViewById<ListView>(R.id.navigation_list)
        val adapter = NavigationListAdapter(this, fragments)
        navigationList.adapter = adapter
        navigationList.onItemClickListener = this

        settings.openDrawer(GravityCompat.START)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when(position) {
            in 0 until fragments.size -> loadFragment(fragments[position])
        }
        settings.closeDrawer(GravityCompat.START)
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
        abstract val icon: Int
    }

    class ActivationFragment: Fragment() {
        override val title = R.string.pref_activation_title
        override val icon = R.drawable.ic_playlist_add_check_black_24dp
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.lboard_pref_activation)
        }
    }

    class CommonFragment: Fragment() {
        override val icon = R.drawable.ic_check_box_black_24dp
        override val title = R.string.pref_common_title
        override fun onCreatePreferences(p0: Bundle?, p1: String?) {
            addPreferencesFromResource(R.xml.lboard_pref_common)
        }
    }

    abstract class InputMethodFragment: Fragment() {
        override val icon = R.drawable.ic_keyboard_black_24dp
    }

    class InputMethodFragmentEn: InputMethodFragment() {
        override val title = R.string.pref_method_en_title
        override fun onCreatePreferences(p0: Bundle?, p1: String?) {
            addPreferencesFromResource(R.xml.lboard_pref_method_en)
        }
    }

    class InputMethodFragmentKo: InputMethodFragment() {
        override val title = R.string.pref_method_ko_title
        override fun onCreatePreferences(p0: Bundle?, p1: String?) {
            addPreferencesFromResource(R.xml.lboard_pref_method_ko)
        }
    }

    class AboutFragment: Fragment() {
        override val title = R.string.pref_about_title
        override val icon = R.drawable.ic_info_outline_black_24dp
        override fun onCreatePreferences(p0: Bundle?, p1: String?) {
            addPreferencesFromResource(R.xml.lboard_pref_about)
            preferenceManager
                    .findPreference("about_copyright_info_license_url")
                    .setOnPreferenceClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("http://www.apache.org/licenses/LICENSE-2.0")
                        startActivity(intent)
                        true
                    }
        }
    }

    class NavigationListAdapter(private val context: Context, private val fragments: List<Fragment>): BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            if(convertView == null) {
                val view = View.inflate(context, R.layout.navigation_item, null)
                view.findViewById<TextView>(R.id.item_label).setText(fragments[position].title)
                view.findViewById<ImageView>(R.id.item_icon).setImageResource(fragments[position].icon)
                return view
            }
            else {
                return convertView
            }
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }

}
