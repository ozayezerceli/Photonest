package com.se302.photonest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    EditText username, full_name, email, password, passwordAgain;
    Button register_btn;
    ImageView register_banner;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username= findViewById(R.id.username_registration);
        full_name= findViewById(R.id.fullname_registration);
        email= findViewById(R.id.email_registration);
        password= findViewById(R.id.password_registration);
        passwordAgain = findViewById(R.id.password_registration2);
        register_btn= findViewById(R.id.btn_register_page);
        register_banner = findViewById(R.id.register_banner);
        auth= FirebaseAuth.getInstance();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegistrationActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_username= username.getText().toString();
                String str_full_name= full_name.getText().toString();
                String str_email = email.getText().toString();
                String str_password= password.getText().toString();
                String str_password2 = passwordAgain.getText().toString();
                if(TextUtils.isEmpty(str_username) ||  TextUtils.isEmpty(str_full_name)
                || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_LONG).show();

                }else if(!str_password.equals(str_password2)){
                    Toast.makeText(RegistrationActivity.this, "Passwords are not the same", Toast.LENGTH_LONG).show();
                }
                else if( str_password.length() <6 ){
                    Toast.makeText(RegistrationActivity.this, "Password must be at least 6 character!", Toast.LENGTH_LONG).show();
                } else {
                    register(str_username, str_full_name,str_email,str_password);

                }
            }
        });

    }

    private void register(final String username, final String full_name, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser firebaseUser = auth.getCurrentUser();
                    String user_id= firebaseUser.getUid();
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    sendEmailVerification();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id",user_id);
                    hashMap.put("username", username.toLowerCase());
                    hashMap.put("fullName", full_name);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/photonest-11327.appspot.com/o/external-content.duckduckgo.com.jfif?alt=media&token=65fb1d0c-90cf-4d2b-b0ff-a02f6b79aef8");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                pd.dismiss();


                                Toast.makeText(RegistrationActivity.this,
                                        "Verification email sent to " + firebaseUser.getEmail(),
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegistrationActivity.this, EmailVerification.class); // Account is created, verification email is sent, user directs to the Email verification paga
                                // if user' account is verified it directs to main page from Email Verification Activity
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(RegistrationActivity.this, "You cannot register with this email or password.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void sendEmailVerification() {
        // [START send_email_verification]
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String msg="message:";
                            Log.d(msg, "Email sent.");
                        }
                    }
                });
        // [END send_email_verification]
    }




    public void buildActionCodeSettings() {
        // [START auth_build_action_code_settings]
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();
        // [END auth_build_action_code_settings]
    }



}
