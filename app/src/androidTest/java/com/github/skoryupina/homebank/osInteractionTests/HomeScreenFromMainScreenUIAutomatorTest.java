package com.github.skoryupina.homebank.osInteractionTests;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.github.skoryupina.homebank.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
public class HomeScreenFromMainScreenUIAutomatorTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    @Test
    public void homeScreenTest() throws UiObjectNotFoundException, RemoteException{
        try {
            UiDevice mobilePhone = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            mobilePhone.pressHome();
            Thread.sleep(1000);
            mobilePhone.pressRecentApps();
            Thread.sleep(2000);
            UiObject bank = new UiObject(new UiSelector().description("Bank"));
            bank.click();
            Thread.sleep(5000);
        }
        catch(RemoteException|InterruptedException e){
            e.getMessage();
        }
    }
}
