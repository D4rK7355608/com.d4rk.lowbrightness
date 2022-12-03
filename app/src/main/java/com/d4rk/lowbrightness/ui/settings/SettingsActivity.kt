package com.d4rk.lowbrightness.ui.settings
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.databinding.ActivitySettingsBinding
import com.google.android.material.textview.MaterialTextView
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val darkModeString = getString(R.string.theme)
        key?.let {
            if (it == darkModeString) sharedPreferences?.let { pref ->
                val darkModeValues = resources.getStringArray(R.array.dark_mode_values)
                when (pref.getString(darkModeString, darkModeValues[0])) {
                    darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkModeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_settings, rootKey)
            val moreApps: Preference? = findPreference("more_apps")
            if (moreApps != null) {
                moreApps.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle(R.string.more_apps)
                    alertDialog.setIcon(R.drawable.ic_shop)
                    val view: View = layoutInflater.inflate(R.layout.fragment_dialog, null)
                    val textViewMusicSleepTimer: MaterialTextView = view.findViewById(R.id.text_view_music_sleep_timer)
                    val textViewEnglishWithLidia: MaterialTextView = view.findViewById(R.id.text_view_english_with_lidia)
                    val textViewQRCodeScanner: MaterialTextView = view.findViewById(R.id.text_view_qr_code_scanner)
                    val textViewAndroidStudioTutorials: MaterialTextView = view.findViewById(R.id.text_view_android_studio_tutorials)
                    alertDialog.setView(view)
                    alertDialog.create()
                    view.findViewById<View?>(R.id.image_view_music_sleep_timer)?.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.musicsleeptimer.plus")))
                        }
                    textViewMusicSleepTimer.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.musicsleeptimer.plus")))
                    }
                    view.findViewById<View?>(R.id.image_view_english_with_lidia)?.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.englishwithlidia.plus")))
                        }
                    textViewEnglishWithLidia.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.englishwithlidia.plus")))
                    }
                    view.findViewById<View?>(R.id.image_view_qr_and_code_scanner)?.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.qrcodescanner.plus")))
                        }
                    textViewQRCodeScanner.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.qrcodescanner.plus")))
                    }
                    view.findViewById<View?>(R.id.image_view_android_studio_tutorials)?.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.androidtutorials")))
                    }
                    textViewAndroidStudioTutorials.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.d4rk.androidtutorials")))
                    }
                    alertDialog.setNegativeButton(android.R.string.cancel, null)
                    alertDialog.show()
                    true
                }
            }
            val changelog: Preference? = findPreference("changelog")
            if (changelog != null) {
                changelog.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    val content = requireContext().getString(R.string.changelog_title, BuildConfig.VERSION_NAME)
                    alertDialog.setTitle(content)
                    alertDialog.setIcon(R.drawable.ic_changelog)
                    alertDialog.setMessage(R.string.changes)
                    alertDialog.setNegativeButton(android.R.string.cancel, null)
                    alertDialog.show()
                    true
                }
            }
            val share: Preference? = findPreference("share")
            if (share != null) {
                share.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.d4rk.androidtutorials")
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_subject)
                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)                     ))
                    true
                }
            }
        }
    }
}