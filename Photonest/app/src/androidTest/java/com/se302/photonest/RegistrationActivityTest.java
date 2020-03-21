package com.se302.photonest;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class RegistrationActivityTest {

    @Rule
    public ActivityTestRule<RegistrationActivity> registrationActivityActivityTestRule = new ActivityTestRule<RegistrationActivity>(RegistrationActivity.class);

    private RegistrationActivity registrationActivity = null;


    @Before
    public void setUp() throws Exception {
        registrationActivity = registrationActivityActivityTestRule.getActivity();
    }

    @Test
    public void testUserInputScenario(){
        Espresso.onView(withId(R.id.username_registration)).perform(typeText("Hasan"));
        Espresso.onView(withId(R.id.fullname_registration)).perform(replaceText("Hasan Yazıcı"));
        Espresso.onView(withId(R.id.email_registration)).perform(typeText("hasan@gmail.com"));
        Espresso.onView(withId(R.id.password_registration)).perform(typeText("123456"));
        Espresso.onView(withId(R.id.password_registration2)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.btn_register_page)).perform(click());

    }

    @Test
    public void testLaunch(){
        View view = registrationActivity.findViewById(R.id.btn_register_page);
        View view2 = registrationActivity.findViewById(R.id.username_registration);
        View view3 = registrationActivity.findViewById(R.id.password_registration);
        View view4 = registrationActivity.findViewById(R.id.email_registration);
        View view5 = registrationActivity.findViewById(R.id.password_registration);

        if(view==null && view2==null && view3==null && view4==null && view5==null) throw new AssertionError("Object cannot be null");
    }

    @After
    public void tearDown() throws Exception {
        registrationActivity = null;
    }

    @Test
    public void sendEmailVerification() {

    }

    @Test
    public void buildActionCodeSettings() {

    }
}