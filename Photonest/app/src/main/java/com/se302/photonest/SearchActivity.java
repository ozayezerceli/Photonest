package com.se302.photonest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Objects;

import DataModels.Hashtag;
import DataModels.User;
import Utils.BottomNavigationViewHelper;
import Utils.HashListAdapter;
import Utils.UserListAdapter;

public class SearchActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 1;
    private Context mContext = SearchActivity.this;
    private UserListAdapter listAdapter;
    private HashListAdapter HListAdapter;
    private ArrayList<User> searchList;
    private ArrayList<Hashtag> searchHashList;
    private FirebaseAuth mAuth;
    private boolean usersClicked = true;
    private TextView hashBtn;
    private TextView userBtn;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mAuth = FirebaseAuth.getInstance();
        final ListView listView = findViewById(R.id.search_list);
        EditText mSearch = findViewById(R.id.search_txt);
        hashBtn = findViewById(R.id.hashtagsBtn);
        userBtn = findViewById(R.id.usersBtn);
        searchList = new ArrayList<>();
        searchHashList = new ArrayList<Hashtag>();
        listAdapter = new UserListAdapter(mContext,R.layout.search_view,searchList);
        HListAdapter = new HashListAdapter(mContext,R.layout.search_hash_view,searchHashList);
        listView.setAdapter(listAdapter);
        userBtn.setSelected(true);
        userBtn.setPaintFlags(userBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        hashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userBtn.setPaintFlags(0);
                hashBtn.setPaintFlags(hashBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                listView.setAdapter(HListAdapter);
                if(keyword !=null) {
                    searchForMatchHashs(keyword);
                }
                usersClicked=false;
            }

        });
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashBtn.setPaintFlags(0);
                userBtn.setPaintFlags(userBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                listView.setAdapter(listAdapter);
                if(keyword != null) {
                    searchForMatchUsers(keyword);
                }
                usersClicked=true;
            }

        });

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString().trim().toLowerCase();
                if(usersClicked==true){
                    searchForMatchUsers(keyword);
                }else {
                    searchForMatchHashs(keyword);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(usersClicked) {
                    if (searchList.get(position).getId().equals(mAuth.getCurrentUser().getUid())) {
                        startActivity(new Intent(mContext, ProfileActivity.class));
                    } else {
                        Intent intent = new Intent(mContext, ViewProfileActivity.class);
                        intent.putExtra(getString(R.string.users_id), searchList.get(position).getId());
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Intent intent = new Intent(mContext, ResultActivity.class);
                    intent.putExtra("hashTags", searchHashList.get(position).getHashTags());
                    startActivity(intent);
                    finish();
                }
            }
        });

        setupBottomNavBar();
    }

    private void searchForMatchUsers(String keyword){
        searchList.clear();
        listAdapter.notifyDataSetChanged();

        if(keyword.length()>0) {
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            Query query = myRef.child(getString(R.string.users_node))
                    .orderByChild(getString(R.string.usernameField)).startAt(keyword).endAt(keyword+"\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    searchList.clear();
                    listAdapter.notifyDataSetChanged();
                    for (final DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            searchList.add(singleSnapshot.getValue(User.class));
                            listAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    private void searchForMatchHashs(String keyword){
        searchHashList.clear();
        HListAdapter.notifyDataSetChanged();

        if(keyword.length()>0) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            Query query = myRef.child("hashTags").orderByChild("hashTags").startAt(keyword).endAt(keyword+"\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   searchHashList.clear();
                    HListAdapter.notifyDataSetChanged();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        searchHashList.add(singleSnapshot.getValue(Hashtag.class));
                        HListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void setupBottomNavBar(){
        BottomNavigationViewEx bottomNavBar = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavBar);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavBar);
        Menu menu = bottomNavBar.getMenu();
        MenuItem mItem = menu.getItem(ACTIVITY_NUM);
        mItem.setChecked(true);
    }
}
