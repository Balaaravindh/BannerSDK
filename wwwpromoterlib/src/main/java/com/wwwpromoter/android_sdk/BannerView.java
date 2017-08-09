package com.wwwpromoter.android_sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BannerView extends RelativeLayout {

    LayoutInflater mInflater;
    public BannerView(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);

    }
    public BannerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);

    }
    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
    }


}