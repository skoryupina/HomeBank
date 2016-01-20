package com.github.skoryupina.homebank.model.dbstaff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.BankContract.*;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.model.Category;

import java.util.ArrayList;

import static com.github.skoryupina.homebank.model.dbstaff.BankContract.OperationTable.CATEGORY;
import static com.github.skoryupina.homebank.model.dbstaff.BankContract.OperationTable.DATETIME;
import static com.github.skoryupina.homebank.model.dbstaff.BankContract.OperationTable.DESCRIPTION;
import static com.github.skoryupina.homebank.model.dbstaff.BankContract.OperationTable.FROM_ACCOUNT;
import static com.github.skoryupina.homebank.model.dbstaff.BankContract.OperationTable.TO_ACCOUNT;
import static com.github.skoryupina.homebank.model.dbstaff.BankContract.OperationTable.VALUE;

public class DatabaseHelper extends SQLiteOpenHelper {
    //for debug
    private static final String LOG = "LOG";
    private static Context mContext;


    public DatabaseHelper(Context context) {
        super(context, BankContract.DATABASE_NAME, null, BankContract.DATABASE_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(BankContract.SQL_CREATE_ACCOUNTS);
            db.execSQL(BankContract.SQL_CREATE_CATEGORIES);
            db.execSQL(BankContract.SQL_CREATE_OPERATIONS);
        } catch (SQLException e) {
            Log.d(LOG, e.getMessage());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(BankContract.SQL_DELETE_OPERATIONS);
        db.execSQL(BankContract.SQL_DELETE_CATEGORIES);
        db.execSQL(BankContract.SQL_DELETE_ACCOUNTS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Category> getCategories() {
        ArrayList<Category> listOfCategories = new ArrayList<Category>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(BankContract.SQL_SELECT_CATEGORIES, null);
        if (cursor.moveToFirst())
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable._ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.CATEGORY)));
                category.setType(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.TYPE)));
                listOfCategories.add(category);
            } while (cursor.moveToNext());
        cursor.close();
        return listOfCategories;
    }


    /***
     * By type of operation
     */
    public ArrayList<String> getCategories(String typeOfOperation) {
        ArrayList<String> listOfCategories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(BankContract.SQL_SELECT_CATEGORIES_BYTYPE + typeOfOperation + "\'", null);
        if (cursor.moveToFirst())
            do {
                String nameOfCategory = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.CATEGORY));
                listOfCategories.add(nameOfCategory);
            } while (cursor.moveToNext());
        cursor.close();
        return listOfCategories;
    }

    public long insertCategory(String category, String type) {
        SQLiteDatabase db = /*this.*/getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryTable.CATEGORY, category);
        values.put(CategoryTable.TYPE, type);
        long newRowId = db.insert(
                CategoryTable.TABLE_NAME,
                null,
                values);
        return newRowId;
    }


    public ArrayList<Account> getAccounts() {
        ArrayList<com.github.skoryupina.homebank.model.Account> listOfAccounts = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(BankContract.SQL_SELECT_ACCOUNTS_GENERAL, null);
            if (cursor.moveToFirst())
                do {
                    Account account = new Account();
                    account.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable._ID)));
                    account.setName(cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.NAME)));
                    account.setBalance(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable.BALANCE)));
                    account.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.DESCRIPTION)));
                    account.setTotalIncome(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable.TOTAL_INCOME)));
                    account.setTotalOutlay(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable.TOTAL_OUTLAY)));
                    listOfAccounts.add(account);
                } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return listOfAccounts;
    }

    public ArrayList<String> getAccounts(int id, int outlay) {
        String TRANSFERABLE_ACCOUNTS = "SELECT _id, name FROM account WHERE balance > " + (-outlay);
        ArrayList<String> listOfAccounts = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(TRANSFERABLE_ACCOUNTS, null);
            if (cursor.moveToFirst())
                do {
                    Account account = new Account();
                    account.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable._ID)));
                    account.setName(cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.NAME)));
                    if (id != account.getId()) {
                        listOfAccounts.add(account.getName());
                    }
                } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return listOfAccounts;
    }

    public long insertAccount(String name, int balance, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccountTable.NAME, name);
        values.put(AccountTable.BALANCE, balance);
        values.put(AccountTable.DESCRIPTION, desc);
        values.put(AccountTable.TOTAL_INCOME, 0);
        values.put(AccountTable.TOTAL_OUTLAY, 0);
        long newRowId = db.insert(
                AccountTable.TABLE_NAME,
                null,
                values);
        return newRowId;
    }

    public Account getAccount(int id) {
        String SELECT_ACCOUNT_BY_ID = "SELECT name, balance, total_income, total_outlay FROM account WHERE _id = \'";
        Account account = new Account();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(SELECT_ACCOUNT_BY_ID + id + "\'", null);
            if (cursor.moveToFirst())
                do {
                    account.setId(id);
                    account.setName(cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.NAME)));
                    account.setBalance(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable.BALANCE)));
                    account.setTotalIncome(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable.TOTAL_INCOME)));
                    account.setTotalOutlay(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable.TOTAL_OUTLAY)));
                } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return account;
    }

    public void updateBalanceAccount(int id, int balance, int value, boolean cancelled) {
        try {
            String UPDATE_BALANCE = "UPDATE account SET balance = " + (balance + value)  + " WHERE _id = \'" + id + "\'";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(UPDATE_BALANCE);
            if (cancelled) {
                if (value < 0) {
                    String UPDATE_TOTAL_INCOME = "UPDATE account SET total_income = total_income + \'" + value + "\' WHERE _id = \'" + id + "\'";
                    db.execSQL(UPDATE_TOTAL_INCOME);
                } else {
                    String UPDATE_TOTAL_OUTLAY = "UPDATE account SET total_outlay = total_outlay + \'" + value + "\' WHERE _id = \'" + id + "\'";
                    db.execSQL(UPDATE_TOTAL_OUTLAY);
                }
            }else{
                if (value < 0){
                    String UPDATE_TOTAL_OUTLAY = "UPDATE account SET total_outlay = total_outlay + \'" + (value) + "\' WHERE _id = \'" + id + "\'";
                    db.execSQL(UPDATE_TOTAL_OUTLAY);
                } else {
                    String UPDATE_TOTAL_INCOME = "UPDATE account SET total_income = total_income + \'" + (value) + "\' WHERE _id = \'" + id + "\'";
                    db.execSQL(UPDATE_TOTAL_INCOME);
                }
            }
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
    }

    public ArrayList<String> getAccounts(int id) {
        ArrayList<String> listOfAccounts = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(BankContract.SQL_SELECT_ACCOUNTS_GENERAL, null);
            if (cursor.moveToFirst())
                do {
                    com.github.skoryupina.homebank.model.Account taskItem = new com.github.skoryupina.homebank.model.Account();
                    taskItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable._ID)));
                    taskItem.setName(cursor.getString(cursor.getColumnIndexOrThrow(AccountTable.NAME)));
                    if (id != taskItem.getId()) {
                        listOfAccounts.add(taskItem.getName());
                    }
                } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return listOfAccounts;
    }

    public void moveOperations(int idFrom, int idTo) {
        try {
            ArrayList<Operation> listOfOperations = getOperationsForAccount(idFrom);
            Account accountTo = getAccount(idTo);
            for (Operation operation : listOfOperations){
                if (operation.getTypeOfCategory().equals(Category.INCOME)){
                   accountTo.changeBalance(Integer.valueOf(operation.getValue()));
                }
                else if (operation.getTypeOfCategory().equals(Category.OUTLAY)){
                    accountTo.changeBalance(-Integer.valueOf(operation.getValue()));
                }
                else if (operation.getToAccount()!=idTo){
                    accountTo.changeBalance(-Integer.valueOf(operation.getValue()));
                }
            }//now balance is actual

            //update operations from_account parameter
            String UPDATE_ACCOUNT_TO  = "UPDATE account SET balance = \'" + accountTo.getBalance()  +
                    "\', total_income = \'" + accountTo.getTotalIncome() +
                    "\', total_outlay = \'" + accountTo.getTotalOutlay() +
                    "\' WHERE _id = \'" + idTo + "\'";
            String UPDATE_OPERATIONS  = "UPDATE operation SET from_account = \'" + idTo  + "\' WHERE from_account = \'" + idFrom + "\'";
            String DELETE_ACCOUNT_FROM = "DELETE FROM account WHERE _id = \'" + idFrom + "\'";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(UPDATE_ACCOUNT_TO);
            db.execSQL(UPDATE_OPERATIONS);
            db.execSQL(DELETE_ACCOUNT_FROM);
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
    }

    public Account getAccountByName(String to) {
        Account account = new Account();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(BankContract.SQL_SELECT_ACCOUNT_ID_BY_NAME + to + "\'", null);
            if (cursor.moveToFirst()) {
                account.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable._ID)));
                account.setBalance(cursor.getInt(cursor.getColumnIndexOrThrow(AccountTable.BALANCE)));
                account.setName(to);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return account;
    }

    public long insertOperation(int from, String datetime, int value, int categoryID, String description) {
        long newRowId = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(FROM_ACCOUNT, from);
            values.put(CATEGORY, categoryID);
            values.put(DATETIME, datetime);
            values.put(VALUE, value);
            values.put(DESCRIPTION, description);
            newRowId = db.insert(
                    OperationTable.TABLE_NAME,
                    null,
                    values);
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return newRowId;
    }


    public long insertOperation(int from, String to, String datetime, int value, String description) {
        long newRowId = -1;
        try {
            int idTo = -1;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(BankContract.SQL_SELECT_ACCOUNT_ID_BY_NAME + to + "\'", null);
            if (cursor.moveToFirst()) {
                idTo = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(AccountTable._ID)));
            }
            ContentValues values = new ContentValues();
            values.put(FROM_ACCOUNT, from);
            values.put(TO_ACCOUNT, idTo);
            values.put(DATETIME, datetime);
            values.put(VALUE, value);
            values.put(DESCRIPTION, description);
            newRowId = db.insert(
                    OperationTable.TABLE_NAME,
                    null,
                    values);
        } catch (Exception e) {
            Log.d(LOG, e.getMessage());
        }
        return newRowId;
    }

    public Category getCategoryType(String name) {
        Category category = new Category();
        category.setName(name);
        String SELECT_CATEGORY_TYPE = "SELECT _id, type FROM category WHERE name = \'" + name + "\'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_CATEGORY_TYPE, null);
        if (cursor.moveToFirst())
            do {
                category.setType(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.TYPE)));
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable._ID)));
            } while (cursor.moveToNext());
        cursor.close();
        return category;
    }

    public Operation getOperation(int id){
        String SELECT_OPERATION = "SELECT op._id, op.datetime, op.description, c.name, c.type, op.value, op.from_account  " +
                " FROM operation op JOIN category c ON op.category = c._id  WHERE op._id = \'" + id + "\'";
        Operation operation = new Operation();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_OPERATION, null);
        if (cursor.moveToFirst())
            do {
                String[] columnNames = cursor.getColumnNames();
                operation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable._ID)));
                operation.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.VALUE)));
                operation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DATETIME)));
                operation.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DESCRIPTION)));
                operation.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.CATEGORY)));
                operation.setTypeOfCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.TYPE)));
                operation.setFromAccount(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.FROM_ACCOUNT)));
            } while (cursor.moveToNext());
        cursor.close();
        return operation;
    }

    public ArrayList<Operation> getOperationsByDescription(String pattern){
        ArrayList<Operation> listOfOperations = new ArrayList<>();
        String SELECT_OPERATIONS = "SELECT op._id, op.from_account, op.datetime, op.description, c.name, c.type, op.value " +
                " FROM operation op JOIN category c ON op.category = c._id WHERE op.description LIKE \'%" + pattern + "%\'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_OPERATIONS, null);
        if (cursor.moveToFirst())
            do {
                Operation operation = new Operation();
                operation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable._ID)));
                operation.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.VALUE)));
                operation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DATETIME)));
                operation.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DESCRIPTION)));
                operation.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.CATEGORY)));
                operation.setTypeOfCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.TYPE)));
                operation.setFromAccount(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.FROM_ACCOUNT)));
                listOfOperations.add(operation);
            } while (cursor.moveToNext());
        return listOfOperations;
    }

    public Operation getOperationByDescription(String description) {
        String SELECT_OPERATION = "SELECT op._id, op.from_account, op.datetime, op.description, c.name, c.type, op.value " +
                " FROM operation op JOIN category c ON op.category = c._id ";
        Operation operation = new Operation();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_OPERATION + " WHERE op.description = \'" + description + "\'", null);
        if (cursor.moveToFirst())
            do {
                operation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable._ID)));
                operation.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.VALUE)));
                operation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DATETIME)));
                operation.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DESCRIPTION)));
                operation.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.CATEGORY)));
                operation.setTypeOfCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.TYPE)));
                operation.setFromAccount(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.FROM_ACCOUNT)));
            } while (cursor.moveToNext());
        return operation;
    }

    public ArrayList<Operation> getOperationsForAccount(int id) {
        String SELECT_OPERATIONS = "SELECT op._id, op.datetime, op.description, c.name, c.type, op.value "+
                " FROM operation op JOIN category c ON op.category = c._id ";
        ArrayList<Operation> listOfOperations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_OPERATIONS + " WHERE op.from_account = \'" + id + "\'", null);
        if (cursor.moveToFirst())
            do {
                Operation operation = new Operation();
                String[] columnNames = cursor.getColumnNames();
                operation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable._ID)));
                operation.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.VALUE)));
                operation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DATETIME)));
                operation.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DESCRIPTION)));
                operation.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.CATEGORY)));
                operation.setTypeOfCategory(cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.TYPE)));
                operation.setFromAccount(id);
                listOfOperations.add(operation);
            } while (cursor.moveToNext());

        //for orders
        SELECT_OPERATIONS = "SELECT _id, datetime, description,to_account, value FROM operation "+
                " WHERE category IS NULL AND from_account = \'" + id + "\'";
        cursor = db.rawQuery(SELECT_OPERATIONS,null);
        if (cursor.moveToFirst()) {
            do {
                Operation operation = new Operation();
                String[] columnNames = cursor.getColumnNames();
                operation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable._ID)));
                operation.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.VALUE)));
                operation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DATETIME)));
                operation.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(OperationTable.DESCRIPTION)));
                operation.setFromAccount(id);
                operation.setToAccount(cursor.getInt(cursor.getColumnIndexOrThrow(OperationTable.TO_ACCOUNT)));
                operation.setCategory(Category.ORDER);
                operation.setTypeOfCategory(Category.ORDER);
                listOfOperations.add(operation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listOfOperations;
    }

    public void deleteCategory(int id) {
        deleteOperationsOfCategory(id);
        String DELETE_CATEGORY = "DELETE FROM " + CategoryTable.TABLE_NAME +
                " WHERE " + CategoryTable._ID + " = '" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_CATEGORY);
    }

    private void deleteOperationsOfCategory(int id) {
        String DELETE_OPERATIONS = "DELETE FROM " + OperationTable.TABLE_NAME +
                " WHERE " + OperationTable.CATEGORY + " = '" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_OPERATIONS);
    }


    public void deleteAccount(int id) {
        String DELETE_ACCOUNT_FROM = "DELETE FROM account WHERE _id = \'" + id + "\'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_ACCOUNT_FROM);
    }

    public void deleteOperation(Operation operationToDelete, boolean cancelled) {
        String typeOfOperation = operationToDelete.getTypeOfCategory();
        switch (typeOfOperation){
            case Category.INCOME:{
                Account account = getAccount(operationToDelete.getFromAccount());
                updateBalanceAccount(account.getId(),account.getBalance(),-Integer.valueOf(operationToDelete.getValue()),cancelled);
            }break;
            case Category.OUTLAY:{
                Account account = getAccount(operationToDelete.getFromAccount());
                updateBalanceAccount(account.getId(),account.getBalance(),Integer.valueOf(operationToDelete.getValue()),cancelled);
            }break;
            case Category.ORDER:{
                Account accountFrom = getAccount(operationToDelete.getFromAccount());
                Account accountTo = getAccount(operationToDelete.getToAccount());
                updateBalanceAccount(accountFrom.getId(),accountFrom.getBalance(),Integer.valueOf(operationToDelete.getValue()),cancelled);
                updateBalanceAccount(accountTo.getId(),accountTo.getBalance(),-Integer.valueOf(operationToDelete.getValue()),cancelled);
            }break;
        }
        String DELETE_OPERATION = "DELETE FROM operation WHERE _id=\'" + operationToDelete.getId() + "\'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_OPERATION);
    }
}


