package com.example.ushalnaidoo.kiwipos;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.model.Categories;
import com.example.ushalnaidoo.kiwipos.model.Items;
import com.example.ushalnaidoo.kiwipos.server.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutDetailFragment extends Fragment {
  public CheckoutDetailFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.checkout_detail, container, false);
    Double total = 0.0;
    for (Map.Entry<Items.Item, Integer> entry : Items.CHECKOUT_ITEMS.entrySet()) {
      Items.Item item = entry.getKey();
      Integer value = entry.getValue();
      total += Double.valueOf(item.itemPrice)*value;
    }

    ((TextView) rootView.findViewById(R.id.total)).setText("Total: $" +  total);

    RecyclerView recyclerView =  rootView.findViewById(R.id.item_list);
    assert recyclerView != null;
//      new GetItemsForCategory(recyclerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    setupRecyclerView(recyclerView);
    return rootView;
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Items.CHECKOUT_ITEMS));
  }

  public static class SimpleItemRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private final Map<Items.Item, Integer> mValues;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Items.Item item = (Items.Item) view.getTag();
      }
    };

    SimpleItemRecyclerViewAdapter(Map<Items.Item, Integer> checkouts) {
      mValues = checkouts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.checkout_list_content, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
      //Get fields to display
      List<String> items = new ArrayList<>();
      List<String> prices = new ArrayList<>();
      for (Map.Entry<Items.Item, Integer> entry : mValues.entrySet()) {
        Items.Item item = entry.getKey();
        Integer amount = entry.getValue();
        items.add(String.valueOf(amount) + "x " + item.itemName);
        prices.add(String.valueOf(Double.valueOf(item.itemPrice)*amount));
      }

      holder.detail.setText(items.get(position));
      holder.amount.setText(String.format("$%s", prices.get(position)));
      holder.itemView.setTag(mValues.get(position));
      holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
      return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      final TextView detail;
      final TextView amount;

      ViewHolder(View view) {
        super(view);
        detail = view.findViewById(R.id.detail);
        amount = view.findViewById(R.id.amount);
      }
    }
  }
}
