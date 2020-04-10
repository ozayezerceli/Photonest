package DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.se302.photonest.Like;

import org.w3c.dom.Comment;

import java.util.List;

public class PhotoInformation implements Parcelable {
    private String caption;
    private String date_created;
    private String image_path;
    private String photo_id;
    private String user_id;
    private String hashTags;
    private List<Like> likes;
    private List<Comment> comments;

    public PhotoInformation() {

    }

    public PhotoInformation(String caption, String date_created, String image_path, String photo_id,
                 String user_id, String hashTags, List<Like> likes, List<Comment> comments) {
        this.caption = caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.hashTags = hashTags;
        this.likes = likes;
        this.comments = comments;
    }


    protected PhotoInformation(Parcel in) {
        caption = in.readString();
        date_created = in.readString();
        image_path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
        hashTags = in.readString();
    }

    public static final Creator<PhotoInformation> CREATOR = new Creator<PhotoInformation>() {
        @Override
        public PhotoInformation createFromParcel(Parcel in) {
            return new PhotoInformation(in);
        }

        @Override
        public PhotoInformation[] newArray(int size) {
            return new PhotoInformation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(caption);
        parcel.writeString(date_created);
        parcel.writeString(image_path);
        parcel.writeString(photo_id);
        parcel.writeString(user_id);
        parcel.writeString(hashTags);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static Creator<PhotoInformation> getCREATOR() {
        return CREATOR;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + hashTags + '\'' +
                ", likes=" + likes +
                '}';
    }

    public Object getPublisher() {
        return new Object(); // will be changed!!!
    }
}
