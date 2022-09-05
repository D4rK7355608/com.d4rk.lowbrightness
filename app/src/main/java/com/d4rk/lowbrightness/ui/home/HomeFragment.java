package com.d4rk.lowbrightness.ui.home;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.d4rk.lowbrightness.MainActivity;
import com.d4rk.lowbrightness.R;
import com.d4rk.lowbrightness.base.Application;
import com.d4rk.lowbrightness.base.Constants;
import com.d4rk.lowbrightness.base.Prefs;
import com.d4rk.lowbrightness.databinding.FragmentHomeBinding;
import com.d4rk.lowbrightness.helpers.RequestDrawOverAppsPermission;
import com.d4rk.lowbrightness.services.SchedulerService;
import com.d4rk.lowbrightness.ui.views.SquareImageView;
import com.thebluealliance.spectrum.SpectrumDialog;
import java.util.ArrayList;
import java.util.List;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        new FastScrollerBuilder(binding.homeScrollView).useMd2Style().build();
        return binding.getRoot();
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        final RequestDrawOverAppsPermission permissionRequester = new RequestDrawOverAppsPermission(getActivity());
        if (!permissionRequester.canDrawOverlays()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("App needs permission!");
            builder.setMessage("Applying a blue light filter to overlay the screen, requires a special permission called \"Display over other apps\". \n\nPlease, allow this permission on the next screen so that the blue light filter can be applied");
            builder.setCancelable(false);
            builder.setPositiveButton("Allow permission", (dialog, id) -> {
                dialog.cancel();
                permissionRequester.requestPermissionDrawOverOtherApps();
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        binding.bColorPicker.setOnClickListener(view -> {
            TypedArray ta = getResources().obtainTypedArray(R.array.filter_colors);
            int[] colors = new int[ta.length()];
            for (int i = 0; i < ta.length(); i++) {
                String hex = ta.getString(i);
                colors[i] = Color.parseColor(hex);
            }
            ta.recycle();
            SharedPreferences prefs = Prefs.get(view.getContext());
            int preselectColor = prefs.getInt(Constants.PREF_OVERLAY_COLOR, colors[0]);
            DialogFragment colorPickerDialog = new SpectrumDialog.Builder(getContext())
                    .setTitle("Select a color")
                    .setColors(colors)
                    .setSelectedColor(preselectColor)
                    .setOnColorSelectedListener((positiveResult, color) -> {
                        if (!positiveResult) return;
                        SharedPreferences sharedPreferences = Prefs.get(requireContext());
                        sharedPreferences.edit().putInt(Constants.PREF_OVERLAY_COLOR, color).apply();
                        Application.refreshServices(getContext());
                        refreshUI();
                    }).build();
            colorPickerDialog.show(getFragmentManager(), "color_picker");
        });
        refreshUI();
        final ColorsAdapter adapter = new ColorsAdapter(getContext());
        binding.gridColors.setAdapter(adapter);
        SharedPreferences sharedPreferences = Prefs.get(requireContext());
        int opacityPercent = sharedPreferences.getInt(Constants.PREF_DIM_LEVEL, 20);
        final int currentColor = sharedPreferences.getInt("overlay_color", Color.BLACK);
        binding.sbDarkenIntensity.setProgress(opacityPercent);
        int totalColors = adapter.getCount();
        for (int i = 0; totalColors > i; i += 1) {
            OverlayColor c = adapter.getItem(i);
            if (c.color == currentColor) {
                adapter.setSelectedPosition(i);
                break;
            }
        }
        binding.gridColors.setOnItemClickListener((parent, v, position, id) -> {
            adapter.setSelectedPosition(position);
            OverlayColor selectedItem = adapter.getItem(position);
            SharedPreferences secondSharedPreferences = Prefs.get(v.getContext());
            secondSharedPreferences.edit().putInt("overlay_color", selectedItem.color).apply();
            Application.refreshServices(v.getContext());
        });
        ((MainActivity) getActivity()).showOrHideSchedulerUI(SchedulerService.isEnabled(getContext()));
    }
    private void refreshUI() {
        SharedPreferences sharedPreferences = Prefs.get(requireContext());
        final int currentColor = sharedPreferences.getInt("overlay_color", Color.BLACK);
        binding.bColorPicker.setBackgroundColor(currentColor);
    }
    static private class OverlayColor {
        public String label;
        public String hex;
        public int color;
        public OverlayColor(String label, String hex) {
            this.label = label;
            this.hex = hex;
            this.color = Color.parseColor(hex);
        }
    }
    static private class ColorsAdapter extends BaseAdapter {
        private final Context mContext;
        private int selectedPosition = 0;
        private final List<OverlayColor> overlayColors = new ArrayList<>();
        public ColorsAdapter(Context context) {
            mContext = context;
            overlayColors.add(new OverlayColor("Black", "#000000"));
            overlayColors.add(new OverlayColor("Brown", "#3E2723"));
            overlayColors.add(new OverlayColor("Indigo", "#3949AB"));
            overlayColors.add(new OverlayColor("Blue", "#0D47A1"));
            overlayColors.add(new OverlayColor("Red", "#B71C1C"));
            overlayColors.add(new OverlayColor("Teal", "#004D40"));
        }
        @Override
        public int getCount() {
            return overlayColors.size();
        }
        @Override
        public OverlayColor getItem(int position) {
            return overlayColors.get(position);
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView imageView;
            if (convertView == null) {
                imageView = new SquareImageView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
                lp.setMargins(100, 100, 100, 100);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (SquareImageView) convertView;
            }
            final OverlayColor overlayColor = getItem(position);
            final int color = android.graphics.Color.parseColor(overlayColor.hex);
            if (position == selectedPosition) {
                imageView.setAlpha(1f);
            } else {
                imageView.setAlpha(0.7f);
            }
            imageView.setImageResource(R.drawable.ic_done);
            final Bitmap bmp = Bitmap.createBitmap(85, 85, Bitmap.Config.ARGB_8888);
            bmp.eraseColor(color);
            final BitmapDrawable ob = new BitmapDrawable(mContext.getResources(), bmp);
            imageView.setBackground(ob);
            return imageView;
        }
        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
            notifyDataSetChanged();
        }
    }
}