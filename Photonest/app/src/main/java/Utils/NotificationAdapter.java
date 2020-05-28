package Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.se302.photonest.MainFragment;
import com.se302.photonest.NotificationFragment;
import com.se302.photonest.PostViewFragment;
import com.se302.photonest.ProfileActivity;
import com.se302.photonest.R;
import com.se302.photonest.ViewProfileActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import DataModels.Like;
import DataModels.Notification;
import DataModels.Photo;
import DataModels.PhotoInformation;
import DataModels.User;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Notification notification = mNotification.get(position);
        holder.text_not.setText(notification.getText());
        getUserInfo(holder.image_profile, holder.username_not, notification.getUserid());

        if(notification.isIspost()){
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
        } else{
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                if(notification.isIspost()){
                    if(!notification.getText().contains("liked your comment")) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("dbname_user_photos").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final ArrayList<PhotoInformation> photoArrayList = new ArrayList<PhotoInformation>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("photo_id").getValue().toString().equals(notification.getPostid())) {
                                        PhotoInformation photoInformation = new PhotoInformation();
                                        photoInformation.setCaption(snapshot.child("caption").getValue().toString());
                                        photoInformation.setPhoto_id(snapshot.child("photo_id").getValue().toString());
                                        photoInformation.setUser_id(snapshot.child("user_id").getValue().toString());
                                        List<String> hashTags = StringManipulation.getHashTags(photoInformation.getCaption());
                                        photoInformation.setHashTags(hashTags);
                                        photoInformation.setDate_created(snapshot.child("date_created").getValue().toString());
                                        photoInformation.setImage_path(snapshot.child("image_path").getValue().toString());

                                        List<Like> likesList = new ArrayList<Like>();
                                        for (DataSnapshot dSnapshot : snapshot
                                                .child("likes").getChildren()) {
                                            Like like = new Like();
                                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                                            likesList.add(like);
                                        }
                                        photoInformation.setLikes(likesList);


                                        photoArrayList.add(photoInformation);
                                    }
                                }
                                PostViewFragment post_view_fragment = new PostViewFragment();
                                Bundle args = new Bundle();
                                args.putParcelable("photo", photoArrayList.get(0));
                                args.putInt("activityNumber", 1);
                                post_view_fragment.setArguments(args);

                                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_notification, post_view_fragment).
                                        addToBackStack("View Post").commit();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("dbname_photos").child(notification.getPostid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final ArrayList<PhotoInformation> photoArrayList = new ArrayList<PhotoInformation>();
                                    PhotoInformation photoInformation = new PhotoInformation();
                                    photoInformation.setCaption(snapshot.child("caption").getValue().toString());
                                    photoInformation.setPhoto_id(snapshot.child("photo_id").getValue().toString());
                                    photoInformation.setUser_id(snapshot.child("user_id").getValue().toString());
                                    List<String> hashTags = StringManipulation.getHashTags(photoInformation.getCaption());
                                    photoInformation.setHashTags(hashTags);
                                    photoInformation.setDate_created(snapshot.child("date_created").getValue().toString());
                                    photoInformation.setImage_path(snapshot.child("image_path").getValue().toString());

                                    List<Like> likesList = new ArrayList<Like>();
                                    for (DataSnapshot dSnapshot : snapshot
                                            .child("likes").getChildren()) {
                                        Like like = new Like();
                                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                                        likesList.add(like);
                                    }
                                    photoInformation.setLikes(likesList);


                                    photoArrayList.add(photoInformation);
                                PostViewFragment post_view_fragment = new PostViewFragment();
                                Bundle args = new Bundle();
                                args.putParcelable("photo", photoArrayList.get(0));
                                args.putInt("activityNumber", 1);
                                post_view_fragment.setArguments(args);


                                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_notification, post_view_fragment).
                                        addToBackStack("View Post").commit();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    if(notification.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mContext.startActivity(new Intent(mContext, ProfileActivity.class));
                         }else {
                         Intent intent = new Intent(mContext, ViewProfileActivity.class);
                         intent.putExtra(mContext.getString(R.string.users_id), notification.getUserid());
                         mContext.startActivity(intent);
                            }
                        }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image;
        public TextView username_not, text_not;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.profile_image_notification);
            post_image = itemView.findViewById(R.id.post_image_notification);
            username_not = itemView.findViewById(R.id.username_notification);
            text_not = itemView.findViewById(R.id.comment_notification);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(User.class)!=null) {
                    GlideImageLoader.loadImageWithOutTransition(mContext, Objects.requireNonNull(dataSnapshot.getValue(User.class)).getImageUrl(), imageView);
                    username.setText(dataSnapshot.getValue(User.class).getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView imageView, String postID){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dbname_photos").child(postID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Photo.class) != null) {
                    GlideImageLoader.loadImageWithOutTransition(mContext, Objects.requireNonNull(dataSnapshot.getValue(Photo.class)).getImage_path(), imageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
