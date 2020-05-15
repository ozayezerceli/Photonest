package com.se302.photonest;

import android.widget.Spinner;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class GalleryFragmentTest {

    @Rule
    public ActivityTestRule<PostActivity> postActivityActivityTestRule = new ActivityTestRule<PostActivity>(PostActivity.class);

    private PostActivity postActivity = null;

    @Before
    public void setUp() throws Exception {
        postActivity = postActivityActivityTestRule.getActivity();
        Espresso.onView(ViewMatchers.withText("Gallery")).perform(click());
    }

    @Test
    public void testLaunch(){
        Espresso.onView(withId(R.id.cancelPost)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.nextBtn)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.spinnerDirectory)).check(matches(isDisplayed()));
    }

    @Test
    public void testNextButton(){
        Espresso.onView(withId(R.id.spinnerDirectory)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)) , is("camera"))).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(0).perform(click());
        Espresso.onView(withId(R.id.nextBtn)).perform(click());
        Espresso.onView(withId(R.id.upload_post_description)).perform(typeText("#testCaption"));
        Espresso.onView(withId(R.id.upload_post_post)).perform(click());
    }

    @Test
    public void testCancel(){
        Espresso.onView(withId(R.id.cancelPost)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        postActivity = null;
    }
}