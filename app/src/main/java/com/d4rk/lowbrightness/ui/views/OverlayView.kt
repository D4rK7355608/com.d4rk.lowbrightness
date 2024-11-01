package com.d4rk.lowbrightness.ui.views;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
public class OverlayView extends AppCompatImageView {
	private final Paint loadPaint;
	private int opacityPercentage;
	private int color;
	public OverlayView(Context context) {
		super(context);
		loadPaint = new Paint();
		loadPaint.setAntiAlias(true);
		loadPaint.setTextSize(10);
		loadPaint.setColor(getColor());
		loadPaint.setAlpha(255 / 100 * getOpacityPercentage());
	}
	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawPaint(loadPaint);
	}
	public void redraw() {
		this.invalidate();
	}
	public void setOpacityPercentage(int opacityPercentage) {
		loadPaint.setAlpha(255 / 100 * opacityPercentage);
		this.opacityPercentage = opacityPercentage;
	}
	public int getOpacityPercentage() {
		return opacityPercentage;
	}
	public void setColor(int color) {
		loadPaint.setColor(color);
		setOpacityPercentage(getOpacityPercentage());
		this.color = color;
	}
	public int getColor() {
		return color;
	}
}