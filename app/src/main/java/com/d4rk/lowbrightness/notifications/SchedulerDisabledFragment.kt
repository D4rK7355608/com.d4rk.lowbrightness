package com.d4rk.lowbrightness.notifications

import android.content.Context
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
            SchedulerService.enable(context)
            bridge.showOrHideSchedulerUI(true)
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bridge = activity as IShowHideScheduler
    }
}
