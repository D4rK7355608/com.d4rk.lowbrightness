package com.d4rk.lowbrightness.ui.views;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.AttributeSet;
import com.d4rk.lowbrightness.base.Application;
import com.d4rk.lowbrightness.base.Constants;
import com.d4rk.lowbrightness.base.Prefs;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
public class DiscreeteSeekBar extends DiscreteSeekBar {
    private void attachListener() {
        this.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            final SharedPreferences sharedPreferences = Prefs.get(getContext());
            final Context context = getContext();
            int currentProgress = 0;
            final Handler h = new Handler();
            final Runnable r = () -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constants.PREF_DIM_LEVEL, currentProgress);
                editor.putBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, currentProgress > 0);
                editor.apply();
                Application.refreshServices(context);
            };
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int intensityPercent, boolean fromUser) {
                if (!fromUser) return;
                currentProgress = intensityPercent;
                h.removeCallbacks(r);
                h.postDelayed(r, 300);
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
    }
    public DiscreeteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        attachListener();
    }
    public DiscreeteSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attachListener();
    }
    public DiscreeteSeekBar(Context context) {
        super(context);
        attachListener();
    }
}