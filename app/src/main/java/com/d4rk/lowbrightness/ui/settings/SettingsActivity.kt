package com.d4rk.lowbrightness.ui.settings
import android.content.Context
import android.content.ClipData
import android.content.SharedPreferences
import android.content.Intent
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.databinding.ActivitySettingsBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

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
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, rootKey: String?) {
        val themeValues = resources.getStringArray(R.array.preference_theme_values)
        when (rootKey) {
            getString(R.string.key_theme) -> sharedPreferences?.let { pref ->
                when (pref.getString(getString(R.string.key_theme), themeValues[0])) {
                    themeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    themeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    themeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    themeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
        val languageCode = sharedPreferences?.getString(getString(R.string.key_language), getString(R.string.default_value_language))
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_settings, rootKey)
            val changelogPreference: Preference? = findPreference(getString(R.string.key_changelog))
            changelogPreference?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.changelog_title, BuildConfig.VERSION_NAME))
                    .setIcon(R.drawable.ic_changelog)
                    .setMessage(R.string.changes)
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
            val notificationsSettings = findPreference<Preference>(getString(R.string.key_notifications_settings))
            notificationsSettings?.setOnPreferenceClickListener {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                startActivity(intent)
                true
            }
            val sharePreference = findPreference<Preference>(getString(R.string.key_share))
            sharePreference?.setOnPreferenceClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                    putExtra(Intent.EXTRA_SUBJECT, R.string.share_subject)
                }
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)))
                true
            }
            val ossPreference = findPreference<Preference>(getString(R.string.key_open_source_licenses))
            ossPreference?.setOnPreferenceClickListener {
                startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
                true
            }
            val deviceInfoPreference = findPreference<Preference>(getString(R.string.key_device_info))
            val version = String.format(
                resources.getString(R.string.app_build),
                "${resources.getString(R.string.manufacturer)} ${Build.MANUFACTURER}",
                "${resources.getString(R.string.device_model)} ${Build.MODEL}",
                "${resources.getString(R.string.android_version)} ${Build.VERSION.RELEASE}",
                "${resources.getString(R.string.api_level)} ${Build.VERSION.SDK_INT}",
                "${resources.getString(R.string.arch)} ${Build.SUPPORTED_ABIS.joinToString()}"
            )
            deviceInfoPreference?.summary = version
            deviceInfoPreference?.setOnPreferenceClickListener {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", version)
                clipboard.setPrimaryClip(clip)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    Snackbar.make(requireView(), R.string.snack_copied_to_clipboard, Snackbar.LENGTH_SHORT).show()
                }
                true
            }
        }
    }
}