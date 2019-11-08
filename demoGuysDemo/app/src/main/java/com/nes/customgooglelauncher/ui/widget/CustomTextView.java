package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.nes.customgooglelauncher.R;
import com.nes.utils.TextTypefaceUtil;

import java.io.IOException;

/**
 * 可以选择字体的TextView
 * @author liuqz
 */
public class CustomTextView extends AppCompatTextView {
    String[] typefacePath = {"Roboto-Light.ttf", "Roboto-Regular.ttf", "Roboto-Thin.ttf", "RobotoCondensed-Light.ttf", "Roboto-Bold.ttf", "Roboto-Medium.ttf"};


    private int mTypeface;
    private Drawable drawable;

    public CustomTextView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            mTypeface = typedArray.getInt(R.styleable.CustomTextView_myTypeface, 0);
            typedArray.recycle();
        }

        try {
            setTypeface(TextTypefaceUtil.getTypeface(context, typefacePath[mTypeface]));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void changeTypeface(boolean focused, TextTypeface typeface) {
        int position;

        if (focused) {
            position = typeface.code;
        } else {
            position = mTypeface;
        }

        try {
            setTypeface(TextTypefaceUtil.getTypeface(getContext(), typefacePath[position]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public enum TextTypeface {
        ROBOTO_LIGHT(0), ROBOTO_REGULAR(1), ROBOTO_THIN(2), ROBOTO_CONDENSED_LIGHT(3), ROBOTO_BOLD(4), ROBOTO_MEDIUM(5);

        private int code;


        TextTypeface(int code) {
            this.code = code;
        }
    }
}
