package com.se302.photonest;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class EditProfileFragmentTest {

    @Rule
    public ActivityTestRule<ProfileActivity> profileActivityActivityTestRule = new ActivityTestRule<ProfileActivity>(ProfileActivity.class);

    public ProfileActivity profileActivity = null;

    @Before
    public void setUp() throws Exception {
        profileActivity = profileActivityActivityTestRule.getActivity();
        Espresso.onView(withId(R.id.edit_profile_button)).perform(click());
    }

    @Test
    public void testlaunch(){
        Espresso.onView(withId(R.id.EditFullName)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.EditUsername)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.EditWebsite)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.EditBio)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.saveChanges)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.profile_image_edit)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.delete_image_photo)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfileInformation(){
        Espresso.onView(withId(R.id.EditFullName)).perform(typeText("TestFullname"));
        Espresso.onView(withId(R.id.EditUsername)).perform(typeText("TestUsername"));
        Espresso.onView(withId(R.id.EditBio)).perform(typeText("TestBio"));

        Espresso.onView(withId(R.id.saveChanges)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        profileActivity = null;
    }
}