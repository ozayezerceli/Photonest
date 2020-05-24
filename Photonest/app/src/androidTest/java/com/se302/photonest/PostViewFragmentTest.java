package com.se302.photonest;


import android.content.Intent;
import android.os.SystemClock;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

public class PostViewFragmentTest {
    @Rule
    public ActivityTestRule<ProfileActivity> mActivityRule = new ActivityTestRule<ProfileActivity>(ProfileActivity.class);

    private ProfileActivity profileActivity = null;

    @Before
    public void setUp() throws Exception {
        SystemClock.sleep(3000);
        mActivityRule.getActivity().getSupportFragmentManager().beginTransaction();
        onData(anything())
                .inAdapterView(withId(R.id.grid_view_profile))
                .atPosition(0)
                .onChildView(withId(R.id.gridImageView))
                .perform(click());
    }

    @Test
    public void testLaunch(){
        onView(withId(R.id.post_image_main_view)).check(matches(isDisplayed()));
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
        onView(withId(R.id.tvBackLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.image_caption_main_view)).check(matches(isDisplayed()));
        onView(withId(R.id.username_main_view)).check(matches(isDisplayed()));
        onView(withId(R.id.image_time_posted_main_view)).check(matches(isDisplayed()));
        onView(withId(R.id.image_egg_not_liked_view)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_photo_main_view)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_postOption)).check(matches(isDisplayed()));

    }

    @Test
    public void testUserDeletePost() {
        onView(withId( R.id.btn_postOption)).perform(click());
        onView(ViewMatchers.withContentDescription("delete"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(ViewActions.click());
        onView(withText("OK")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void testUserEditPost() {
        onView(withId( R.id.btn_postOption)).perform(click());
        onView(ViewMatchers.withContentDescription("edit"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(ViewActions.click());
        Espresso.onView(withContentDescription("new caption")).perform(replaceText("Test new caption #test"));
        Espresso.closeSoftKeyboard();
        onView(withText("Edit")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }
    @Test
    public void testUserLikePost(){
        onView(withId(R.id.image_egg_not_liked_view)).perform(click());
        onView(withId(R.id.image_egg_liked_view)).check(matches(isDisplayed()));

    }

    @Test
    public void testUserUnlikePost(){
        onView(withId(R.id.image_egg_liked_view)).perform(click());
        onView(withId(R.id.image_egg_not_liked_view)).check(matches(isDisplayed()));

    }




    @Test
    public void testUserQuitPostView() {
        onView(withId(R.id.backArrow)).perform(click());
    }
}

