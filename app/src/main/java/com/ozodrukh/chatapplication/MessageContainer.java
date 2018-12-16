package com.ozodrukh.chatapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MessageContainer extends LinearLayout {
  public MessageContainer(Context context) {
    this(context, null);
  }

  public MessageContainer(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MessageContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int maxWidth = (int) (getResources().getDisplayMetrics().density * 220);
    final int atMostWidthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);

    super.onMeasure(atMostWidthSpec, heightMeasureSpec);
  }
}
