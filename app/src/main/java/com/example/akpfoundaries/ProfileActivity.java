package com.example.akpfoundaries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    ImageView profileImageView;
    TextView profileNameTextView;
    TextView profilePhoneTextView;
    ProgressBar profileProgressBar;

    private Uri profileImageUri;

    private String userMobile;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://akp-foundaries-default-rtdb.firebaseio.com/").child("users");

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profile_imageview);
        addProfileImage();
        BottomNavigationView BottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigation.setOnItemSelectedListener(this);
        BottomNavigation.setSelectedItemId(R.id.bottom_profile);
        profileNameTextView = findViewById(R.id.profile_name_textview);
        profilePhoneTextView = findViewById(R.id.profile_phone_textview);
        profileProgressBar = findViewById(R.id.profile_progressBar);
        profileProgressBar.setVisibility(View.INVISIBLE);
        sharedPreferences = getSharedPreferences("AKPSharedPreferenceFile",MODE_PRIVATE);
        userMobile = sharedPreferences.getString("LOGIN_NUMBER","");
        addDataToProfile();
    }

    public void onClickImage(View view) {
        CropImage.activity().start(ProfileActivity.this);
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


    public void addProfileImage(){
        profileImageView.setImageResource(R.drawable.profile_person_icon);
        final String[] profileUri = new String[1];
        databaseReference.child(userMobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("profileImageUri")){
                    profileUri[0] = snapshot.child("profileImageUri").getValue(String.class);
                    Glide.with(getApplicationContext()).load(profileUri[0]).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).into(profileImageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addDataToProfile(){
        profileNameTextView.setText(sharedPreferences.getString("name",""));
        profilePhoneTextView.setText(userMobile);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){
                profileImageUri=result.getUri();
                Glide.with(this).load(profileImageUri).circleCrop().into(profileImageView);
                uploadToFirebase(profileImageUri);
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e=result.getError();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadToFirebase(Uri imageuri){
        StorageReference ref = storageReference.child(userMobile);
        ref.putFile(imageuri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri ->
                    databaseReference.child("users").child(userMobile).child("profileImageUri").setValue(uri.toString()));
                    Toast.makeText(this, "Profile Image updated sucessfully", Toast.LENGTH_SHORT).show();
                    profileProgressBar.setVisibility(View.INVISIBLE);
        }).addOnProgressListener(snapshot -> {
            profileProgressBar.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            profileProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Profile Image updating failed", Toast.LENGTH_SHORT).show();
        });
    }
}