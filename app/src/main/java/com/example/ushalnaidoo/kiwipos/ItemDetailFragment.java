package com.example.ushalnaidoo.kiwipos;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

  public class SimpleItemRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private final ItemDetailFragment mParentActivity;
    private final List<Items.Item> mValues;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(final View view) {
        Items.Item item = (Items.Item) view.getTag();
        if (!item.hasSubItems) {
          Items.addToCheckout(item);
          Bundle arguments = new Bundle();
          arguments.putString(ItemDetailFragment.CATEGORY_ID, item.id);
          CheckoutDetailFragment checkoutDetailFragment = new CheckoutDetailFragment();
          checkoutDetailFragment.setArguments(arguments);
          mParentActivity.getActivity().getSupportFragmentManager().beginTransaction()
                  .replace(R.id.checkout_detail_container, checkoutDetailFragment)
                  .commit();
        } else {
          AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
          builderSingle.setIcon(R.drawable.ic_find_previous_holo_light);
          builderSingle.setTitle("Select One Name:-");

          final ArrayAdapter<Items.Item> arrayAdapter = new ArrayAdapter<Items.Item>(view.getContext(), android.R.layout.select_dialog_singlechoice);
          arrayAdapter.addAll(Items.Item.subItems);

          builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

          builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              String strName = arrayAdapter.getItem(which).itemName;
              AlertDialog.Builder builderInner = new AlertDialog.Builder(view.getContext());
              builderInner.setMessage(strName);
              builderInner.setTitle("Your Selected Item is");
              builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {
                  dialog.dismiss();
                }
              });
              builderInner.show();
            }
          });
          builderSingle.show();
        }
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
      holder.content.setBackgroundColor(mValues.get(position).hasSubItems ? getResources().getColor(android.R.color.holo_purple) : getResources().getColor(android.R.color.holo_green_light));
      holder.itemName.setText(mValues.get(position).itemName);
      holder.price.setText(String.format(mValues.get(position).hasSubItems ? "Click for more" : "$%s", mValues.get(position).itemPrice));
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
      final LinearLayout content;

      ViewHolder(View view) {
        super(view);
        itemName = view.findViewById(R.id.itemName);
        price = view.findViewById(R.id.price);
        content =view.findViewById(R.id.content);
      }
    }
  }
  @SuppressLint("StaticFieldLeak")
  private class GetItemsForCategory extends AsyncTask<Integer, Integer,  List<Items.Item>> {
    RecyclerView recyclerView;

    GetItemsForCategory(RecyclerView recyclerView) {
      this.recyclerView = recyclerView;
    }
    @Override
    protected  List<Items.Item> doInBackground(Integer... params) {
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

      JSONObject json;
      JSONArray jsonPosts;
      List<Items.Item> itemsForCache = new ArrayList<>();
      if(Categories.readFromCache(mItem) == null) {
        try {
          json = new JSONObject(ConnectToServer.getItemsForCategory(mItem.id));
          jsonPosts = json.getJSONArray(ConnectToServer.ITEMS);
          if (jsonPosts != null) {
            for (int i = 0; i < jsonPosts.length(); i++) {
              JSONObject jsonObject = jsonPosts.getJSONObject(i);
              Items.Item item = Items.createItem(jsonObject.getString("_id"),
                      jsonObject.getString("name"),
                      jsonObject.getString("price"),
                      jsonObject.getBoolean("hasSubItems"));

              if (item.hasSubItems) {
                JSONObject json1 = new JSONObject(ConnectToServer.getSubItemsForItem(item.id));
                JSONArray jsonPosts1 = json1.getJSONArray(ConnectToServer.SUB_ITEMS);
                  if (jsonPosts1 != null) {
                    for (int j = 0; j < jsonPosts1.length(); j++) {
                      JSONObject jsonObject1 = jsonPosts1.getJSONObject(i);
                      Items.Item item1 = Items.createItem(jsonObject1.getString("_id"),
                              jsonObject.getString("name"),
                              jsonObject.getString("price"), false);
                       //ADD to SUB ITEM LIST
                      Items.Item.buildSubItems(item1);
                    }
                  }
              }
              Items.addItem(item);
              itemsForCache.add(item);
            }
            Categories.addToCache(mItem, itemsForCache);
          }
        }
        catch (JSONException e) {
          Log.e("Error", "error getting categories ", e);
        }
      }
      else {
        Items.replaceItems(Categories.readFromCache(mItem));
      }
      return itemsForCache;
    }

    @Override
    protected void onPostExecute(List<Items.Item> result) {
      setupRecyclerView(recyclerView);
    }
  }
}
