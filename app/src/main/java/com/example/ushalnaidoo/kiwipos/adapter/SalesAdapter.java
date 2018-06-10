package com.example.ushalnaidoo.kiwipos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.R;
import com.example.ushalnaidoo.kiwipos.model.Sale;

import java.util.ArrayList;
import java.util.List;

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
            listItem = LayoutInflater.from(context).inflate(R.layout.sales_list_content,parent,false);

        Sale currentMovie = salesList.get(position);

        TextView details = listItem.findViewById(R.id.detail);
        details.setText(currentMovie.getNotes());

        TextView price = listItem.findViewById(R.id.amount);
        price.setText("$"+currentMovie.getAmount());

        return listItem;
    }
}