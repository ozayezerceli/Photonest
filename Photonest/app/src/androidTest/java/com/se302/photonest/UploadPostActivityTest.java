package com.se302.photonest;

import android.content.Intent;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class UploadPostActivityTest {

    @Rule
    public ActivityTestRule<UploadPostActivity> uploadpostActivityActivityTestRule = new ActivityTestRule<UploadPostActivity>(UploadPostActivity.class);

    UploadPostActivity uploadpostActivity = null;
    @Before
    public void setUp() throws Exception {
        uploadpostActivity = uploadpostActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view1 = uploadpostActivity.findViewById(R.id.upload_post_close);
        View view2 = uploadpostActivity.findViewById(R.id.upload_post_post);
        View view3 = uploadpostActivity.findViewById(R.id.upload_post_image_added);
        View view4 = uploadpostActivity.findViewById(R.id.upload_post_description);
        View view5 = uploadpostActivity.findViewById(R.id.upload_post_add_location_btn);
        if(view1==null && view2==null && view3==null && view4==null && view5==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void testCancel(){
        Espresso.onView(withId(R.id.upload_post_close)).perform(click());
    }

    @Test
    public void testUploadPost(){
        Espresso.onView(withId(R.id.upload_post_description)).perform(typeText("#testCaption"));
        Espresso.onView(withId(R.id.upload_post_add_location_btn)).perform(click());
        Espresso.onView(withId(R.id.upload_post_post)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        uploadpostActivity = null;
    }
}