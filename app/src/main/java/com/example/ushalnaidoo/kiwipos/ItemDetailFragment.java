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
import java.util.List;

public class ItemDetailFragment extends Fragment {

  public static final String CATEGORY_ID = "category_id";
  private Categories.Category mItem;

  public ItemDetailFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments().containsKey(CATEGORY_ID)) {
      mItem = Categories.HASH_MAP.get(getArguments().getString(CATEGORY_ID));
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.item_detail, container, false);

    if (mItem != null) {
      ((TextView) rootView.findViewById(R.id.tvTitle)).setText(mItem.categoryName);

      RecyclerView recyclerView =  rootView.findViewById(R.id.item_list);
      assert recyclerView != null;
      RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
      recyclerView.setLayoutManager(mLayoutManager);
      Items.ITEMS.clear();
      new GetItemsForCategory(recyclerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    return rootView;
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, Items.ITEMS));
  }

  public static class SimpleItemRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private final ItemDetailFragment mParentActivity;
    private final List<Items.Item> mValues;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Items.Item item = (Items.Item) view.getTag();
        Items.addToCheckout(item);
        Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragment.CATEGORY_ID, item.id);
        CheckoutDetailFragment checkoutDetailFragment = new CheckoutDetailFragment();
        checkoutDetailFragment.setArguments(arguments);
        mParentActivity.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.checkout_detail_container, checkoutDetailFragment)
                .commit();
      }
    };

    SimpleItemRecyclerViewAdapter(ItemDetailFragment parent, List<Items.Item> items) {
      mValues = items;
      mParentActivity = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_list_content, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
      holder.itemName.setText(mValues.get(position).itemName);
      holder.price.setText(String.format("$%s", mValues.get(position).itemPrice));
      holder.itemView.setTag(mValues.get(position));
      holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
      return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      final TextView itemName;
      final TextView price;

      ViewHolder(View view) {
        super(view);
        itemName = view.findViewById(R.id.itemName);
        price = view.findViewById(R.id.price);
      }
    }
  }
  @SuppressLint("StaticFieldLeak")
  private class GetItemsForCategory extends AsyncTask<Integer, Integer, String> {
    RecyclerView recyclerView;

    GetItemsForCategory(RecyclerView recyclerView) {
      this.recyclerView = recyclerView;
    }
    @Override
    protected String doInBackground(Integer... params) {
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//      return Categories.readFromCache(mItem) == null ? ConnectToServer.getItemsForCategory(mItem.id) : Categories.readFromCache(mItem);
      return ConnectToServer.getItemsForCategory(mItem.id);
    }

    @Override
    protected void onPostExecute(String result) {
      JSONObject json;
      JSONArray jsonPosts;
      try {
        json = new JSONObject(result);
        jsonPosts = json.getJSONArray(ConnectToServer.ITEMS);
        if (jsonPosts != null) {
          List<Items.Item> itemsForCache = new ArrayList<>();
          for (int i = 0; i < jsonPosts.length(); i++) {
            JSONObject jsonObject = jsonPosts.getJSONObject(i);
            Items.Item item = Items.createItem(jsonObject.getString("_id"), jsonObject.getString("name"), jsonObject.getString("price"));
            Items.addItem(item);
            itemsForCache.add(item);
          }
          Categories.addToCache(mItem, itemsForCache);
          setupRecyclerView(recyclerView);
        }
      }
      catch (JSONException e) {
        Log.e("Error", "error getting categories ", e);
      }
    }
  }
}
