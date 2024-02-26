package com.example.akpfoundaries;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.database.core.operation.AckUserWrite;

import java.util.HashMap;

public class ProfileInfoFragment extends Fragment {

    ImageView profileImageView;
    TextView profileNameTextView;
    TextView profilePhoneTextView;

    Button editButton;
    TextView changePasswordButton;
    TextView logout_Button;


    ProfileActivity activity;

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        activity = (ProfileActivity) getActivity();

        profileNameTextView = view.findViewById(R.id.profile_name_textview);
        profilePhoneTextView = view.findViewById(R.id.profile_phone_textview);
        profileImageView = view.findViewById(R.id.profile_imageview);
        editButton = view.findViewById(R.id.edit_profile_button);
        logout_Button = view.findViewById(R.id.logout_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);

        // Inflate the layout for this fragment
        addDataToProfile();
        if(activity.profileImageUri1 == null){
            profileImageView.setImageResource(R.drawable.profile_person_icon);
        }
        else {
            Glide.with(activity.getApplicationContext()).load(activity.profileImageUri1).circleCrop().transition(DrawableTransitionOptions.withCrossFade()).into(profileImageView);
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,ProfileEditFragment.class,null)
                        .setReorderingAllowed(true)
                        .addToBackStack("BACK_STACK")
                        .commit();
            }
        });

        logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = activity.sharedPreferences.edit();
                edit.clear();
                edit.apply();
                startActivity(new Intent(activity,LoginActivity.class));
                activity.finish();
            }
        });


        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Change password");
                final View cutomLayout = getLayoutInflater().inflate(R.layout.change_password_layout,null);
                EditText newPassword =  cutomLayout.findViewById(R.id.changeNew_password_edittext);
                EditText confirmPassword = cutomLayout.findViewById(R.id.changeConfirm_password_edittext);
                builder.setView(cutomLayout);
                builder.setPositiveButton("Change", ((dialogInterface, i) -> {
                    String newPasswordTxt = newPassword.getText().toString();
                    String confirmPasswordTxt = confirmPassword.getText().toString();
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("password",newPasswordTxt);
                    if(newPasswordTxt.isBlank() || confirmPasswordTxt.isBlank()){
                        Toast.makeText(activity, "Please fill all data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(newPasswordTxt.equals(confirmPasswordTxt)){
                            activity.databaseReference.child(activity.userMobile).updateChildren(map);
                            Toast.makeText(activity, "password change sucessful", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(activity, "Passwords does not match", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
                builder.setNegativeButton("cancel",((dialogInterface, i) -> {
                    Toast.makeText(activity, "cancelled", Toast.LENGTH_SHORT).show();
                }));
                builder.setCancelable(true);
                builder.setIcon(R.drawable.akp_foundaries_logo);
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        return view;
    }

    private void addDataToProfile(){
        profileNameTextView.setText(activity.sharedPreferences.getString("name",""));
        profilePhoneTextView.setText(activity.userMobile);
    }

}