package com.se302.photonest;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class EditProfileActivityTest {

    @Rule
    public ActivityTestRule<EditProfileActivity> editprofileActivityTestRule = new ActivityTestRule<EditProfileActivity>(EditProfileActivity.class);

    public EditProfileActivity editProfileActivity = null;

    @Before
    public void setUp() throws Exception {
        editProfileActivity = editprofileActivityTestRule.getActivity();
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
        Espresso.onView(withId(R.id.EditFullName)).perform(clearText()).perform(typeText("TestName"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.EditUsername)).perform(clearText()).perform(typeText("TestUser"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.EditWebsite)).perform(clearText()).perform(typeText("TestWebsite"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.EditBio)).perform(clearText()).perform(typeText("TestBio"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.saveChanges)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        editProfileActivity = null;
    }
}