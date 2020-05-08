package DataModels;

public class Comment {
    private String id;
    private String comment;
    private String date_added;
    private String username;
    private String profile_image;
    private long comment_likes;

    public Comment() {
    }


    public Comment(String id,String comment, String date_added, String username, String profileImage, long commentLikes) {
        this.id = id;
        this.comment = comment;
        this.date_added = date_added;
        this.username = username;
        this.profile_image = profileImage;
        this.comment_likes = commentLikes;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getUser_name() {
        return username;
    }

    public void setUser_name(String username) {
        this.username = username;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public long getComment_likes() {
        return comment_likes;
    }

    public void setComment_likes(long comment_likes) {
        this.comment_likes = comment_likes;
    }

}
