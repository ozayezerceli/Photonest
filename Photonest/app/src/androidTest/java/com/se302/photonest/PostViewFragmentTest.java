package com.se302.photonest;


import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

public class PostViewFragmentTest {
    @Rule
    public ActivityTestRule<ProfileActivity> mActivityRule = new ActivityTestRule<ProfileActivity>(ProfileActivity.class);

    private ProfileActivity profileActivity = null;

    @Before
    public void setUp() throws Exception {
        Espresso.onData(anything())
                .inAdapterView(allOf(withId(R.id.grid_view)))
                .atPosition(0).perform(click());
    }

    @Test
    public void testUserDeletePost() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withText("Delete Post"))
                .perform(click());
    }

    @Test
    public void testLaunch(){
        Espresso.onView(withId(R.id.post_image)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.tvBackLabel)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.image_caption)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.username)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.image_time_posted)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.image_egg_unliked_post_view)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.profile_photo)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.btn_postOption)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserQuitPostView() {
        Espresso.onView(withId(R.id.backArrow)).perform(click());
    }
}

