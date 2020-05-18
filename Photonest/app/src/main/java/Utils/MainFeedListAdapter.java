package Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.se302.photonest.Model.FollowersActivity;
import com.se302.photonest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import DataModels.Photo;
import DataModels.UserInformation;
import com.mikhaellopez.circularimageview.CircularImageView;


public class MainFeedListAdapter extends ArrayAdapter<Object> {
    private int mResource;
    private Activity mContext;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private FirebaseMethods firebaseMethods;
    private Photo photo;
    private Egg mEgg;
    private String likeId;
    private String mLikesString;
    private boolean mLikedByCurrentUser = false;
    private StringBuilder mStringBuilder;

    public MainFeedListAdapter(@NonNull Activity context, int resource, @NonNull List<Object> list) {
        super(context, resource, list);
        mResource = resource;
        mContext = context;
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);
        mEgg = new Egg();
    }


    private static class ViewHolder {
        ImageView post;
        TextView likedBy;
        CircularImageView profileImage;
        TextView username;
        ImageView likedEgg;
        ImageView unlikedEgg;
        TextView caption;
        TextView date;
        TextView commentsLink;
        ProgressBar progressBar;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.post = convertView.findViewById(R.id.post_image_main);
            holder.progressBar = convertView.findViewById(R.id.progressBar);
            holder.likedBy = convertView.findViewById(R.id.image_likes_info_main_feed);
            holder.profileImage = convertView.findViewById(R.id.profile_photo_main);
            holder.username = convertView.findViewById(R.id.username_main);
            holder.likedEgg = convertView.findViewById(R.id.image_egg_liked);
            holder.unlikedEgg = convertView.findViewById(R.id.image_egg_not_liked);
            holder.caption = convertView.findViewById(R.id.image_caption_main);
            holder.date = convertView.findViewById(R.id.image_time_posted_main);
            holder.commentsLink = convertView.findViewById(R.id.image_comments_link_main);
            convertView.setTag(holder);
            setTags(holder.caption, holder.caption.getText().toString());

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Object object = getItem(position);
        setLikeListeners(holder.unlikedEgg,holder.likedEgg,object,holder.likedBy);
        holder.likedBy.setTag(position);
        holder.likedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pp=(Integer)v.getTag();
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",Objects.requireNonNull((Photo)getItem(pp)).getPhoto_id());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });
        if (Objects.requireNonNull(object).getClass() == Photo.class) {
            photo = (Photo) object;
            setUserLikes(holder.unlikedEgg,holder.likedEgg,mContext.getString(R.string.field_likes),photo.getPhoto_id(),holder.likedBy);
            setProfileInfo(photo.getUser_id(), holder.profileImage, holder.username);
            launchComment(mContext.getString(R.string.dbname_photos), photo.getPhoto_id(), convertView);
            GlideImageLoader.loadImageWithTransition(mContext, photo.getImage_path(), holder.post, holder.progressBar);
            holder.caption.setText(photo.getCaption());
            setTags(holder.caption, holder.caption.getText().toString());
            holder.date.setText(photo.getDate_created());
            reference.child(mContext.getString(R.string.dbname_photos)).child(photo.getPhoto_id()).child(mContext.getString(R.string.fieldComment)).orderByChild(mContext.getString(R.string.dateField))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()==0){
                                holder.commentsLink.setText("Be first to comment");
                            }else {
                                holder.commentsLink.setText("View all " + dataSnapshot.getChildrenCount() + " comments");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }



        return convertView;
    }


    private void setProfileInfo(final String userId, final CircularImageView profileImage, final TextView username) {

        Query query = reference.child(mContext.getString(R.string.users_node))
                .orderByKey().equalTo(Objects.requireNonNull(photo).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (userId.equals(mAuth.getCurrentUser().getUid())) {
                        profileImgUrl = Objects.requireNonNull(ds.getValue(UserInformation.class)).getImageurl();
                    }
                    GlideImageLoader.loadImageWithOutTransition(mContext, ds.getValue(UserInformation.class).getImageurl(), profileImage);
                    username.setText(Objects.requireNonNull(ds.getValue(UserInformation.class)).getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void launchComment(String mediaNode, String mediaId, View view) {

        ImageView comment = view.findViewById(R.id.comment_main);
        TextView viewComments = view.findViewById(R.id.image_comments_link_main);
        final Intent mediaIntent = new Intent(mContext, CommentActivity.class);
        mediaIntent.putExtra("mediaID", mediaId);
        mediaIntent.putExtra("mediaNode", mediaNode);
        mediaIntent.putExtra(mContext.getString(R.string.profilePhotoField), profileImgUrl);
        mediaIntent.putExtra("photoUser",photo.getUser_id());

        viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(mediaIntent);
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(mediaIntent);
            }
        });
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
    private void addNotifications(String userid, String postid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> hash = new HashMap<>();
        hash.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hash.put("text", "liked your post");
        hash.put("postid",postid);
        hash.put("ispost", true);

        ref.push().setValue(hash);
    }


    private void setUserLikes(final ImageView unlikedEgg, final ImageView likedEgg,String mediaNode, String mediaId,final TextView likedBy){

        Query query = reference.child(mediaNode).child(mediaId)
                .child(mContext.getString(R.string.field_likes));

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

                        if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
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
        if (Objects.requireNonNull(object).getClass()==Photo.class) {
            photo = (Photo)object;
            Query query = reference.child(mContext.getString(R.string.field_likes)).child(photo.getPhoto_id()).child(mContext.getString(R.string.field_likes));
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

                            if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                mLikedByCurrentUser = true;
                                likeId = ds.getKey();
                                unlikedEgg.setVisibility(View.GONE);
                                likedEgg.setVisibility(View.VISIBLE);
                                mEgg.toggleLike(unlikedEgg, likedEgg);
                                firebaseMethods.removeNewLike(mContext.getString(R.string.field_likes), photo.getPhoto_id(), likeId);
                                setUserLikes(unlikedEgg,likedEgg,mContext.getString(R.string.field_likes),photo.getPhoto_id(),likedBy);
                            }
                        }
                    }

                    if(!mLikedByCurrentUser){
                        unlikedEgg.setVisibility(View.VISIBLE);
                        likedEgg.setVisibility(View.GONE);
                        mEgg.toggleLike(unlikedEgg, likedEgg);
                        firebaseMethods.addNewLike(mContext.getString(R.string.field_likes),photo.getPhoto_id());
                        setUserLikes(unlikedEgg,likedEgg,mContext.getString(R.string.field_likes),photo.getPhoto_id(),likedBy);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

        }

    }


    private void setLikeText(final String user_id, final TextView likedBy){

        mStringBuilder = new StringBuilder();
        Query query = reference.child(mContext.getString(R.string.users_node)).child(user_id);
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
}