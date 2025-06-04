package com.d4rk.lowbrightness.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.d4rk.lowbrightness.MainActivity
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.base.Application
import com.d4rk.lowbrightness.base.Constants
import com.d4rk.lowbrightness.base.Prefs
import com.d4rk.lowbrightness.databinding.FragmentHomeBinding
import com.d4rk.lowbrightness.helpers.RequestDrawOverAppsPermission
import com.d4rk.lowbrightness.helpers.RequestAccessibilityPermission
import com.d4rk.lowbrightness.services.SchedulerService
import com.d4rk.lowbrightness.ui.views.SquareImageView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thebluealliance.spectrum.SpectrumDialog
import me.zhanghai.android.fastscroll.FastScrollerBuilder

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding?.scrollView?.let {
            FastScrollerBuilder(it).useMd2Style().build()
        }
        return binding?.root ?: throw IllegalStateException("Binding is null")
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        MobileAds.initialize(requireContext())

        val adRequest = AdRequest.Builder().build()
        binding?.adView?.loadAd(adRequest)

        val permissionRequester = RequestDrawOverAppsPermission(requireActivity())
        if (!permissionRequester.canDrawOverlays()) {
            showPermissionDialog(permissionRequester)
        }

        val accessibilityRequester = RequestAccessibilityPermission(requireActivity())
        if (!accessibilityRequester.isAccessibilityEnabled()) {
            showAccessibilityPermissionDialog(accessibilityRequester)
        }

        setupColorPicker()
        refreshUI()
        setupGridView()
        (requireActivity() as MainActivity).showOrHideSchedulerUI(SchedulerService.isEnabled(requireContext()))
    }

    private fun showPermissionDialog(permissionRequester: RequestDrawOverAppsPermission) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.notification_app_needs_permission_title)
            .setIcon(R.drawable.ic_eye)
            .setMessage(R.string.summary_app_needs_permission)
            .setCancelable(false)
            .setPositiveButton(R.string.allow_permission) { dialog, _ ->
                dialog.cancel()
                val launcher = (requireActivity() as MainActivity).overlayPermissionLauncher
                permissionRequester.requestPermissionDrawOverOtherApps(launcher)
            }
            .show()
    }

    private fun showAccessibilityPermissionDialog(permissionRequester: RequestAccessibilityPermission) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.accessibility_permission_title)
            .setIcon(R.drawable.ic_eye)
            .setMessage(R.string.summary_accessibility_permission)
            .setCancelable(true)
            .setPositiveButton(R.string.allow_permission) { dialog, _ ->
                dialog.cancel()
                permissionRequester.requestAccessibilityPermission()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun setupColorPicker() {
        binding?.buttonColorPicker?.setOnClickListener { view ->
            val colors = resources.obtainTypedArray(R.array.filter_colors).let { array ->
                IntArray(array.length()).apply {
                    for (i in indices) {
                        val hex = array.getString(i)
                        this[i] = Color.parseColor(hex)
                    }
                }.also { array.recycle() }
            }

            val prefs = Prefs.get(view.context)
            val preselectColor = prefs.getInt(Constants.PREF_OVERLAY_COLOR, colors[0])

            val colorPickerDialog = SpectrumDialog.Builder(context)
                    .setTitle(getString(R.string.select_a_color))
                    .setColors(colors)
                    .setSelectedColor(preselectColor)
                    .setOnColorSelectedListener { positiveResult, color ->
                        if (positiveResult) {
                            prefs.edit().putInt(Constants.PREF_OVERLAY_COLOR, color).apply()
                            Application.refreshServices(view.context)
                            refreshUI()
                        }
                    }
                    .build()

            colorPickerDialog.show(parentFragmentManager, "color_picker")
        }
    }

    private fun setupGridView() {
        val adapter = ColorsAdapter(requireContext())
        binding?.gridViewColors?.adapter = adapter

        val sharedPreferences = Prefs.get(requireContext())
        val opacityPercent = sharedPreferences.getInt(Constants.PREF_DIM_LEVEL, 20)
        val currentColor = sharedPreferences.getInt(Constants.PREF_OVERLAY_COLOR, Color.BLACK)

        binding?.materialSlider?.value = opacityPercent.toFloat()

        // Set the listener for the slider
        binding?.materialSlider?.addOnChangeListener { _, value, _ ->
            val newOpacity = value.toInt()
            sharedPreferences.edit().putInt(Constants.PREF_DIM_LEVEL, newOpacity).apply()
            Application.refreshServices(requireContext())
        }

        // Get the index of the current color from the adapter's colors
        val selectedIndex = adapter.overlayColors.indexOfFirst { it.color == currentColor }
        adapter.setSelectedPosition(selectedIndex.takeIf { it >= 0 } ?: 0)

        binding?.gridViewColors?.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    adapter.setSelectedPosition(position)
                    val selectedItem = adapter.getItem(position)
                    sharedPreferences.edit()
                            .putInt(Constants.PREF_OVERLAY_COLOR, selectedItem.color)
                            .apply()
                    Application.refreshServices(requireContext())
                }
    }



    private fun refreshUI() {
        val currentColor = Prefs.get(requireContext()).getInt(Constants.PREF_OVERLAY_COLOR, Color.BLACK)
        binding?.buttonColorPicker?.setBackgroundColor(currentColor)
    }

    private class OverlayColor(val label: String, hex: String) {
        val color: Int = Color.parseColor(hex)
    }

    private class ColorsAdapter(private val context: Context) : BaseAdapter() {
        private var selectedPosition = 0
        val overlayColors = listOf(
            OverlayColor("Black", "#000000"),
            OverlayColor("Brown", "#3E2723"),
            OverlayColor("Indigo", "#3949AB"),
            OverlayColor("Blue", "#0D47A1"),
            OverlayColor("Red", "#B71C1C"),
            OverlayColor("Teal", "#004D40")
        )

        override fun getCount(): Int = overlayColors.size

        override fun getItem(position: Int): OverlayColor = overlayColors[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val imageView: SquareImageView = convertView as? SquareImageView ?: SquareImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(100, 100, 100, 100)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(8, 8, 8, 8)
            }

            val overlayColor = getItem(position)
            imageView.alpha = if (position == selectedPosition) 1f else 0.7f
            imageView.setImageResource(R.drawable.ic_done)

            val bmp = Bitmap.createBitmap(85, 85, Bitmap.Config.ARGB_8888).apply {
                eraseColor(overlayColor.color)
            }
            imageView.background = BitmapDrawable(context.resources, bmp)
            return imageView
        }

        fun setSelectedPosition(position: Int) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }



    override fun onPause() {
        binding?.adView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding?.adView?.resume()
        Application.refreshServices(requireContext())
    }

    override fun onDestroy() {
        binding?.adView?.destroy()
        binding = null
        super.onDestroy()
    }
}
