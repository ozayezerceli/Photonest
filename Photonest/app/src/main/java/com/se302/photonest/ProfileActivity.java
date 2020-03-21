package com.se302.photonest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;

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

import Utils.BottomNavigationViewHelper;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private TextView username;
    private static final int ACTIVITY_NUM = 4;
    private Context myContext = ProfileActivity.this;
    private String userID;

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
                username.setText(showData(dataSnapshot));
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
    }

    private String showData(DataSnapshot dataSnapshot) {
        UserInformation uInfo = new UserInformation();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            //uInfo.setFullName(ds.child(userID).getValue(UserInformation.class).getFullName());
            //uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail());
            uInfo.setUsername(ds.child(userID).getValue(UserInformation.class).getUsername());
            //uInfo.setBio(ds.child(userID).getValue(UserInformation.class).getBio());
            //uInfo.setImageurl(ds.child(userID).getValue(UserInformation.class).getImageurl());
            /*
            ArrayList<String> array = new ArrayList<>();
            array.add(uInfo.getFullName());
            array.add(uInfo.getUsername());
            array.add(uInfo.getEmail());
            array.add(uInfo.getBio());
            array.add(uInfo.getImageurl()); */
        }
        return uInfo.getUsername();
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
