package com.akitektuo.smartlist.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by AoD Akitektuo on 15-Mar-17.
 */

public class Constant {

    public static final String CURRENCY_RON = "RON";
    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_JPY = "JPY";
    public static final String CURRENCY_GBP = "GBP";
    public static final String CURRENCY_AUD = "AUD";
    public static final String CURRENCY_CAD = "CAD";
    public static final String CURRENCY_CHF = "CHF";
    public static final String CURRENCY_CNY = "CNY";
    public static final String CURRENCY_RUB = "RUB";
    public static final String CURRENCY_KRW = "KRW";
    public static final String CURRENCY_SEK = "SEK";
    public static final String CURRENCY_AED = "AED";

    public static final Handler handler = new Handler(Looper.getMainLooper());

    public static final int PRICE_LIMIT = 1001;

    public static final String KEY_INITIALIZE = "initialize";
    public static final String KEY_CREATED = "created";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_RECOMMENDATIONS = "recommendations";
    public static final String KEY_AUTO_FILL = "auto_fill";
    public static final String KEY_AUTO_FILL_WANTED = "auto_fill_wanted";
    public static final String KEY_SMART_PRICE = "smart_price";

    public static Preference preference;
    public static double totalCount;

}
