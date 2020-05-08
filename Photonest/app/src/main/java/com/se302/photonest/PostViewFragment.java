package com.se302.photonest;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.se302.photonest.Model.FollowersActivity;

import java.util.ArrayList;
import java.util.Objects;

import DataModels.PhotoInformation;
import DataModels.UserInformation;
import Utils.Egg;
import Utils.FirebaseMethods;
import Utils.SquareImageView;
import Utils.UniversalImageLoader;


public class PostViewFragment extends Fragment {

    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, likedBy;
    private ImageView mBackArrow, postOptions, likedEgg, unlikedEgg, mProfileImage;
    private UserInformation userInformation;
    private boolean mLikedByCurrentUser = false;
    private PhotoInformation photo;
    private String likeId;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase database;
    private GestureDetector detector;
    private StringBuilder mStringBuilder;
    private String mLikesString;
    private Egg mEgg;
    public PostViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_view, container, false);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mPostImage = view.findViewById(R.id.post_image);
        mBackArrow = view.findViewById(R.id.backArrow);
        mBackLabel = view.findViewById(R.id.tvBackLabel);
        mCaption = view.findViewById(R.id.image_caption);
        mUsername = view.findViewById(R.id.username);
        mTimestamp = view.findViewById(R.id.image_time_posted);
        likedEgg = view.findViewById(R.id.image_egg_liked_post);
        likedBy = view.findViewById(R.id.image_likes_info_main);
        unlikedEgg = view.findViewById(R.id.image_egg_unliked_post_view);
        mProfileImage = view.findViewById(R.id.profile_photo);
        postOptions = view.findViewById(R.id.btn_postOption);
        firebaseMethods = new FirebaseMethods(getActivity());
        ImageView mBackArrow = view.findViewById(R.id.backArrow);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        postOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpopupMenu(view);
            }
        });
        mEgg = new Egg();
        setLikeListeners(unlikedEgg,likedEgg,photo,likedBy);
        likedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), FollowersActivity.class);
                intent.putExtra("id",photo.getPhoto_id());
                intent.putExtra("title", "likes");
                startActivity(intent);
            }
        });
        init();
        getPhotoDetails();


        return view;
    }

    private void init(){
        photo = getPhotoFromBundle();
        UniversalImageLoader.setImage(photo.getImage_path(), mPostImage, null, "");
        mTimestamp.setText(photo.getDate_created());
        mCaption.setText(photo.getCaption());
        if(photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            postOptions.setVisibility(View.VISIBLE);
        }
        setUserLikes(unlikedEgg,likedEgg,getActivity().getString(R.string.field_likes),photo.getPhoto_id(),likedBy);
    }

    public void showpopupMenu(View v) {

        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.view_post_menu, popup.getMenu());
        for (int i = 0; i < popup.getMenu().size(); i++){
            popup.getMenu().getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit_post:

                            String photo_id = photo.getPhoto_id();
                            firebaseMethods.editPost(photo_id, photo, getContext());
                            break;
                        case R.id.delete_post:
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("Your profile photo will be deleted. \nAre you sure?");
                            alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    firebaseMethods.deletePost(photo);
                                }
                            });
                            alert.show();
                            break;
                    }
                    return false;
                }
            });
        }
        popup.show();
    }

    private void getPhotoDetails(){
        myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef
                .child("Users")
                .orderByChild("id")
                .equalTo(photo.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                     userInformation = singleSnapshot.getValue(UserInformation.class);
                }
                UniversalImageLoader.setImage(userInformation.getImageurl(), mProfileImage, null, "");
                mUsername.setText(userInformation.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private PhotoInformation getPhotoFromBundle(){
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable("photo");
        }else{
            return null;
        }
    }

    private void setLikeListeners(final ImageView unlikedEgg, final ImageView likedEgg, final Object object, final TextView likedBy){

        unlikedEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(unlikedEgg,likedEgg,object,likedBy);
            }
        });
        likedEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(unlikedEgg,likedEgg,object,likedBy);
            }
        });
    }

    private void setUserLikes(final ImageView unlikedEgg, final ImageView likedEgg,String mediaNode, String mediaId,final TextView likedBy){

        Query query = myRef.child(mediaNode).child(mediaId)
                .child(getActivity().getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    likedBy.setClickable(false);
                    unlikedEgg.setVisibility(View.VISIBLE);
                    likedEgg.setVisibility(View.GONE);
                    likedBy.setText("No one liked this post! Be first!");
                }else {
                    likedBy.setClickable(true);
                    likedEgg.setVisibility(View.GONE);
                    unlikedEgg.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.child(getActivity().getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            unlikedEgg.setVisibility(View.GONE);
                            likedEgg.setVisibility(View.VISIBLE);
                        }
                        String ds1 =  ds.child("user_id").getValue().toString();
                        setLikeText(ds1,likedBy);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void toggleLike(final ImageView unlikedEgg, final ImageView likedEgg, Object object, final TextView likedBy) {

        mLikedByCurrentUser  = false;

            Query query = myRef.child(getActivity().getString(R.string.field_likes)).child(photo.getPhoto_id()).child(getActivity().getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists()){
                        mLikesString = "";
                        mLikedByCurrentUser = false;
                        likedEgg.setVisibility(View.GONE);
                        unlikedEgg.setVisibility(View.VISIBLE);
                    }else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.child(getActivity().getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                mLikedByCurrentUser = true;
                                likeId = ds.getKey();
                                unlikedEgg.setVisibility(View.GONE);
                                likedEgg.setVisibility(View.VISIBLE);
                                mEgg.toggleLike(unlikedEgg, likedEgg);
                                firebaseMethods.removeNewLike(getActivity().getString(R.string.field_likes), photo.getPhoto_id(), likeId);
                                setUserLikes(unlikedEgg,likedEgg,getActivity().getString(R.string.field_likes),photo.getPhoto_id(),likedBy);
                            }
                        }
                    }

                    if(!mLikedByCurrentUser){
                        unlikedEgg.setVisibility(View.VISIBLE);
                        likedEgg.setVisibility(View.GONE);
                        mEgg.toggleLike(unlikedEgg, likedEgg);
                        firebaseMethods.addNewLike(getActivity().getString(R.string.field_likes),photo.getPhoto_id());
                        setUserLikes(unlikedEgg,likedEgg,getActivity().getString(R.string.field_likes),photo.getPhoto_id(),likedBy);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });


    }


    private void setLikeText(String dataSnapshot, final TextView likedBy){

        mStringBuilder = new StringBuilder();
        Query query = myRef.child(getActivity().getString(R.string.users_node)).child(dataSnapshot);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userInfo = new ArrayList<>();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    userInfo.add(singleSnapshot.getValue().toString());
                }
                mStringBuilder.append(userInfo.get(5));
                mStringBuilder.append(",");
                String[] splitUsers = mStringBuilder.toString().split(",");
                int length = splitUsers.length;
                System.out.println("users: "+mStringBuilder.toString());
                System.out.println("liked users:"+splitUsers[0]+"-"+length);
                if(length == 1){
                    mLikesString = "Liked by " + splitUsers[0];
                }
                else if(length == 2){
                    mLikesString = "Liked by " + splitUsers[0]
                            + " and " + splitUsers[1];
                }
                else if(length == 3){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + " and " + splitUsers[2];

                }
                else if(length == 4){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + ", " + splitUsers[2]
                            + " and " + splitUsers[3];
                }
                else if(length > 4){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + ", " + splitUsers[2]
                            + " and " + (dataSnapshot.getChildrenCount() - 3) + " others";
                }
                likedBy.setText(mLikesString);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
