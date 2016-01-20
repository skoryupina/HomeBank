package com.github.skoryupina.homebank.modelTests;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.model.Category;
import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.view.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DeleteAccountTest extends ActivityInstrumentationTestCase2   {
    private DatabaseHelper dbHelper;
    private int balanceBefore;
    private int balanceAfter;
    private Instrumentation mInstrumentation;
    private int  idCategory;
    private Account accountToDetele;
    private Account accountToTransfer;
    private int total_income;
    private int total_outlay;
    private int idOperation;
    private Operation operationTransferred;

    public DeleteAccountTest() {
        super(MainActivity.class);
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
        dbHelper.insertAccount("accountToDetele",2000,"Description: accountToDetele");
        accountToDetele=dbHelper.getAccountByName("accountToDetele");
        dbHelper.insertAccount("accountToTransfer",2000,"Description: accountToTransfer");
        accountToTransfer=dbHelper.getAccountByName("accountToTransfer");
        idCategory = (int)dbHelper.insertCategory("TestAccountDelete", "OUTLAY");
        idOperation = (int) dbHelper.insertOperation(accountToDetele.getId(), "2016-12-10", 2000, idCategory, "Description: operationToMove");
        dbHelper.updateBalanceAccount(accountToDetele.getId(), 2000, -2000, false);

        total_income = dbHelper.getAccount(accountToDetele.getId()).getTotalIncome();
        total_outlay = dbHelper.getAccount(accountToDetele.getId()).getTotalOutlay();
    }



    @Test
    public void testDeleteAccount() {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        assertNotNull(accountToDetele);
        assertNotNull(accountToTransfer);
        balanceBefore = accountToTransfer.getBalance();
        dbHelper.moveOperations(accountToDetele.getId(), accountToTransfer.getId());
        balanceAfter = dbHelper.getAccount(accountToTransfer.getId()).getBalance();
        assertEquals(balanceAfter, (balanceBefore + total_income + total_outlay));
        operationTransferred = dbHelper.getOperation(idOperation);
        assertEquals(operationTransferred.getFromAccount(), accountToTransfer.getId());
    }

    @After
    public void tearDown() throws Exception {
        deleteTestData();
        super.tearDown();
    }

    private void deleteTestData(){
        dbHelper.deleteOperation(operationTransferred,true);
        dbHelper.deleteCategory(idCategory);
        dbHelper.deleteAccount(accountToTransfer.getId());
        dbHelper.close();
    }
}
