package com.se302.photonest;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import DataModels.Photo;
import Utils.FirebaseMethods;
import Utils.MainFeedListAdapter;

public class MainFragment extends Fragment {
    View view;
    private Context mContext;
    private ListView mainList;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ArrayList<Object> mediaList;
    private MainFeedListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main,container,false);
        mainList = view.findViewById(R.id.mainListView);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mediaList = new ArrayList<>();
        myRef = database.getReference();
        setSelfFollowing();

        addContent();
        adapter = new MainFeedListAdapter(Objects.requireNonNull(getActivity()),R.layout.mainfeed_item,mediaList);
        mainList.setAdapter(adapter);

    }


    private void addContent(){

        Query query = myRef.child(mContext.getString(R.string.following_node))
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mediaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    getPhoto(ds);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }




    private void getPhoto(DataSnapshot ds){

        Query query = myRef.child(mContext.getString(R.string.dbname_photos)).orderByChild(mContext.getString(R.string.users_id)).equalTo(ds.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Photo photo = ds.getValue(Photo.class);
                    mediaList.add(0,photo);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void setSelfFollowing(){

        Query query = myRef.child(mContext.getString(R.string.following_node)).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .orderByChild(mContext.getString(R.string.users_id)).equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    new FirebaseMethods(getActivity()).addFollowingAndFollowers(mAuth.getCurrentUser().getUid());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }



}