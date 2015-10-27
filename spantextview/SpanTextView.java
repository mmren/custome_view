package com.zd.myd.custome_view.spantextview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class SpanTextView extends TextView {
	boolean linkHit;
	boolean dontConsumeNonUrlClicks = true;

	public SpanTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SpanTextView(Context context){
	    super(context);
	}

	public void setKeyworkClickable(SpannableString spannableString, Pattern pattern, MyClickSpan clickableSpan) {
		Matcher matcher = pattern.matcher(spannableString);
		String str = pattern.toString();
		clickableSpan.setSpannableString(spannableString);
		int findStart = 0;
		while (matcher.find()) {
			String key = matcher.group();
			if (!"".equals(key)) {
				clickableSpan.setMyClickText(key);
				
				int start = spannableString.toString().indexOf(key, findStart);
				int end = start + key.length();
				findStart = end;
				try {
					setClickTextView(spannableString, (MyClickSpan)(clickableSpan.clone()), start, end);
//					setClickTextView(spannableString, clickableSpan, start, end);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				// 评论里面的昵称匹配找到第一个就好了
				if (str.endsWith("\\S+")) {
					break;
				}
			}
			
			
		}
	}

	private void setClickTextView(SpannableString spannableString, MyClickSpan clickableSpan, int start, int end) {
		spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		setText(spannableString);
		setMovementMethod(LocalLinkMovementMethod.getInstance());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		linkHit = false;
		boolean res = super.onTouchEvent(event);
		if (dontConsumeNonUrlClicks)
			return linkHit;
		return res;
	}

	public static class LocalLinkMovementMethod extends LinkMovementMethod {
		static LocalLinkMovementMethod sInstance;
		public static LocalLinkMovementMethod getInstance() {
			if (sInstance == null)
				sInstance = new LocalLinkMovementMethod();
			return sInstance;
		}
		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();
				x += widget.getScrollX();
				y += widget.getScrollY();
				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);
				ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
					}
					if (widget instanceof SpanTextView) {
						((SpanTextView) widget).linkHit = true;
					}
					return true;
				} else {
					Selection.removeSelection(buffer);
					Touch.onTouchEvent(widget, buffer, event);
					return false;
				}
			}
			return Touch.onTouchEvent(widget, buffer, event);
		}
	}
}
