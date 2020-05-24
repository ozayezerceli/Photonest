package com.se302.photonest;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import Utils.CommentListAdapter;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;

import Utils.CommentActivity;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

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
                        extras.putString("mediaID", "-M816x1oocvBThvGyPx4");
                        extras.putString("mediaNode", "/dbname_photos/");
                        extras.putString("comment", "-M82LmiFNvVe5kVafI9N");
                        extras.putString("imageurl", "https://firebasestorage.googleapis.com/v0/b/photonest-11327.appspot.com/o/imagephoto%2F4m71yIjpG1asMmpVzK7tgcFAAhC3?alt=media&token=da7d03c3-a8f8-48cd-b71b-bb48c9b6f408");
                        result.putExtras(extras);
                        return result;
                    }
                };
        private CommentActivity commentActivity =null;

    @Before
    public void setUp() throws Exception {
        commentActivity = mActivityRule.getActivity();
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
        public void testUserAddComment() {
            Espresso.onView(withId(R.id.comment)).perform(replaceText("Test Comment"));
            Espresso.closeSoftKeyboard();
            Espresso.onView(withId(R.id.post_comment)).perform(click());
        }


        @Test
        public void testUserLikeComment(){
           /* ListView mList= commentActivity.findViewById(R.id.comment_list);
            mList.getAdapter().getItemViewType(R.id.comment_heart);
            mList.getAdapter().getItemViewType(R.id.comment_heart_liked);
            Espresso.onView(withId(mList.getAdapter().getItemViewType(R.id.comment_heart))).perform(click());
            Espresso.onView(withId(mList.getAdapter().getItemViewType(R.id.comment_heart_liked))).check(matches(isDisplayed())); */
         //   Espresso.onData(withId(R.id.comment_list)).onChildView(withId(R.id.comment_heart)).perform(click());
           // Espresso.onData(withId(R.id.comment_list)).onChildView(withId(R.id.comment_heart_liked)).check(matches(isDisplayed()));
            Espresso.onView(withId(R.id.comment_list)).check(matches(isDisplayed()));
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.comment_list))
                    .atPosition(0)
                    .onChildView(withId(R.id.comment_heart))
                    .perform(click());
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.comment_list))
                    .atPosition(0)
                    .onChildView(withId(R.id.comment_heart_liked))
                    .check(matches(isDisplayed()));

        }
        @Test
        public void testDeleteComment(){
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.comment_list))
                    .atPosition(0)
                    .onChildView(withId(R.id.comment_text))
                    .perform(longClick());
            Espresso.onView(withId(R.id.action_delete_comment)).perform(click());


        }



    @Test
    public void testUserQuitComments() {
        Espresso.onView(withId(R.id.back)).perform(click());
    }
    }


