package com.github.skoryupina.homebank.modelTests;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.view.ListOfAccountOperationsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class DeleteOperationFromAccountTest extends ActivityInstrumentationTestCase2<ListOfAccountOperationsActivity> {
    ListOfAccountOperationsActivity mActivity;
    DatabaseHelper dbHelper;
    Instrumentation mInstrumentation;
    public static final String DEFAULT_NAME = "TEST_DELETE_OPERATION";
    public static final int DEFAULT_VALUE = 20;
    public static final String DEFAULT_DESCRIPTION = "Description";
    Operation operation;
    private int idOperation;
    private int  idCategory;
    private int  idAccount;
    Account accountBefore;

    public DeleteOperationFromAccountTest() {
        super(ListOfAccountOperationsActivity.class);
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
        idOperation = (int)dbHelper.insertOperation(idAccount, "2016-16-01", DEFAULT_VALUE, idCategory, DEFAULT_DESCRIPTION);
        dbHelper.updateBalanceAccount(idAccount, accountBefore.getBalance(), DEFAULT_VALUE, false);
    }


    @Test
    public void testDeleteOperation() {
        assertNotNull(accountBefore);
        int startBalance = dbHelper.getAccountByName(DEFAULT_NAME).getBalance();;
        Operation operationToDelete = dbHelper.getOperation(idOperation);
        dbHelper.deleteOperation(operationToDelete, true);  //second parameter - about cancellation
        int afterBalance = dbHelper.getAccountByName(DEFAULT_NAME).getBalance();
        assertEquals("accountTo:", startBalance - afterBalance, DEFAULT_VALUE);
    }

    @After
    public void tearDown() throws Exception {
        deleteTestData();
        super.tearDown();
    }

    private void deleteTestData(){
        dbHelper.deleteCategory(idCategory);
        dbHelper.deleteAccount(idAccount);
    }
}
