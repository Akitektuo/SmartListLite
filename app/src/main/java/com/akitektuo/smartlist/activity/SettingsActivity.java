package com.akitektuo.smartlist.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.akitektuo.smartlist.util.Constant.COLOR_BLACK;
import static com.akitektuo.smartlist.util.Constant.COLOR_BLUE;
import static com.akitektuo.smartlist.util.Constant.COLOR_GREEN;
import static com.akitektuo.smartlist.util.Constant.COLOR_ORANGE;
import static com.akitektuo.smartlist.util.Constant.COLOR_RED;
import static com.akitektuo.smartlist.util.Constant.COLOR_YELLOW;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_AED;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_AUD;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CAD;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CHF;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CNY;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_EUR;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_GBP;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_JPY;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_KRW;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_RON;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_RUB;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_SEK;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_USD;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL;
import static com.akitektuo.smartlist.util.Constant.KEY_COLOR;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;
import static com.akitektuo.smartlist.util.Constant.KEY_SMART_PRICE;
import static com.akitektuo.smartlist.util.Constant.KEY_STORAGE;
import static com.akitektuo.smartlist.util.Constant.PRICE_LIMIT;
import static com.akitektuo.smartlist.util.Constant.STORAGE_EXTERNAL;
import static com.akitektuo.smartlist.util.Constant.STORAGE_INTERNAL;
import static com.akitektuo.smartlist.util.Constant.preference;

public class SettingsActivity extends Activity implements View.OnClickListener {

    private SwitchButton switchRecommendations;
    private SwitchButton switchFill;
    private DatabaseHelper database;
    private RelativeLayout layoutHeader;
    private ImageView[] imageViews;
    private TextView textCurrency;
    private TextView textRecommendations;
    private TextView textFill;
    private TextView textColor;
    private TextView textExcel;
    private TextView textStorage;
    private ImageView imageCurrency;
    private ImageView imageRecommendation;
    private ImageView imageFill;
    private ImageView imageColor;
    private ImageView imageExcel;
    private ImageView imageStorage;
    private Drawable drawableInternal;
    private Drawable drawableExternal;
    private File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        layoutHeader = (RelativeLayout) findViewById(R.id.layout_settings_header);
        imageViews = new ImageView[6];
        imageViews[0] = (ImageView) findViewById(R.id.image_settings_0);
        imageViews[1] = (ImageView) findViewById(R.id.image_settings_1);
        imageViews[2] = (ImageView) findViewById(R.id.image_settings_2);
        imageViews[3] = (ImageView) findViewById(R.id.image_settings_3);
        imageViews[4] = (ImageView) findViewById(R.id.image_settings_4);
        imageViews[5] = (ImageView) findViewById(R.id.image_settings_5);
        textCurrency = (TextView) findViewById(R.id.text_settings_currency);
        textRecommendations = (TextView) findViewById(R.id.text_settings_recommendations);
        textFill = (TextView) findViewById(R.id.text_settings_fill);
        textColor = (TextView) findViewById(R.id.text_settings_color);
        textStorage = (TextView) findViewById(R.id.text_settings_storage);
        textExcel = (TextView) findViewById(R.id.text_settings_excel);
        imageCurrency = (ImageView) findViewById(R.id.image_settings_currency);
        imageRecommendation = (ImageView) findViewById(R.id.image_settings_recommendations);
        imageFill = (ImageView) findViewById(R.id.image_settings_fill);
        imageColor = (ImageView) findViewById(R.id.image_settings_color);
        imageStorage = (ImageView) findViewById(R.id.image_settings_storage);
        imageExcel = (ImageView) findViewById(R.id.image_settings_excel);
        LinearLayout layoutColor = (LinearLayout) findViewById(R.id.layout_color);
        database = new DatabaseHelper(this);
        findViewById(R.id.button_back).setOnClickListener(this);
        findViewById(R.id.layout_currency).setOnClickListener(this);
        switchRecommendations = (SwitchButton) findViewById(R.id.switch_settings_recommendations);
        switchRecommendations.setChecked(preference.getPreferenceBoolean(KEY_RECOMMENDATIONS));
        switchRecommendations.setOnClickListener(this);
        findViewById(R.id.layout_recommendations).setOnClickListener(this);
        switchFill = (SwitchButton) findViewById(R.id.switch_settings_fill);
        switchFill.setChecked(preference.getPreferenceBoolean(KEY_AUTO_FILL));
        switchFill.setOnClickListener(this);
        findViewById(R.id.layout_fill).setOnClickListener(this);
        layoutColor.setOnClickListener(this);
        findViewById(R.id.layout_storage).setOnClickListener(this);
        findViewById(R.id.layout_excel).setOnClickListener(this);
        refreshForColor(preference.getPreferenceString(KEY_COLOR));
        ActivityCompat.requestPermissions(SettingsActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            imageViews[2].setVisibility(View.GONE);
            layoutColor.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ListActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                startActivity(new Intent(this, ListActivity.class));
                finish();
                break;
            case R.id.layout_currency:
                AlertDialog.Builder builderCurrency = new AlertDialog.Builder(this);
                builderCurrency.setTitle("Select currency");
                builderCurrency.setItems(R.array.currency, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String currency = "";
                        switch (i) {
                            case 0:
                                currency = CURRENCY_RON;
                                break;
                            case 1:
                                currency = CURRENCY_USD;
                                break;
                            case 2:
                                currency = CURRENCY_EUR;
                                break;
                            case 3:
                                currency = CURRENCY_JPY;
                                break;
                            case 4:
                                currency = CURRENCY_GBP;
                                break;
                            case 5:
                                currency = CURRENCY_AUD;
                                break;
                            case 6:
                                currency = CURRENCY_CAD;
                                break;
                            case 7:
                                currency = CURRENCY_CHF;
                                break;
                            case 8:
                                currency = CURRENCY_CNY;
                                break;
                            case 9:
                                currency = CURRENCY_RUB;
                                break;
                            case 10:
                                currency = CURRENCY_KRW;
                                break;
                            case 11:
                                currency = CURRENCY_SEK;
                                break;
                            case 12:
                                currency = CURRENCY_AED;
                                break;
                        }
                        preference.setPreference(KEY_CURRENCY, currency);
                        Toast.makeText(getApplicationContext(), "Currency set to " + currency + ".", Toast.LENGTH_SHORT).show();
                    }
                });
                builderCurrency.setNeutralButton("Cancel", null);
                AlertDialog alertDialogCurrency = builderCurrency.create();
                alertDialogCurrency.show();
                break;
            case R.id.switch_settings_recommendations:
                preference.setPreference(KEY_RECOMMENDATIONS, switchRecommendations.isChecked());
                break;
            case R.id.layout_recommendations:
                AlertDialog.Builder builderRecommendations = new AlertDialog.Builder(this);
                builderRecommendations.setTitle("Select product to delete");
                List<String> listProducts = new ArrayList<>();
                Cursor cursorProducts = database.getUsage(database.getReadableDatabase());
                if (cursorProducts.moveToFirst()) {
                    do {
                        listProducts.add(cursorProducts.getString(0));
                    } while (cursorProducts.moveToNext());
                }
                final String[] arrayProducts = listProducts.toArray(new String[listProducts.size()]);
                builderRecommendations.setItems(arrayProducts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.deleteUsage(database.getWritableDatabase(), arrayProducts[i]);
                        Toast.makeText(getApplicationContext(), "Successfully deleted " + arrayProducts[i] + " product.", Toast.LENGTH_SHORT).show();
                    }
                });
                builderRecommendations.setNeutralButton("Cancel", null);
                AlertDialog alertDialogRecommendations = builderRecommendations.create();
                alertDialogRecommendations.show();
                break;
            case R.id.switch_settings_fill:
                preference.setPreference(KEY_AUTO_FILL, switchFill.isChecked());
                break;
            case R.id.layout_fill:
                AlertDialog.Builder builderFill = new AlertDialog.Builder(this);
                View viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_fill, null);
                final TextView textDialog = (TextView) viewDialog.findViewById(R.id.text_dialog_limit);
                final EditText editLimit = (EditText) viewDialog.findViewById(R.id.edit_dialog_limit);
                switch (preference.getPreferenceString(KEY_COLOR)) {
                    case COLOR_BLUE:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlue));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlue));
                        break;
                    case COLOR_YELLOW:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryYellow));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryYellow));
                        break;
                    case COLOR_RED:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryRed));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryRed));
                        break;
                    case COLOR_GREEN:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryGreen));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryGreen));
                        break;
                    case COLOR_ORANGE:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryOrange));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryOrange));
                        break;
                    case COLOR_BLACK:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlack));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlack));
                        break;
                }
                editLimit.setText(String.valueOf(preference.getPreferenceInt(KEY_SMART_PRICE) - 1));
                builderFill.setView(viewDialog);
                builderFill.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String limit = editLimit.getText().toString();
                        if (limit.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Set the limit.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Integer.parseInt(limit) < PRICE_LIMIT) {
                                preference.setPreference(KEY_SMART_PRICE, Integer.parseInt(limit) + 1);
                            } else {
                                Toast.makeText(getApplicationContext(), "Limit too high.", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getApplicationContext(), "Limit set to " + limit + ".", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builderFill.setNeutralButton("Cancel", null);
                builderFill.show();
                break;
            case R.id.layout_color:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AlertDialog.Builder builderColor = new AlertDialog.Builder(this);
                    builderColor.setTitle("Select color");
                    builderColor.setItems(R.array.color, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String color = null;
                            switch (i) {
                                case 0:
                                    color = COLOR_BLUE;
                                    break;
                                case 1:
                                    color = COLOR_YELLOW;
                                    break;
                                case 2:
                                    color = COLOR_RED;
                                    break;
                                case 3:
                                    color = COLOR_GREEN;
                                    break;
                                case 4:
                                    color = COLOR_ORANGE;
                                    break;
                                case 5:
                                    color = COLOR_BLACK;
                                    break;
                            }
                            refreshForColor(color);
                            preference.setPreference(KEY_COLOR, color);
                            Toast.makeText(getApplicationContext(), "Color set to " + color + ".", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builderColor.setNeutralButton("Cancel", null);
                    AlertDialog alertDialogColor = builderColor.create();
                    alertDialogColor.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Android 6.0 Marshmallow or higher required", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_storage:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String storageLocation = "Storage switched to %1$s";
                    switch (preference.getPreferenceInt(KEY_STORAGE)) {
                        case STORAGE_INTERNAL:
                            preference.setPreference(KEY_STORAGE, STORAGE_EXTERNAL);
                            storageLocation = String.format(storageLocation, "external storage");
                            break;
                        case STORAGE_EXTERNAL:
                            preference.setPreference(KEY_STORAGE, STORAGE_INTERNAL);
                            storageLocation = String.format(storageLocation, "internal storage");
                            break;
                    }
                    Toast.makeText(this, storageLocation, Toast.LENGTH_SHORT).show();
                    changeStorageSettings();
                } else {
                    Toast.makeText(this, "Micro SD card not detected", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_excel:
                exportToExcel(database.getList());
                AlertDialog.Builder builderOpenXls = new AlertDialog.Builder(this);
                builderOpenXls.setTitle("Open Excel File");
                builderOpenXls.setMessage("Are you sure you want to open the file now? You can see it in Internal Storage -> SmartList - > SmartList.xls");
                builderOpenXls.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openGeneratedFile();
                    }
                });
                builderOpenXls.setNegativeButton("Cancel", null);
                AlertDialog dialogOpenXls = builderOpenXls.create();
                dialogOpenXls.show();
        }
    }

    private void changeStorageSettings() {
        switch (preference.getPreferenceInt(KEY_STORAGE)) {
            case STORAGE_INTERNAL:
                imageStorage.setImageDrawable(drawableInternal);
                path = Environment.getExternalStorageDirectory();
                break;
            case STORAGE_EXTERNAL:
                imageStorage.setImageDrawable(drawableExternal);
                path = Environment.getExternalStorageDirectory();
                break;
        }
    }

    private void refreshForColor(String color) {
        switch (color) {
            case COLOR_BLUE:
                setColor(R.style.Theme_Blue, R.color.colorPrimaryBlue, R.color.colorPrimaryDarkBlue);
                imageCurrency.setImageDrawable(getDrawable(R.drawable.currency_blue));
                imageRecommendation.setImageDrawable(getDrawable(R.drawable.recommendation_blue));
                imageFill.setImageDrawable(getDrawable(R.drawable.fill_blue));
                imageColor.setImageDrawable(getDrawable(R.drawable.color_blue));
                imageExcel.setImageDrawable(getDrawable(R.drawable.excel_blue));
                drawableInternal = getDrawable(R.drawable.internal_storage_blue);
                drawableExternal = getDrawable(R.drawable.external_storage_blue);
                // reset image
                break;
            case COLOR_YELLOW:
                setColor(R.style.Theme_Yellow, R.color.colorPrimaryYellow, R.color.colorPrimaryDarkYellow);
                imageCurrency.setImageDrawable(getDrawable(R.drawable.currency_yellow));
                imageRecommendation.setImageDrawable(getDrawable(R.drawable.recommendation_yellow));
                imageFill.setImageDrawable(getDrawable(R.drawable.fill_yellow));
                imageColor.setImageDrawable(getDrawable(R.drawable.color_yellow));
                imageExcel.setImageDrawable(getDrawable(R.drawable.excel_yellow));
                drawableInternal = getDrawable(R.drawable.internal_storage_yellow);
                drawableExternal = getDrawable(R.drawable.external_storage_yellow);
                break;
            case COLOR_RED:
                setColor(R.style.Theme_Red, R.color.colorPrimaryRed, R.color.colorPrimaryDarkRed);
                imageCurrency.setImageDrawable(getDrawable(R.drawable.currency_red));
                imageRecommendation.setImageDrawable(getDrawable(R.drawable.recommendation_red));
                imageFill.setImageDrawable(getDrawable(R.drawable.fill_red));
                imageColor.setImageDrawable(getDrawable(R.drawable.color_red));
                imageExcel.setImageDrawable(getDrawable(R.drawable.excel_red));
                drawableInternal = getDrawable(R.drawable.internal_storage_red);
                drawableExternal = getDrawable(R.drawable.external_storage_red);
                break;
            case COLOR_GREEN:
                setColor(R.style.Theme_Green, R.color.colorPrimaryGreen, R.color.colorPrimaryDarkGreen);
                imageCurrency.setImageDrawable(getDrawable(R.drawable.currency_green));
                imageRecommendation.setImageDrawable(getDrawable(R.drawable.recommendation_green));
                imageFill.setImageDrawable(getDrawable(R.drawable.fill_green));
                imageColor.setImageDrawable(getDrawable(R.drawable.color_green));
                imageExcel.setImageDrawable(getDrawable(R.drawable.excel_green));
                drawableInternal = getDrawable(R.drawable.internal_storage_green);
                drawableExternal = getDrawable(R.drawable.external_storage_green);
                break;
            case COLOR_ORANGE:
                setColor(R.style.Theme_Orange, R.color.colorPrimaryOrange, R.color.colorPrimaryDarkOrange);
                imageCurrency.setImageDrawable(getDrawable(R.drawable.currency_orange));
                imageRecommendation.setImageDrawable(getDrawable(R.drawable.recommendation_orange));
                imageFill.setImageDrawable(getDrawable(R.drawable.fill_orange));
                imageColor.setImageDrawable(getDrawable(R.drawable.color_orange));
                imageExcel.setImageDrawable(getDrawable(R.drawable.excel_orange));
                drawableInternal = getDrawable(R.drawable.internal_storage_orange);
                drawableExternal = getDrawable(R.drawable.external_storage_orange);
                break;
            case COLOR_BLACK:
                setColor(R.style.Theme_Black, R.color.colorPrimaryBlack, R.color.colorPrimaryDarkBlack);
                imageCurrency.setImageDrawable(getDrawable(R.drawable.currency_black));
                imageRecommendation.setImageDrawable(getDrawable(R.drawable.recommendation_black));
                imageFill.setImageDrawable(getDrawable(R.drawable.fill_black));
                imageColor.setImageDrawable(getDrawable(R.drawable.color_black));
                imageExcel.setImageDrawable(getDrawable(R.drawable.excel_black));
                drawableInternal = getDrawable(R.drawable.internal_storage_black);
                drawableExternal = getDrawable(R.drawable.external_storage_black);
                break;
        }
        changeStorageSettings();
    }

    private void setColor(int theme, int colorPrimary, int colorPrimaryDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setTheme(theme);
            getWindow().setStatusBarColor(getColor(colorPrimaryDark));
            layoutHeader.setBackgroundColor(getResources().getColor(colorPrimary));
            for (ImageView x : imageViews) {
                x.setBackgroundColor(getResources().getColor(colorPrimary));
            }
            textCurrency.setTextColor(getResources().getColor(colorPrimary));
            textRecommendations.setTextColor(getResources().getColor(colorPrimary));
            textFill.setTextColor(getResources().getColor(colorPrimary));
            textColor.setTextColor(getResources().getColor(colorPrimary));
            textStorage.setTextColor(getResources().getColor(colorPrimary));
            textExcel.setTextColor(getResources().getColor(colorPrimary));
            switchRecommendations.setTintColor(getResources().getColor(colorPrimary));
            switchFill.setTintColor(getResources().getColor(colorPrimary));
        }
    }

    private void exportToExcel(Cursor cursor) {
        File file = new File(path + File.separator + "SmartList", "SmartList_" + new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + ".xls");
        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                Toast.makeText(getApplicationContext(), "Smart list failed to generate the file, please check the permissions or switch to internal storage.", Toast.LENGTH_SHORT).show();
            }
        }

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("NewList", 0);

            try {
                Toast.makeText(getApplicationContext(), "Excel generated successfully.", Toast.LENGTH_SHORT).show();
                sheet.addCell(new Label(0, 0, "Price")); // column and row
                sheet.addCell(new Label(1, 0, "Product"));
                sheet.addCell(new Label(2, 0, "Time added"));
                if (cursor.moveToFirst()) {
                    do {
                        int i = cursor.getPosition() + 1;
                        sheet.addCell(new Label(0, i, cursor.getString(1)));
                        sheet.addCell(new Label(1, i, cursor.getString(2)));
                        sheet.addCell(new Label(2, i, cursor.getString(3)));
                    } while (cursor.moveToNext());
                }
                //closing cursor
                cursor.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openGeneratedFile() {
        File file = new File(path + File.separator + "SmartList", "SmartList_" + new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + ".xls");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
        }
    }
}
