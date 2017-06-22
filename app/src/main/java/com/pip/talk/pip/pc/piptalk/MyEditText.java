package com.pip.talk.pip.pc.piptalk;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;


public class MyEditText extends EditText {
    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Lato-Light.ttf");
            setTypeface(tf);
        }
    }


    @Override
    public void setError(CharSequence error, Drawable icon) {
        super.setError(error, icon);
        setCompoundDrawables(null, null, icon, null);
    }
}
