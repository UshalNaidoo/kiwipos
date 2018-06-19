package com.example.ushalnaidoo.kiwipos.apps.POS;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.R;
import com.example.ushalnaidoo.kiwipos.adapter.SalesAdapter;
import com.example.ushalnaidoo.kiwipos.enums.SaleStatus;
import com.example.ushalnaidoo.kiwipos.model.Addons;
import com.example.ushalnaidoo.kiwipos.model.Categories;
import com.example.ushalnaidoo.kiwipos.model.Items;
import com.example.ushalnaidoo.kiwipos.model.Sale;
import com.example.ushalnaidoo.kiwipos.model.TenderedSale;
import com.example.ushalnaidoo.kiwipos.server.ConnectToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.ushalnaidoo.kiwipos.helper.EmailHelper.emailResultsToUser;
import static com.example.ushalnaidoo.kiwipos.model.Items.CHECKOUT_ITEMS;

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

        FloatingActionButton checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (CHECKOUT_ITEMS.size() == 0) {
                    return;
                }
                final Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.setContentView(R.layout.dialog_tender);
                final EditText customerCash = dialog.findViewById(R.id.customerCash);
                final EditText notes = dialog.findViewById(R.id.notes);
                Button dialogButtonHaveHere = dialog.findViewById(R.id.dialogButtonHaveHere);
                dialogButtonHaveHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveTenderedSale(v, customerCash, dialog, notes, activity, "0");
                    }
                });

                Button dialogButtonTakeAway = dialog.findViewById(R.id.dialogButtonTakeAway);
                dialogButtonTakeAway.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveTenderedSale(v, customerCash, dialog, notes, activity, "1");
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

        FloatingActionButton todaysSalesButton = findViewById(R.id.todaysSalesButton);
        todaysSalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new GetTodaysSales(activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        new GetCategories(recyclerView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void saveTenderedSale(final View v, EditText customerCash, Dialog dialog, final EditText notes, final CategoryListActivity activity,
                                  final String isTakeAway) {
        if (customerCash.getText().toString().isEmpty()) {
            return;
        }
        final Double cash = Double.valueOf(customerCash.getText().toString());
        if (cash - Items.getCheckoutTotal() < 0) {
            return;
        }
        dialog.dismiss();

        new TenderSaleAsync(notes.getText().toString(), String.format(Locale.getDefault(), "%.2f", Items.getCheckoutTotal()), isTakeAway)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        final Dialog dialog1 = new Dialog(v.getContext());
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog1.setContentView(R.layout.dialog_tender_complete);
        dialog1.setCanceledOnTouchOutside(false);
        TextView change = dialog1.findViewById(R.id.change);
        final String changeToGive = "Change : $" + String.format(Locale.getDefault(), "%.2f", cash - Items.getCheckoutTotal());
        change.setText(changeToGive);

        Button dialogButtonNextOrder = dialog1.findViewById(R.id.dialogButtonNextOrder);
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

        Button dialogButtonEmail = dialog1.findViewById(R.id.dialogButtonEmail);
        dialogButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder emailBody = new StringBuilder();
                emailBody.append("We hope that you enjoyed your meal today at Coconut Grove" + '\n');
                if (isTakeAway.equals("1")) {
                    emailBody.append("Take Away order ");
                } else {
                    emailBody.append("Dine in order ");
                }
                emailBody.append(notes.getText().toString()).append('\n').append('\n');

                for (Map.Entry<Items.CheckoutItem, Integer> entry : CHECKOUT_ITEMS.entrySet()) {
                    Items.CheckoutItem checkoutItem = entry.getKey();
                    int quantity = entry.getValue();
                    Double cost = quantity * Double.valueOf(checkoutItem.getItemPrice());
                    emailBody.append(quantity).append("x ").append(checkoutItem.getItemName()).append('\t').append("$")
                            .append(String.format(Locale.getDefault(), "%.2f", cost)).append('\n');
                    for (Addons.Addon addon : checkoutItem.getAssignedAddons()) {
                        emailBody.append('\t').append(" + ").append(addon.getAddonName()).append('\t').append("$")
                                .append(String.format(Locale.getDefault(), "%.2f", Double.valueOf(addon.getAdjustmentAmount()))).append('\n');
                    }
                    emailBody.append('\n');
                }
                emailBody.append("=============================" + '\n');
                emailBody.append("Total : $").append(String.format(Locale.getDefault(), "%.2f", Items.getCheckoutTotal())).append('\n');
                emailBody.append("Paid : $").append(String.format(Locale.getDefault(), "%.2f", cash)).append('\n');
                emailBody.append(changeToGive).append('\n');
                emailBody.append("=============================" + '\n' + '\n');
                emailBody.append("Thank you for your support" + '\n');
                emailBody.append("Please follow us on Facebook at https://www.facebook.com/CoconutGroveNZ/" + '\n');
                emailResultsToUser(activity, emailBody.toString(), "Receipt for Coconut Grove");
            }
        });
        dialog1.show();
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
                if (!category.getId().equals("9999")) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.CATEGORY_ID, category.getId());
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.item_detail_container, fragment)
                                   .commit();
                }
                else {
                    final Dialog dialog = new Dialog(view.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.setContentView(R.layout.dialog_misc_item);
                    final EditText itemName = dialog.findViewById(R.id.item);
                    final EditText itemCost = dialog.findViewById(R.id.itemCost);
                    Button dialogButtonAdd = dialog.findViewById(R.id.dialogButtonAdd);
                    dialogButtonAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Items.Item item = Items.createItem("9999", itemName.getText().toString(), itemCost.getText().toString(), false, false);
                            Items.CheckoutItem checkoutItem = new Items.CheckoutItem(item, false);
                            ItemDetailFragment.getSimpleItemRecyclerViewAdapter().updateCheckout(checkoutItem);
                            dialog.dismiss();
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
                Categories.addCategory(Categories.createCategory("9999", "Misc"));
            }
            catch (JSONException e) {
                Log.e("Error", "error getting categories ", e);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TenderSaleAsync extends AsyncTask<Integer, Integer, String> {

        String notes;

        String amount;

        String isTakeAway;

        TenderSaleAsync(String notes, String amount, String isTakeAway) {
            this.notes = notes;
            this.amount = amount;
            this.isTakeAway = isTakeAway;
        }

        @Override
        protected String doInBackground(Integer... params) {
            JSONArray jsonArray = new JSONArray();
            try {
                //0=normal, 1= subitem, 2=addon
                for (Map.Entry<Items.CheckoutItem, Integer> entry : CHECKOUT_ITEMS.entrySet()) {
                    Items.CheckoutItem checkoutItem = entry.getKey();
                    int quantity = entry.getValue();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("product", checkoutItem.getItemName());
                    jsonObject.put("itemid", checkoutItem.getId());
                    jsonObject.put("quantity", quantity);
                    jsonObject.put("type", checkoutItem.isSubType() ? "1" : "0");
                    jsonArray.put(jsonObject);
                    for (Addons.Addon addon : checkoutItem.getAssignedAddons()) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("product", addon.getAddonName());
                        jsonObject1.put("itemid", addon.getId());
                        jsonObject1.put("quantity", quantity);
                        jsonObject1.put("type", "2");
                        jsonArray.put(jsonObject1);
                    }

                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            ConnectToServer.tenderSale(notes, amount, isTakeAway, jsonArray.toString(), df.format(date));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTodaysSales extends AsyncTask<Integer, Integer, String> {

        Activity activity;

        GetTodaysSales(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Integer... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            return ConnectToServer.getTodaysSales();
        }


        @Override
        protected void onPostExecute(String result) {
            JSONObject json;
            JSONArray jsonPosts;
            try {
                json = new JSONObject(result);
                jsonPosts = json.getJSONArray(ConnectToServer.SALES);
                if (jsonPosts != null && jsonPosts.length() > 0) {
                    final List<Sale> sales = new ArrayList<>();
                    Double todaysTotalSales = 0d;
                    for (int i = 0; i < jsonPosts.length(); i++) {
                        JSONObject jsonObject = jsonPosts.getJSONObject(i);
                        Double saleAmount = jsonObject.getDouble("amount");
                        todaysTotalSales += saleAmount;
                        Sale sale = new Sale(jsonObject.getString("_id"), jsonObject.getString("created"), jsonObject.getString("notes"), saleAmount, jsonObject.getString("takeaway").equals("1"), SaleStatus.values()[jsonObject.getInt("done")]);
                        sales.add(sale);
                    }
                    final Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.setContentView(R.layout.dialog_todays_sales);
                    final TextView customerCount = dialog.findViewById(R.id.customerCount);
                    final TextView averageCheque = dialog.findViewById(R.id.averageCheque);
                    final TextView subTotal = dialog.findViewById(R.id.subTotal);

                    ListView dialog_ListView = dialog.findViewById(R.id.dialoglist);
                    SalesAdapter adapter = new SalesAdapter(activity, sales);
                    dialog_ListView.setAdapter(adapter);
                    dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                        }
                    });

                    Double average = todaysTotalSales / jsonPosts.length();
                    final String customerCountText = "Count: " + jsonPosts.length();
                    customerCount.setText(customerCountText);
                    final String averageChequeText = "Average: $" + String.format(Locale.getDefault(), "%.2f", average);
                    averageCheque.setText(averageChequeText);
                    String subTotalText = "Subtotal: $" + String.format(Locale.getDefault(), "%.2f", todaysTotalSales);
                    subTotal.setText(subTotalText);
                    Button cancelButton = dialog.findViewById(R.id.dialogButtonCancel);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button cashUpButton = dialog.findViewById(R.id.dialogButtonCashUp);
                    cashUpButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Date date = new Date();
                            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                            StringBuilder emailBody = new StringBuilder();
                            emailBody.append("Cashed up at ").append((df.format(date)));
                            emailBody.append("==================").append('\n');
                            emailBody.append(customerCountText).append('\n');
                            emailBody.append(averageChequeText).append('\n');
                            emailBody.append(subTotal).append('\n');
                            emailBody.append("==================").append('\n');

                            for (Sale sale : sales) {
                                emailBody.append(sale.getTime()).append(" ").append(sale.getNotes()).append("    ").append("$").append(String.format(Locale.getDefault(), "%.2f",sale.getAmount())).append('\n');
                            }
                            emailBody.append("=============================" + '\n');
                            emailResultsToUser(activity, emailBody.toString(), "End of day cash up: " + df.format(date));
                            new CashUp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });

                    dialog.show();
                }
            } catch (JSONException e) {
                Log.e("Error", "error getting categories ", e);
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class CashUp extends AsyncTask<Integer, Integer, String> {

        CashUp() {
        }

        @Override
        protected String doInBackground(Integer... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            ConnectToServer.cashUp();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}