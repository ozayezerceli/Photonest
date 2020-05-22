package Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.se302.photonest.Model.FollowersActivity;
import com.se302.photonest.ProfileActivity;
import com.se302.photonest.R;
import com.se302.photonest.ViewProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import DataModels.Comment;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private Activity mContext;
    private int layoutResource;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;
    private long limit=20;
    private Egg mEgg;
    private String likeId;
    private UtilityInterface utilityInterface;
    private boolean mLikedByCurrentUser = false;
    private StringBuilder mStringBuilder;

    public CommentListAdapter(@NonNull Activity context, int resource, ArrayList<Comment> comments) {
        super(context,resource,comments);
        mContext = context;
        layoutResource = resource;
        utilityInterface = (UtilityInterface)mContext;
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);
        mEgg = new Egg();
    }

    private static class ViewHolder{

        ImageView profileImage;
        TextView comment,likes;
        TextView dateAdded;
        ImageView likedEgg;
        ImageView unlikedEgg;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.profileImage = convertView.findViewById(R.id.comment_profile);
            holder.comment = convertView.findViewById(R.id.comment_text);
            holder.likes = convertView.findViewById(R.id.commentLike);
            holder.dateAdded = convertView.findViewById(R.id.date_added);
            holder.likedEgg = convertView.findViewById(R.id.comment_heart_liked);
            holder.unlikedEgg = convertView.findViewById(R.id.comment_heart);
            convertView.setTag(holder);
            setTags(holder.comment, holder.comment.getText().toString());


        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        final Comment commentData = getItem(position);
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",commentData.getId());
                intent.putExtra("title", "commentLikes");
                mContext.startActivity(intent);
            }
        });
        GlideImageLoader.loadImageWithOutTransition(mContext, Objects.requireNonNull(commentData).getProfile_image(),holder.profileImage);
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentData.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mContext.startActivity(new Intent(mContext, ProfileActivity.class));
                }else {
                    Intent intent = new Intent(mContext, ViewProfileActivity.class);
                    intent.putExtra(mContext.getString(R.string.users_id), commentData.getUserId());
                    mContext.startActivity(intent);
                }
            }
        });
        String username = Objects.requireNonNull(commentData).getUser_name();
        String ss = username+" "+commentData.getComment();
        SpannableString str = new SpannableString(ss);
        ForegroundColorSpan fcsDark = new ForegroundColorSpan(Color.parseColor("#F99F63"));
        str.setSpan(fcsDark, 0, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.comment.setText(str);
        setTags(holder.comment, holder.comment.getText().toString());
        //Setting date
        holder.dateAdded.setText(commentData.getDate_added());
        setLikeListeners(holder.unlikedEgg,holder.likedEgg,commentData,holder.likes);
        setUserLikes(holder.unlikedEgg,holder.likedEgg,mContext.getString(R.string.field_likes_comment),commentData.getId(),holder.likes);

        if(position>=limit-1) {
            limit+=20;
            utilityInterface.loadMore(limit);
        }
        try{
            if(position == -1){
                //holder.likedEgg.setVisibility(View.GONE);
                holder.likes.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){

        }
        return  convertView;
    }

    private void setLikeListeners(final ImageView unlikedEgg, final ImageView likedEgg, final Comment comment, final TextView likes){

        unlikedEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(unlikedEgg,likedEgg,comment,likes);
                addNotificationsLike(comment.getUserId(), comment.getPhotoId());
            }
        });
        likedEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike(unlikedEgg, likedEgg, comment, likes);
            }
        });
    }

    private void addNotificationsLike(String userid, String postid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> hash = new HashMap<>();
        hash.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hash.put("text", "liked your comment");
        hash.put("postid",postid);
        hash.put("ispost", true);

        ref.push().setValue(hash);
    }

    private void toggleLike(final ImageView unlikedEgg, final ImageView likedEgg, final Comment comment, final TextView likes) {

        mLikedByCurrentUser  = false;
        Query query = reference.child(mContext.getString(R.string.field_likes_comment)).child(comment.getId()).child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
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
                            firebaseMethods.removeNewLike(mContext.getString(R.string.field_likes_comment), comment.getId(), likeId);
                            setUserLikes(unlikedEgg,likedEgg,mContext.getString(R.string.field_likes_comment),comment.getId(),likes);
                        }
                    }
                }

                if(!mLikedByCurrentUser){
                    Log.d("TAG","Datasnapshot doesn't exists");
                    unlikedEgg.setVisibility(View.VISIBLE);
                    likedEgg.setVisibility(View.GONE);
                    mEgg.toggleLike(unlikedEgg, likedEgg);
                    firebaseMethods.addNewLike(mContext.getString(R.string.field_likes_comment),comment.getId());
                    setUserLikes(unlikedEgg,likedEgg,mContext.getString(R.string.field_likes_comment),comment.getId(),likes);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setUserLikes(final ImageView unlikedEgg, final ImageView likedEgg, String mediaNode, final String mediaId, final TextView likes){
        Query query = reference.child(mediaNode).child(mediaId)
                .child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    unlikedEgg.setVisibility(View.VISIBLE);
                    likedEgg.setVisibility(View.GONE);
                    likes.setText("");
                }else {
                    likedEgg.setVisibility(View.GONE);
                    unlikedEgg.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            unlikedEgg.setVisibility(View.GONE);
                            likedEgg.setVisibility(View.VISIBLE);
                        }
                        setLikeText(mediaId,likes);

                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setLikeText(final String dataSnapshot, final TextView likes){
        mStringBuilder = new StringBuilder();
        Query query = reference.child(mContext.getString(R.string.field_likes_comment)).child(dataSnapshot).child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int likeNum = (int) dataSnapshot.getChildrenCount();
                if(likeNum == 1){
                    likes.setText(String.valueOf(likeNum)+" like");
                }else{
                    likes.setText(String.valueOf(likeNum)+" likes");
                }

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
