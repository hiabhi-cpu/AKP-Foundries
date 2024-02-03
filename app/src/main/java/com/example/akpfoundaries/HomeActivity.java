package com.example.akpfoundaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    SharedPreferences sharedPreferences;

    ArrayList<SliderData> imageUriList = new ArrayList<>();

    ArrayList<SliderData> imageUriList2 = new ArrayList<>();

    ImageView interesting_facts_image;
    ImageView quality_certification_image;

    SliderView sliderView;
    SliderView sliderView2;

    SliderAdapter adapter;
    SliderAdapter adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sliderView = findViewById(R.id.sliderView);
        sliderView2 = findViewById(R.id.sliderView2);

        interesting_facts_image = findViewById(R.id.akp_interesting_facts);
        quality_certification_image = findViewById(R.id.akp_quality_certification);

        imageUriList.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fakp_slider1_image.jpg?alt=media&token=759ac0c3-c5cf-4691-a348-abdb1a25eebe"));
        adapter = new SliderAdapter(this,imageUriList,1);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
        initializeArrayWithImageUri();

        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fakp_interesting_facts.png?alt=media&token=73affc4f-4874-411a-aca2-ed02a86c7503").into(interesting_facts_image);
        initializeArrayWithImageUri2();
        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fakp_quality_certification.png?alt=media&token=4df88a0a-c621-4e19-af1d-a9406abb0edd").into(quality_certification_image);
        adapter2 = new SliderAdapter(this,imageUriList2,2);
        sliderView2.setSliderAdapter(adapter2);
        sliderView2.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView2.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView2.setScrollTimeInSec(3);
        sliderView2.startAutoCycle();



        BottomNavigationView BottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigation.setOnItemSelectedListener(this);
        BottomNavigation.setSelectedItemId(R.id.bottom_home);

    }

    private void initializeArrayWithImageUri2() {
        imageUriList2.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fhorizontal_scroll_1.png?alt=media&token=7d3c8622-426a-49b3-87cb-cb547d25d6b6"));
        imageUriList2.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fhorizontal_scroll2.png?alt=media&token=b6ab48e7-f06e-4e75-a4fb-9baf954ec8da"));
        imageUriList2.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fhorizontal_scroll3.png?alt=media&token=cbf044e0-c68e-4311-afe0-35e549071ba9"));
        imageUriList2.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fhorizontal_scroll4.jpg?alt=media&token=1d42b557-69d7-46c7-8f58-52169c2f4a17"));
        imageUriList2.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fhorizontal_scroll5.png?alt=media&token=bb4caf3b-0d26-47b6-b731-215b91c71412"));
    }

    private void initializeArrayWithImageUri() {
        imageUriList.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fakp_slider2_image.jpg?alt=media&token=07b3e4e2-b9da-446a-88fc-6994ab43f98e"));
        adapter.notifyDataSetChanged();
        imageUriList.add(new SliderData("https://firebasestorage.googleapis.com/v0/b/akp-foundaries.appspot.com/o/AKP_home_images%2Fakp_slider3_image.jpg?alt=media&token=9fa878c4-2bf4-4904-a343-df8e65423ebf"));
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "images loaded", Toast.LENGTH_SHORT).show();
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