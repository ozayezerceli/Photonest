package com.se302.photonest;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RegistrationActivityTest {

    @Rule
    public ActivityTestRule<RegistrationActivity> registrationActivityActivityTestRule = new ActivityTestRule<RegistrationActivity>(RegistrationActivity.class);

    private RegistrationActivity registrationActivity = null;

    @Before
    public void setUp() throws Exception {
        registrationActivity = registrationActivityActivityTestRule.getActivity();
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