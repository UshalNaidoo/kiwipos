package com.example.ushalnaidoo.kiwipos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ushalnaidoo.kiwipos.model.Addons;
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
    for (Map.Entry<Items.CheckoutItem, Integer> entry : Items.CHECKOUT_ITEMS.entrySet()) {
      Items.Item item = entry.getKey();
      Integer value = entry.getValue();
      total += Double.valueOf(item.getItemPrice())*value;
    }

    String totalString = "Total: $" +  String.format("%.2f", total);
    totalText.setText(totalString);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Items.CHECKOUT_ITEMS));
  }

  public static class SimpleItemRecyclerViewAdapter
          extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private final Map<Items.CheckoutItem, Integer> mValues;

    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter = this;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Items.CheckoutItem item = (Items.CheckoutItem) view.getTag();
      }
    };

    SimpleItemRecyclerViewAdapter(Map<Items.CheckoutItem, Integer> checkouts) {
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
      final List<Items.CheckoutItem> items = new ArrayList<>();
      List<String> itemNames = new ArrayList<>();
      List<String> prices = new ArrayList<>();

      for (Map.Entry<Items.CheckoutItem, Integer> entry : mValues.entrySet()) {
        Items.CheckoutItem item = entry.getKey();
        Integer amount = entry.getValue();
        // Loop through assigned addons and adjust the price and name of the checkout item
        Collections.sort(item.getAssignedAddons());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(item.getItemName());
        Double price = Double.valueOf(item.getItemPrice());
        for (Addons.Addon addon : item.getAssignedAddons()) {
          stringBuilder.append('\n' + " - ").append(addon.getAddonName());
          if (Addons.AddonType.ACTUAL.equals(addon.getAddonType())) {
            price = price - Double.valueOf(addon.getAdjustmentAmount());
          }
          else if (Addons.AddonType.PERCENTAGE.equals(addon.getAddonType())) {
            price = price - (Double.valueOf(addon.getAdjustmentAmount())/100 * price);
          }
        }

        items.add(item);
        itemNames.add(String.valueOf(amount) + "x " + stringBuilder.toString());
        prices.add(String.format("%.2f",price*amount));
      }

      holder.detail.setText(itemNames.get(position));
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

      holder.editItem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
          final ArrayAdapter<Addons.Addon> arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.select_dialog_singlechoice);
          Collections.sort(items.get(holder.getAdapterPosition()).getAddons());
          arrayAdapter.addAll(items.get(holder.getAdapterPosition()).getAddons());

          builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

          builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              Addons.Addon addon = arrayAdapter.getItem(which);
              items.get(holder.getAdapterPosition()).buildAssignedAddons(addon);
              simpleItemRecyclerViewAdapter.notifyDataSetChanged();
            }
          });
          builderSingle.show();
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
      final ImageView editItem;

      ViewHolder(View view) {
        super(view);
        detail = view.findViewById(R.id.detail);
        amount = view.findViewById(R.id.amount);
        addMore = view.findViewById(R.id.addMore);
        minusMore = view.findViewById(R.id.minusMore);
        removeAll = view.findViewById(R.id.removeAll);
        editItem = view.findViewById(R.id.editItem);
      }
    }
  }
}
