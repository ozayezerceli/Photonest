package com.se302.photonest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EmailVerification extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private Button activate_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        activate_btn = findViewById(R.id.activate_account_btn);

        activate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           auth = FirebaseAuth.getInstance();

                firebaseUser = auth.getCurrentUser();


                if(getUserProfile(auth)){ // if email is verified
                    Toast.makeText(EmailVerification.this, "Your account is verified!", Toast.LENGTH_LONG).show();
                    Intent emailVerification_Login_intent = new Intent(EmailVerification.this, LoginActivity.class);
                    startActivity(emailVerification_Login_intent);
                    finish();

                } else {
                    Toast.makeText(EmailVerification.this, "Your account is not verified!", Toast.LENGTH_LONG).show();
                    Intent emailVerification_register_intent = new Intent(EmailVerification.this, RegistrationActivity.class);
                    startActivity(emailVerification_register_intent);
                    finish();

                }




            }
        });
/*
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((width*6),(height*5));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);*/
    }
    private  boolean getUserProfile(FirebaseAuth auth) {
        // [START get_user_profile]
        FirebaseUser user = auth.getInstance().getCurrentUser();
        if (user != null) {

            String email = user.getEmail();
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            if (emailVerified) {
                return true;

            }

        }
        return true;
    }
}
