package com.example.ushalnaidoo.kiwipos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.model.Categories;
import com.example.ushalnaidoo.kiwipos.model.Items;
import com.example.ushalnaidoo.kiwipos.server.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

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
        final CategoryListActivity activity = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (Items.CHECKOUT_ITEMS.size() == 0 ) {
                    return;
                }
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_tender);
                Button dialogButtonHaveHere = dialog.findViewById(R.id.dialogButtonHaveHere);
                final EditText customerCash = dialog.findViewById(R.id.customerCash);
                dialogButtonHaveHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double cash =  Double.valueOf(customerCash.getText().toString());
                        if (cash - Items.getCheckoutTotal() < 0) {
                            return;
                        }
                        dialog.dismiss();
                        final Dialog dialog1 = new Dialog(v.getContext());
                        dialog1.setContentView(R.layout.dialog_tender_complete);
                        TextView change = dialog1.findViewById(R.id.change);
                        TextView dialogButtonNextOrder = dialog1.findViewById(R.id.dialogButtonNextOrder);
                        String changeToGive = "Change : $" + String.format(Locale.getDefault(), "%.2f", cash - Items.getCheckoutTotal());
                        change.setText(changeToGive);

                        dialogButtonNextOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Items.clearCheckout();
                                CheckoutDetailFragment checkoutDetailFragment = new CheckoutDetailFragment();
                                activity.getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.checkout_detail_container, checkoutDetailFragment)
                                        .commit();
                                dialog1.dismiss();
                            }
                        });
                        dialog1.show();
                    }
                });

                Button dialogButtonTakeAway = dialog.findViewById(R.id.dialogButtonTakeAway);
                dialogButtonTakeAway.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final Dialog dialog1 = new Dialog(v.getContext());
                        dialog1.setContentView(R.layout.dialog_tender_complete);
                        dialog1.show();
                    }
                });

                Button cancelButton = dialog.findViewById(R.id.dialogButtonCancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
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
            } catch (JSONException e) {
                Log.e("Error", "error getting categories ", e);
            }
        }
    }
}