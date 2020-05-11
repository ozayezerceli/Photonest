package com.se302.photonest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import Utils.FilePaths;
import Utils.FirebaseMethods;
import Utils.GlideImageLoader;
import Utils.UniversalImageLoader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;


import DataModels.UserInformation;


import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;
    private FirebaseUser user;
    private UserInformation uInfo;
    private static int GaleriPick=1;
    final int PIC_CROP = 1;
    private Uri ImageUri;
    private Button delete_photo;
    private Button change_photo;
    private StorageTask uploadtask;
    private  StorageTask delete_photo_task;
    private StorageReference user_image_ref;
    private  Context context= getActivity();
    private String downloadURL;
    private Intent intent;
    private String imgUrl;
    private Bitmap bitmap;


    //EditProfile Fragment widgets
    private EditText EditFullName, EditUsername, EditWebsite, EditBio;
    private  ImageView edit_profile_image;
    private  String def_image="https://firebasestorage.googleapis.com/v0/b/photonest-11327.appspot.com/o/defaultphoto%2Fplace_holder_photo.png?alt=media&token=f450daed-b913-4991-8456-ff6920d63b25";

    //private TextView mChangeProfilePhoto;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        EditFullName = view.findViewById(R.id.EditFullName);
        EditUsername = view.findViewById(R.id.EditUsername);
        EditWebsite = view.findViewById(R.id.EditWebsite);
        EditBio = view.findViewById(R.id.EditBio);
        edit_profile_image= view.findViewById(R.id.profile_image_edit);
        delete_photo = view.findViewById(R.id.delete_image_photo);
        user_image_ref= FirebaseStorage.getInstance().getReference().child("imagephoto");



      edit_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeriIntent= new Intent();
                galeriIntent.setAction(Intent.ACTION_GET_CONTENT);
                galeriIntent.setType("image/*");
                startActivityForResult(galeriIntent,GaleriPick);

            }
        });

    /*   edit_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Attempting to upload new photo", Toast.LENGTH_SHORT).show();

                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), null, 0, imgUrl,null, edit_profile_image.toString());

                }
            }
        }); */


        mAuth=FirebaseAuth.getInstance();
        userID =mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Users").child(userID);


        ImageView backarrow = view.findViewById(R.id.backArrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ImageView checkMark = view.findViewById(R.id.saveChanges);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
             if(ImageUri != null){
                    uploadImage();
             }
             Toast.makeText(getActivity(), "Profile Edited!", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(getActivity(),ProfileActivity.class));



            }
        });

        delete_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhoto();

            }

        });



        myRef.addValueEventListener(new ValueEventListener() { //to get information of user
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uInfo = dataSnapshot.getValue(UserInformation.class);
                EditFullName.setText(uInfo.getFullName());
                EditUsername.setText(uInfo.getUsername());
                EditBio.setText(uInfo.getBio());
                EditWebsite.setText(uInfo.getWebsite_link());
                String Image_Url= uInfo.getImageurl();
                Picasso.get().load(Image_Url).into(edit_profile_image);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
    private void deletePhoto(){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        alert.setMessage("Your profile photo will be deleted. \nAre you sure?");
        alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String Image_Url = dataSnapshot.child("imageurl").getValue().toString();
                        if(!Image_Url.equals(def_image)){
                            //Toast.makeText(getActivity(), "Deleting profile photo...", Toast.LENGTH_SHORT).show();

                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Image_Url);
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    myRef.child("imageurl").setValue(def_image);
                                    Picasso.get().load(def_image).into(edit_profile_image);
                                    Toast.makeText(getActivity(), "Profile photo is deleted", Toast.LENGTH_SHORT).show();
                                //    getActivity().finish();
                                    startActivity(new Intent(getActivity(),ProfileActivity.class));
                                    Log.d(TAG, "onSuccess: deleted file");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getActivity(), "Profile photo is not deleted.", Toast.LENGTH_SHORT).show();
                                 //   getActivity().finish();
                                    startActivity(new Intent(getActivity(),ProfileActivity.class));
                                    Log.d(TAG, "onFailure: did not delete file");
                                }
                            });

                        } else {
                          //  Toast.makeText(getActivity(), "You cannot delete profile photo.", Toast.LENGTH_SHORT).show();
                          //  getActivity().finish();
                            startActivity(new Intent(getActivity(),ProfileActivity.class));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        alert.show();
    }



  @Override
 public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
     super.onActivityResult(requestCode, resultCode, data);

     if (requestCode==GaleriPick && resultCode==RESULT_OK && data!=null){
     /*   imgUrl=data.getData().toString();
        edit_profile_image.setImageURI(ImageUri); */
         ImageUri=data.getData();

         edit_profile_image.setImageURI(ImageUri);
     }  else {
         Toast.makeText(getActivity(),"Something gone wrong!", Toast.LENGTH_SHORT).show();

     }
 }



    private void uploadImage(){

        if(ImageUri !=  null){ //if a image is selected from gallery
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String default_image = "https://firebasestorage.googleapis.com/v0/b/photonest-11327.appspot.com/o/defaultphoto%2Fplace_holder_photo.png?alt=media&token=f450daed-b913-4991-8456-ff6920d63b25";

                    String Image_Url = dataSnapshot.child("imageurl").getValue().toString(); // get the current profile photo
                    if(!Image_Url.equals(default_image)){ //if current profile photo is not equal to the default one

                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FilePaths filePaths = new FilePaths();

                        final StorageReference myStrRef =  FirebaseStorage.getInstance().getReference();
                        final StorageReference filePath=myStrRef
                                .child(filePaths.PROFILE_PHOTO_STORAGE + "/" + user_id);


                        UploadTask uploadTask= filePath.putFile(ImageUri);


                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                            {

                                if (!task.isSuccessful())
                                {

                                    throw task.getException();
                                }

                                downloadURL=filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task)
                            {
                                if (task.isSuccessful())
                                {
                                    downloadURL=task.getResult().toString();


                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("imageurl",""+downloadURL);
                                    reference.child("imageurl").setValue(downloadURL);

                                    edit_profile_image.setImageURI(task.getResult());

                                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                              //  Toast.makeText(getActivity().getApplicationContext(), "Profile Photo is changed.", Toast.LENGTH_SHORT).show();
                                                  getActivity().finish();
                                                  startActivity(new Intent(getActivity(),ProfileActivity.class));

                                            }
                                        }
                                    });

                                }else {
                                    Toast.makeText(getActivity(), "Failed.",Toast.LENGTH_SHORT).show();
                                }



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });



                    } //first if ends

                    if(Image_Url.equals(default_image)) { //if current photo is equal to defa image
                     //   final StorageReference storageReference= user_image_ref.child(System.currentTimeMillis()
                     //           +"."+ getFileExtension(ImageUri));
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FilePaths filePaths = new FilePaths();
                        final StorageReference myStrRef =  FirebaseStorage.getInstance().getReference();
                        final StorageReference filePath=myStrRef
                                .child(filePaths.PROFILE_PHOTO_STORAGE + "/" + user_id);


                        UploadTask uploadTask= filePath.putFile(ImageUri);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                            {

                                if (!task.isSuccessful())
                                {

                                    throw task.getException();
                                }

                                downloadURL=filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task)
                            {
                                if (task.isSuccessful())
                                {
                                    downloadURL=task.getResult().toString();


                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("imageurl",""+downloadURL);
                                    reference.child("imageurl").setValue(downloadURL);

                                    edit_profile_image.setImageURI(task.getResult());


                                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                               // Toast.makeText(getActivity(), "Profile Photo is changed.", Toast.LENGTH_SHORT).show();
                                                getActivity().finish();
                                                startActivity(new Intent(getActivity(),ProfileActivity.class));

                                            }
                                        }
                                    });

                                }else {
                                    Toast.makeText(getActivity(), "Failed.",Toast.LENGTH_SHORT).show();
                                }



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }  //second if ends

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        } else {
            Toast.makeText(getActivity(),"No image selected.", Toast.LENGTH_SHORT).show();
        }
    }




    //The method below takes the input from user and updates the user information on firebase
    private void updateData(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = EditUsername.getText().toString();
                String fullname = EditFullName.getText().toString();
                String bio = EditBio.getText().toString();
                String websitelink = EditWebsite.getText().toString();

                if(!dataSnapshot.child("username").getValue().toString().equals(username)){
                    checkIfUsernameExists(username);
                }
                if(!dataSnapshot.child("fullName").getValue().toString().equals(fullname)){
                    myRef.child("fullName").setValue(fullname);

                }
                if(!dataSnapshot.child("bio").getValue().toString().equals(bio)){
                    myRef.child("bio").setValue(bio);

                }
                if(!dataSnapshot.child("website_link").getValue().toString().equals(websitelink)){
                    myRef.child("website_link").setValue(websitelink);

                }
                /*if(!dataSnapshot.child("WebsiteLink").getValue().toString().equals(websitelink)){
                    myRef.child("WebsiteLink").setValue(websitelink);
                    }
                */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //The method below checks if the new username already exist in the database,
    //if username exists, it does not allow user to change his username
    private void checkIfUsernameExists(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Users").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    myRef.child("username").setValue(username);
                    Toast.makeText(getActivity(), "saved username.", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getActivity(), "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
