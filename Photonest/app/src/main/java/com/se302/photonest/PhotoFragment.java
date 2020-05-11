package com.se302.photonest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import Utils.Permissions;

import static android.app.Activity.RESULT_OK;


public class PhotoFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 5;
    private static final int PHOTO_FRAGMENT_NUM = 0;
    final String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera";
    File imageFile;
    private String selectedImage;



    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView camera = (ImageView) Objects.requireNonNull(getView()).findViewById(R.id.launch_camera);
        ImageView close = (ImageView)getView().findViewById(R.id.photo_close);
        //Ignoring FileUri expose exception
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateAdded = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                imageFile = new File(outputDirectory,"insta_cam_"+dateAdded+".jpg");
                if(((PostActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM) {
                    if (((PostActivity) getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

                    }else{
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(getActivity(), UploadPostActivity.class);
            intent.putExtra(getString(R.string.selected_bitmap), bitmap);
            startActivity(intent);
            getActivity().finish();
        }

    }
}


