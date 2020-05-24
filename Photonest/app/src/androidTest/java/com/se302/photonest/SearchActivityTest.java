package com.se302.photonest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

public class SearchActivityTest {
    @Rule
    public ActivityTestRule<SearchActivity> mActivityRule= new ActivityTestRule<>(SearchActivity.class);
    public SearchActivity searchActivity = null;


    @Before
    public void setUp() throws Exception {
        searchActivity=mActivityRule.getActivity();



    }

    @Test
    public void testLaunch(){
        Espresso.onView(withId(R.id.imageView)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.usersBtn)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.hashtagsBtn)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.search_list)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.search_txt)).check(matches(isDisplayed()));
      

    }

    @Test
    public void testSearchUser(){
        Espresso.onView(withId(R.id.usersBtn)).perform(click());
        Espresso.onView(withId(R.id.search_txt)).perform(typeText("senafrakara"));
        onData(anything())
                .inAdapterView(withId(R.id.search_list))
                .atPosition(0)
                .onChildView(withId(R.id.search_photo))
                .perform(click());


    }
    @Test
    public void testSearchHashtags(){

        Espresso.onView(withId(R.id.search_txt)).perform(typeText("drawing"));
        Espresso.onView(withId(R.id.hashtagsBtn)).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.search_list))
                .atPosition(0)
                .onChildView(withId(R.id.search_hashtag))
                .perform(click());


    }

    @After
    public void tearDown() throws Exception {
        searchActivity=null;
    }
}