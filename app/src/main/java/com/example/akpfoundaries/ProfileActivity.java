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

public class ProfileActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    ImageView profileImageView;
    TextView profileNameTextView;
    TextView profilePhoneTextView;
    ProgressBar profileProgressBar;

    private Uri profileImageUri;

    private String userMobile;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://akp-foundaries-default-rtdb.firebaseio.com/");

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        BottomNavigationView BottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigation.setOnItemSelectedListener(this);
        BottomNavigation.setSelectedItemId(R.id.bottom_profile);

        profileImageView = findViewById(R.id.profile_imageview);
        profileNameTextView = findViewById(R.id.profile_name_textview);
        profilePhoneTextView = findViewById(R.id.profile_phone_textview);
        profileProgressBar = findViewById(R.id.profile_progressBar);
        profileProgressBar.setVisibility(View.INVISIBLE);


        sharedPreferences = getSharedPreferences("AKPSharedPreferenceFile",MODE_PRIVATE);

        userMobile = sharedPreferences.getString("LOGIN_NUMBER","");
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,2);
            }
        });

        addDataToProfile();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bottom_home:
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
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

    private void addDataToProfile(){
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(userMobile).child("name").getValue(String.class);
                profileNameTextView.setText(name);
                profilePhoneTextView.setText(userMobile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null){
            profileImageUri = data.getData();
            Glide.with(this).load(profileImageUri).circleCrop().into(profileImageView);
            uploadToFirebase(profileImageUri);
        }
        else{
            Toast.makeText(this, "Please select a image", Toast.LENGTH_SHORT).show();
        }
    }

//    private String getFileExtension(Uri uri){
//        ContentResolver resolver = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(resolver.getType(uri));
//    }

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