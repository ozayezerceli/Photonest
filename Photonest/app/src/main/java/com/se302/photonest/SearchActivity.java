package com.se302.photonest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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

import DataModels.User;
import Utils.BottomNavigationViewHelper;
import Utils.UserListAdapter;

public class SearchActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 1;
    private Context mContext = SearchActivity.this;
    private UserListAdapter listAdapter;
    private ArrayList<User> searchList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mAuth = FirebaseAuth.getInstance();
        ListView listView = findViewById(R.id.search_list);
        EditText mSearch = findViewById(R.id.search_txt);
        searchList = new ArrayList<>();
        listAdapter = new UserListAdapter(mContext,R.layout.search_view,searchList);
        listView.setAdapter(listAdapter);


        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String keyword = s.toString().trim().toLowerCase();
                searchForMatch(keyword);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(searchList.get(position).getUser_id().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                    startActivity(new Intent(mContext, ProfileActivity.class));
                }else {
                    //Intent intent = new Intent(mContext, ViewProfileActivity.class);
                    //intent.putExtra(getString(R.string.users_id),searchList.get(position).getUser_id());
                    //startActivity(intent);
                }
            }
        });

        setupBottomNavBar();
    }

    private void searchForMatch(String keyword){
        searchList.clear();
        listAdapter.notifyDataSetChanged();

        if(keyword.length()>0) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            Query query = myRef.child(getString(R.string.users_node)).orderByChild(getString(R.string.usernameField))
                    .startAt(keyword).endAt(keyword + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    searchList.clear();
                    listAdapter.notifyDataSetChanged();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        System.out.println("WORKED---");
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

    private void setupBottomNavBar(){
        BottomNavigationViewEx bottomNavBar = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavBar);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavBar);
        Menu menu = bottomNavBar.getMenu();
        MenuItem mItem = menu.getItem(ACTIVITY_NUM);
        mItem.setChecked(true);
    }

}
