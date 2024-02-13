package com.example.akpfoundaries;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;


public class ProfileEditFragment extends Fragment {
    ProfileActivity activity;
    Uri profileImageUri;
    ImageView profileImageView;
    ProgressBar progressBar;
    EditText profileNameEditText;
    Button saveBtn;
    Button cancelBtn;


    public ProfileEditFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        // Inflate the layout for this fragment
        activity = (ProfileActivity) getActivity();
        profileImageView = view.findViewById(R.id.profile_edit_imageview);
        progressBar = view.findViewById(R.id.profile_progressBar);
        profileNameEditText = view.findViewById(R.id.profile_name_editText);
        saveBtn = view.findViewById(R.id.save_button);
        cancelBtn = view.findViewById(R.id.cancel_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!activity.getSupportFragmentManager().popBackStackImmediate("BACK_STACK",0)){
                    Toast.makeText(activity, "Fragment created", Toast.LENGTH_SHORT).show();
                    activity.getSupportFragmentManager().beginTransaction()
                            .addToBackStack("BACK_STACK")
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container,ProfileInfoFragment.class,null).commit();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        addDataToProfile();
        if(activity.profileImageUri1 == null){
            profileImageView.setImageResource(R.drawable.profile_person_icon);
        }
        else {
            Glide.with(activity.getApplicationContext()).load(activity.profileImageUri1).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).into(profileImageView);
        }
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(activity,ProfileEditFragment.this);
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(activity, "onActivivty result", Toast.LENGTH_SHORT).show();
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode==activity.RESULT_OK){
                profileImageUri=result.getUri();
                Glide.with(this).load(profileImageUri).circleCrop().into(profileImageView);
                uploadToFirebase(profileImageUri);
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e=result.getError();
                Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addDataToProfile(){
        profileNameEditText.setText(activity.sharedPreferences.getString("name",""));
    }

    private void uploadToFirebase(Uri imageuri){
        StorageReference ref = activity.storageReference.child(activity.userMobile);
        ref.putFile(imageuri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri ->
                activity.databaseReference.child(activity.userMobile).child("profileImageUri").setValue(uri.toString()));
                Toast.makeText(activity, "Profile Image updated sucessfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
        }).addOnProgressListener(snapshot -> {
                progressBar.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(activity, "Profile Image updating failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveChanges(){
        String changedName = profileNameEditText.getText().toString();
        SharedPreferences.Editor editor = activity.sharedPreferences.edit();
        editor  .putString("name",changedName);
        editor.apply();
        activity.databaseReference.child(activity.userMobile).child("name").setValue(changedName);
    }

}