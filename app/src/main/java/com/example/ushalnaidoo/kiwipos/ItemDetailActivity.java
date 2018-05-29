package com.example.ushalnaidoo.kiwipos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ushalnaidoo.kiwipos.model.Items;

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
    Items.CHECKOUT_ITEMS.clear();
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      Bundle arguments = new Bundle();
      arguments.putString(ItemDetailFragment.CATEGORY_ID,
                          getIntent().getStringExtra(ItemDetailFragment.CATEGORY_ID));
      ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
      itemDetailFragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
              .add(R.id.item_detail_container, itemDetailFragment)
              .commit();
      CheckoutDetailFragment checkoutDetailFragment = new CheckoutDetailFragment();
      getSupportFragmentManager().beginTransaction()
              .add(R.id.checkout_detail_container, checkoutDetailFragment)
              .commit();
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
