package com.example.androidremake2.utils;

import android.content.Context;

import java.util.Locale;

public class UserUtils {

    public static String getUserCountryCode(Context context) {
        Locale userLocale = context.getResources().getConfiguration().locale;
        return userLocale.getCountry().toLowerCase();
    }
}
