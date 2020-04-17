package Utils;

import android.app.Activity;
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
import com.se302.photonest.R;

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

    public MainFeedListAdapter(@NonNull Activity context, int resource, @NonNull List<Object> list) {
        super(context, resource, list);
        mResource = resource;
        mContext = context;
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);
    }


    private static class ViewHolder{
        ImageView post;
        TextView likedBy;
        CircularImageView profileImage;
        TextView username;
        ImageView likedEgg;
        ImageView unlikedEgg;
        TextView caption;
        TextView date;
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
            holder.likedBy = convertView.findViewById(R.id.image_likes_info_main);
            holder.profileImage = convertView.findViewById(R.id.profile_photo_main);
            holder.username = convertView.findViewById(R.id.username_main);
            holder.likedEgg = convertView.findViewById(R.id.image_egg_liked);
            holder.unlikedEgg = convertView.findViewById(R.id.image_egg_not_liked);
            holder.caption = convertView.findViewById(R.id.image_caption_main);
            holder.date = convertView.findViewById(R.id.image_time_posted_main);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        Object object = getItem(position);

        if(Objects.requireNonNull(object).getClass()==Photo.class) {
            photo = (Photo) object;
            setProfileInfo(photo.getUser_id(), holder.profileImage, holder.username);
            GlideImageLoader.loadImageWithTransition(mContext, photo.getImage_path(), holder.post, holder.progressBar);
            holder.caption.setText(photo.getCaption());
            holder.date.setText(photo.getDate_created());
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
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}