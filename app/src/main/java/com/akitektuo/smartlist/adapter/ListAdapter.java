package com.akitektuo.smartlist.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.util.ListModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.akitektuo.smartlist.util.Constant.COLOR_BLACK;
import static com.akitektuo.smartlist.util.Constant.COLOR_BLUE;
import static com.akitektuo.smartlist.util.Constant.COLOR_GREEN;
import static com.akitektuo.smartlist.util.Constant.COLOR_ORANGE;
import static com.akitektuo.smartlist.util.Constant.COLOR_RED;
import static com.akitektuo.smartlist.util.Constant.COLOR_YELLOW;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL;
import static com.akitektuo.smartlist.util.Constant.KEY_COLOR;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;
import static com.akitektuo.smartlist.util.Constant.handler;
import static com.akitektuo.smartlist.util.Constant.preference;
import static com.akitektuo.smartlist.util.Constant.totalCount;

/**
 * Created by AoD Akitektuo on 22-Aug-17 at 19:27.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;
    private List<ListModel> listModels;
    private DatabaseHelper database;
    private TextView textTotal;

    public ListAdapter(Context context, List<ListModel> listModels, TextView textTotal) {
        this.context = context;
        this.listModels = listModels;
        database = new DatabaseHelper(context);
        this.textTotal = textTotal;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListModel listModel = listModels.get(position);
        holder.textNumber.setText(changeNumber(listModel.getNumber()));
        holder.editValue.setText(listModel.getValue());
        holder.textCurrency.setText(listModel.getCurrency());
        holder.editAutoProduct.setText(listModel.getProduct());
        holder.editAutoProduct.setSingleLine();
        switch (listModel.getButtonType()) {
            case 0:
                holder.buttonSave.setVisibility(View.VISIBLE);
                holder.buttonDelete.setVisibility(View.GONE);
                break;
            case 1:
                holder.buttonSave.setVisibility(View.GONE);
                holder.buttonDelete.setVisibility(View.VISIBLE);
                break;
            default:
                holder.buttonSave.setVisibility(View.VISIBLE);
                holder.buttonDelete.setVisibility(View.VISIBLE);
        }
        refreshList(holder.editAutoProduct);
        holder.editAutoProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                if (holder.editValue.getText().toString().isEmpty() && preference.getPreferenceBoolean(KEY_AUTO_FILL)) {
                    handler.post(new Runnable() {
                        public void run() {
                            int mostUsedPrice = database.getCommonPriceForProduct(adapterView.getItemAtPosition(i).toString());
                            if (mostUsedPrice != 0) {
                                holder.editValue.setText(String.valueOf(mostUsedPrice));
                            }
                        }
                    });
                }
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Item deleted...", Toast.LENGTH_SHORT).show();
                totalCount -= Double.parseDouble(listModels.get(holder.getAdapterPosition()).getValue());
                database.deleteList(holder.getAdapterPosition() + 1);
                listModels.remove(holder.getAdapterPosition());
                updateDatabase(holder.getAdapterPosition());
                notifyDataSetChanged();
                textTotal.setText(context.getString(R.string.total, new DecimalFormat("0.#").format(totalCount), preference.getPreferenceString(KEY_CURRENCY)));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                holder.buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.editValue.getText().toString().isEmpty() || holder.editAutoProduct.getText().toString().isEmpty()) {
                            Toast.makeText(context, "Fill in all fields.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (holder.editAutoProduct.getText().toString().substring(0, 1).equals("-")) {
                                holder.editValue.setText(holder.editAutoProduct.getText().toString());
                            }
                            int lastItem = listModels.size();
                            String value = holder.editValue.getText().toString();
                            String product = holder.editAutoProduct.getText().toString();
                            if (holder.getAdapterPosition() + 1 == lastItem) {
                                database.addList(lastItem, value, product,
                                        new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
                                totalCount += Double.parseDouble(value);
                                listModels.set(listModels.size() - 1, new ListModel(lastItem, value, preference.getPreferenceString(KEY_CURRENCY), product, 1));
                                listModels.add(new ListModel(listModels.size() + 1, "", preference.getPreferenceString(KEY_CURRENCY), "", 0));
                                notifyDataSetChanged();
                                textTotal.setText(context.getString(R.string.total, new DecimalFormat("0.#").format(totalCount), preference.getPreferenceString(KEY_CURRENCY)));
                                Toast.makeText(context, "Item saved...", Toast.LENGTH_SHORT).show();
                            } else {
                                database.updateList(holder.getAdapterPosition() + 1, listModel.getNumber(), value, product);
                                totalCount += Double.parseDouble(value) - Double.parseDouble(listModels.get(holder.getAdapterPosition()).getValue());
                                listModels.set(holder.getAdapterPosition(), new ListModel(listModel.getNumber(), value, preference.getPreferenceString(KEY_CURRENCY), product, 1));
                                notifyDataSetChanged();
                                textTotal.setText(context.getString(R.string.total, new DecimalFormat("0.#").format(totalCount), preference.getPreferenceString(KEY_CURRENCY)));
                                Toast.makeText(context, "Item updated...", Toast.LENGTH_SHORT).show();
                            }
                            database.updatePrices(holder.editAutoProduct.getText().toString(),
                                    holder.editValue.getText().toString());
                            refreshList(holder.editAutoProduct);
                        }
                    }
                });
            }
        }).start();
        holder.editValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (listModel.getButtonType() != 0) {
                    holder.buttonSave.setVisibility(View.VISIBLE);
                    holder.buttonDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.buttonDelete.setVisibility(View.GONE);
                }
            }
        });
        holder.editAutoProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (listModel.getButtonType() != 0) {
                    holder.buttonSave.setVisibility(View.VISIBLE);
                    holder.buttonDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.buttonDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    private String changeNumber(int num) {
        if (num < 10) {
            return " " + num;
        }
        return String.valueOf(num);
    }

    private void updateDatabase(int position) {
        int lastIndex = listModels.size();
        System.out.println(position + " " + lastIndex);
        for (int i = position; i < lastIndex; i++) {
            ListModel listModel = listModels.get(i);
            listModel.decrementNumber();
            System.out.println((i + 2) + " " + listModel.getNumber() + " " + listModel.getValue() + " " + listModel.getProduct());
            database.updateList(i + 2, listModel.getNumber(), listModel.getValue(), listModel.getProduct());
            listModels.set(i, listModel);
        }
    }

    private void refreshList(final AutoCompleteTextView autoCompleteTextView) {
        if (preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
            handler.post(new Runnable() {
                public void run() {
                    ArrayList<String> list = new ArrayList<>();
                    Cursor cursor = database.getUsage(database.getReadableDatabase());
                    if (cursor.moveToFirst()) {
                        do {
                            list.add(cursor.getString(0));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
                    autoCompleteTextView.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNumber;
        EditText editValue;
        TextView textCurrency;
        AutoCompleteTextView editAutoProduct;
        Button buttonDelete;
        Button buttonSave;

        ViewHolder(View view) {
            super(view);
            textNumber = (TextView) view.findViewById(R.id.text_item_number);
            editValue = (EditText) view.findViewById(R.id.edit_item_value);
            textCurrency = (TextView) view.findViewById(R.id.text_item_currency);
            editAutoProduct = (AutoCompleteTextView) view.findViewById(R.id.edit_auto_item_product);
            buttonDelete = (Button) view.findViewById(R.id.button_delete);
            buttonSave = (Button) view.findViewById(R.id.button_save);
            switch (preference.getPreferenceString(KEY_COLOR)) {
                case COLOR_BLUE:
                    textNumber.setTextColor(context.getResources().getColor(R.color.colorPrimaryBlue));
                    buttonDelete.setBackground(context.getDrawable(R.drawable.delete_blue));
                    buttonSave.setBackground(context.getDrawable(R.drawable.save_blue));
                    break;
                case COLOR_YELLOW:
                    textNumber.setTextColor(context.getResources().getColor(R.color.colorPrimaryYellow));
                    buttonDelete.setBackground(context.getDrawable(R.drawable.delete_yellow));
                    buttonSave.setBackground(context.getDrawable(R.drawable.save_yellow));
                    break;
                case COLOR_RED:
                    textNumber.setTextColor(context.getResources().getColor(R.color.colorPrimaryRed));
                    buttonDelete.setBackground(context.getDrawable(R.drawable.delete_red));
                    buttonSave.setBackground(context.getDrawable(R.drawable.save_red));
                    break;
                case COLOR_GREEN:
                    textNumber.setTextColor(context.getResources().getColor(R.color.colorPrimaryGreen));
                    buttonDelete.setBackground(context.getDrawable(R.drawable.delete_green));
                    buttonSave.setBackground(context.getDrawable(R.drawable.save_green));
                    break;
                case COLOR_ORANGE:
                    textNumber.setTextColor(context.getResources().getColor(R.color.colorPrimaryOrange));
                    buttonDelete.setBackground(context.getDrawable(R.drawable.delete_orange));
                    buttonSave.setBackground(context.getDrawable(R.drawable.save_orange));
                    break;
                case COLOR_BLACK:
                    textNumber.setTextColor(context.getResources().getColor(R.color.colorPrimaryBlack));
                    buttonDelete.setBackground(context.getDrawable(R.drawable.delete_black));
                    buttonSave.setBackground(context.getDrawable(R.drawable.save_black));
                    break;
            }
        }
    }

}
