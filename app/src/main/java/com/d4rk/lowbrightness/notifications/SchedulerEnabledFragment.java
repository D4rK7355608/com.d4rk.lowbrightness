package com.d4rk.lowbrightness.notifications;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.d4rk.lowbrightness.R;
import com.d4rk.lowbrightness.base.Application;
import com.d4rk.lowbrightness.base.Prefs;
import com.d4rk.lowbrightness.databinding.FragmentSchedulerEnabledBinding;
import com.d4rk.lowbrightness.helpers.IShowHideScheduler;
import com.d4rk.lowbrightness.services.SchedulerService;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
@SuppressWarnings({"deprecation", "ConstantConditions"})
public class SchedulerEnabledFragment extends Fragment {
    private FragmentSchedulerEnabledBinding binding;
    private IShowHideScheduler bridge;
    private CountDownTimer timer;
    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSchedulerEnabledBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bridge = ((IShowHideScheduler) getActivity());
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSchedule.setOnClickListener(v -> {
            SchedulerService.disable(getContext());
            bridge.showOrHideSchedulerUI(false);
        });
        reloadButtonUIs();
        binding.buttonHourFrom.setOnClickListener(v -> {
            final SharedPreferences sharedPreferences = Prefs.get(requireContext());
            final int scheduleFromHour = sharedPreferences.getInt("scheduleFromHour", 20);
            final int scheduleFromMinute = sharedPreferences.getInt("scheduleFromMinute", 0);
            TimePickerDialog dialogTimePicker = TimePickerDialog.newInstance(
                    (view12, hourOfDay, minute, seconds) -> {
                        final SharedPreferences sp12 = Prefs.get(requireContext());
                        sp12.edit().putInt("scheduleFromHour", hourOfDay).putInt("scheduleFromMinute", minute).apply();
                        reloadButtonUIs();
                    },
                    scheduleFromHour,
                    scheduleFromMinute,
                    true
            );
            dialogTimePicker.show(getFragmentManager(), "timepicker_dialog");
        });
        binding.buttonHourTo.setOnClickListener(v -> {
            final SharedPreferences sharedPreferences = Prefs.get(requireContext());
            final int scheduleToHour = sharedPreferences.getInt("scheduleToHour", 6);
            final int scheduleToMinute = sharedPreferences.getInt("scheduleToMinute", 0);
            TimePickerDialog dialogTimePicker = TimePickerDialog.newInstance(
                    (view1, hourOfDay, minute, seconds) -> {
                        final SharedPreferences sp1 = Prefs.get(requireContext());
                        sp1.edit().putInt("scheduleToHour", hourOfDay).putInt("scheduleToMinute", minute).apply();
                        reloadButtonUIs();
                    },
                    scheduleToHour,
                    scheduleToMinute,
                    true
            );
            dialogTimePicker.show(getFragmentManager(), "timepicker_dialog");
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    private void reloadButtonUIs() {
        if (getView() == null) return;
        final SharedPreferences sharedPreferences = Prefs.get(requireContext());
        final int scheduleFromHour = sharedPreferences.getInt("scheduleFromHour", 20);
        final int scheduleFromMinute = sharedPreferences.getInt("scheduleFromMinute", 0);
        final int scheduleToHour = sharedPreferences.getInt("scheduleToHour", 6);
        final int scheduleToMinute = sharedPreferences.getInt("scheduleToMinute", 0);
        Calendar cNow = Calendar.getInstance();
        Calendar cStart = SchedulerService._getCalendarForStart(getContext());
        final Calendar cEnd = SchedulerService._getCalendarForEnd(getContext());
        if (timer != null) {
            timer.cancel();
        }
        final String time_remaining_to_darken_label = getResources().getString(R.string.time_remaining_to_darken_label);
        final String time_remaining_to_lighten_label = getResources().getString(R.string.time_remaining_to_lighten_label);
        binding.textViewTimeRemaining.setVisibility(View.VISIBLE);
        if (cNow.getTimeInMillis() > cStart.getTimeInMillis() && cNow.getTimeInMillis() < cEnd.getTimeInMillis()) {
            long remainingMillisToLighten = cEnd.getTimeInMillis() - cNow.getTimeInMillis();
            timer = new CountDownTimer(remainingMillisToLighten, 1000) {
                public void onTick(long millisUntilFinished) {
                    String t = time_remaining_to_lighten_label + ": " + String.format(Locale.getDefault(), "%tT", (millisUntilFinished - TimeZone.getDefault().getRawOffset()));
                    binding.textViewTimeRemaining.setText(t);
                }
                public void onFinish() {
                    reloadButtonUIs();
                }
            }.start();
        } else if (cNow.getTimeInMillis() < cStart.getTimeInMillis()) {
            long remainingMillisToDarken = cStart.getTimeInMillis() - cNow.getTimeInMillis();
            timer = new CountDownTimer(remainingMillisToDarken, 1000) {
                public void onTick(long millisUntilFinished) {
                    String t = time_remaining_to_darken_label + ": " + String.format(Locale.getDefault(), "%tT", (millisUntilFinished - TimeZone.getDefault().getRawOffset()));
                    binding.textViewTimeRemaining.setText(t);
                }
                public void onFinish() {
                    reloadButtonUIs();
                }
            }.start();
        } else {
            binding.textViewTimeRemaining.setVisibility(View.GONE);
        }
        binding.buttonHourFrom.setText(String.format(Locale.getDefault(), "%02d:%02d", scheduleFromHour, scheduleFromMinute));
        binding.buttonHourTo.setText(String.format(Locale.getDefault(), "%02d:%02d", scheduleToHour, scheduleToMinute));
        if (getContext() != null) {
            Application.refreshServices(getContext());
        }
    }
}