package com.se302.photonest;

import android.view.View;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class LogoutTest {

    @Rule
    public ActivityTestRule<ProfileActivity> profileActivityActivityTestRule = new ActivityTestRule<ProfileActivity>(ProfileActivity.class);

    private ProfileActivity profileActivity = null;

    @Before
    public void setUp() throws Exception {
        profileActivity = profileActivityActivityTestRule.getActivity();
    }

    @Test
    public void profileMenuDisplayed(){
        View view = profileActivity.findViewById(R.id.profile_menu_view);
        View view1 = profileActivity.findViewById(R.id.profile_change_password);
        View view2 = profileActivity.findViewById(R.id.profile_delete_account);
        View view3 = profileActivity.findViewById(R.id.profile_logout);
        if(view == null && view1 == null && view2 == null && view3 == null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void LogoutTest(){
        Espresso.onView(withId(R.id.profile_menu_view)).perform(click());
        Espresso.onView(withText("Logout")).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        profileActivity = null;
    }
}
