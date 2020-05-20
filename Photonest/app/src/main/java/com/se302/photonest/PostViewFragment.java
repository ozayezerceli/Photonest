package com.se302.photonest;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import DataModels.Photo;
import DataModels.PhotoInformation;
import DataModels.UserInformation;
import Utils.Egg;
import Utils.CommentActivity;
import Utils.FirebaseMethods;
import Utils.SquareImageView;
import Utils.UniversalImageLoader;


public class PostViewFragment extends Fragment {

    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;

    private TextView mBackLabel, mCaption, mUsername, mTimestamp, likedBy, mtxtComment;
    private ImageView mBackArrow, postOptions, likedEgg, unlikedEgg, mProfileImage, mComments;
    private UserInformation userInformation;
    private boolean mLikedByCurrentUser = false;

    private String currentprofile = "";
    private String profilePhotoURL = "";


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
    Intent intent;
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
        mComments = view.findViewById(R.id.speech_bubble);
        mtxtComment = view.findViewById(R.id.image_comments_link);
       // setTags(mCaption, mCaption.getText().toString());

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
        getCurrentProfile();
        launchComment(getString(R.string.dbname_photos), photo.getPhoto_id());
        myRef.child(getContext().getString(R.string.dbname_photos)).child(photo.getPhoto_id()).child(getContext().getString(R.string.fieldComment)).orderByChild(getContext().getString(R.string.dateField))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()==0){
                            mtxtComment.setText("Be first to comment");
                        }else {
                            mtxtComment.setText("View all " + dataSnapshot.getChildrenCount() + " comments");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return view;
    }

    private void getCurrentProfile() {

        Query query = myRef.child(getContext().getString(R.string.users_node))
                .orderByKey().equalTo(mAuth.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    currentprofile = Objects.requireNonNull(ds.getValue(UserInformation.class)).getImageurl();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void launchComment(final String mediaNode, final String mediaId) {

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mediaIntent = new Intent(getActivity(), CommentActivity.class);
                mediaIntent.putExtra("mediaID", mediaId);
                mediaIntent.putExtra("mediaNode", mediaNode);
                mediaIntent.putExtra(getString(R.string.profilePhotoField), currentprofile);
                mediaIntent.putExtra("photoUser",photo.getUser_id());
                startActivity(mediaIntent);
            }
        });

        mtxtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mediaIntent = new Intent(getActivity(), CommentActivity.class);
                mediaIntent.putExtra("mediaID", mediaId);
                mediaIntent.putExtra("mediaNode", mediaNode);
                mediaIntent.putExtra(getString(R.string.profilePhotoField), currentprofile);
                mediaIntent.putExtra("photoUser",photo.getUser_id());
                startActivity(mediaIntent);
            }
        });
    }

    private void init(){
        photo = getPhotoFromBundle();
        UniversalImageLoader.setImage(photo.getImage_path(), mPostImage, null, "");
        mTimestamp.setText(photo.getDate_created());
      //  mCaption.setText(photo.getCaption());
        setTags(mCaption, photo.getCaption());
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
                            String new_caption = firebaseMethods.editPost(photo_id, photo, getContext(),mCaption);

                            break;
                        case R.id.delete_post:
                            final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("Your post will be deleted. \nAre you sure?");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                                    progressDialog.setMessage("Deleting post!");
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progressDialog.show();
                                    firebaseMethods.deletePost(photo);
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
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
                profilePhotoURL = userInformation.getImageurl();
                mProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            getActivity().startActivity(new Intent(getActivity(), ProfileActivity.class));
                        }else {
                            Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                            intent.putExtra(getActivity().getString(R.string.users_id), photo.getUser_id());
                            getActivity().startActivity(intent);
                        }
                    }
                });
                mUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            getActivity().startActivity(new Intent(getActivity(), ProfileActivity.class));
                        }else {
                            Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                            intent.putExtra(getActivity().getString(R.string.users_id), photo.getUser_id());
                            getActivity().startActivity(intent);
                        }
                    }
                });
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
                addNotifications(photo.getUser_id(), photo.getPhoto_id());
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
                    likedBy.setText("No Likes!");
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
    private void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || pTagString.charAt(i) == '\n' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#F99F63"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }

    private void addNotifications(String userid, String postid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> hash = new HashMap<>();
        hash.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hash.put("text", "liked your post");
        hash.put("postid",postid);
        hash.put("ispost", true);

        ref.push().setValue(hash);
    }


}
