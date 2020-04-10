package DataModels;

public class User{

    private String id;
    private String email;
    private String username;
    private String fullName;
    private String imageurl;
    private String bio;

    public User(String id, String email, String username, String fullName, String imageurl, String bio) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.imageurl = imageurl;
        this.bio = bio;
    }

    public User() {

    }


    public String getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public String setBio() {
        return this.bio = bio;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageurl;
    }

    public void setImageUrl() {
        this.imageurl = imageurl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", full name='" + fullName + '\'' +
                ", image url='" + imageurl + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }


}