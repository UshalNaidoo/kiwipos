package com.example.ushalnaidoo.kiwipos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.R;
import com.example.ushalnaidoo.kiwipos.enums.ItemType;
import com.example.ushalnaidoo.kiwipos.model.TenderedItem;
import com.example.ushalnaidoo.kiwipos.model.TenderedSale;
import com.example.ushalnaidoo.kiwipos.server.ConnectToServer;

import java.util.ArrayList;
import java.util.List;

public class BumpAdapter extends ArrayAdapter<TenderedSale> {
    BumpAdapter bumpAdapter;
    private Context context;
    private List<TenderedSale> salesList = new ArrayList<>();

    public BumpAdapter(Context context, List<TenderedSale> list) {
        super(context, 0 , list);
        this.context = context;
        salesList = list;
        bumpAdapter = this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(     Context.LAYOUT_INFLATER_SERVICE );
            v = inflater.inflate(R.layout.bump_grid_content, parent, false);
        } else {
            v = (View) convertView;
        }

        final TenderedSale sale = salesList.get(position);
        LinearLayout layout = v.findViewById(R.id.layout);
        layout.setBackgroundColor(Color.parseColor(sale.getTakeAway()? "#ACE7FF" : "#ECD4FF"));
        TextView header = v.findViewById(R.id.header);
        header.setText(sale.getTakeAway()? "AWAY "  +  sale.getNotes(): "IN " +  sale.getNotes());

        TextView items = v.findViewById(R.id.items);

        StringBuilder builder = new StringBuilder();
        for(TenderedItem item : sale.getItems()) {
            if(ItemType.ADDON.equals(item.getType())) {
                builder.append(" ").append(" - ").append(item.getProduct()).append('\n');
            } else {
                builder.append(item.getQuantity()).append(" x ").append(item.getProduct()).append('\n');
            }
        }

        items.setText(builder);
        Button bump = v.findViewById(R.id.button_bump);
        bump.setText(sale.getTime());

        bump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salesList.remove(sale);
                new BumpOrder(sale.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
            }
        });

        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private class BumpOrder extends AsyncTask<Integer, Integer, String> {
        String orderId;
        BumpOrder(String orderId) {
            this.orderId = orderId;
        }

        @Override
        protected String doInBackground(Integer... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            ConnectToServer.bumpOrders(orderId);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            bumpAdapter.notifyDataSetChanged();
        }
    }
}
