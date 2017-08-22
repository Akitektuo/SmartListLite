package com.akitektuo.smartlist.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.ListAdapter;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.util.ListModel;
import com.akitektuo.smartlist.util.Preference;

import java.util.ArrayList;
import java.util.List;

import static com.akitektuo.smartlist.util.Constant.COLOR_BLACK;
import static com.akitektuo.smartlist.util.Constant.COLOR_BLUE;
import static com.akitektuo.smartlist.util.Constant.COLOR_GREEN;
import static com.akitektuo.smartlist.util.Constant.COLOR_ORANGE;
import static com.akitektuo.smartlist.util.Constant.COLOR_RED;
import static com.akitektuo.smartlist.util.Constant.COLOR_YELLOW;
import static com.akitektuo.smartlist.util.Constant.KEY_COLOR;
import static com.akitektuo.smartlist.util.Constant.KEY_CREATED;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.handler;
import static com.akitektuo.smartlist.util.Constant.preference;
import static com.akitektuo.smartlist.util.Constant.totalCount;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private static DatabaseHelper database;
    private RecyclerView list;
    private TextView textResult;
    private RelativeLayout layoutHeader;
    private List<ListModel> listModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        preference = new Preference(this);
        if (!preference.getPreferenceBoolean(KEY_CREATED)) {
            preference.setDefault();
        }
        database = new DatabaseHelper(this);
        list = (RecyclerView) findViewById(R.id.list_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(linearLayoutManager);
        textResult = (TextView) findViewById(R.id.text_result);
        layoutHeader = (RelativeLayout) findViewById(R.id.layout_list_header);
        findViewById(R.id.button_delete_all).setOnClickListener(this);
        findViewById(R.id.button_settings).setOnClickListener(this);
        refreshForColor(preference.getPreferenceString(KEY_COLOR));
        totalCount = 0;
        listModels = new ArrayList<>();
        Cursor cursor = database.getList();
        if (cursor.moveToFirst()) {
            do {
                listModels.add(new ListModel(cursor.getInt(0), cursor.getString(1), preference.getPreferenceString(KEY_CURRENCY), cursor.getString(2), 1));
                totalCount += Integer.parseInt(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        listModels.add(new ListModel(listModels.size() + 1, "", preference.getPreferenceString(KEY_CURRENCY), "", 0));
        list.setAdapter(new ListAdapter(this, listModels, textResult));
        textResult.setText(getString(R.string.total, totalCount, preference.getPreferenceString(KEY_CURRENCY)));
    }

    private void deleteAllItems() {
        handler.post(new Runnable() {
            public void run() {
                for (int i = 1; i < database.getListNumberNew(); i++) {
                    database.deleteList(i);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_delete_all:
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(this);
                builderDelete.setTitle("Delete All Items");
                builderDelete.setMessage("Are you sure you want to delete all items?");
                builderDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAllItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listModels.clear();
                                listModels.add(new ListModel(listModels.size() + 1, "", preference.getPreferenceString(KEY_CURRENCY), "", 0));
                                totalCount = 0;
                                textResult.setText(getBaseContext().getString(R.string.total, totalCount, preference.getPreferenceString(KEY_CURRENCY)));
                                list.getAdapter().notifyDataSetChanged();
                            }
                        }, 500);
                        Toast.makeText(getApplicationContext(), "All items deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
                builderDelete.setNegativeButton("Cancel", null);
                AlertDialog dialogDelete = builderDelete.create();
                dialogDelete.show();
                break;
            case R.id.button_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
        }
    }

    private void refreshForColor(String color) {
        switch (color) {
            case COLOR_BLUE:
                setColor(R.style.Theme_Blue, R.color.colorPrimaryBlue, R.color.colorPrimaryDarkBlue);
                break;
            case COLOR_YELLOW:
                setColor(R.style.Theme_Yellow, R.color.colorPrimaryYellow, R.color.colorPrimaryDarkYellow);
                break;
            case COLOR_RED:
                setColor(R.style.Theme_Red, R.color.colorPrimaryRed, R.color.colorPrimaryDarkRed);
                break;
            case COLOR_GREEN:
                setColor(R.style.Theme_Green, R.color.colorPrimaryGreen, R.color.colorPrimaryDarkGreen);
                break;
            case COLOR_ORANGE:
                setColor(R.style.Theme_Orange, R.color.colorPrimaryOrange, R.color.colorPrimaryDarkOrange);
                break;
            case COLOR_BLACK:
                setColor(R.style.Theme_Black, R.color.colorPrimaryBlack, R.color.colorPrimaryDarkBlack);
                break;
        }
    }

    private void setColor(int theme, int colorPrimary, int colorPrimaryDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setTheme(theme);
            getWindow().setStatusBarColor(getColor(colorPrimaryDark));
            layoutHeader.setBackgroundColor(getResources().getColor(colorPrimary));
            textResult.setBackgroundColor(getResources().getColor(colorPrimary));
        }
    }
}