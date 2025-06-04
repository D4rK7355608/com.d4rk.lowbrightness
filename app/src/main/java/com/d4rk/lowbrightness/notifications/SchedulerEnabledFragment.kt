package com.d4rk.lowbrightness.notifications

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.base.Application
import com.d4rk.lowbrightness.base.Prefs
import com.d4rk.lowbrightness.databinding.FragmentSchedulerEnabledBinding
import com.d4rk.lowbrightness.helpers.IShowHideScheduler
import com.d4rk.lowbrightness.services.SchedulerService
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class SchedulerEnabledFragment : Fragment() {
    private var _binding: FragmentSchedulerEnabledBinding? = null
    private val binding get() = _binding!!

    private lateinit var bridge: IShowHideScheduler
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchedulerEnabledBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bridge = activity as IShowHideScheduler
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSchedule.setOnClickListener {
            SchedulerService.disable(requireContext())
            bridge.showOrHideSchedulerUI(false)
        }
        reloadButtonUIs()
        binding.buttonHourFrom.setOnClickListener {
            val sharedPreferences = Prefs.get(requireContext())
            val scheduleFromHour = sharedPreferences.getInt("scheduleFromHour", 20)
            val scheduleFromMinute = sharedPreferences.getInt("scheduleFromMinute", 0)
            val dialogTimePicker = TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                Prefs.get(requireContext()).edit()
                    .putInt("scheduleFromHour", hourOfDay)
                    .putInt("scheduleFromMinute", minute)
                    .apply()
                reloadButtonUIs()
            }, scheduleFromHour, scheduleFromMinute, true)
            dialogTimePicker.show(childFragmentManager, "timepicker_dialog")
        }
        binding.buttonHourTo.setOnClickListener {
            val sharedPreferences = Prefs.get(requireContext())
            val scheduleToHour = sharedPreferences.getInt("scheduleToHour", 6)
            val scheduleToMinute = sharedPreferences.getInt("scheduleToMinute", 0)
            val dialogTimePicker = TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                Prefs.get(requireContext()).edit()
                    .putInt("scheduleToHour", hourOfDay)
                    .putInt("scheduleToMinute", minute)
                    .apply()
                reloadButtonUIs()
            }, scheduleToHour, scheduleToMinute, true)
            dialogTimePicker.show(childFragmentManager, "timepicker_dialog")
        }
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer = null
    }

    private fun reloadButtonUIs() {
        if (view == null) return

        val sharedPreferences = Prefs.get(requireContext())
        val scheduleFromHour = sharedPreferences.getInt("scheduleFromHour", 20)
        val scheduleFromMinute = sharedPreferences.getInt("scheduleFromMinute", 0)
        val scheduleToHour = sharedPreferences.getInt("scheduleToHour", 6)
        val scheduleToMinute = sharedPreferences.getInt("scheduleToMinute", 0)

        val cNow = Calendar.getInstance()
        val cStart = SchedulerService.getCalendarForStart(requireContext())
        val cEnd = SchedulerService.getCalendarForEnd(requireContext())

        timer?.cancel()

        val timeRemainingToDarkenLabel = resources.getString(R.string.time_remaining_to_darken_label)
        val timeRemainingToLightenLabel = resources.getString(R.string.time_remaining_to_lighten_label)
        binding.textViewTimeRemaining.visibility = View.VISIBLE

        when {
            cNow.timeInMillis > cStart.timeInMillis && cNow.timeInMillis < cEnd.timeInMillis -> {
                val remainingMillisToLighten = cEnd.timeInMillis - cNow.timeInMillis
                timer = object : CountDownTimer(remainingMillisToLighten, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val t = timeRemainingToLightenLabel + ": " + String.format(
                            Locale.getDefault(),
                            "%tT",
                            millisUntilFinished - TimeZone.getDefault().rawOffset
                        )
                        binding.textViewTimeRemaining.text = t
                    }

                    override fun onFinish() {
                        reloadButtonUIs()
                    }
                }.start()
            }
            cNow.timeInMillis < cStart.timeInMillis -> {
                val remainingMillisToDarken = cStart.timeInMillis - cNow.timeInMillis
                timer = object : CountDownTimer(remainingMillisToDarken, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val t = timeRemainingToDarkenLabel + ": " + String.format(
                            Locale.getDefault(),
                            "%tT",
                            millisUntilFinished - TimeZone.getDefault().rawOffset
                        )
                        binding.textViewTimeRemaining.text = t
                    }

                    override fun onFinish() {
                        reloadButtonUIs()
                    }
                }.start()
            }
            else -> {
                binding.textViewTimeRemaining.visibility = View.GONE
            }
        }

        binding.buttonHourFrom.text = String.format(Locale.getDefault(), "%02d:%02d", scheduleFromHour, scheduleFromMinute)
        binding.buttonHourTo.text = String.format(Locale.getDefault(), "%02d:%02d", scheduleToHour, scheduleToMinute)

        Application.refreshServices(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
