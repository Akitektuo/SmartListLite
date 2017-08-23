package com.akitektuo.smartlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;

import static com.akitektuo.smartlist.util.Constant.KEY_SMART_PRICE;
import static com.akitektuo.smartlist.util.Constant.PRICE_LIMIT;
import static com.akitektuo.smartlist.util.Constant.handler;
import static com.akitektuo.smartlist.util.Constant.preference;

/**
 * Created by Akitektuo on 15.03.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_QUERY_LIST = "CREATE TABLE " + DatabaseContract.ListContractEntry.TABLE_NAME + " (" +
            DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " NUMBER," +
            DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE + " TEXT," +
            DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT + " TEXT," +
            DatabaseContract.ListContractEntry.COLUMN_NAME_DATE + " TEXT" + ");";

    private static final String DATABASE_QUERY_USAGE = "CREATE TABLE " + DatabaseContract.UsageContractEntry.TABLE_NAME + " (" +
            DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " TEXT," +
            DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES + " TEXT" + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_QUERY_LIST);
        sqLiteDatabase.execSQL(DATABASE_QUERY_USAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addList(int number, String value, String product, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER, number);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE, value);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT, product);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_DATE, date);
        getWritableDatabase().insert(DatabaseContract.ListContractEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getList() {
        String[] list = {DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER,
                DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE,
                DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT,
                DatabaseContract.ListContractEntry.COLUMN_NAME_DATE};
        return getReadableDatabase().query(DatabaseContract.ListContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    public int getListNumberNew() {
        int number = 0;
        Cursor cursor = getList();
        if (cursor.moveToLast()) {
            number = cursor.getInt(0);
        }
        cursor.close();
        return ++number;
    }

    public Cursor getListForNumber(int number) {
        String[] results = {DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER,
                DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE,
                DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT,
                DatabaseContract.ListContractEntry.COLUMN_NAME_DATE};
        String selection = DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(number)};
        return getReadableDatabase().query(DatabaseContract.ListContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
    }

    public void deleteList(int number) {
        String selection = DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(number)};
        getWritableDatabase().delete(DatabaseContract.ListContractEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateList(int oldNumber, int number, String value, String product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER, number);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE, value);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT, product);
        String selection = DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(oldNumber)};
        getWritableDatabase().update(DatabaseContract.ListContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    private void addUsage(String products, String prices) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS, products);
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES, prices);
        getWritableDatabase().insert(DatabaseContract.UsageContractEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getUsage(SQLiteDatabase database) {
        String[] list = {DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS,
                DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES};
        return database.query(DatabaseContract.UsageContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    private String getPricesForProducts(String products) {
        String[] results = {DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES};
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {products};
        Cursor cursor = getReadableDatabase().query(DatabaseContract.UsageContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }

    private boolean isProduct(String products) {
        String[] results = {DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS};
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {products};
        Cursor cursor = getReadableDatabase().query(DatabaseContract.UsageContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        cursor.close();
        return false;
    }

    private void updateUsage(String products, String prices) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES, prices);
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {products};
        getWritableDatabase().update(DatabaseContract.UsageContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public int getCommonPriceForProduct(String product) {
        String[] pricesRaw = getPricesForProducts(product).split("_");
        int[] prices = new int[preference.getPreferenceInt(KEY_SMART_PRICE)];
        int priceMax = Integer.MIN_VALUE, res = 0;
        for (int i = 0; i < prices.length; i++) {
            prices[i] = Integer.parseInt(pricesRaw[i]);
            priceMax = Math.max(priceMax, prices[i]);
            if (priceMax == prices[i]) {
                res = i;
                if (priceMax == 0) {
                    res = 0;
                }
            }
        }
        return res;
    }

    private void addProduct(String product, String price) {
        int roundedPrice = (int) Math.ceil(Double.parseDouble(price));
        int[] pricesGenerate = new int[PRICE_LIMIT];
        String stringBuilder = "";
        for (int i = 0; i < pricesGenerate.length; i++) {
            pricesGenerate[i] = 0;
            if (i == roundedPrice) {
                pricesGenerate[i]++;
            }
            stringBuilder = stringBuilder + new DecimalFormat("0.#").format(pricesGenerate[i]) + "_";
        }
        addUsage(product, stringBuilder.substring(0, stringBuilder.length() - 1));
    }

    public void updatePrices(final String products, final String price) {
        final int roundedPrice = (int) Math.ceil(Double.parseDouble(price));
        if (roundedPrice > 0) {
            handler.post(new Runnable() {
                public void run() {
                    if (isProduct(products)) {
                        String stringBuilder = "";
                        String[] pricesRaw = getPricesForProducts(products).split("_");
                        int[] pricesExisting = new int[preference.getPreferenceInt(KEY_SMART_PRICE)];
                        for (int i = 0; i < pricesExisting.length; i++) {
                            pricesExisting[i] = Integer.parseInt(pricesRaw[i]);
                            if (pricesExisting[i] == roundedPrice) {
                                pricesExisting[i]++;
                            }
                            stringBuilder = stringBuilder + pricesExisting[i] + "_";
                        }
                        updateUsage(products, stringBuilder);
                    } else {
                        addProduct(products, price);
                    }
                }
            });
        }
    }

    public void deleteUsage(SQLiteDatabase database, String product) {
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {product};
        database.delete(DatabaseContract.UsageContractEntry.TABLE_NAME, selection, selectionArgs);
    }

}
