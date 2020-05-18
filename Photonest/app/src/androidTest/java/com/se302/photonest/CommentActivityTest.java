package com.se302.photonest;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.espresso.Espresso;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import Utils.CommentActivity;



import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class CommentActivityTest {

        @Rule
        public ActivityTestRule<CommentActivity> mActivityRule =
                new ActivityTestRule<CommentActivity>(CommentActivity.class) {
                    @Override
                    protected Intent getActivityIntent() {
                        Context targetContext = InstrumentationRegistry.getInstrumentation()
                                .getTargetContext();
                        Intent result = new Intent(targetContext, CommentActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("mediaID", "-M6o3G_rpae5YECZuwSa");
                        extras.putString("mediaNode", "/dbname_photos/");
                        extras.putString("imageurl", "https://firebasestorage.googleapis.com/v0/b/photonest-11327.appspot.com/o/imagephoto%2Fqa9BSK5TyieZ4JQcmPkDZRlDxQU2?alt=media&token=fba3fdfe-a596-4e1c-98f0-bb2f550ce87a");
                        result.putExtras(extras);
                        return result;
                    }
                };

        @Test
        public void testUserAddComment() {
            Espresso.onView(withId(R.id.comment)).perform(replaceText("Test Comment"));
            Espresso.closeSoftKeyboard();
            Espresso.onView(withId(R.id.post_comment)).perform(click());
        }


        @Test
        public void testUserLikeComment(){

        }

    @Test
    public void testLaunch() {
        Espresso.onView(withId(R.id.post_comment)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.comment_profile_image)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.comment)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.back)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.comment_list)).check(matches(isDisplayed()));

    }

    @Test
    public void testUserQuitComments() {
        Espresso.onView(withId(R.id.back)).perform(click());
    }
    }


