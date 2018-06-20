package com.example.ushalnaidoo.kiwipos.apps.Bump;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.GridView;

import com.example.ushalnaidoo.kiwipos.R;
import com.example.ushalnaidoo.kiwipos.adapter.BumpAdapter;
import com.example.ushalnaidoo.kiwipos.enums.ItemType;
import com.example.ushalnaidoo.kiwipos.enums.SaleStatus;
import com.example.ushalnaidoo.kiwipos.model.TenderedItem;
import com.example.ushalnaidoo.kiwipos.model.TenderedSale;
import com.example.ushalnaidoo.kiwipos.server.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BumpScreen extends Activity {

    final List<TenderedSale> sales = new ArrayList<>();
    BumpAdapter adapter;
    GridView gv;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_bump_screen);
        gv= findViewById(R.id.gridview);

        adapter = new BumpAdapter(this, sales);
        gv.setAdapter(adapter);
        startRepeatingTask();
    }

    final int INTERVAL = 1000 * 5;
    final Handler mHandler = new Handler();
    final Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {

            new GetTenderedSales(activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTenderedSales extends AsyncTask<Integer, Integer, String> {
        Activity activity;

        GetTenderedSales(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Integer... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            if (sales.size() != ConnectToServer.getTenderedSalesCount()) {
                return ConnectToServer.getTenderedSales();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            JSONObject json;
            JSONArray jsonPosts;
            try {
                sales.clear();
                json = new JSONObject(result);
                jsonPosts = json.getJSONArray(ConnectToServer.SALES);
                if (jsonPosts != null && jsonPosts.length() > 0) {
                    for (int i = 0; i < jsonPosts.length(); i++) {
                        JSONObject jsonObject = jsonPosts.getJSONObject(i);
                        TenderedSale sale = new TenderedSale(jsonObject.getString("_id"),
                                jsonObject.getString("created"), jsonObject.getString("notes"),
                                jsonObject.getDouble("amount"), jsonObject.getString("takeaway").equals("1"),
                                SaleStatus.TENDERED);
                        JSONArray jsonarray = jsonObject.getJSONArray("items");
                        List<TenderedItem> tenderedItems = new ArrayList<>();
                        for (int j = 0; j < jsonarray.length(); j++) {
                            TenderedItem tenderedItem = new TenderedItem();
                            JSONObject jsonobject = jsonarray.getJSONObject(j);
                            tenderedItem.setProduct(jsonobject.getString("product"));
                            tenderedItem.setQuantity(jsonobject.getInt("quantity"));
                            tenderedItem.setType(ItemType.values()[jsonobject.getInt("type")]);
                            tenderedItems.add(tenderedItem);
                        }
                        sale.setItems(tenderedItems);
                        sales.add(sale);
                    }
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.e("Error", "error getting categories ", e);
            }
        }
    }
}
