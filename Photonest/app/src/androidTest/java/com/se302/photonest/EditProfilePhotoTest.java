package com.se302.photonest;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class EditProfilePhotoTest {

    @Rule
    IntentsTestRule<ProfileActivity> profileActivityActivityTestRule = new IntentsTestRule<ProfileActivity>(ProfileActivity.class);

    ProfileActivity profileActivity = null;

    @Before
    public void setUp() throws Exception {
        profileActivity = profileActivityActivityTestRule.getActivity();
        Espresso.onView(withId(R.id.edit_profile_button)).perform(click());
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK,null));
    }

    /*private Instrumentation.ActivityResult stubActivityResult(){
        Intent resultdata = new Intent();
        //int uri = firebasestorage.googleapis.com/v0/b/photonest-11327.appspot.com/o/place_holder_photo.png?alt=media&token=60a9a8bb-5f09-41de-986c-16bc44497adb;
        //resultdata.setData(uri);
    }
*/
    @After
    public void tearDown() throws Exception {
        profileActivity = null;
    }
}