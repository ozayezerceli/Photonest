package com.se302.photonest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import java.util.ArrayList;

import DataModels.UserInformation;
import Utils.BottomNavigationViewHelper;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private TextView username;
    private static final int ACTIVITY_NUM = 4;
    private Context myContext = ProfileActivity.this;
    private String userID;

    ImageView image_profile;
    TextView posts, followers,following, fullname, bio;
    Button edit_profile;
    FirebaseUser firebaseUser;
    String profileid;

    ImageButton my_photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = findViewById(R.id.usernameTxt);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        FirebaseUser user =mAuth.getCurrentUser();
        userID = user.getUid();



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(myContext == null){
                    return;
                }
                ArrayList<String> array = showData(dataSnapshot);
                username.setText(array.get(1));
                fullname.setText(array.get(0));
                bio.setText(array.get(3));
                Glide.with(getApplicationContext()).load(array.get(4)).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ActionMenuView actionMenuView = findViewById(R.id.profile_menu_view);
        Menu bottomMenu = actionMenuView.getMenu();
        getMenuInflater().inflate(R.menu.profile_menu, bottomMenu);
        setupBottomNavBar();

        for (int i = 0; i < bottomMenu.size(); i++){
            bottomMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.profile_logout:
                            mAuth.signOut();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case R.id.profile_change_password:
                            Intent i = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                            startActivity(i);
                            finish();
                            break;
                        case R.id.profile_delete_account:
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user!=null){
                                new AlertDialog.Builder(ProfileActivity.this)
                                        .setTitle("Delete")
                                        .setMessage("Your account will be deleted. \nAre you sure?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(ProfileActivity.this, "Account deleted", Toast.LENGTH_LONG).show();
                                                            Intent i = new Intent(ProfileActivity.this,LoginActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                        else{
                                                            Toast.makeText(ProfileActivity.this, "Account could not be deleted", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();
                            }
                            break;
                    }
                    return false;
                }
            });
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs= myContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        profileid = prefs.getString("id" ,  FirebaseAuth.getInstance().getCurrentUser().getUid());


        image_profile= findViewById(R.id.profile_image);
        posts = findViewById(R.id.posts);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        fullname = findViewById(R.id.fullname_profile);
        bio= findViewById(R.id.bio_profile);
        edit_profile= findViewById(R.id.edit_profile_button);
        my_photos= findViewById(R.id.my_photos);

        getFollowers();
        getNrPosts();

        if(profileid.equals((firebaseUser.getUid()))){
            edit_profile.setText("EDIT PROFILE");
        } else{
            checkFollow();
        }


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if(btn.equals("Edit Profile")){
                    //go to edit profile

                } else if(btn.equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userID).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userID)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);

                } else if(btn.equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userID)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });


    }






    private  void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).exists()){
                    edit_profile.setText("Following");
                } else {
                    edit_profile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(userID).child("Followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(userID).child("Following");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i =0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Photo post= snapshot.getValue(Photo.class);
                    if(post.getPublisher().equals(userID)){ //getPublisher() doldurulacak
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private ArrayList showData(DataSnapshot dataSnapshot) {
        UserInformation uInfo = new UserInformation();
        ArrayList<String> array = new ArrayList<>();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            uInfo.setFullName(ds.child(userID).getValue(UserInformation.class).getFullName());
            uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail());uInfo.setImageurl(ds.child(userID).getValue(UserInformation.class).getImageurl());
            uInfo.setUsername(ds.child(userID).getValue(UserInformation.class).getUsername());
            uInfo.setBio(ds.child(userID).getValue(UserInformation.class).getBio());



            array.add(uInfo.getFullName());
            array.add(uInfo.getUsername());
            array.add(uInfo.getEmail());
            array.add(uInfo.getBio());
            array.add(uInfo.getImageurl());
        }
        return array;
    }


    private void setupBottomNavBar(){
        BottomNavigationViewEx bottomNavBar = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavBar);
        BottomNavigationViewHelper.enableNavigation(myContext, this, bottomNavBar);
        Menu menu = bottomNavBar.getMenu();
        MenuItem mItem = menu.getItem(ACTIVITY_NUM);
        mItem.setChecked(true);
    }


}
