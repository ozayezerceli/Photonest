package com.se302.photonest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import Utils.CommentActivity;
import androidx.appcompat.view.menu.ListMenuItemView;
import androidx.appcompat.widget.ActionMenuView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.hasWindowLayoutParams;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isFocusable;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow;
import static androidx.test.espresso.matcher.RootMatchers.isTouchable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

public class ViewProfileActivityTest {
    @Rule
    public ActivityTestRule<ViewProfileActivity> viewProilfeActivityTestRule = new ActivityTestRule<ViewProfileActivity>(ViewProfileActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, ViewProfileActivity.class);
            Bundle extras = new Bundle();
            extras.putString("user_id", "yTwwjxRIL0UBHLtLL6HU8LzRY3M2");
            result.putExtras(extras);
            return result;
        }
    };

    private ViewProfileActivity viewProfile = null;

    @Before
    public void setUp() throws Exception {
        viewProfile= viewProilfeActivityTestRule.getActivity();
      //  Espresso.onView(withId(R.id.username_main)).perform(click());
    }


    @Test
    public void testLaunch(){
        Espresso.onView(withId(R.id.View_profile_image)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.ViewusernameTxt)).check(matches(isDisplayed()));
       Espresso.onView(withId(R.id.view_profile_menu_view)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.View_bio_profile)).check(matches(isDisplayed()));

    }

    @Test
    public void testBlockUser(){
        Espresso.onView(withId( R.id.view_profile_menu_view)).perform(click());
   //     Espresso.onView(withId(R.id.view_profile_block)).inRoot(isPlatformPopup()).perform(click());
            Espresso.onData(withId(R.id.view_profile_block)).inRoot(RootMatchers.isPlatformPopup())
                    .perform(ViewActions.click());

    }

    @After
    public void tearDown() throws Exception {
        viewProfile=null;
    }
}