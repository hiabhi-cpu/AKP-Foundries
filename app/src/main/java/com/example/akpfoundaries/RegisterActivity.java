package com.example.akpfoundaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://akp-foundaries-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }


        final EditText name = findViewById(R.id.register_name);
        final EditText mobile = findViewById(R.id.register_mobile_number);
        final EditText password = findViewById(R.id.register_password);
        final EditText confirmPassword = findViewById(R.id.register_confirm_password);
        final Button submit = findViewById(R.id.register_submit_btn);
        final TextView loginNOwBtn = findViewById(R.id.register_login_now_btn);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nameTxt = name.getText().toString();
                final String mobileTxt = mobile.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String confirmPasswordTxt = confirmPassword.getText().toString();

                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(mobileTxt)){
                            Toast.makeText(RegisterActivity.this, "Phone already registered", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            if(nameTxt.isBlank() || mobileTxt.isBlank() || passwordTxt.isBlank() || confirmPasswordTxt.isBlank()){
                                Toast.makeText(RegisterActivity.this, "Please fill all entries", Toast.LENGTH_SHORT).show();
                            }
                            else if(!passwordTxt.equals(confirmPasswordTxt)){
                                Toast.makeText(RegisterActivity.this, "Passwords does not match", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                databaseReference.child("users").child(mobileTxt).child("name").setValue(nameTxt);
                                databaseReference.child("users").child(mobileTxt).child("password").setValue(passwordTxt);

                                Toast.makeText(RegisterActivity.this, "Register sucessfull", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        loginNOwBtn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        });
    }
}