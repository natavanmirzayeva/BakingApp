package com.project.udacity.bakingapp;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by mehseti on 3.6.2018.
 */
@RunWith(AndroidJUnit4.class)
public class DataTest
{
    public final ActivityTestRule<MainActivity> myActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private static final Intent MY_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), MainActivity.class);

    @Before
    public void setup()
    {
        myActivityTestRule.launchActivity(MY_ACTIVITY_INTENT);
    }

    @Test
    public void checkTextDisplayedInDynamicallyCreatedFragment()
    {
        try {
            //Delay to have list available for test
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(withId(R.id.recipe_recycler),isCompletelyDisplayed())).perform(RecyclerViewActions.scrollToPosition(1)).check(matches(hasDescendant(withText("Nutella Pie"))));


    }


}
