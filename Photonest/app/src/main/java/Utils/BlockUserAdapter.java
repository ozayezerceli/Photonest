package Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.se302.photonest.BlockUserActivity;
import com.se302.photonest.R;
import com.se302.photonest.ViewProfileActivity;

import java.util.List;

import DataModels.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class BlockUserAdapter extends RecyclerView.Adapter<BlockUserAdapter.ImageViewHolder>{

    private Activity mContext;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;
    FirebaseMethods firebaseMethods;

    public BlockUserAdapter(Activity context, List<User> users, boolean isFragment){
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
        firebaseMethods = new FirebaseMethods(mContext);
    }

    @NonNull
    @Override
    public BlockUserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.block_user_item, parent, false);
        return new BlockUserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlockUserAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_block.setVisibility(View.VISIBLE);
        checkBlock(user.getId(), holder.btn_block);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullName());
        Glide.with(mContext).load(user.getImageUrl()).into(holder.image_profile);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.btn_block.setVisibility(View.GONE);
        }

        holder.btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn_block.getText().toString().equals("Block User")) {
                    firebaseMethods.blockUser(user.getId());
                    holder.btn_block.setText("Blocked");

                    // addNotification(user.getId());
                } else {
                    firebaseMethods.unblockUser(user.getId());
                    holder.btn_block.setText("Block User");
                }
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("user_id", user.getId());
                editor.apply();
                Intent intent = new Intent(mContext, ViewProfileActivity.class);
                intent.putExtra("user_id", user.getId());
                mContext.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_block;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_useritem);
            fullname = itemView.findViewById(R.id.fullname_useritem);
            image_profile = itemView.findViewById(R.id.image_profile_useritem);
            btn_block = itemView.findViewById(R.id.btn_block);
        }
    }

    private void checkBlock(final String viewUserID, final Button btn_block){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Blocked").child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(viewUserID).exists()){
                    btn_block.setText("Blocked");
                }else{
                    btn_block.setText("Block User");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
