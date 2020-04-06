package DataModels;

public class User{

    private String user_id;
    private String email;
    private String username;
    private String fullName;
    private String imageUrl;

    public User(String user_id, String email, String username, String fullName, String imageUrl) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    public User() {

    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
        return imageUrl;
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
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", full name='" + fullName + '\'' +
                ", image url='" + imageUrl + '\'' +
                '}';
    }


}