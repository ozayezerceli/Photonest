package Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.se302.photonest.R;

import DataModels.CustomDialog;
import DataModels.Photo;
import DataModels.PhotoInformation;

public class GlobalUtils {
    public static int rating;
    public static void showDialog(Photo photo, final String userId, final Context context, final DialogCallback dialogCallback){
        final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_dialog,null);
        dialog.setContentView(v);


        TextView btn_done = dialog.findViewById(R.id.btn_done);
        final RatingBar ratingBar = dialog.findViewById(R.id.post_rating_bar);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(context.getString(R.string.ratings)).child(photo.getPhoto_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equals(userId)){
                        String rate = ds.getValue().toString();
                        ratingBar.setRating(Integer.parseInt(rate));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    rating = (int) ratingBar.getRating();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogCallback != null)
                    dialogCallback.callback(rating);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showDialog(PhotoInformation photo, final String userId, final Context context, final DialogCallback dialogCallback){
        final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_dialog,null);
        dialog.setContentView(v);


        TextView btn_done = dialog.findViewById(R.id.btn_done);
        final RatingBar ratingBar = dialog.findViewById(R.id.post_rating_bar);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(context.getString(R.string.ratings)).child(photo.getPhoto_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equals(userId)){
                        String rate = ds.getValue().toString();
                        ratingBar.setRating(Integer.parseInt(rate));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = (int) ratingBar.getRating();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogCallback != null)
                    dialogCallback.callback(rating);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
