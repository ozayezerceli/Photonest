package Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.se302.photonest.R;

import java.util.List;
import java.util.Objects;

import DataModels.User;

public class UserListAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private int layoutResource;
    private DatabaseReference reference;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        layoutResource = resource;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
        TextView username = convertView.findViewById(R.id.search_username);
        TextView fullname = convertView.findViewById(R.id.search_fullname);
        final ImageView profileImage = convertView.findViewById(R.id.search_photo);
        final User user = getItem(position);

        username.setText(Objects.requireNonNull(user).getUsername());
        fullname.setText(user.getFullName());

        Query query = reference.child(mContext.getString(R.string.user_account_settings_node))
                .orderByChild(mContext.getString(R.string.usernameField))
                .equalTo(user.getUsername());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.place_holder_photo));
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    GlideImageLoader.loadImageWithOutTransition(mContext,
                            Objects.requireNonNull(ds.getValue(User.class)).getImageUrl(), profileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return convertView;
    }
}
