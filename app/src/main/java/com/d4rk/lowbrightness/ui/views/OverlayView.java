package com.d4rk.lowbrightness.ui.views;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
public class OverlayView extends AppCompatImageView {
	private final Paint mLoadPaint;
	private int opacityPercent = 20;
	private int color = Color.BLACK;
	public OverlayView(Context context) {
		super(context);
		mLoadPaint = new Paint();
		mLoadPaint.setAntiAlias(true);
		mLoadPaint.setTextSize(10);
		mLoadPaint.setColor(getColor());
		mLoadPaint.setAlpha(255 / 100 * getOpacityPercent());
	}
	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawPaint(mLoadPaint);
	}
	public void redraw() {
		this.invalidate();
	}
	public void setOpacityPercent(int opacityPercent) {
		mLoadPaint.setAlpha(255 / 100 * opacityPercent);
		this.opacityPercent = opacityPercent;
	}
	public int getOpacityPercent() {
		return opacityPercent;
	}
	public void setColor(int color) {
		mLoadPaint.setColor(color);
		setOpacityPercent(getOpacityPercent());
		this.color = color;
	}
	public int getColor() {
		return color;
	}
}