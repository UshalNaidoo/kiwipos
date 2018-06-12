package com.example.ushalnaidoo.kiwipos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.R;
import com.example.ushalnaidoo.kiwipos.model.Sale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesAdapter extends ArrayAdapter<Sale> {
    private Context context;
    private List<Sale> salesList = new ArrayList<>();

    public SalesAdapter(Context context, List<Sale> list) {
        super(context, 0 , list);
        this.context = context;
        salesList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.sales_list_content, parent,false);


        Sale sale = salesList.get(position);
        RelativeLayout layout = listItem.findViewById(R.id.RelativeLayout01);
        layout.setBackgroundColor(Color.parseColor(sale.getTakeAway() ? "#ACE7FF" : "#ECD4FF"));
        TextView time = listItem.findViewById(R.id.time);
        time.setText(sale.getTime());
        TextView details = listItem.findViewById(R.id.detail);
        details.setText(sale.getNotes());
        TextView price = listItem.findViewById(R.id.amount);
        String priceValue = "$" + String.format(Locale.getDefault(),"%.2f", sale.getAmount());
        price.setText(priceValue);

        return listItem;
    }
}