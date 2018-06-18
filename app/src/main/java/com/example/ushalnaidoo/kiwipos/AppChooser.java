package com.example.ushalnaidoo.kiwipos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ushalnaidoo.kiwipos.apps.Bump.BumpScreen;
import com.example.ushalnaidoo.kiwipos.apps.POS.CategoryListActivity;

public class AppChooser extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_chooser);
        Button posButton = findViewById(R.id.button_pos);
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AppChooser.this, CategoryListActivity.class);
                AppChooser.this.startActivity(myIntent);
            }
        });

        Button bumpButton = findViewById(R.id.button_bump);
        bumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AppChooser.this, BumpScreen.class);
                AppChooser.this.startActivity(myIntent);
            }
        });
    }

}
