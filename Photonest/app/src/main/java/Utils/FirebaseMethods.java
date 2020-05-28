package Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import DataModels.PhotoInformation;

import com.se302.photonest.PostViewFragment;
import com.se302.photonest.ProfileActivity;
import com.se302.photonest.R;
import com.se302.photonest.ResultActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

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

        addNotifications(uid);

    }

    private void addNotifications(String userid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> hash = new HashMap<>();
        hash.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hash.put("text", "has followed you");
        hash.put("postid","");
        hash.put("ispost", false);
        ref.push().setValue(hash);
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
            String id = UUID.randomUUID().toString();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (id));

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
                            mActivity.finish();
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
        String newPhotoKey = myRef.child(mActivity.getString(R.string.dbname_photos)).push().getKey();
        photoInformation = new PhotoInformation();
        photoInformation.setCaption(caption);
        List<String> hashTags = StringManipulation.getHashTags(photoInformation.getCaption());
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
                .child(userID)
                .getChildren()) {
            count++;
        }
        return count;
    }
    public int getImageCount2(DataSnapshot dataSnapshot, String id) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mActivity.getString(R.string.dbname_user_photos))
                .child(id)
                .getChildren()) {
            count++;
        }
        return count;
    }

    public void deleteUserAccount() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog
                    .setTitle("Delete")
                    .setMessage("Your account will be deleted. \nConfirm by typing your password.");
            final EditText input = new EditText(mActivity);
            input.setHint("Password");
            input.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String password = input.getText().toString();
                            final AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(),password);
                            final String user_id = user.getUid();
                            deleteUserPosts(user_id);
                            final DatabaseReference user_info_ref = mFirebaseDatabase.getReference().child("Users");
                            user_info_ref.child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(mActivity, "Account deleted", Toast.LENGTH_LONG).show();
                                                        Intent i = new Intent(mActivity, LoginActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
             //   setTags(tv, comment);
                String username = Objects.requireNonNull(dataSnapshot.child(mActivity.getString(R.string.usernameField)).getValue()).toString();
                String profileImage = Objects.requireNonNull(dataSnapshot.child(mActivity.getString(R.string.profilePhotoField)).getValue()).toString();
                Comment comment_model = new Comment(userID, mediaId, commentId, comment, dateAdded, username, profileImage);
                myRef.child(node).child(mediaId).child(mActivity.getString(R.string.fieldComment))
                        .child(Objects.requireNonNull(commentId)).setValue(comment_model);
                List<String> hashTags2 = StringManipulation.getHashTags(comment); //new caption's tags taken

                Map<String, Object> hashtag_list = new HashMap<>();
                for (String hashtag : hashTags2) {
                    hashtag_list.put("/hashTags/" + hashtag + "/" + mediaId + "/photoId", mediaId);
                    hashtag_list.put("/hashTags/" + hashtag + "/hashTags", hashtag);
                }
                myRef.updateChildren(hashtag_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void deleteComment(final Comment commentSelected, String mediaNode, String mediaId, String photocaption){
        List<String> hashTags = StringManipulation.getHashTags(commentSelected.getComment());
        List<String> captionHashTags = StringManipulation.getHashTags(photocaption);
        for(String hashTag : hashTags){
            if(!captionHashTags.contains(hashTag)) {
                myRef.child("hashTags").child(hashTag).child(mediaId).removeValue(); //remove photoid from old hastags
            }
        }
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(mediaNode).child(mediaId).child(mActivity.getString(R.string.fieldComment));
        reference.child(commentSelected.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child(mActivity.getString(R.string.field_likes_comment));
                reference1.child(commentSelected.getId()).removeValue();
                Toast.makeText(mActivity, "Comment deleted.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mActivity, "Error occured!", Toast.LENGTH_LONG).show();
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
        final ProgressDialog progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Deleting post!");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo.getImage_path());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                dbRef.child(mActivity.getString(R.string.dbname_photos)).child(photo.getPhoto_id())
                        .child(mActivity.getString(R.string.fieldComment)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            dbRef.child(mActivity.getString(R.string.field_likes_comment)).child(ds.child("id").getValue().toString()).removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                dbRef.child(mActivity.getString(R.string.dbname_photos)).child(photo.getPhoto_id()).removeValue();
                dbRef.child(mActivity.getString(R.string.dbname_user_photos)).child(userID).child(photo.getPhoto_id()).removeValue();
                dbRef.child("hashTags").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ds.child(photo.getPhoto_id()).getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                dbRef.child(mActivity.getString(R.string.field_likes)).child(photo.getPhoto_id()).removeValue();

                dbRef.child("Notifications").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            for(DataSnapshot ds2 : ds.getChildren()){
                                if(ds2.child("postid").getValue().toString().equals(photo.getPhoto_id())) {
                                    ds2.getRef().removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dbRef.child(mActivity.getString(R.string.ratings)).child(photo.photo_id).removeValue();
                Toast.makeText(mActivity,"Photo deleted successfully.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mActivity, ProfileActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(mActivity,"Error occured during process!",Toast.LENGTH_LONG).show();
            }
        });
    }


    public void deletePostForDeleteAccount(PhotoInformation photo1){
        final PhotoInformation photo = photo1;
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo.getImage_path());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                myRef.child(mActivity.getString(R.string.dbname_photos)).child(photo.getPhoto_id()).removeValue();
                myRef.child(mActivity.getString(R.string.dbname_user_photos)).child(userID).child(photo.getPhoto_id()).removeValue();
                List<String> hashTags = StringManipulation.getHashTags(photo.getCaption());
                for(String hashTag : hashTags){
                    myRef.child("hashTags").child(hashTag).child(photo.getPhoto_id()).removeValue();
                }

                myRef.child(mActivity.getString(R.string.field_likes)).child(photo.getPhoto_id()).removeValue();

                myRef.child("Notifications").child(photo.getUser_id())
                        .orderByChild("postid").equalTo(photo.getPhoto_id()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            myRef.child("Notifications").child(photo.getUser_id()).child(ds.getKey()).removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mActivity,"Notification deleting error.",Toast.LENGTH_LONG).show();
                    }
                });
                myRef.child(mActivity.getString(R.string.ratings)).child(photo.photo_id).removeValue();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                //Toast.makeText(mActivity,"Error occured during process!",Toast.LENGTH_LONG).show();
            }
        });

    }


    public String editPost(final String postid, final PhotoInformation photo, final Context context, final TextView tv){
        final Intent intent= new Intent(context, PostViewFragment.class);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Edit Post");
        final EditText editText = new EditText(context);
        editText.setContentDescription("New Caption");
        editText.setHint("New Caption");
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
                        if(editText.getText().toString().isEmpty() || editText.getText().toString().trim().equals("")){
                            Toast.makeText(mActivity, "Sorry you did't type anything for caption", Toast.LENGTH_SHORT).show();
                        }else{
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
                            setTags(tv,editText.getText().toString());
                            photo.setCaption(editText.getText().toString());
                            Toast.makeText(mActivity,"Photo is edited  successfully.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        alertDialog.show();
        return editText.getText().toString();

    }

    private void getText(String postid, PhotoInformation photo,final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("dbname_photos").child(postid).child("caption").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    public void blockUser(String blockedUserID){
        myRef.child("Blocked").child(userID).child(blockedUserID)
                .child(mActivity.getString(R.string.users_id)).setValue(blockedUserID);

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.following_node))
                .child(blockedUserID)
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.followers_node))
                .child(userID)
                .child(blockedUserID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.following_node))
                .child(userID)
                .child(blockedUserID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.followers_node))
                .child(blockedUserID)
                .child(userID)
                .removeValue();

        deleteBlockedUserCommentAndLike(userID,blockedUserID);
        deleteBlockedUserCommentAndLike(blockedUserID,userID);
    }

    private void deleteBlockedUserCommentAndLike(final String user1, final String user2){
        myRef.child(mActivity.getString(R.string.dbname_photos))
                .orderByChild(mActivity.getString(R.string.users_id)).equalTo(user1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.child(mActivity.getString(R.string.fieldComment)).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds2 : dataSnapshot.getChildren()){
                                if(ds2.child("userId").getValue().toString().equals(user2) && ds2.getKey()!=null){
                                    List<String> hashTags = StringManipulation.getHashTags(ds2.child("comment").getValue().toString());
                                    List<String> captionHashTags = StringManipulation.getHashTags(ds.child("caption").getValue().toString());
                                    for(String hashTag : hashTags){
                                        if(!captionHashTags.contains(hashTag)) {
                                            myRef.child("hashTags").child(hashTag).child(ds.getKey()).removeValue(); //remove photoid from old hastags
                                        }
                                    }
                                    ds2.getRef().removeValue();
                                    myRef.child(mActivity.getString(R.string.field_likes_comment)).child(ds2.getKey()).removeValue();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        myRef.child(mActivity.getString(R.string.dbname_user_photos)).child(user1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey() != null) {
                        myRef.child(mActivity.getString(R.string.field_likes)).child(ds.getKey()).child(mActivity.getString(R.string.field_likes)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds2 : dataSnapshot.getChildren()) {
                                    if (ds2.child("user_id").getValue().toString().equals(user2)) {
                                        ds2.getRef().removeValue();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        myRef.child("Notifications").child(user1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("userid").getValue().toString().equals(user2)){
                        ds.getRef().removeValue();
                    }else {
                        myRef.child(mActivity.getString(R.string.dbname_user_photos)).child(user2).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!ds.child("postid").getValue().toString().equals("") && dataSnapshot.child(ds.child("postid").getValue().toString()).exists()) {
                                    ds.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void unblockUser(String blockedUserID){
        myRef.child("Blocked").child(userID)
                .child(blockedUserID).removeValue();
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

                            Intent i = new Intent(Context, ResultActivity.class);
                            i.putExtra("hashTags", tag);
                            Context.startActivity(i);
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

    public void deleteUserPosts(String userID) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("dbname_user_photos").child(userID);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PhotoInformation photoInformation;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    photoInformation = ds.getValue(PhotoInformation.class);
                    deletePostForDeleteAccount(photoInformation);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        deleteUserAllRealtedInfos();
    }


    public void deleteUserAllRealtedInfos(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equals(userID)){
                        ds.getRef().removeValue();
                    }else{
                        for(DataSnapshot ds2 : ds.getChildren()){
                            if(ds2.child("userid").getValue().toString().equals(userID)){
                                ds2.getRef().removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child(mActivity.getString(R.string.followers_node)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equals(userID)){
                        ds.getRef().removeValue();
                    }else{
                        for(DataSnapshot ds2 : ds.getChildren()){
                            if(ds2.getKey().equals(userID)){
                                ds2.getRef().removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child(mActivity.getString(R.string.following_node)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equals(userID)){
                        ds.getRef().removeValue();
                    }else{
                        for(DataSnapshot ds2 : ds.getChildren()){
                            if(ds2.getKey().equals(userID)){
                                ds2.getRef().removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child(mActivity.getString(R.string.field_likes)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.child(mActivity.getString(R.string.field_likes)).getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds2 : dataSnapshot.getChildren()){
                                if(ds2.child("user_id").getValue().toString().equals(userID)){
                                    ds2.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child(mActivity.getString(R.string.dbname_photos)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.child("comment").getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds2 : dataSnapshot.getChildren()){
                                if(ds2.child("userId").getValue().toString().equals(userID)){
                                    ds2.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child(mActivity.getString(R.string.ratings)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    for(DataSnapshot ds2 : ds.getChildren()){
                        if(ds2.getKey().equals(userID)){
                            ds2.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}



