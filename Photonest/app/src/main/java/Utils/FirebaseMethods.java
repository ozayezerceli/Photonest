package Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.se302.photonest.MainActivity;

import DataModels.PhotoInformation;

import com.se302.photonest.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FirebaseMethods {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private PhotoInformation photoInformation;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private Activity mActivity;
    private String userID;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Activity activity) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();;
        myRef = mFirebaseDatabase.getReference();
        mActivity = activity;
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void addFollowingAndFollowers(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.following_node))
                .child(userID)
                .child(uid)
                .child(mActivity.getString(R.string.users_id))
                .setValue(uid);

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.followers_node))
                .child(uid)
                .child(userID)
                .child(mActivity.getString(R.string.users_id))
                .setValue(userID);
    }


    public void removeFollowingAndFollowers(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.following_node))
                .child(userID)
                .child(uid)
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.followers_node))
                .child(uid)
                .child(userID)
                .removeValue();
    }

    public void uploadNewPhoto(String photoType, final String caption, final int count, final String imgUrl,
                               Bitmap bm) {


        FilePaths filePaths = new FilePaths();
        //case1) new photo
        if (photoType.equals(mActivity.getString(R.string.new_photo))) {

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            if (bm == null) {
                bm = ImageManager.getBitmap(imgUrl);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> firebaseUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            addPhotoToDatabase(caption, uri.toString());
                        }
                    });

                    Toast.makeText(mActivity, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node


                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    mActivity.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mActivity, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mActivity, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }


                }
            });

        }
    }

    private void addPhotoToDatabase(@Nullable String caption, String url){
        List<String> hashTags = StringManipulation.getHashTags(caption);
        String newPhotoKey = myRef.child(mActivity.getString(R.string.dbname_photos)).push().getKey();
        photoInformation = new PhotoInformation();
        photoInformation.setCaption(caption);
        photoInformation.setDate_created(getTimestamp());
        photoInformation.setImage_path(url);
        //photoInformation.setHashTags(hashTags);
        photoInformation.setUser_id(userID);
        photoInformation.setPhoto_id(newPhotoKey);
        Map<String,Object> postValues = photoInformation.toMap();
        //insert into database
        Map<String, Object> hashtag_list = new HashMap<>();
        for(String hashtag:hashTags){
            hashtag_list.put("/hashTags/"+hashtag+"/"+newPhotoKey+"/photoId",newPhotoKey);
            hashtag_list.put("/hashTags/"+hashtag+"/hashTags",hashtag);
        }
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(mActivity.getString(R.string.dbname_user_photos)+userID+"/"+newPhotoKey,postValues);
        childUpdates.put(mActivity.getString(R.string.dbname_photos)+newPhotoKey,postValues);
        myRef.updateChildren(childUpdates);
        myRef.updateChildren(hashtag_list);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss", new Locale("en"));
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        return sdf.format(new Date());
    }

    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mActivity.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }
}
