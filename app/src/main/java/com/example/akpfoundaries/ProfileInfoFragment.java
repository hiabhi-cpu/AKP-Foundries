package com.example.akpfoundaries;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ProfileInfoFragment extends Fragment {

    ImageView profileImageView;
    TextView profileNameTextView;
    TextView profilePhoneTextView;

    Button editButton;
    Button logout_Button;


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
        return view;
    }

    private void addDataToProfile(){
        profileNameTextView.setText(activity.sharedPreferences.getString("name",""));
        profilePhoneTextView.setText(activity.userMobile);
    }

}