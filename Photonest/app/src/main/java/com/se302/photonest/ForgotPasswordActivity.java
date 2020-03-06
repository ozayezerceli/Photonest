package com.se302.photonest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText txtEmail;
    private Button SendEmail;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        init();

        //The code below is the method of send button in the forgot password page, it sends the verification link to user's email.
        SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = txtEmail.getText().toString();
                if(TextUtils.isEmpty(useremail)){
                    Toast.makeText(ForgotPasswordActivity.this,"You need to enter a valid Email!", Toast.LENGTH_LONG).show();
                }else{
                    auth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your email!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            }else {
                                String msg = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this,"Error occurred : "+ msg , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void init(){
        txtEmail = findViewById(R.id.txtEmail_ForgotPassword);
        SendEmail = findViewById(R.id.btnSend_ForgotPassword);
        auth = FirebaseAuth.getInstance();
    }
}
