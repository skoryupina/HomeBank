package com.github.skoryupina.homebank.osInteractionTests;

import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;

import com.github.skoryupina.homebank.view.CategoriesActivity;
import com.github.skoryupina.homebank.view.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RotateCategoriesScreenUIAutomatorTest {
    @Rule
    public final ActivityTestRule<CategoriesActivity> categories = new ActivityTestRule<>(CategoriesActivity.class);

    @Test
    public void rotateTest(){
        try {
            UiDevice mobilePhone = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            mobilePhone.setOrientationLeft();
            Thread.sleep(3000);
            mobilePhone.setOrientationNatural();
            Thread.sleep(3000);
        }
        catch(RemoteException|InterruptedException e){
            e.getMessage();
        }
    }
}
