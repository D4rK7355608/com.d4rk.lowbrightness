package com.d4rk.lowbrightness.notifications

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.d4rk.lowbrightness.databinding.FragmentSchedulerDisabledBinding
import com.d4rk.lowbrightness.helpers.IShowHideScheduler
import com.d4rk.lowbrightness.services.SchedulerService

class SchedulerDisabledFragment : Fragment() {
    private lateinit var bridge: IShowHideScheduler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSchedulerDisabledBinding.inflate(inflater, container, false)
        binding.buttonSchedule.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return@setOnClickListener
                }
            }
            context?.let { SchedulerService.enable(it) }
            bridge.showOrHideSchedulerUI(true)
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bridge = activity as IShowHideScheduler
    }
}
