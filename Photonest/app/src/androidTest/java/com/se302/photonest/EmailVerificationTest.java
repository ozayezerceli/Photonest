package com.se302.photonest;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EmailVerificationTest {

    @Rule
    public ActivityTestRule<EmailVerification> emailVerificationActivityTestRule = new ActivityTestRule<EmailVerification>(EmailVerification.class);

    private EmailVerification emailVerification = null;
    @Before
    public void setUp() throws Exception {
            emailVerification = emailVerificationActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = emailVerification.findViewById(R.id.activate_account_btn);

        if(view==null) throw new AssertionError("Object cannot be null");
    }

    @After
    public void tearDown() throws Exception {
        emailVerification = null;
    }
}