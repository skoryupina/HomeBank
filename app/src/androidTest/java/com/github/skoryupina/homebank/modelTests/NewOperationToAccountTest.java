package com.github.skoryupina.homebank.modelTests;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.RenamingDelegatingContext;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.view.NewOperationActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@RunWith(AndroidJUnit4.class)
public class NewOperationToAccountTest extends ActivityInstrumentationTestCase2  {
    Instrumentation mInstrumentation;
    Account accountBefore;
    DatabaseHelper dbHelper;
    public static final String DEFAULT_NAME = "TEST_NEW_OPERATION";
    public static final int DEFAULT_VALUE = 20;
    public static final String DEFAULT_DESCRIPTION = "Description";
    private int idOperation;
    private int  idCategory;
    private int  idAccount;

    public NewOperationToAccountTest() {
        super(NewOperationActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mInstrumentation = getInstrumentation();
        addTestData();
    }

    private void addTestData(){
        dbHelper=new DatabaseHelper(getActivity());
        idAccount = (int)dbHelper.insertAccount(DEFAULT_NAME, 2000, "Test Description");
        accountBefore = dbHelper.getAccountByName(DEFAULT_NAME);
        idCategory = (int)dbHelper.insertCategory("TestCategory", "INCOME");
    }

    @Test
    public void testAddOperation(){
        assertNotNull(accountBefore);
        idOperation = (int)dbHelper.insertOperation(idAccount, "2016-16-01", DEFAULT_VALUE, idCategory, DEFAULT_DESCRIPTION);
        dbHelper.updateBalanceAccount(idAccount, accountBefore.getBalance(),DEFAULT_VALUE,false);
        Account accountAfter = dbHelper.getAccount(idAccount);
        assertNotNull(accountAfter);
        assertEquals(accountAfter.getBalance() - accountBefore.getBalance(), DEFAULT_VALUE);
    }

    @After
    public void tearDown() throws Exception{
        deleteTestData();
        super.tearDown();
    }

    private void deleteTestData(){
        Operation operationToDelete = dbHelper.getOperation(idOperation);
        dbHelper.deleteOperation(operationToDelete,true);
        dbHelper.deleteCategory(idCategory);
        dbHelper.deleteAccount(idAccount);
    }

}


