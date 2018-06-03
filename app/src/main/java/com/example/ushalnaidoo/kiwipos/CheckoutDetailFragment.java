package com.example.ushalnaidoo.kiwipos;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ushalnaidoo.kiwipos.model.Addons;
import com.example.ushalnaidoo.kiwipos.model.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutDetailFragment extends Fragment {
  public CheckoutDetailFragment() {
  }

  private static TextView totalText;

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
      Items.CheckoutItem item = entry.getKey();
      Integer value = entry.getValue();

      Double price = Double.valueOf(item.getItemPrice());
      for (Addons.Addon addon : item.getAssignedAddons()) {
        if (Addons.AddonType.ACTUAL.equals(addon.getAddonType())) {
          if (!addon.getAdjustmentAmount().equals("0.00")) {
            price = price + Double.valueOf(addon.getAdjustmentAmount());
          }
        }
        else if (Addons.AddonType.PERCENTAGE.equals(addon.getAddonType())) {
          if (!addon.getAdjustmentAmount().equals("0.00")) {
            price = price - (Double.valueOf(addon.getAdjustmentAmount()) / 100 * price);
          }
        }
      }
      total += price * value;
    }

    String totalString = "Total: $" +  String.format(Locale.getDefault(),"%.2f", total);
    totalText.setText(totalString);
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Items.CHECKOUT_ITEMS));
  }

  public static class SimpleItemRecyclerViewAdapter
          extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private final Map<Items.CheckoutItem, Integer> mValues;
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter = this;

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
      final List<Items.CheckoutItem> items = new ArrayList<>();
      List<String> itemNames = new ArrayList<>();
      List<String> prices = new ArrayList<>();

      for (Map.Entry<Items.CheckoutItem, Integer> entry : mValues.entrySet()) {
        Items.CheckoutItem item = entry.getKey();
        Integer amount = entry.getValue();
        Collections.sort(item.getAssignedAddons());
        StringBuilder stringBuilder = new StringBuilder(item.getItemName());
        Double price = Double.valueOf(item.getItemPrice());
        StringBuilder priceBuilder = new StringBuilder(String.format(Locale.getDefault(),"%.2f",price*amount));
        Double priceForCalculations = Double.valueOf(item.getItemPrice());
        for (Addons.Addon addon : item.getAssignedAddons()) {
          stringBuilder.append('\n' + " - ").append(addon.getAddonName());
          if (!addon.getAdjustmentAmount().equals("0.00")) {
            if (Addons.AddonType.ACTUAL.equals(addon.getAddonType())) {
              priceForCalculations = priceForCalculations + Double.valueOf(addon.getAdjustmentAmount());
              priceBuilder.append('\n' + "$").append(String.format(Locale.getDefault(), "%.2f", Double.valueOf(addon.getAdjustmentAmount()) * amount));
            } else if (Addons.AddonType.PERCENTAGE.equals(addon.getAddonType())) {
              priceBuilder.append('\n' + "- $").append(String.format(Locale.getDefault(), "%.2f", Double.valueOf(addon.getAdjustmentAmount()) / 100 * priceForCalculations * amount));
              priceForCalculations = priceForCalculations - (Double.valueOf(addon.getAdjustmentAmount()) / 100 * priceForCalculations);
            }
          }
          else {
            priceBuilder.append('\n');
          }
        }

        items.add(item);
        itemNames.add(String.valueOf(amount) + "x " + stringBuilder.toString());
        prices.add(priceBuilder.toString());
      }

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          showAddons(view.getContext(), items.get(holder.getAdapterPosition()));
        }
      });
      holder.detail.setText(itemNames.get(position));
      holder.amount.setText(String.format("$%s", prices.get(position)));
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
          showAddons(view.getContext(), items.get(holder.getAdapterPosition()));
        }
      });
    }

    private void showAddons(Context context, final Items.CheckoutItem item) {
      AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
      Collections.sort(item.getAddons());
      final List<Addons.Addon> possibleAddons = item.getAddons();
      final List<Addons.Addon> selectedAddons = item.getAssignedAddons();
      String[] possibleAddonsStringArr = new String[possibleAddons.size()];
      final boolean[] selectedItems = new boolean[possibleAddons.size()];
      for(int i = 0; i < possibleAddonsStringArr.length ; i++){
        possibleAddonsStringArr[i] = possibleAddons.get(i).getAddonName();
        selectedItems[i] = false;
        for(int j = 0 ; j < selectedAddons.size() ; j++){
          if(selectedAddons.get(j).getId().equals(possibleAddons.get(i).getId())) {
            selectedItems[i] = true;
            break;
          }
        }
      }

      alertBuilder.setMultiChoiceItems(possibleAddonsStringArr, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
        }
      }).setPositiveButton("OK",new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int ii) {
          for(int i = 0 ; i < selectedItems.length ; i++) {
            if (selectedItems[i]) {
              item.buildAssignedAddons(possibleAddons.get(i));
            } else {
              item.removeAssignedAddons(possibleAddons.get(i));
            }
          }
          setTotalValue();
          simpleItemRecyclerViewAdapter.notifyDataSetChanged();
        }
      }).setCancelable(false).create().show();
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
