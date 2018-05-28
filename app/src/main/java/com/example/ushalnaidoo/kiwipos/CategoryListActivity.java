package com.example.ushalnaidoo.kiwipos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.dummy.DummyContent;
import com.example.ushalnaidoo.kiwipos.server.ConnectToServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CategoryListActivity extends AppCompatActivity {

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_list);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action111", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    if (findViewById(R.id.item_detail_container) != null) {
      mTwoPane = true;
    }

    View recyclerView = findViewById(R.id.item_list);
    assert recyclerView != null;
    new GetCategories(recyclerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
  }

  public static class SimpleItemRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final CategoryListActivity mParentActivity;
    private final List<DummyContent.DummyItem> mValues;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
        if (mTwoPane) {
          Bundle arguments = new Bundle();
          arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
          ItemDetailFragment fragment = new ItemDetailFragment();
          fragment.setArguments(arguments);
          mParentActivity.getSupportFragmentManager().beginTransaction()
              .replace(R.id.item_detail_container, fragment)
              .commit();
        } else {
          Context context = view.getContext();
          Intent intent = new Intent(context, ItemDetailActivity.class);
          intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

          context.startActivity(intent);
        }
      }
    };

    SimpleItemRecyclerViewAdapter(CategoryListActivity parent,
                                  List<DummyContent.DummyItem> items,
                                  boolean twoPane) {
      mValues = items;
      mParentActivity = parent;
      mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_list_content, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      holder.mContentView.setText(mValues.get(position).content);
      holder.itemView.setTag(mValues.get(position));
      holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
      return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      final TextView mContentView;

      ViewHolder(View view) {
        super(view);
        mContentView = view.findViewById(R.id.content);
      }
    }
  }

  @SuppressLint("StaticFieldLeak")
  private class GetCategories extends AsyncTask<Integer, Integer, String> {
    View recyclerView;

    GetCategories(View recyclerView) {
      this.recyclerView = recyclerView;
    }
    @Override
    protected String doInBackground(Integer... params) {
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
      return ConnectToServer.getCategories();
    }

    @Override
    protected void onPostExecute(String result) {
      JSONObject json;
      JSONArray jsonPosts;
      try {
        json = new JSONObject(result);
        jsonPosts = json.getJSONArray("categories");

        if (jsonPosts != null) {
          for (int i = 0; i < jsonPosts.length(); i++) {
            JSONObject jsonObject = jsonPosts.getJSONObject(i);
            DummyContent.addItem(DummyContent.createDummyItem(jsonObject.getString("_id"), jsonObject.getString("name")));
          }

          setupRecyclerView((RecyclerView) recyclerView);
        }
      }
      catch (JSONException e) {
        Log.e("Error", "error ", e);
      }
    }
  }
}