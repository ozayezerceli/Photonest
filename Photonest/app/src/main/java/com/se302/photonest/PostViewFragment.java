package com.se302.photonest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import DataModels.Photo;
import DataModels.PhotoInformation;
import DataModels.UserInformation;
import Utils.SquareImageView;
import Utils.UniversalImageLoader;


public class PostViewFragment extends Fragment {

    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp;
    private ImageView mBackArrow, postOptions, mHeartRed, mHeartWhite, mProfileImage;
    private UserInformation userInformation;

    private PhotoInformation photo;

    public PostViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_view, container, false);
        setHasOptionsMenu(true);
        mPostImage = view.findViewById(R.id.post_image);
        mBackArrow = view.findViewById(R.id.backArrow);
        mBackLabel = view.findViewById(R.id.tvBackLabel);
        mCaption = view.findViewById(R.id.image_caption);
        mUsername = view.findViewById(R.id.username);
        mTimestamp = view.findViewById(R.id.image_time_posted);
        mHeartRed = view.findViewById(R.id.image_heart_red);
        mHeartWhite = view.findViewById(R.id.image_heart);
        mProfileImage = view.findViewById(R.id.profile_photo);
        postOptions = view.findViewById(R.id.btn_postOption);

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
                            break;
                        case R.id.delete_post:
                            break;
                    }
                    return false;
                }
            });
        }
        popup.show();
    }

    private void getPhotoDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
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
}
