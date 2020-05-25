package com.se302.photonest;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ProgressBar;
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
import java.util.Map;
import java.util.Objects;

import DataModels.Photo;
import DataModels.PhotoInformation;
import DataModels.UserInformation;
import Utils.DialogCallback;
import Utils.Egg;
import Utils.CommentActivity;
import Utils.FirebaseMethods;
import Utils.GlideImageLoader;
import Utils.GlobalUtils;
import Utils.SquareImageView;
import Utils.UniversalImageLoader;


public class PostViewFragment extends Fragment {

    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;

    private TextView mBackLabel, mCaption, mUsername, mTimestamp, likedBy, mtxtComment,rateTx1pv,rateTx2pv,rateTx3pv,rateTx4pv,rateTx5pv;
    private ImageView mBackArrow, postOptions, likedEgg, unlikedEgg, mProfileImage, mComments,rate1pv,rate2pv,rate3pv,rate4pv,rate5pv;
    private ProgressBar progressbar;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_view, container, false);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mPostImage = view.findViewById(R.id.post_image_main_view);
        mBackArrow = view.findViewById(R.id.backArrow);
        mBackLabel = view.findViewById(R.id.tvBackLabel);
        mCaption = view.findViewById(R.id.image_caption_main_view);
        mUsername = view.findViewById(R.id.username_main_view);
        mTimestamp = view.findViewById(R.id.image_time_posted_main_view);
        likedEgg = view.findViewById(R.id.image_egg_liked_view);
        likedBy = view.findViewById(R.id.image_likes_info_main_feed_view);
        unlikedEgg = view.findViewById(R.id.image_egg_not_liked_view);
        rate1pv = view.findViewById(R.id.rating_like_1_pv);
        rateTx1pv = view.findViewById(R.id.rating_like_text_1_pv);
        rate2pv = view.findViewById(R.id.rating_like_2_pv);
        rateTx2pv = view.findViewById(R.id.rating_like_text_2_pv);
        rate3pv = view.findViewById(R.id.rating_like_3_pv);
        rateTx3pv = view.findViewById(R.id.rating_like_text_3_pv);
        rate4pv = view.findViewById(R.id.rating_like_4_pv);
        rateTx4pv = view.findViewById(R.id.rating_like_text_4_pv);
        rate5pv = view.findViewById(R.id.rating_like_5_pv);
        rateTx5pv = view.findViewById(R.id.rating_like_text_5_pv);
        mProfileImage = view.findViewById(R.id.profile_photo_main_view);
        progressbar = view.findViewById(R.id.progressBar_view);
        postOptions = view.findViewById(R.id.btn_postOption);
        mComments = view.findViewById(R.id.comment_main_view);
        mtxtComment = view.findViewById(R.id.image_comments_link_main_view);
       // setTags(mCaption, mCaption.getText().toString());

        firebaseMethods = new FirebaseMethods(getActivity());
        ImageView mBackArrow = view.findViewById(R.id.backArrow);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getActivity().getIntent());
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
        getUsersRated();
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
        setRateListener();
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
    private void setRateListener(){
        unlikedEgg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog(view,photo);
                return false;
            }
        });
        likedEgg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog(view,photo);
                return false;
            }
        });
    }

    private void showDialog(View view, final PhotoInformation photo){
        GlobalUtils.showDialog(photo,mAuth.getCurrentUser().getUid(),getContext(), new DialogCallback() {
            @Override
            public void callback(int ratings) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(getContext().getString(R.string.ratings)+ "/" + photo.getPhoto_id() + "/" + mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getUid());
                childUpdates.put(getContext().getString(R.string.ratings)+ "/" + photo.getPhoto_id() + "/" + mAuth.getCurrentUser().getUid(),ratings);
                ref.updateChildren(childUpdates);
            }
        });
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
        GlideImageLoader.loadImageWithTransition(getContext(), photo.getImage_path(), mPostImage, progressbar);
        mTimestamp.setText(photo.getDate_created());
      //  mCaption.setText(photo.getCaption());
        setTags(mCaption, photo.getCaption());
        if(photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            postOptions.setVisibility(View.VISIBLE);
        }
        setUserLikes(unlikedEgg,likedEgg,getActivity().getString(R.string.field_likes),photo.getPhoto_id(),likedBy);
        setRatingNumbers(rateTx1pv,rateTx2pv,rateTx3pv,rateTx4pv,rateTx5pv,photo);
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

    private void getUsersRated(){
        rateTx1pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",photo.getPhoto_id());
                intent.putExtra("title", "rates");
                getContext().startActivity(intent);
            }
        });

        rateTx2pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",photo.getPhoto_id());
                intent.putExtra("title", "rates");
                getContext().startActivity(intent);
            }
        });

        rateTx3pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",photo.getPhoto_id());
                intent.putExtra("title", "rates");
                getContext().startActivity(intent);
            }
        });

        rateTx4pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",photo.getPhoto_id());
                intent.putExtra("title", "rates");
                getContext().startActivity(intent);
            }
        });

        rateTx5pv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pp=(Integer)view.getTag();
                Intent intent= new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",photo.getPhoto_id());
                intent.putExtra("title", "rates");
                getContext().startActivity(intent);
            }
        });
    }


    private void setRatingNumbers(final TextView rateTx1, final TextView rateTx2, final TextView rateTx3, final TextView rateTx4, final TextView rateTx5, PhotoInformation photo){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getActivity().getString(R.string.ratings)).child(photo.getPhoto_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> ratedUserList = new HashMap<>();
                int c1 = 0,c2 = 0,c3 = 0, c4 = 0 ,c5 = 0;
                rateTx1.setText(": "+c1);rateTx1.setTypeface(null, Typeface.NORMAL);rateTx1.setTextColor(Color.parseColor("#4E260E"));
                rateTx2.setText(": "+c2);rateTx2.setTypeface(null, Typeface.NORMAL);rateTx2.setTextColor(Color.parseColor("#4E260E"));
                rateTx3.setText(": "+c3);rateTx3.setTypeface(null, Typeface.NORMAL);rateTx3.setTextColor(Color.parseColor("#4E260E"));
                rateTx4.setText(": "+c4);rateTx4.setTypeface(null, Typeface.NORMAL);rateTx4.setTextColor(Color.parseColor("#4E260E"));
                rateTx5.setText(": "+c5);rateTx5.setTypeface(null, Typeface.NORMAL);rateTx5.setTextColor(Color.parseColor("#4E260E"));
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getValue().toString().equals("0")){
                        ds.getRef().removeValue();
                    }else if(ds.getValue().toString().equals("1")){
                        ratedUserList.put("rated1",ds.getKey());
                        c1 = c1+1;
                        rateTx1.setText(": "+c1);
                        rateTx1.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx1.setTypeface(null, Typeface.BOLD);
                        rateTx1.setClickable(true);
                    }else if(ds.getValue().toString().equals("2")){
                        ratedUserList.put("rated2",ds.getKey());
                        c2 = c2+1;
                        rateTx2.setText(": "+c2);
                        rateTx2.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx2.setTypeface(null, Typeface.BOLD);
                        rateTx2.setClickable(true);
                    }else if(ds.getValue().toString().equals("3")){
                        ratedUserList.put("rated3",ds.getKey());
                        c3 = c3+1;
                        rateTx3.setText(": "+c3);
                        rateTx3.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx3.setTypeface(null, Typeface.BOLD);
                        rateTx3.setClickable(true);
                    }else if(ds.getValue().toString().equals("4")){
                        ratedUserList.put("rated4",ds.getKey());
                        c4 = c4+1;
                        rateTx4.setText(": "+c4);
                        rateTx4.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx4.setTypeface(null, Typeface.BOLD);
                        rateTx4.setClickable(true);
                    }else{
                        ratedUserList.put("rated5",ds.getKey());
                        c5 = c5+1;
                        rateTx5.setText(": "+c5);
                        rateTx5.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx5.setTypeface(null, Typeface.BOLD);
                        rateTx5.setClickable(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    likedBy.setText("No Like");
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
                    mLikesString = length+" like";
                }else{
                    mLikesString = length+" likes";
                }
                SpannableString ss = new SpannableString(mLikesString);
                ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.parseColor("#AB4C11"));
                ss.setSpan(fcsOrange,0, mLikesString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                likedBy.setText(ss);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setTags(TextView pTextView, String pTagString) {
        final SpannableString string = new SpannableString(pTagString);

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

                    final String tag = pTagString.substring(start, i).replaceFirst("#", "");
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));
                            Intent i = new Intent(getActivity(), ResultActivity.class);
                            i.putExtra("hashTags", tag);
                            getActivity().startActivity(i);
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
