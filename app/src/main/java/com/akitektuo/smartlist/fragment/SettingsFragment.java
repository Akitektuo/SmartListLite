package com.akitektuo.smartlist.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.database.DatabaseHelper;

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
import static com.akitektuo.smartlist.util.Constant.preference;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private DatabaseHelper database;
    private Switch switchRecommendations;
    private Switch switchFill;

    public SettingsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switchRecommendations = (Switch) getActivity().findViewById(R.id.switch_light_recommendations);
        switchFill = (Switch) getActivity().findViewById(R.id.switch_light_fill);

        switchRecommendations.setChecked(preference.getPreferenceBoolean(KEY_RECOMMENDATIONS));
        switchFill.setChecked(preference.getPreferenceBoolean(KEY_AUTO_FILL));

        switchRecommendations.setOnCheckedChangeListener(this);
        switchFill.setOnCheckedChangeListener(this);

        getActivity().findViewById(R.id.layout_light_currency).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_products).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_limit).setOnClickListener(this);

        database = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_light_products:
                removeProduct();
                break;
            case R.id.layout_light_limit:
                setLimit();
                break;
            case R.id.layout_light_currency:
                changeCurrency();
                break;
        }
    }

    private void removeProduct() {
        AlertDialog.Builder builderRecommendations = new AlertDialog.Builder(getContext());
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
            }
        });
        builderRecommendations.setNeutralButton("Cancel", null);
        AlertDialog alertDialogRecommendations = builderRecommendations.create();
        alertDialogRecommendations.show();
    }

    private void setLimit() {
        AlertDialog.Builder builderFill = new AlertDialog.Builder(getContext());
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_light_fill, null);
        final EditText editLimit = (EditText) viewDialog.findViewById(R.id.edit_dialog_light_limit);
        final SeekBar barLimit = (SeekBar) viewDialog.findViewById(R.id.bar_light_limit);
        barLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    editLimit.setText(String.valueOf(i + 1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        barLimit.setProgress(preference.getPreferenceInt(KEY_SMART_PRICE) - 2);
        editLimit.setText(String.valueOf(preference.getPreferenceInt(KEY_SMART_PRICE) - 1));
        editLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String limitInput = editable.toString();
                if (limitInput.isEmpty()) {
                    barLimit.setProgress(0);
                } else if (checkInteger(limitInput)) {
                    if (limitInput.equals("0")) {
                        barLimit.setProgress(0);
                    } else if (Integer.parseInt(limitInput) > 1000) {
                        barLimit.setProgress(999);
                    } else {
                        barLimit.setProgress(Integer.parseInt(limitInput));
                    }
                }
            }
        });
        builderFill.setView(viewDialog);
        builderFill.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preference.setPreference(KEY_SMART_PRICE, barLimit.getProgress() + 1);
            }
        });
        builderFill.setNeutralButton("Cancel", null);
        builderFill.show();
    }

    private void changeCurrency() {
        AlertDialog.Builder builderCurrency = new AlertDialog.Builder(getContext());
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
            }
        });
        builderCurrency.setNeutralButton("Cancel", null);
        AlertDialog alertDialogCurrency = builderCurrency.create();
        alertDialogCurrency.show();
    }

    private boolean checkInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_light_recommendations:
                preference.setPreference(KEY_RECOMMENDATIONS, isChecked);
                if (isChecked && preference.getPreferenceBoolean(KEY_AUTO_FILL_WANTED)) {
                    switchFill.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                } else if (!isChecked) {
                    switchFill.setChecked(false);
                    preference.setPreference(KEY_AUTO_FILL, false);
                }
                break;
            case R.id.switch_light_fill:
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
