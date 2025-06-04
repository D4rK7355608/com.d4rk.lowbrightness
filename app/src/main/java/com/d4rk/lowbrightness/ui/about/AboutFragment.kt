package com.d4rk.lowbrightness.ui.about
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.d4rk.lowbrightness.BuildConfig
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.databinding.FragmentAboutBinding
import com.d4rk.lowbrightness.ui.viewmodel.AboutViewModel
import com.d4rk.lowbrightness.helpers.copyToClipboard
import com.d4rk.lowbrightness.helpers.openUrl
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
class AboutFragment : Fragment() {
    private lateinit var _binding: FragmentAboutBinding
    private val binding get() = _binding
    private val calendar: Calendar = Calendar.getInstance()
    private var originalNavBarColor: Int? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        ViewModelProvider(this)[AboutViewModel::class.java]
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        originalNavBarColor = activity?.window?.navigationBarColor
        setOriginalNavigationBarColor()
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        MobileAds.initialize(requireContext())
        binding.adView.loadAd(AdRequest.Builder().build())
        val version = String.format(resources.getString(R.string.app_version), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        binding.textViewAppVersion.text = version
        val simpleDateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val dateText = simpleDateFormat.format(calendar.time)
        val copyright = requireContext().getString(R.string.copyright, dateText)
        binding.textViewCopyright.text = copyright
        binding.textViewAppVersion.setOnLongClickListener {
            requireContext().copyToClipboard(binding.textViewAppVersion.text)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                Snackbar.make(requireView(), R.string.snack_copied_to_clipboard, Snackbar.LENGTH_SHORT).show()
            true
        }
        binding.imageViewAppIcon.setOnClickListener {
            requireContext().openUrl("https://sites.google.com/view/d4rk7355608")
        }
        binding.chipGoogleDev.setOnClickListener {
            requireContext().openUrl("https://g.dev/D4rK7355608")
        }
        binding.chipYoutube.setOnClickListener {
            requireContext().openUrl("https://www.youtube.com/c/D4rK7355608")
        }
        binding.chipGithub.setOnClickListener {
            requireContext().openUrl("https://github.com/D4rK7355608/" + BuildConfig.APPLICATION_ID)
        }
        binding.chipTwitter.setOnClickListener {
            requireContext().openUrl("https://twitter.com/D4rK7355608")
        }
        binding.chipXda.setOnClickListener {
            requireContext().openUrl("https://forum.xda-developers.com/m/d4rk7355608.10095012")
        }
        binding.chipMusic.setOnClickListener {
            requireContext().openUrl("https://sites.google.com/view/d4rk7355608/tracks")
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.navigationBarColor = originalNavBarColor!!
        _binding
    }

    private fun setOriginalNavigationBarColor() {
        activity?.window?.navigationBarColor = originalNavBarColor!!
    }
}