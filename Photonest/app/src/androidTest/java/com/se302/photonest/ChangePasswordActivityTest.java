package com.se302.photonest;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class ChangePasswordActivityTest {

    @Rule
    public ActivityTestRule<ChangePasswordActivity> changePasswordActivityTestRule = new ActivityTestRule<ChangePasswordActivity>(ChangePasswordActivity.class);

    ChangePasswordActivity changePassword = null;
    @Before
    public void setUp() throws Exception {
        changePassword = changePasswordActivityTestRule.getActivity();
    }

    @Test
    public void changePasswordDisplayed() {
        View view = changePassword.findViewById(R.id.txt_ChangePwd);
        if(view==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void changePassword2Displayed() {
        View view = changePassword.findViewById(R.id.txt_ChangePwd2);
        if(view==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void currentPasswordDisplayed() {
        View view = changePassword.findViewById(R.id.txt_CurrentPwd);
        if(view==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void buttonchangePasswordDisplayed() {
        View view = changePassword.findViewById(R.id.btnChangePwd);
        if(view==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void clickonDoneButton(){
        Espresso.onView(withId(R.id.txt_CurrentPwd)).perform(typeText("1234567"));
        Espresso.onView(withId(R.id.txt_ChangePwd)).perform(typeText("123456"));
        Espresso.onView(withId(R.id.txt_ChangePwd2)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.btnChangePwd)).perform(click());
        //Espresso.onView(withId(R.id.mainpagelayout)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        changePassword = null;
    }
}