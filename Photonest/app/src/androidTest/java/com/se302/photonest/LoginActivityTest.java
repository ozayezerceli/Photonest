package com.se302.photonest;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity loginActivity = null;

    @Before
    public void setUp() throws Exception {
        loginActivity = loginActivityActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        loginActivity = null;
    }

    @Test
    public void testLaunch(){
        View view = loginActivity.findViewById(R.id.txtInfo1);
        if(view==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void init() {
        View view = loginActivity.findViewById(R.id.main_email_adress);
        View view1 = loginActivity.findViewById(R.id.main_login_button);
        if(view==null && view1==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void signInClicked() {
        View view = loginActivity.findViewById(R.id.main_login_button);
        if(view==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void signUpClicked() {
        View view = loginActivity.findViewById(R.id.main_signup_button_text);
        View view1 = loginActivity.findViewById(R.id.main_signup_text);
        if(view==null && view1==null) throw new AssertionError("Object cannot be null");
    }

    @Test
    public void forgotpasswordClicked() {
        View view = loginActivity.findViewById(R.id.main_text_forgot_password);
        if(view==null) throw new AssertionError("Object cannot be null");
    }
}