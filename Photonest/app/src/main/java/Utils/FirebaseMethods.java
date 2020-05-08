package Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.se302.photonest.LoginActivity;
import com.se302.photonest.MainActivity;

import DataModels.Comment;
import DataModels.Photo;
import DataModels.PhotoInformation;

import com.se302.photonest.ProfileActivity;
import com.se302.photonest.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
    private Context Context;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public FirebaseMethods(Activity activity) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        ;
        myRef = mFirebaseDatabase.getReference();
        mActivity = activity;
        Context = activity.getApplicationContext();
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void addFollowingAndFollowers(String uid) {

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


    public void removeFollowingAndFollowers(String uid) {

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

    public int getFollowerCount(DataSnapshot dataSnapshot, String uid) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mActivity.getString(R.string.followers_node))
                .child(uid).getChildren()) {
            count++;
        }
        return count;
    }

    public int getFollowingCount(DataSnapshot dataSnapshot, String uid) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mActivity.getString(R.string.following_node))
                .child(uid).getChildren()) {
            count++;
        }
        return count;
    }

    public void uploadNewPhoto(String photoType, final String caption, final int count, final String imgUrl,
                               Bitmap bm, final String post_location) {


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
                            addPhotoToDatabase(caption, uri.toString(), post_location);
                            Intent intent = new Intent(mActivity, MainActivity.class);
                            mActivity.startActivity(intent);
                            Toast.makeText(mActivity, "Photo upload success", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //navigate to the main feed so the user can see their photo
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mActivity, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mActivity, "Photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }


                }
            });

        } else if (photoType.equals(Context.getString(R.string.profile_photo))) {
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

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
                    Task<Uri> firebaseUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    Toast.makeText(mActivity, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    setProfilePhoto(firebaseUrl.toString());

              /*    ((AccountSettingsActivity)Context).setViewPager(
                            ((AccountSettingsActivity)Context).pagerAdapter
                                    .getFragmentNumber(Context.getString(R.string.edit_profile_fragment))
                    ); */

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

    private void setProfilePhoto(String url) {

        myRef.child(mActivity.getString(R.string.users_node))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mActivity.getString(R.string.imageurl))
                .setValue(url);
    }


    private void addPhotoToDatabase(@Nullable String caption, String url, String post_location) {
        List<String> hashTags = StringManipulation.getHashTags(caption);
        String newPhotoKey = myRef.child(mActivity.getString(R.string.dbname_photos)).push().getKey();
        photoInformation = new PhotoInformation();
        photoInformation.setCaption(caption);
        photoInformation.setDate_created(getTimestamp());
        photoInformation.setImage_path(url);
        //photoInformation.setHashTags(hashTags);
        photoInformation.setUser_id(userID);
        photoInformation.setPhoto_id(newPhotoKey);
        photoInformation.setLocation(post_location);
        Map<String, Object> postValues = photoInformation.toMap();
        //insert into database
        Map<String, Object> hashtag_list = new HashMap<>();
        for (String hashtag : hashTags) {
            hashtag_list.put("/hashTags/" + hashtag + "/" + newPhotoKey + "/photoId", newPhotoKey);
            hashtag_list.put("/hashTags/" + hashtag + "/hashTags", hashtag);
        }
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(mActivity.getString(R.string.dbname_user_photos) + userID + "/" + newPhotoKey, postValues);
        childUpdates.put(mActivity.getString(R.string.dbname_photos) + newPhotoKey, postValues);
        myRef.updateChildren(childUpdates);
        myRef.updateChildren(hashtag_list);
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss", new Locale("en"));
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        return sdf.format(new Date());
    }

    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mActivity.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {
            count++;
        }
        return count;
    }

    public void deleteUserAccount() {
        final DatabaseReference user_info_ref = mFirebaseDatabase.getReference().child("Users");
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("Delete")
                    .setMessage("Your account will be deleted. \nAre you sure?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            user_info_ref.child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mActivity, "Account deleted", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(mActivity, LoginActivity.class);
                                                mActivity.startActivity(i);
                                                mActivity.finish();
                                            } else {
                                                Toast.makeText(mActivity, "Account could not be deleted", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    public void addNewComment(final String node, final String mediaId, final String comment) {

        final String commentId = myRef.push().getKey();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss", new Locale("en"));
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        final String dateAdded = sdf.format(Calendar.getInstance().getTime());
        Query query = myRef.child(mActivity.getString(R.string.users_node)).child(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String username = Objects.requireNonNull(dataSnapshot.child(mActivity.getString(R.string.usernameField)).getValue()).toString();
                String profileImage = Objects.requireNonNull(dataSnapshot.child(mActivity.getString(R.string.profilePhotoField)).getValue()).toString();
                Comment comment_model = new Comment(commentId,comment, dateAdded, username, profileImage, 0);
                myRef.child(node).child(mediaId).child(mActivity.getString(R.string.fieldComment))
                        .child(Objects.requireNonNull(commentId)).setValue(comment_model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(Context, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /*Code below deletes the choosen post from firebase database and storage */
    public void deletePost(PhotoInformation photo1){
        final PhotoInformation photo = photo1;
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo.getImage_path());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                myRef.child(mActivity.getString(R.string.dbname_photos)).child(photo.getPhoto_id()).removeValue();
                myRef.child(mActivity.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(photo.getPhoto_id()).removeValue();
                List<String> hashTags = StringManipulation.getHashTags(photo.getCaption());
                for(String hashTag : hashTags){
                    myRef.child("hashTags").child(hashTag).child(photo.getPhoto_id()).removeValue();
                }

                Toast.makeText(mActivity,"Photo deleted successfully.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mActivity, ProfileActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(mActivity,"Error occured during process!",Toast.LENGTH_LONG).show();
            }
        });
    }



    public void editPost(final String postid, final PhotoInformation photo,Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(context);
        editText.setContentDescription("new caption");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, photo,editText);

        alertDialog.setNeutralButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        List<String> hashTags = StringManipulation.getHashTags(photo.getCaption());
                        for(String hashTag : hashTags){
                            myRef.child("hashTags").child(hashTag).child(photo.getPhoto_id()).removeValue(); //first remove photoid from old hastags
                        }
                        //then updates new ones
                        myRef.child(mActivity.getString(R.string.dbname_photos)).child(photo.getPhoto_id()).child("caption").setValue(editText.getText().toString()); //dbname_photos updated
                        myRef.child(mActivity.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(photo.getPhoto_id())
                                .child("caption").setValue(editText.getText().toString()); //dbname_user_photos updated

                        List<String> hashTags2 = StringManipulation.getHashTags(editText.getText().toString()); //new caption's tags taken

                        String newPhotoKey = myRef.child(mActivity.getString(R.string.dbname_photos)).push().getKey();

                        Map<String, Object> hashtag_list = new HashMap<>();
                        for (String hashtag : hashTags2) {
                            hashtag_list.put("/hashTags/" + hashtag + "/" + photo.getPhoto_id() + "/photoId", photo.getPhoto_id());
                            hashtag_list.put("/hashTags/" + hashtag + "/hashTags", hashtag);
                        }
                        myRef.updateChildren(hashtag_list);
                        Toast.makeText(mActivity,"Photo is edited  successfully.",Toast.LENGTH_LONG).show();

                    }
                });
        alertDialog.show();


    }

    private void getText(String postid, PhotoInformation photo,final EditText editText){
        final  PhotoInformation photo1= photo;
        editText.setText(photo1.getCaption());

    }


    public void addNewLike(String node,String mediaId){

        String likesId = myRef.push().getKey();
        myRef.child(node).child(mediaId).child(mActivity.getString(R.string.field_likes))
                .child(likesId).child(mActivity.getString(R.string.users_id)).setValue(userID);
    }


    public void removeNewLike(String node,String mediaId,String likesId){

        myRef.child(node).child(mediaId).child(mActivity.getString(R.string.field_likes))
                .child(likesId).removeValue();
    }

}



