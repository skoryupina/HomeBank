package com.github.skoryupina.homebank.uiTests;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.Root;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.view.WindowManager;

import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.view.ListOfAccountOperationsActivity;
import com.github.skoryupina.homebank.view.NewOperationActivity;
import com.github.skoryupina.homebank.viewmodel.OperationsAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;

public class DeleteOperationEspressoTest {
    public static final String DEFAULT_NAME = "mainTest";
    DatabaseHelper dbHelper;

    @Rule
    public final ActivityTestRule<ListOfAccountOperationsActivity> operation = new ActivityTestRule<ListOfAccountOperationsActivity>(ListOfAccountOperationsActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            dbHelper=new DatabaseHelper(targetContext);
            Intent intent = new Intent(targetContext, ListOfAccountOperationsActivity.class);
            intent.putExtra("id", -1);
            intent.putExtra("name", DEFAULT_NAME);
            intent.putExtra("balance", 1000);
            intent.putExtra("description", "Test description");
            return intent;
        }
    };

    @Test
    public void deleteOperationEspresso() {
        Operation operation = ListOfAccountOperationsActivity.operation;
        Espresso.onData(is(instanceOf(OperationsAdapter.class))).inAdapterView(withId(R.id.lvOperations));
        onData(allOf(is(instanceOf(Operation.class)), withListItemCheck(operation.getValue().toString()))).perform(longClick());
        onView(withText("Yes")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(click());
        onView(withText("Operation deleted.")).inRoot(new ToastMatcher()).perform(click());
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

    public static Matcher<Object> withListItemCheck(final String value) {

        return new BoundedMatcher<Object,Operation>(Operation.class) {
            @Override
            public boolean matchesSafely(Operation myObj) {
                return (Integer.valueOf(myObj.getValue())==Integer.valueOf(value));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content '" + value + "'");
            }
        };
    }

    @After
    public void deleteTestData(){
        Account account = dbHelper.getAccountByName(DEFAULT_NAME);
        dbHelper.deleteAccount(account.getId());
        dbHelper.close();
    }
}
