package com.se302.photonest;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class EditProfilePhotoTest {

    @Rule
    public IntentsTestRule<ProfileActivity> profileActivityActivityTestRule = new IntentsTestRule<ProfileActivity>(ProfileActivity.class);

    private ProfileActivity profileActivity = null;

    @Before
    public void setUp() throws Exception {
        profileActivity = profileActivityActivityTestRule.getActivity();
        Espresso.onView(withId(R.id.edit_profile_button)).perform(click());
    }

    @Test
    public void changephotoTest(){

        Espresso.onView(withId(R.id.profile_image_edit)).check(matches(isDisplayed()));
        /*Bitmap icon = BitmapFactory.decodeResource(
                profileActivityActivityTestRule.getActivity().getResources(),
                R.drawable.liked_egg);
*/
        //Uri uri = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/myimage");

        Intent resultData = new Intent();
        //Resources resources = InstrumentationRegistry.getInstrumentation().getContext().getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                profileActivityActivityTestRule.getActivity().getResources().getResourcePackageName(R.drawable.liked_egg) + "/" +
                profileActivityActivityTestRule.getActivity().getResources().getResourceTypeName(R.drawable.liked_egg) + "/" +
                profileActivityActivityTestRule.getActivity().getResources().getResourceEntryName(R.drawable.liked_egg));
        resultData.setData(uri);

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Gallery
        Espresso.onView(withId(R.id.profile_image_edit)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        profileActivity = null;
    }
}