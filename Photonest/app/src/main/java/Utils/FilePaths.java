package Utils;

import android.os.Environment;

public class FilePaths {

    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String WHATSAPP = ROOT_DIR + "/WhatsApp/Media/WhatsApp Images";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
    public String PROFILE_PHOTO_STORAGE ="imagephoto";

}
