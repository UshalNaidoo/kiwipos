package com.example.ushalnaidoo.kiwipos;

import android.annotation.SuppressLint;
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

import com.example.ushalnaidoo.kiwipos.model.Categories;
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_list);

    Categories.getCategories().clear();
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

    View recyclerView = findViewById(R.id.item_list);
    assert recyclerView != null;
    new GetCategories(recyclerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, Categories.getCategories()));
  }

  public static class SimpleItemRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final CategoryListActivity mParentActivity;
    private final List<Categories.Category> categories;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Categories.Category category = (Categories.Category) view.getTag();
          Bundle arguments = new Bundle();
          arguments.putString(ItemDetailFragment.CATEGORY_ID, category.getId());
          ItemDetailFragment fragment = new ItemDetailFragment();
          fragment.setArguments(arguments);
          mParentActivity.getSupportFragmentManager().beginTransaction()
              .replace(R.id.item_detail_container, fragment)
              .commit();
      }
    };

    SimpleItemRecyclerViewAdapter(CategoryListActivity parent,
                                  List<Categories.Category> categories) {
      this.categories = categories;
      mParentActivity = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.category_list_content, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      holder.categoryName.setText(categories.get(position).getCategoryName());
      holder.itemView.setTag(categories.get(position));
      holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
      return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      final TextView categoryName;

      ViewHolder(View view) {
        super(view);
        categoryName = view.findViewById(R.id.categoryName);
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
        jsonPosts = json.getJSONArray(ConnectToServer.CATEGORIES);
        if (jsonPosts != null) {
          for (int i = 0; i < jsonPosts.length(); i++) {
            JSONObject jsonObject = jsonPosts.getJSONObject(i);
            Categories.addCategory(Categories.createCategory(jsonObject.getString("_id"), jsonObject.getString("name")));
          }

          setupRecyclerView((RecyclerView) recyclerView);
        }
      }
      catch (JSONException e) {
        Log.e("Error", "error getting categories ", e);
      }
    }
  }
}