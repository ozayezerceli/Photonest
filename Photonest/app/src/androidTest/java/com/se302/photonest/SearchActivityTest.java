package com.se302.photonest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
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
        Espresso.onView(withId(R.id.imageView));
        Espresso.onView(withId(R.id.usersBtn));
        Espresso.onView(withId(R.id.hashtagsBtn));
        Espresso.onView(withId(R.id.search_list));
        Espresso.onView(withId(R.id.search_txt));
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
        Espresso.onView(withId(R.id.hashtagsBtn)).perform(click());
        Espresso.onView(withId(R.id.search_txt)).perform(typeText("drawing"));
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