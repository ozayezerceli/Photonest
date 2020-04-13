package DataModels;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInformation implements Parcelable {
    private String fullName;
    private String email;
    private String bio;
    private String website_link;
    private String username;
    private String imageurl;
    private String id;


    public UserInformation() {
    }

    protected UserInformation(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        bio = in.readString();
        website_link = in.readString();
        username = in.readString();
        imageurl = in.readString();
        id = in.readString();
    }

    public static final Creator<UserInformation> CREATOR = new Creator<UserInformation>() {
        @Override
        public UserInformation createFromParcel(Parcel in) {
            return new UserInformation(in);
        }

        @Override
        public UserInformation[] newArray(int size) {
            return new UserInformation[size];
        }
    };

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite_link(){
        return website_link;
    }

    public void setWebsite_link(String website_link){
        this.website_link = website_link;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeString(bio);
        parcel.writeString(website_link);
        parcel.writeString(username);
        parcel.writeString(imageurl);
        parcel.writeString(id);

    }
}
