package com.example.ushalnaidoo.kiwipos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ushalnaidoo.kiwipos.model.Items;

public class CheckoutDetailFragment extends Fragment {
  public CheckoutDetailFragment() {
  }
  static TextView totalText;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.checkout_detail, container, false);
    totalText = rootView.findViewById(R.id.total);
    setTotalValue();

    RecyclerView recyclerView =  rootView.findViewById(R.id.item_list);
    assert recyclerView != null;
    setupRecyclerView(recyclerView);
    return rootView;
  }

  private static void setTotalValue() {
    Double total = 0.0;
    for (Map.Entry<Items.Item, Integer> entry : Items.CHECKOUT_ITEMS.entrySet()) {
      Items.Item item = entry.getKey();
      Integer value = entry.getValue();
      total += Double.valueOf(item.itemPrice)*value;
    }

    String totalString = "Total: $" +  String.format("%.2f", total);
    totalText.setText(totalString);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Items.CHECKOUT_ITEMS));
  }

  public static class SimpleItemRecyclerViewAdapter
          extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private final Map<Items.Item, Integer> mValues;

    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter = this;
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
      final List<Items.Item> items = new ArrayList<>();
      List<String> itemNames = new ArrayList<>();
      List<String> prices = new ArrayList<>();

      for (Map.Entry<Items.Item, Integer> entry : mValues.entrySet()) {
        Items.Item item = entry.getKey();
        items.add(item);
        Integer amount = entry.getValue();
        itemNames.add(String.valueOf(amount) + "x " + item.itemName);
        prices.add(String.format("%.2f",Double.valueOf(item.itemPrice)*amount));
      }

      holder.detail.setText(itemNames.get(position) + '\n' + itemNames.get(position));
      holder.amount.setText(String.format("$%s", prices.get(position)));
      holder.itemView.setOnClickListener(mOnClickListener);
      holder.addMore.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Items.addToCheckout(items.get(holder.getAdapterPosition()));
          setTotalValue();
          simpleItemRecyclerViewAdapter.notifyDataSetChanged();

        }
      });
      holder.minusMore.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Items.subtractFromCheckout(items.get(holder.getAdapterPosition()));
          setTotalValue();
          simpleItemRecyclerViewAdapter.notifyDataSetChanged();

        }
      });
      holder.removeAll.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          new AlertDialog.Builder(view.getContext())
                  .setTitle("Remove item from checkout?")
                  .setMessage("Are you sure that you want to remove this item?")
                  .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      Items.removeFromCheckout(items.get(holder.getAdapterPosition()));
                      setTotalValue();
                      simpleItemRecyclerViewAdapter.notifyDataSetChanged();
                    }
                  })
                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                  })
                  .show();

        }
      });
    }

    @Override
    public int getItemCount() {
      return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      final TextView detail;
      final TextView amount;
      final ImageView addMore;
      final ImageView minusMore;
      final ImageView removeAll;

      ViewHolder(View view) {
        super(view);
        detail = view.findViewById(R.id.detail);
        amount = view.findViewById(R.id.amount);
        addMore = view.findViewById(R.id.addMore);
        minusMore = view.findViewById(R.id.minusMore);
        removeAll = view.findViewById(R.id.removeAll);
      }
    }
  }
}
