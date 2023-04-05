package com.d4rk.lowbrightness.notifications;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.d4rk.lowbrightness.databinding.FragmentSchedulerDisabledBinding;
import com.d4rk.lowbrightness.helpers.IShowHideScheduler;
import com.d4rk.lowbrightness.services.SchedulerService;
public class SchedulerDisabledFragment extends Fragment {
    private IShowHideScheduler bridge;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSchedulerDisabledBinding binding = FragmentSchedulerDisabledBinding.inflate(inflater, container, false);
        binding.buttonSchedule.setOnClickListener(v -> {
            SchedulerService.enable(getContext());
            bridge.showOrHideSchedulerUI(true);
        });
        return binding.getRoot();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bridge = (IShowHideScheduler) getActivity();
    }
}