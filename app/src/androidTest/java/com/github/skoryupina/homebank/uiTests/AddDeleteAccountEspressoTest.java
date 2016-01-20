package com.github.skoryupina.homebank.uiTests;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Root;
import android.support.test.espresso.core.deps.guava.io.Resources;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.view.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

@RunWith(AndroidJUnit4.class)
public class AddDeleteAccountEspressoTest {
    @Rule
    public final ActivityTestRule<MainActivity> account = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = getInstrumentation()
                    .getTargetContext();
            return new Intent(targetContext, MainActivity.class);
        }
    };

    @Test
    public void addDeleteAccountEspresso() {


        //create an account
       // android.content.res.Resources resources = getInstrumentation().getTargetContext().getResources();
       // int actionAdd = resources.getIdentifier(String.valueOf(R.string.action_add), String.valueOf(R.id.action_add), "android");
        /*onView(withId(R.id.action_help)).perform(click());
        onView(withId(actionAdd)).perform(click());*/
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
       // onView(withText(R.string.action_help)).perform(click());
        onView(withText(R.string.action_add)).perform(click());
       // onView(ViewMatchers.withId(R.id.action_add)).perform(click());
        onView(withText("New account")).check(matches(allOf(withText("New account"), isDisplayed())));
        onView(withId(R.id.nameText)).perform(clearText());
        onView(withId(R.id.nameText)).perform(typeText("vtb"));
        onView(withId(R.id.balanceText)).perform(clearText());
        onView(withId(R.id.balanceText)).perform(typeText("800"));
        onView(withId(R.id.descriptionText)).perform(clearText());
        onView(withId(R.id.descriptionText)).perform(typeText("Test description"));
        onView(withId(android.R.id.button1)).perform(click());

        //delete the account

        onData(allOf(is(instanceOf(Account.class)), withListItemCheck("vtb"))).perform(click());
        /*onView(allOf(is(instanceOf(Account.class)), hasSibling(withText("vtb"))))
                .perform(click());*/
        onView(withText("Delete")).perform(click());
        onView(withText(R.string.delete_account_toast_message)).inRoot(new ToastMatcher()).perform(click());
    }

    public static Matcher<Object> withListItemCheck(final String value) {

        return new BoundedMatcher<Object,Account>(Account.class) {
            @Override
            public boolean matchesSafely(Account myObj) {
                return myObj.getName().equals(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content \'" + value + "\'");
            }
        };
    }

    private class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    // windowToken == appToken means this window isn't contained by any other windows.
                    // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                    return true;
                }
            }
            return false;
        }
    }

}
