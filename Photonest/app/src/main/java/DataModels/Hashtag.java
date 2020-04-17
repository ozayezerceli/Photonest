package DataModels;

import com.se302.photonest.Like;

import org.w3c.dom.Comment;

import java.util.List;

public class Hashtag {

    private String hashTags;

    public Hashtag(String hashTags) {
        this.hashTags = hashTags;
    }

    public Hashtag() {

    }


    public String getHashTags() {
        return hashTags;
    }
}