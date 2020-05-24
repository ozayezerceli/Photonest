package com.se302.photonest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

public class NotificationActivityTest {

    @Rule
    public ActivityTestRule<NotificationActivity> notificationActivityActivityTestRule = new ActivityTestRule<NotificationActivity>(NotificationActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, NotificationActivity.class);
            Bundle extras = new Bundle();
            //  extras.putString("id", "-M816x1oocvBThvGyPx4");
            extras.putString("ispost", "true");
            extras.putString("userid", "4m71yIjpG1asMmpVzK7tgcFAAhC3");
            extras.putString("postid", "-M816x1oocvBThvGyPx4");
            result.putExtras(extras);
            return result;
        }
    };

    public NotificationActivity mActivity= null;


    @Before
    public void setUp() throws Exception {
        mActivity=notificationActivityActivityTestRule.getActivity();

    }

    @Test
    public  void testLaunch(){
        //
        Espresso.onView(withId(R.id.notificatonTxt)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.container_notification)).check(matches(isDisplayed()));


    }

    @Test
    public void testItemClick(){
        //  Espresso.onView(withId(R.id.comment_notification)).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.recycleview_notification))
                .atPosition(0).onChildView(withId(R.id.comment_notification))
                .perform(click());
        //   Espresso.onView(withId(R.id.container_notification)).perform(click());

    }

    @After
    public void tearDown() throws Exception {
        mActivity=null;
    }
}