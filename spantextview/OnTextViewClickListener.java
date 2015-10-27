package com.zd.myd.custome_view.spantextview;

import android.text.TextPaint;

public abstract class OnTextViewClickListener  {
	public abstract void clickTextView(String clickString);
	public abstract void setStyle(TextPaint tp);
	public abstract void clickOtherTextView(String clickString);
	
	protected Object clone(final OnTextViewClickListener clickListener) throws CloneNotSupportedException {
		
		return new OnTextViewClickListener() {
			@Override
			public void setStyle(TextPaint tp) {
				clickListener.setStyle(tp);
			}
			@Override
			public void clickTextView(String clickString) {
				clickListener.clickTextView(clickString);
			}
			@Override
			public void clickOtherTextView(String clickString) {
				clickListener.clickOtherTextView(clickString);
			}
		};
	}
	
}