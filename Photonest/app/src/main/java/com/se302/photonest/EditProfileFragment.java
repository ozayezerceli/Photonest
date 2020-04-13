package com.se302.photonest;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import Utils.GlideImageLoader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
import java.io.IOException;
import java.util.HashMap;


import DataModels.UserInformation;


import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    //private FirebaseMethods mFirebaseMethods;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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




        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
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
                startActivity(new Intent(getActivity().getApplicationContext(),ProfileActivity.class));
            }
        });



      /*  delete_photo.setOnClickListener(new View.OnClickListener() { //if the request of deletion profile photo comes from user
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity().getApplicationContext())
                        .setTitle("Delete")
                        .setMessage("Your account will be deleted. \nAre you sure?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String Image_Url = dataSnapshot.child("imageurl").getValue().toString();
                                        if(!Image_Url.equals(def_image)){
                                            Toast.makeText(getActivity().getApplicationContext(), "Deleting profile photo...", Toast.LENGTH_SHORT).show();

                                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Image_Url);
                                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    myRef.child("imageurl").setValue(def_image);
                                                    Toast.makeText(getActivity().getApplicationContext(), "Profile photo is deleted", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onSuccess: deleted file");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "Profile photo is not deleted.", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onFailure: did not delete file");
                                                }
                                            });



                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();



            }
        }); */
      /*  delete_photo.setOnClickListener(new View.OnClickListener() { //if the request of deletion profile photo comes from user
            @Override
            public void onClick(View v) {
                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String Image_Url = dataSnapshot.child("imageurl").getValue().toString();
                                        if(!Image_Url.equals(def_image)){
                                            Toast.makeText(getActivity().getApplicationContext(), "Deleting profile photo...", Toast.LENGTH_SHORT).show();

                                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Image_Url);
                                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    myRef.child("imageurl").setValue(def_image);
                                                    Toast.makeText(getActivity().getApplicationContext(), "Profile photo is deleted", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onSuccess: deleted file");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "Profile photo is not deleted.", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onFailure: did not delete file");
                                                }
                                            });

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

            }

        }); */


        myRef.addValueEventListener(new ValueEventListener() { //to get information of user
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uInfo = dataSnapshot.getValue(UserInformation.class);
                EditFullName.setText(uInfo.getFullName());
                EditUsername.setText(uInfo.getUsername());
                EditBio.setText(uInfo.getBio());
                EditWebsite.setText(uInfo.getWebsite_link());
                String Image_Url = dataSnapshot.child("imageurl").getValue().toString();
             //   GlideImageLoader.loadImageWithOutTransition(getActivity().getApplicationContext(),Image_Url,edit_profile_image);
                Glide.with(getActivity().getApplicationContext()).load(Uri.parse(Image_Url)).into(edit_profile_image);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }



 @Override
 public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
     super.onActivityResult(requestCode, resultCode, data);

     if (requestCode==GaleriPick && resultCode==RESULT_OK && data!=null){
         ImageUri=data.getData();
         uploadImage();
         edit_profile_image.setImageURI(ImageUri);
         final StorageReference filePath=user_image_ref
                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


     }  else {
         Toast.makeText(getActivity().getApplicationContext(),"Something gone wrong!", Toast.LENGTH_SHORT).show();

     }
 }




    private  String getFileExtension(Uri uri){

        ContentResolver  contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){


        if(ImageUri !=  null){ //if a image is selected from gallery
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String Image_Url = dataSnapshot.child("imageurl").getValue().toString(); // get the current profile photo
                    if(!Image_Url.equals(def_image)){ //if current profile photo is not equal to the default one //yanlış


                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Image_Url);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Current photo is removed");

                                final StorageReference storageReference= user_image_ref.child(System.currentTimeMillis()
                                        +"."+ getFileExtension(ImageUri));

                                uploadtask = storageReference.putFile(ImageUri);
                                uploadtask.continueWithTask(new Continuation() {
                                    @Override
                                    public Object then(@NonNull Task task) throws Exception {
                                        if(!task.isSuccessful()){
                                            throw  task.getException();
                                        }
                                        return storageReference.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){
                                            Uri downloadUri = task.getResult();
                                            String myUrl= downloadUri.toString();

                                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                            HashMap<String,Object> hashMap = new HashMap<>();
                                            hashMap.put("imageurl",""+myUrl);
                                            reference.child("imageurl").setValue(myUrl);

                                            reference.updateChildren(hashMap);

                                        } else {
                                            Toast.makeText(getActivity().getApplicationContext(), "Failed.",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d(TAG, "Failed when current photo is removed");
                            }
                        });

                    } else { //if current photo is equal to defa image
                        final StorageReference storageReference= user_image_ref.child(System.currentTimeMillis()
                                +"."+ getFileExtension(ImageUri));

                        uploadtask = storageReference.putFile(ImageUri);
                        uploadtask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if(!task.isSuccessful()){
                                    throw  task.getException();
                                }
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    Uri downloadUri = task.getResult();
                                    String myUrl= downloadUri.toString();

                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("imageurl",""+myUrl);
                                    reference.child("imageurl").setValue(myUrl);

                                    reference.updateChildren(hashMap);

                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "Failed.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





        } else {
            Toast.makeText(getActivity().getApplicationContext(),"No image selected.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Full name changed!", Toast.LENGTH_SHORT).show();
                }
                if(!dataSnapshot.child("bio").getValue().toString().equals(bio)){
                    myRef.child("bio").setValue(bio);
                    Toast.makeText(getActivity(), "Bio changed!", Toast.LENGTH_SHORT).show();
                }
                if(!dataSnapshot.child("website_link").getValue().toString().equals(websitelink)){
                    myRef.child("website_link").setValue(websitelink);
                    Toast.makeText(getActivity(), "WebSite Link Changed!", Toast.LENGTH_SHORT).show();
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
}
