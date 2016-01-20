package com.github.skoryupina.homebank.uiTests;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Root;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.view.ListOfAccountOperationsActivity;
import com.github.skoryupina.homebank.view.NewOperationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddOperationsEspressoTest {
    public static final String DEFAULT_NAME = "mainTest";
    DatabaseHelper dbHelper;

   @Rule
    public final ActivityTestRule<NewOperationActivity> operation = new ActivityTestRule<NewOperationActivity>(NewOperationActivity.class) {
       @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            dbHelper=new DatabaseHelper(targetContext);
            Intent intent = new Intent(targetContext, NewOperationActivity.class);
            intent.putExtra("id", -1);
            intent.putExtra("name", DEFAULT_NAME);
            intent.putExtra("balance", 1000);
            return intent;
        }
    };

    @Test
    public void addOperationEspresso() {
        onView(withId(R.id.spinnerCategory)).check(matches(withSpinnerText(containsString("TestCategory"))));
        onView(withId(R.id.etValue)).perform(replaceText("15"));
        onView(withId(R.id.etDescription)).perform(replaceText("Test description"));
        onView(withId(R.id.create_button)).perform(
                click());
        onView(withText("Income operation added.")).inRoot(new ToastMatcher()).perform(click());
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
