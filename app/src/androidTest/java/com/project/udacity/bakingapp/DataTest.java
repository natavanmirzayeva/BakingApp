package com.project.udacity.bakingapp;

import android.content.ClipData;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.project.udacity.bakingapp.ui.RecipeFragment;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
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
    public void setup() {


        myActivityTestRule.launchActivity(MY_ACTIVITY_INTENT);
    }



    @Test
    public void checkTextDisplayedInDynamicallyCreatedFragment() {
        RecipeFragment fragment = new RecipeFragment();
        myActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_recipe, fragment,"recipe").commit();
//.check(matches(withText("Nutella Pie")))
        //onView(allOf(withId(R.id.recipe_recycler), isDisplayed())).perform(RecyclerViewActions.scrollToPosition(0));
        //onView(allOf(withId(R.id.recipe_recycler),isDisplayed())).perform(RecyclerViewActions.scrollToPosition(3)).check(matches(withText("Cheesecake")));
        //onView(ViewMatchers.withText("Nutella Pie")).check(matches(isDisplayed()));
       // onView(allOf(withId(R.id.recipe_recycler),isCompletelyDisplayed())).perform(ViewActions.swipeUp()).perform(RecyclerViewActions.scrollToPosition(3)).check(matches(withText("Yellow Cake")));
        // onView(ViewMatchers.withText("Cheesecake")).check(matches(isDisplayed()));
        //check(matches((withText(""))));
       // Log.d("456Test456",onView(allOf(withId(R.id.recipe_recycler),isCompletelyDisplayed())).perform(RecyclerViewActions.scrollToPosition(3)).toString());

        try {
            //Delay to have list available for test
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //onView(allOf(ViewMatchers.withId(R.id.recipe_recycler), isCompletelyDisplayed())).perform(RecyclerViewActions.scrollToPosition(1));
        //onView(ViewMatchers.withText("Brownies")).check(matches(isCompletelyDisplayed()));
        // onView(allOf(withId(R.id.recipe_recycler),isCompletelyDisplayed())).perform(RecyclerViewActions.scrollToPosition(1)).check(matches(withText("\"Brownies\"")));
        /*onView(allOf(withId(R.id.recipe_recycler),isCompletelyDisplayed()))
                .check(matches(atPosition(1, hasDescendant(withText("Brownies")))));*/
        onView(allOf(withId(R.id.recipe_recycler),isCompletelyDisplayed())).perform(RecyclerViewActions.scrollToPosition(1)).check(matches(hasDescendant(withText("Nutella Pie"))));


    }


}
