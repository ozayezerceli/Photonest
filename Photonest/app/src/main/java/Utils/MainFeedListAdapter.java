package Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.se302.photonest.ProfileActivity;
import com.se302.photonest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import DataModels.Photo;
import DataModels.UserInformation;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.se302.photonest.ResultActivity;
import com.se302.photonest.ViewProfileActivity;

import static android.view.View.GONE;


public class MainFeedListAdapter extends ArrayAdapter<Object> {
    private int mResource;
    private Activity mContext;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String currentprofile = "";
    private FirebaseMethods firebaseMethods;
    private Photo photo;
    private Egg mEgg;
    private String likeId;
    private String mLikesString = "";
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
        ImageView post,rate1,rate2,rate3,rate4,rate5;
        TextView likedBy,rateTx1,rateTx2,rateTx3,rateTx4,rateTx5;
        CircularImageView profileImage;
        TextView username;
        ImageView likedEgg;
        ImageView unlikedEgg;
        TextView caption;
        TextView date, location;
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
            holder.rate1 = convertView.findViewById(R.id.rating_like_1);
            holder.rateTx1 = convertView.findViewById(R.id.rating_like_text_1);
            holder.rate2 = convertView.findViewById(R.id.rating_like_2);
            holder.rateTx2 = convertView.findViewById(R.id.rating_like_text_2);
            holder.rate3 = convertView.findViewById(R.id.rating_like_3);
            holder.rateTx3 = convertView.findViewById(R.id.rating_like_text_3);
            holder.rate4 = convertView.findViewById(R.id.rating_like_4);
            holder.rateTx4 = convertView.findViewById(R.id.rating_like_text_4);
            holder.rate5 = convertView.findViewById(R.id.rating_like_5);
            holder.rateTx5 = convertView.findViewById(R.id.rating_like_text_5);
            holder.profileImage = convertView.findViewById(R.id.profile_photo_main);
            holder.username = convertView.findViewById(R.id.username_main);
            holder.likedEgg = convertView.findViewById(R.id.image_egg_liked);
            holder.unlikedEgg = convertView.findViewById(R.id.image_egg_not_liked);
            holder.caption = convertView.findViewById(R.id.image_caption_main);
            holder.date = convertView.findViewById(R.id.image_time_posted_main);
            holder.location = convertView.findViewById(R.id.location_main);
            holder.commentsLink = convertView.findViewById(R.id.image_comments_link_main);
            convertView.setTag(holder);
            setTags(holder.caption, holder.caption.getText().toString());

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Object object = getItem(position);
        holder.unlikedEgg.setTag(position);
        holder.unlikedEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pp=(Integer)v.getTag();
                toggleLike(holder.unlikedEgg,holder.likedEgg,Objects.requireNonNull(getItem(pp)),holder.likedBy);
                addNotifications(photo.getUser_id(), photo.getPhoto_id());
            }
        });
        holder.likedEgg.setTag(position);
        holder.likedEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pp=(Integer)v.getTag();
                toggleLike(holder.unlikedEgg,holder.likedEgg,Objects.requireNonNull(getItem(pp)),holder.likedBy);
            }
        });
        holder.unlikedEgg.setTag(position);
        holder.unlikedEgg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pp=(Integer)view.getTag();
                showDialog(view,Objects.requireNonNull((Photo)getItem(pp)));
                return false;
            }
        });
        holder.likedEgg.setTag(position);
        holder.likedEgg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pp=(Integer)view.getTag();
                showDialog(view,Objects.requireNonNull((Photo)getItem(pp)));
                return false;
            }
        });
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
        holder.rateTx1.setTag(position);
        holder.rateTx1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pp=(Integer)view.getTag();
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",Objects.requireNonNull((Photo)getItem(pp)).getPhoto_id());
                intent.putExtra("title", "1 Rated List");
                mContext.startActivity(intent);
            }
        });
        holder.rateTx2.setTag(position);
        holder.rateTx2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pp=(Integer)view.getTag();
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",Objects.requireNonNull((Photo)getItem(pp)).getPhoto_id());
                intent.putExtra("title", "2 Rated List");
                mContext.startActivity(intent);
            }
        });
        holder.rateTx3.setTag(position);
        holder.rateTx3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pp=(Integer)view.getTag();
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",Objects.requireNonNull((Photo)getItem(pp)).getPhoto_id());
                intent.putExtra("title", "3 Rated List");
                mContext.startActivity(intent);
            }
        });
        holder.rateTx4.setTag(position);
        holder.rateTx4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pp=(Integer)view.getTag();
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",Objects.requireNonNull((Photo)getItem(pp)).getPhoto_id());
                intent.putExtra("title", "4 Rated List");
                mContext.startActivity(intent);
            }
        });
        holder.rateTx5.setTag(position);
        holder.rateTx5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pp=(Integer)view.getTag();
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",Objects.requireNonNull((Photo)getItem(pp)).getPhoto_id());
                intent.putExtra("title", "5 Rated List");
                mContext.startActivity(intent);
            }
        });
        if (Objects.requireNonNull(object).getClass() == Photo.class) {
            photo = (Photo) object;
            setUserLikes(holder.unlikedEgg,holder.likedEgg,mContext.getString(R.string.field_likes),photo.getPhoto_id(),holder.likedBy);
            setRatingNumbers(holder.rateTx1,holder.rateTx2,holder.rateTx3,holder.rateTx4,holder.rateTx5,photo);
            setProfileInfo(photo.getUser_id(), holder.profileImage, holder.username);
            holder.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mContext.startActivity(new Intent(mContext, ProfileActivity.class));
                    }else {
                        Intent intent = new Intent(mContext, ViewProfileActivity.class);
                        intent.putExtra(mContext.getString(R.string.users_id), photo.getUser_id());
                        mContext.startActivity(intent);
                    }
                }
            });
            getCurrentProfile();
            launchComment(mContext.getString(R.string.dbname_photos), photo.getPhoto_id(), convertView);
            GlideImageLoader.loadImageWithTransition(mContext, photo.getImage_path(), holder.post, holder.progressBar);
            holder.caption.setText(photo.getCaption());
            setTags(holder.caption, holder.caption.getText().toString());
            holder.date.setText(photo.getDate_created());
            if(photo.getLocation().length()>=1) {
                holder.location.setText(photo.getLocation());
            }else{
                holder.location.setText("Location Unknown");
            }
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
                    GlideImageLoader.loadImageWithOutTransition(mContext, ds.getValue(UserInformation.class).getImageurl(), profileImage);
                    username.setText(Objects.requireNonNull(ds.getValue(UserInformation.class)).getUsername());
                    username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                mContext.startActivity(new Intent(mContext, ProfileActivity.class));
                            }else {
                                Intent intent = new Intent(mContext, ViewProfileActivity.class);
                                intent.putExtra(mContext.getString(R.string.users_id),userId);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getCurrentProfile() {

        Query query = reference.child(mContext.getString(R.string.users_node))
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

    private void launchComment(final String mediaNode, final String mediaId, View view) {

        ImageView comment = view.findViewById(R.id.comment_main);
        TextView viewComments = view.findViewById(R.id.image_comments_link_main);


        viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mediaIntent = new Intent(mContext, CommentActivity.class);
                mediaIntent.putExtra("mediaID", mediaId);
                mediaIntent.putExtra("mediaNode", mediaNode);
                mediaIntent.putExtra(mContext.getString(R.string.profilePhotoField), currentprofile);
                mediaIntent.putExtra("photoUser",photo.getUser_id());
                mContext.startActivity(mediaIntent);
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mediaIntent = new Intent(mContext, CommentActivity.class);
                mediaIntent.putExtra("mediaID", mediaId);
                mediaIntent.putExtra("mediaNode", mediaNode);
                mediaIntent.putExtra(mContext.getString(R.string.profilePhotoField), currentprofile);
                mediaIntent.putExtra("photoUser",photo.getUser_id());
                mContext.startActivity(mediaIntent);
            }
        });
    }

    private void setRatingNumbers(final TextView rateTx1, final TextView rateTx2, final TextView rateTx3, final TextView rateTx4, final TextView rateTx5, Photo photo){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.ratings)).child(photo.getPhoto_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> ratedUserList = new HashMap<>();
                int c1 = 0,c2 = 0,c3 = 0, c4 = 0 ,c5 = 0;
                rateTx1.setText(" "+c1);rateTx1.setTypeface(null, Typeface.NORMAL);rateTx1.setTextColor(Color.parseColor("#4E260E"));rateTx1.setClickable(false);
                rateTx2.setText(" "+c2);rateTx2.setTypeface(null, Typeface.NORMAL);rateTx2.setTextColor(Color.parseColor("#4E260E"));rateTx2.setClickable(false);
                rateTx3.setText(" "+c3);rateTx3.setTypeface(null, Typeface.NORMAL);rateTx3.setTextColor(Color.parseColor("#4E260E"));rateTx3.setClickable(false);
                rateTx4.setText(" "+c4);rateTx4.setTypeface(null, Typeface.NORMAL);rateTx4.setTextColor(Color.parseColor("#4E260E"));rateTx4.setClickable(false);
                rateTx5.setText(" "+c5);rateTx5.setTypeface(null, Typeface.NORMAL);rateTx5.setTextColor(Color.parseColor("#4E260E"));rateTx5.setClickable(false);
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getValue().toString().equals("0")){
                        ds.getRef().removeValue();
                    }else if(ds.getValue().toString().equals("1")){
                        ratedUserList.put("rated1",ds.getKey());
                        c1 = c1+1;
                        rateTx1.setText(" "+c1);
                        rateTx1.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx1.setTypeface(null, Typeface.BOLD);
                        rateTx1.setClickable(true);
                    }else if(ds.getValue().toString().equals("2")){
                        ratedUserList.put("rated2",ds.getKey());
                        c2 = c2+1;
                        rateTx2.setText(" "+c2);
                        rateTx2.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx2.setTypeface(null, Typeface.BOLD);
                        rateTx2.setClickable(true);
                    }else if(ds.getValue().toString().equals("3")){
                        ratedUserList.put("rated3",ds.getKey());
                        c3 = c3+1;
                        rateTx3.setText(" "+c3);
                        rateTx3.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx3.setTypeface(null, Typeface.BOLD);
                        rateTx3.setClickable(true);
                    }else if(ds.getValue().toString().equals("4")){
                        ratedUserList.put("rated4",ds.getKey());
                        c4 = c4+1;
                        rateTx4.setText(" "+c4);
                        rateTx4.setTextColor(Color.parseColor("#AB4C11"));
                        rateTx4.setTypeface(null, Typeface.BOLD);
                        rateTx4.setClickable(true);
                    }else{
                        ratedUserList.put("rated5",ds.getKey());
                        c5 = c5+1;
                        rateTx5.setText(" "+c5);
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
    private void showDialog(View view, final Photo photo){
        GlobalUtils.showDialog(photo,mAuth.getCurrentUser().getUid(),mContext, new DialogCallback() {
            @Override
            public void callback(int ratings) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(mContext.getString(R.string.ratings)+ "/" + photo.getPhoto_id() + "/" + mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getUid());
                childUpdates.put(mContext.getString(R.string.ratings)+ "/" + photo.getPhoto_id() + "/" + mAuth.getCurrentUser().getUid(),ratings);
                ref.updateChildren(childUpdates);
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


    private void setUserLikes(final ImageView unlikedEgg, final ImageView likedEgg, String mediaNode, final String mediaId, final TextView likedBy){

        Query query = reference.child(mediaNode).child(mediaId)
                .child(mContext.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    likedBy.setClickable(false);
                    unlikedEgg.setVisibility(View.VISIBLE);
                    likedEgg.setVisibility(GONE);
                    likedBy.setText("No like");

                }else {
                    likedBy.setClickable(true);
                    likedEgg.setVisibility(GONE);
                    unlikedEgg.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            unlikedEgg.setVisibility(GONE);
                            likedEgg.setVisibility(View.VISIBLE);
                        }
                        //String ds1 = ds.child("user_id").getValue().toString();
                        setLikeText(mediaId,likedBy);
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
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(mContext.getString(R.string.field_likes)).child(photo.getPhoto_id()).child(mContext.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        mLikesString = "";
                        mLikedByCurrentUser = false;
                        likedEgg.setVisibility(GONE);
                        unlikedEgg.setVisibility(View.VISIBLE);
                    }else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                mLikedByCurrentUser = true;
                                likeId = ds.getKey();
                                unlikedEgg.setVisibility(GONE);
                                likedEgg.setVisibility(View.VISIBLE);
                                mEgg.toggleLike(unlikedEgg, likedEgg);
                                firebaseMethods.removeNewLike(mContext.getString(R.string.field_likes), photo.getPhoto_id(), likeId);
                                setUserLikes(unlikedEgg,likedEgg,mContext.getString(R.string.field_likes),photo.getPhoto_id(),likedBy);
                            }
                        }
                    }
                    if(!mLikedByCurrentUser){
                        unlikedEgg.setVisibility(View.VISIBLE);
                        likedEgg.setVisibility(GONE);
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


    private void setLikeText(final String photo_id, final TextView likedBy){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.field_likes)).child(photo_id).child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long likeCount = 0;
                if(!(dataSnapshot.getChildrenCount() == 0)){
                    likeCount = dataSnapshot.getChildrenCount();
                }
                if(likeCount == 1){
                    mLikesString = likeCount+" like";
                }else if(likeCount > 1){
                    mLikesString = likeCount+" likes";
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

                    final String tag = pTagString.substring(start, i).replaceFirst("#", "");
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));
                            Intent i = new Intent(mContext, ResultActivity.class);
                            i.putExtra("hashTags", tag);
                            mContext.startActivity(i);
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