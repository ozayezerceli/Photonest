package com.se302.photonest;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PhotoFragmentTest {

    //@Rule
    //public ActivityTestRule<PostActivity> postActivityActivityTestRule = new ActivityTestRule<PostActivity>(PostActivity.class);
    @Rule
    public IntentsTestRule<PostActivity> intentsRule = new IntentsTestRule<PostActivity>(PostActivity.class);

    private PostActivity postActivity = null;

    @Before
    public void setUp() throws Exception {
        postActivity = intentsRule.getActivity();
        Espresso.onView(ViewMatchers.withText("CAMERA")).perform(click());
    }


    @Test
    public void testLaunch(){
        Espresso.onView(withId(R.id.photo_close)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.launch_camera)).check(matches(isDisplayed()));
    }

    @Test
    public void testCameraIntent() throws InterruptedException {
        Bitmap icon = BitmapFactory.decodeResource(
                intentsRule.getActivity().getResources(),
                R.drawable.rating_bar_empty_egg);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", icon);

        resultData.putExtras(bundle);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        Espresso.onView(withId(R.id.launch_camera)).perform(click());
        Espresso.onView(withId(R.id.upload_post_description)).perform(typeText("#testCaption"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.upload_post_add_location_btn)).perform(click());
        Espresso.onView(withId(R.id.action_geolocate)).perform(click());
        Thread.sleep(3000);
        Espresso.onView(withId(R.id.listPlaces)).check(matches(isDisplayed()));
        Espresso.onData(anything()).inAdapterView(withId(R.id.listPlaces)).atPosition(0).perform(click());
        Espresso.onView(withId(R.id.upload_post_post)).perform(click());
        Thread.sleep(5000);
        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        //intended(toPackage("com.android.camera2"));

    }

    @Test
    public void testCancel(){
        Espresso.onView(withId(R.id.photo_close)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        postActivity = null;
    }
}