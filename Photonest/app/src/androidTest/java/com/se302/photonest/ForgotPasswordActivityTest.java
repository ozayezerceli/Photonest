package com.se302.photonest;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ForgotPasswordActivityTest {

    @Rule
    public ActivityTestRule<ForgotPasswordActivity> forgotPasswordActivityActivityTestRule = new ActivityTestRule<ForgotPasswordActivity>(ForgotPasswordActivity.class);

    private ForgotPasswordActivity forgotPasswordActivity = null;

    @Before
    public void setUp() throws Exception {
        forgotPasswordActivity = forgotPasswordActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = forgotPasswordActivity.findViewById(R.id.btnSend_ForgotPassword);
        View view2 = forgotPasswordActivity.findViewById(R.id.txtEmail_ForgotPassword);

        if(view==null && view2==null) throw new AssertionError("Object cannot be null");
    }

    @After
    public void tearDown() throws Exception {
        forgotPasswordActivity = null;
    }
}