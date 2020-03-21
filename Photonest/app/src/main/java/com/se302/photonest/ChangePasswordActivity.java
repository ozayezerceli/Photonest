package com.se302.photonest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText password;
    private EditText confirmPassword;
    private Button Done;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        init();
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwd();
            }
        });
    }

    private void init(){
        password = findViewById(R.id.txt_ChangePwd);
        confirmPassword = findViewById(R.id.txt_ChangePwd2);
        Done = findViewById(R.id.btnChangePwd);
        auth = FirebaseAuth.getInstance();
    }

    private void changePwd(){
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            String password1 = password.getText().toString();
            String confirmPassword1 = confirmPassword.getText().toString();
            if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(confirmPassword1)) {
                Toast.makeText(this, "Password or ConfirmPassword can not be empty!", Toast.LENGTH_LONG).show();
            } else if (!password1.equals(confirmPassword1)) {
                Toast.makeText(this, "Password and ConfirmPassword must be the same!", Toast.LENGTH_LONG).show();
            } else {
                user.updatePassword(password1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Your password has been changed.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }else{
            Toast.makeText(ChangePasswordActivity.this, "A user must be logged in!", Toast.LENGTH_LONG).show();
        }
    }

}
