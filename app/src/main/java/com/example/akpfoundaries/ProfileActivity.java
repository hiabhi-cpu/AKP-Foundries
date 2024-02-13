package com.example.akpfoundaries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class ProfileActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    String profileImageUri1;
    String userMobile;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://akp-foundaries-default-rtdb.firebaseio.com/").child("users");

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("AKPSharedPreferenceFile",MODE_PRIVATE);
        userMobile = sharedPreferences.getString("LOGIN_NUMBER","");
        loadProfileImageUri();
        setContentView(R.layout.activity_profile);
        BottomNavigationView BottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigation.setOnItemSelectedListener(this);
        BottomNavigation.setSelectedItemId(R.id.bottom_profile);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bottom_home:
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                return true;
            case R.id.bottom_chart:
                startActivity(new Intent(ProfileActivity.this,ChartActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                return true;
            case R.id.bottom_profile:
                return true;
        }
        return false;
    }


    public void loadProfileImageUri(){
        databaseReference.child(userMobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("profileImageUri")){
                    profileImageUri1 = snapshot.child("profileImageUri").getValue(String.class);
                    if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ProfileInfoFragment){
                        ProfileInfoFragment fragment = (ProfileInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        Glide.with(getApplicationContext()).load(profileImageUri1).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).into(fragment.profileImageView);
                    }
                    else{
                        ProfileEditFragment fragment = (ProfileEditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        Glide.with(getApplicationContext()).load(profileImageUri1).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).into(fragment.profileImageView);
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}