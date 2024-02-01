package com.example.akpfoundaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://akp-foundaries-default-rtdb.firebaseio.com/");

    SharedPreferences sharedPreferences;

    private final String login_number = "LOGIN_NUMBER";
    private final String login_password = "LOGIN_PASSWORD";

    private boolean isReady = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        sharedPreferences = getSharedPreferences("AKPSharedPreferenceFile",MODE_PRIVATE);
        String user_number = sharedPreferences.getString(login_number,"");
        String user_password = sharedPreferences.getString(login_password,"");
        if(!(user_number.length() == 0) || !(user_password.length() == 0)){
            checkUser(user_number,user_password);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                        splashScreenView,
                        View.TRANSLATION_Y,
                        10f,
                        -splashScreenView.getHeight()
                );
                slideUp.setInterpolator(new AnticipateInterpolator());
                slideUp.setDuration(800L);
                // Call SplashScreenView.remove at the end of your custom animation.
                slideUp.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            splashScreenView.remove();
                            System.out.println("Splash screen removed ..........................................................................");
                        }
                    }
                });
                // Run your animation.
                slideUp.start();
            });
        }
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Check whether the initial data is ready.
                        if (isReady) {
                            // The content is ready. Start drawing.
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        }
                        dismiss();
                        return false;
                    }
                });

        System.out.println("Setting content     ......................................................................................................................................");
        setContentView(R.layout.activity_login);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        final EditText mobile = findViewById(R.id.login_mobile_number);
        final EditText password = findViewById(R.id.login_password);
        final TextView register = findViewById(R.id.login_register_now_btn);
        final Button login = findViewById(R.id.login_submit_btn);

        login.setOnClickListener(v -> {
            final String mobileTxt = mobile.getText().toString();
            final String passwordTxt = password.getText().toString();

            if(mobileTxt.isBlank() || passwordTxt.isBlank())
                Toast.makeText(LoginActivity.this, "Fill all data", Toast.LENGTH_SHORT).show();
            else{
                checkUser(mobileTxt,passwordTxt);
            }
        });

        register.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            finish();
        });
    }

    private void checkUser(String userNumber,String userPassword){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userNumber)){
                    final String getPassword = snapshot.child(userNumber).child("password").getValue(String.class);
                    if(getPassword.equals(userPassword)){
                        edit.putString("LOGIN_NUMBER",userNumber);
                        edit.putString("LOGIN_PASSWORD",userPassword);
                        edit.commit();
                        Toast.makeText(LoginActivity.this, "Login Sucessful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Invalid name or password", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "user not registered", Toast.LENGTH_SHORT).show();
                    edit.remove(login_number);
                    edit.remove(login_password);
                    edit.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void dismiss(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isReady = true;
            }
        }, 1000);
    }

}