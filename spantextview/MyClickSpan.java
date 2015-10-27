package com.zd.myd.custome_view.spantextview;

import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


public class MyClickSpan extends ClickableSpan implements Cloneable{
	private OnTextViewClickListener mListener;
	private String myClickText;
	private boolean replace;
	private SpannableString spannableString;
	public MyClickSpan(OnTextViewClickListener mListener) {
		this(mListener, false);
	}
	
	/**
	 * 处理空格
	 * @param mListener
	 * @param replace
	 */
	public MyClickSpan(OnTextViewClickListener mListener, boolean replace) {
		super();
		this.mListener = mListener;
		this.replace = replace;
	}

	@Override
	public void onClick(View widget) {
		if (replace) {
			myClickText = myClickText.replaceAll("_", " ");
		}
		mListener.clickTextView(myClickText);
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		mListener.setStyle(ds);
		
	}

	public String getMyClickText() {
		return myClickText;
	}

	public void setMyClickText(String myClickText) {
		this.myClickText = myClickText;
	}

	public OnTextViewClickListener getmListener() {
		return mListener;
	}

	public void setmListener(OnTextViewClickListener mListener) {
		this.mListener = mListener;
	}

	public void setSpannableString(SpannableString spannableString) {
        this.spannableString = spannableString;
    }

    @Override
	protected Object clone() throws CloneNotSupportedException {
		MyClickSpan clickSpan=new MyClickSpan((OnTextViewClickListener)(mListener.clone(mListener)));
		clickSpan.setMyClickText(myClickText);
		clickSpan.setSpannableString(spannableString);
		return clickSpan;
	}

}