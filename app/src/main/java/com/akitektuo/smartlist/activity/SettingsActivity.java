package com.akitektuo.smartlist.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.List;

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
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL_WANTED;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;
import static com.akitektuo.smartlist.util.Constant.KEY_SMART_PRICE;
import static com.akitektuo.smartlist.util.Constant.PRICE_LIMIT;
import static com.akitektuo.smartlist.util.Constant.preference;

public class SettingsActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private SwitchButton switchRecommendations;
    private SwitchButton switchFill;
    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        database = new DatabaseHelper(this);
        findViewById(R.id.button_back).setOnClickListener(this);
        findViewById(R.id.layout_currency).setOnClickListener(this);
        switchRecommendations = (SwitchButton) findViewById(R.id.switch_settings_recommendations);
        switchRecommendations.setChecked(preference.getPreferenceBoolean(KEY_RECOMMENDATIONS));
        switchRecommendations.setOnCheckedChangeListener(this);
        findViewById(R.id.layout_products).setOnClickListener(this);
        switchFill = (SwitchButton) findViewById(R.id.switch_settings_fill);
        switchFill.setChecked(preference.getPreferenceBoolean(KEY_AUTO_FILL));
        switchFill.setOnCheckedChangeListener(this);
        findViewById(R.id.layout_limit).setOnClickListener(this);
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
            case R.id.layout_products:
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
            case R.id.layout_limit:
                AlertDialog.Builder builderFill = new AlertDialog.Builder(this);
                View viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_fill, null);
                final EditText editLimit = (EditText) viewDialog.findViewById(R.id.edit_dialog_limit);
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
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_settings_recommendations:
                preference.setPreference(KEY_RECOMMENDATIONS, isChecked);
                if (isChecked && preference.getPreferenceBoolean(KEY_AUTO_FILL_WANTED)) {
                    switchFill.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                } else if (!isChecked) {
                    switchFill.setChecked(false);
                    preference.setPreference(KEY_AUTO_FILL, false);
                }
                break;
            case R.id.switch_settings_fill:
                preference.setPreference(KEY_AUTO_FILL, isChecked);
                if (isChecked && !preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
                    switchRecommendations.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                    preference.setPreference(KEY_AUTO_FILL_WANTED, true);
                } else if (isChecked) {
                    preference.setPreference(KEY_AUTO_FILL_WANTED, true);
                } else if (preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
                    preference.setPreference(KEY_AUTO_FILL_WANTED, false);
                }
                break;
        }
    }
}
