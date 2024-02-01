package com.example.akpfoundaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("AKPSharedPreferenceFile",MODE_PRIVATE);

        BottomNavigationView BottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigation.setOnItemSelectedListener(this);
        BottomNavigation.setSelectedItemId(R.id.bottom_home);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        switch (item.getItemId()){
            case R.id.logout:
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                edit.clear();
                edit.commit();
                finish();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_settings,menu);
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bottom_home:
                return true;
            case R.id.bottom_chart:
                startActivity(new Intent(HomeActivity.this,ChartActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                return true;
            case R.id.bottom_profile:
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                return true;
        }
        return false;
    }
}