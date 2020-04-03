package com.se302.photonest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import DataModels.UserInformation;


public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    //private FirebaseMethods mFirebaseMethods;
    private String userID;
    private FirebaseUser user;
    private UserInformation uInfo;


    //EditProfile Fragment widgets
    private EditText EditFullName, EditUsername, EditWebsite, EditBio;
    //private TextView mChangeProfilePhoto;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        EditFullName = view.findViewById(R.id.EditFullName);
        EditUsername = view.findViewById(R.id.EditUsername);
        EditWebsite = view.findViewById(R.id.EditWebsite);
        EditBio = view.findViewById(R.id.EditBio);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Users").child(userID);

        ImageView backarrow = view.findViewById(R.id.backArrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ImageView checkMark = view.findViewById(R.id.saveChanges);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                getActivity().finish();
            }
        });


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uInfo = dataSnapshot.getValue(UserInformation.class);
                EditFullName.setText(uInfo.getFullName());
                EditUsername.setText(uInfo.getUsername());
                EditBio.setText(uInfo.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    //The method below takes the input from user and updates the user information on firebase
    private void updateData(){
        String username = EditUsername.getText().toString();
        String fullname = EditFullName.getText().toString();
        String bio = EditBio.getText().toString();
        String websitelink = EditWebsite.getText().toString();
        if(!myRef.child("username").equals(username)){
            checkIfUsernameExists(username);
        }
        if(!myRef.child("fullFame").equals(fullname)){
            myRef.child("fullFame").setValue(fullname);
        }
        if(!myRef.child("bio").equals(bio)){
            myRef.child("bio").setValue(bio);
        }

    }

    //The method below checks if the new username already exist in the database,
    //if username exists, it does not allow user to change his username
    private void checkIfUsernameExists(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Users").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    myRef.child("username").setValue(username);
                    Toast.makeText(getActivity(), "saved username.", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
