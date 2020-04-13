package com.se302.photonest;



import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;


import Utils.FirebaseMethods;
import Utils.UniversalImageLoader;

public class UploadPostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private Uri imageUri;
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private Intent intent;
    private ImageView close, image_added;
    private TextView post;
    private EditText description;
    private Button addLocation;
    Spannable mspanable;
    int hashTagIsComing = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        close = findViewById(R.id.upload_post_close);
        image_added = findViewById(R.id.upload_post_image_added);
        post = findViewById(R.id.upload_post_post);
        description = findViewById(R.id.upload_post_description);
        addLocation = findViewById(R.id.upload_post_add_location_btn);
        setupFirebaseAuth();
        mFirebaseMethods = new FirebaseMethods(UploadPostActivity.this);
        //storageReference = FirebaseStorage.getInstance().getReference("gs://photonest-11327.appspot.com/");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadPostActivity.this,PostActivity.class));
                finish();
            }
        });



        mspanable = description.getText();
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String startChar = null;

                try{
                    startChar = Character.toString(charSequence.charAt(start));
                    Log.i(getClass().getSimpleName(), "CHARACTER OF NEW WORD: " + startChar);
                }
                catch(Exception ex){
                    startChar = " ";
                }

                if (startChar.equals("#")) {
                    tagCheck(charSequence.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }

                if(startChar.equals(" ")){
                    hashTagIsComing = 0;
                }

                if(hashTagIsComing != 0) {
                    tagCheck(charSequence.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UploadPostActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = description.getText().toString();

                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl,null);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null,bitmap);
                }
            }
        });

        setImage();

    }


    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    //Toast.makeText(UploadPostActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    //Toast.makeText(UploadPostActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void setImage(){
        intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            UniversalImageLoader.setImage(imgUrl, image_added, null, mAppend);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            image_added.setImageBitmap(bitmap);
        }
    }

    private void tagCheck(String s, int start, int end) {
        mspanable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_hashtag)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}



