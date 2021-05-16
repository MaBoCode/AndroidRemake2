package com.example.androidremake2.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DimUtils {

    public static float dp2px(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

}