/**
 * 优分享VR
 * copy right: youkes.com
 * author:xuming
 * licence:GPL2
 */
package com.youkes.vr.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.youkes.vr.R;

public class ChangeColorIconWithTextView extends View {

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	/**
	 * 颜色
	 */
	private int mColor = 0xFF45C01A;
	/**
	 * 透明度 0.0-1.0
	 */
	private float mAlpha = 0f;
	/**
	 * 图标
	 */
	private Bitmap mIconBitmap;
	/**
	 * 限制绘制icon的范围
	 */
	private Rect mIconRect;
	/**
	 * icon底部文本
	 */
	private String mText = "";
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
	private Paint mTextPaint;
	private Rect mTextBound = new Rect();

	public ChangeColorIconWithTextView(Context context) {
		super(context);
	}

	/**
	 * 初始化自定义属性值
	 * 
	 * @param context
	 * @param attrs
	 */
	public ChangeColorIconWithTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 获取设置的图标
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ChangeColorIconView);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {

			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.ChangeColorIconView_icon:
				BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
				mIconBitmap = drawable.getBitmap();
				break;
			case R.styleable.ChangeColorIconView_color:
				
				mColor = a.getColor(attr, 0x45C01A);
				break;
			case R.styleable.ChangeColorIconView_text:
				mText = a.getString(attr);
				break;
			case R.styleable.ChangeColorIconView_text_size:
				mTextSize = (int) a.getDimension(attr, TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
								getResources().getDisplayMetrics()));
				break;

			}
		}

		a.recycle();

		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0xff555555);
		// 得到text绘制范围
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 得到绘制icon的宽
		int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight(), getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom() - mTextBound.height());

		int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
		int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapWidth
				/ 2;

		// special
		// bitmapWidth=0;

		int bitmapHeight = 20;
		// 设置icon的绘制范围
		mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapHeight);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		int alpha = (int) Math.ceil((255 * mAlpha));
		if(isFreezed){
			alpha=1;
		}
		// canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		setupTargetBitmap(alpha);

		drawSourceText(canvas, alpha);
		drawTargetText(canvas, alpha);

		// canvas.drawBitmap(mBitmap, 0, 0, null);

	}

	private void setupTargetBitmap(int alpha) {
		if (mIconBitmap == null) {
			return;
		}
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		if (isFreezed) {
			mPaint.setColor(0xFF333333);
		} else {
			mPaint.setColor(mColor);
		}

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
	}

	private void drawSourceText(Canvas canvas, int alpha) {
		// mTextPaint.setTex
		mTextPaint.setTextSize(mTextSize);
		if (isFreezed) {
			mTextPaint.setColor(0xff888888);
		} else {
			mTextPaint.setColor(0xff333333);
		}

		mTextPaint.setAlpha(255 - alpha);
		mTextPaint.setAntiAlias(true);
		canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
				- mTextBound.width() / 2,
				mIconRect.bottom + mTextBound.height(), mTextPaint);
	}

	private void drawTargetText(Canvas canvas, int alpha) {
		mTextPaint.setColor(mColor);
		mTextPaint.setAlpha(alpha);
		mTextPaint.setAntiAlias(true);
		canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
				- mTextBound.width() / 2,
				mIconRect.bottom + mTextBound.height(), mTextPaint);

	}

	public void setIconAlpha(float alpha) {
		this.mAlpha = alpha;
		invalidateView();
	}

	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	public void setIconColor(int color) {
		mColor = color;
	}

	public void setIcon(int resId) {
		this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
		if (mIconRect != null)
			invalidateView();
	}

	public void setIcon(Bitmap iconBitmap) {
		this.mIconBitmap = iconBitmap;
		if (mIconRect != null)
			invalidateView();
	}

	private static final String INSTANCE_STATE = "instance_state";
	private static final String STATE_ALPHA = "state_alpha";

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putFloat(STATE_ALPHA, mAlpha);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			mAlpha = bundle.getFloat(STATE_ALPHA);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
		} else {
			super.onRestoreInstanceState(state);
		}

	}

	public void setText(String txt) {
		mText = txt;
		invalidate();
	}

	boolean isFreezed = false;

	public void setFreeze(boolean b) {

		this.isFreezed = b;
		
		invalidate();
	}

}
