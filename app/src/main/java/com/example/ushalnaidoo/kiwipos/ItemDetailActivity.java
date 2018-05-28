package com.example.ushalnaidoo.kiwipos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.dummy.DummyContent;

import java.util.List;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CategoryListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_detail);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      Bundle arguments = new Bundle();
      arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                          getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
      ItemDetailFragment fragment = new ItemDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
                                 .add(R.id.item_detail_container, fragment)
                                 .commit();
    }

    View recyclerView = findViewById(R.id.item_list);
    assert recyclerView != null;
    setupRecyclerView((RecyclerView) recyclerView);
//    new CategoryListActivity.GetCategories(recyclerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.getTestItems()));
  }
  public static class SimpleItemRecyclerViewAdapter
          extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final ItemDetailActivity mParentActivity;
    private final List<DummyContent.DummyItem> mValues;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
//        if (mTwoPane) {
//          Bundle arguments = new Bundle();
//          arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
//          ItemDetailFragment fragment = new ItemDetailFragment();
//          fragment.setArguments(arguments);
//          mParentActivity.getSupportFragmentManager().beginTransaction()
//                  .replace(R.id.item_detail_container, fragment)
//                  .commit();
//        } else {
//          Context context = view.getContext();
//          Intent intent = new Intent(context, ItemDetailActivity.class);
//          intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);
//
//          context.startActivity(intent);
//        }
      }
    };

    SimpleItemRecyclerViewAdapter(ItemDetailActivity parent,
                                  List<DummyContent.DummyItem> items
                                  ) {
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
      holder.mContentView.setText("ssss");
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


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      NavUtils.navigateUpTo(this, new Intent(this, CategoryListActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
