package Utils;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.se302.photonest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import DataModels.Comment;
import DataModels.UserInformation;

public class CommentActivity extends AppCompatActivity implements UtilityInterface {
    Context mContext = CommentActivity.this;
    private String mediaId;
    private String mediaNode;
    private String profileImage;
    private ListView commentList;
    private ArrayList<Comment> list;
    private CommentListAdapter listAdapter;
    private boolean isCommentAdded = false;
    private long startLimit = -1;
    Intent intent;
    private EditText commentText;
    int hashTagIsComing = 0;
    Spannable mspanable;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase database;
    private FirebaseMethods firebaseMethods;

    private ActionMode mode;
    private Comment commentSelected;
    private String photoUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentText = findViewById(R.id.comment);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        firebaseMethods = new FirebaseMethods(CommentActivity.this);

        Intent mediaIntent = getIntent();
        mediaId = mediaIntent.getStringExtra("mediaID");
        mediaNode = mediaIntent.getStringExtra("mediaNode");
        profileImage = mediaIntent.getStringExtra("imageurl");
        photoUserID = mediaIntent.getStringExtra("photoUser");

        setCommentProfileImage(profileImage);
        addComment(mediaNode, mediaId);


        commentList = findViewById(R.id.comment_list);
        list = new ArrayList<>();
        listAdapter = new CommentListAdapter(CommentActivity.this, R.layout.comment_item, list);
        retrieveAllComments(mediaNode, mediaId, 20);
        commentList.setAdapter(listAdapter);

        setCommentTags();
        mspanable = commentText.getText();
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String startChar = null;
                try{
                    startChar = Character.toString(s.charAt(start));
                    Log.i(getClass().getSimpleName(), "CHARACTER OF NEW WORD: " + startChar);
                }
                catch(Exception ex){
                    startChar = " ";
                }

                if (startChar.equals("#")) {
                    tagCheck(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }

                if(startChar.equals(" ")){
                    hashTagIsComing = 0;
                }

                if(hashTagIsComing != 0) {
                    tagCheck(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        goBack();

        commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                if(mode != null){
                    return false;
                }

                if(list.get(i).getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) || photoUserID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    mode = startActionMode(modeCallBack);
                    view.setSelected(true);
                    commentSelected = list.get(i);
                }
                return true;
            }
        });

    }

    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.selected_comment_menu,menu);
            findViewById(R.id.relativeL13).setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.action_delete_comment:
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(mediaNode).child(mediaId).child(getString(R.string.fieldComment));
                    reference.child(commentSelected.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            list.remove(commentSelected);
                            listAdapter.notifyDataSetChanged();
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child(getString(R.string.field_likes_comment));
                            reference1.child(commentSelected.getId()).removeValue();
                            actionMode.finish();
                            Toast.makeText(CommentActivity.this, "Comment deleted.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CommentActivity.this, "Error occured!", Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mode = null;
            findViewById(R.id.relativeL13).setVisibility(View.VISIBLE);
        }
    };

    private void tagCheck(String s, int start, int end) {
        mspanable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_hashtag)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void retrieveAllComments(String mediaNode, String mediaId, final long endLimit){

        Query query = myRef.child(mediaNode).child(mediaId).child(getString(R.string.fieldComment)).orderByChild(getString(R.string.dateField));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long  commentsLength = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(isCommentAdded&&commentsLength==dataSnapshot.getChildrenCount()-1){

                        Comment comment = snapshot.getValue(Comment.class);
                        list.add(0,new Comment(comment.getUserId(),comment.getPhotoId(),comment.getId(),Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getProfile_image(), comment.getComment_likes()));
                        commentList.smoothScrollToPosition(0);
                        isCommentAdded = false;
                    }
                    else if (!isCommentAdded&&commentsLength <= endLimit && commentsLength > startLimit) {
                        Comment comment = snapshot.getValue(Comment.class);
                        list.add(new Comment(comment.getUserId(),comment.getPhotoId(),comment.getId(),Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getProfile_image(), comment.getComment_likes()));
                    }
                    commentsLength++;
                }
                startLimit = endLimit;
                listAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setCommentTags(){

        intent=getIntent();
        if(intent.hasExtra("commentText")){
            String comment = intent.getStringExtra("commentText");
            commentText.setText(comment);
            setTags(commentText,commentText.getText().toString());

        }
    }



    private void addComment(final String mediaNode, final String mediaId){

        final TextView postComment = findViewById(R.id.post_comment);
        final EditText commentText = findViewById(R.id.comment);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentText.getText().toString().length()>0) {
                    isCommentAdded = true;
                    firebaseMethods.addNewComment(mediaNode,mediaId, commentText.getText().toString());
                    addNotificationsComment(photoUserID,mediaId, commentText.getText().toString());
                }
            }
        });
    }

    private void addNotificationsComment(String userid, String postid, String text){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> hash = new HashMap<>();
        hash.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hash.put("text", "commented: "+text);
        hash.put("postid",postid);
        hash.put("ispost", true);

        ref.push().setValue(hash);
    }

    private void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || pTagString.charAt(i) == '\n' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#F99F63"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }

    private void setCommentProfileImage(String image){
        CircularImageView profileImageView = findViewById(R.id.comment_profile_image);
        GlideImageLoader.loadImageWithOutTransition(mContext,image,profileImageView);
        System.out.println("yey");
    }



    private void goBack(){
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void loadMore(long limit) {
        retrieveAllComments(mediaNode,mediaId,limit);
    }
}
