package com.github.skoryupina.homebank.model.dbstaff;

import android.provider.BaseColumns;

public final class BankContract {
    //for debug
    private static final String LOG = "LOG";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HomeBank";

    public static final String SQL_CREATE_ACCOUNTS =
            "CREATE TABLE account (" +
                    AccountTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " name TEXT NOT NULL UNIQUE, "+
                    " description TEXT,"+
                    " balance INTEGER," +
                    " total_income INTEGER," +
                    " total_outlay INTEGER)";

    public static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE category (" +
                    CategoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE,"+
                    "type TEXT)";

    public static final String SQL_CREATE_OPERATIONS =
            "CREATE TABLE operation (" +
                    OperationTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " from_account INTEGER,"+
                    " to_account INTEGER,"+
                    " datetime TEXT," +
                    " value INTEGER,"+
                    " category INTEGER, description TEXT, type TEXT,"+
                    " FOREIGN KEY (from_account) REFERENCES account (" + AccountTable._ID + "),"+
                    " FOREIGN KEY (to_account) REFERENCES account (" + AccountTable._ID + "),"+
                    " FOREIGN KEY (category) REFERENCES account (" + CategoryTable._ID + "))";



    public static final String SQL_DELETE_ACCOUNTS = "DROP TABLE  account";
    public static final String SQL_DELETE_OPERATIONS = "DROP TABLE  operation";
    public static final String SQL_DELETE_CATEGORIES = "DROP TABLE  category";


    public static final String SQL_SELECT_ACCOUNTS_GENERAL = "SELECT _id, name, balance, description,total_income,total_outlay FROM account";
    public static final String SQL_SELECT_ACCOUNT_ID_BY_NAME = "SELECT _id, balance FROM account WHERE name = \'";
    public static final String SQL_SELECT_ACCOUNT_BY_ID = "SELECT name, balance FROM account WHERE _id = \'";

    public static final String SQL_SELECT_CATEGORIES= "SELECT _id, name, type FROM category";
    public static final String SQL_SELECT_CATEGORY_ID_BY_NAME = "SELECT _id FROM category WHERE name = \'";


    public static final String SQL_SELECT_OPERATIONS = "SELECT _id, datetime, value FROM operation";

    public static final String SQL_DELETE_ACCOUNT= "DELETE FROM account WHERE _id = \'";
    public static final String SQL_SELECT_CATEGORIES_BYTYPE = "SELECT name FROM category WHERE type = \'";
    public static final String SQL_SELECT_OPERATION_TO_DELETE = " SELECT from_account,to_account,category FROM operation WHERE value = \'";


    public static abstract class AccountTable implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String BALANCE = "balance";
        public static final String TOTAL_INCOME = "total_income";
        public static final String TOTAL_OUTLAY = "total_outlay";
    }

    public static abstract class OperationTable implements BaseColumns {
        public static final String TABLE_NAME = "operation";
        public static final String FROM_ACCOUNT = "from_account";
        public static final String TO_ACCOUNT = "to_account";
        public static final String DATETIME = "datetime";
        public static final String VALUE = "value";
        public static final String CATEGORY = "category";
        public static final String DESCRIPTION = "description";
        public static final String TYPE = "type";
    }

    public static abstract class CategoryTable implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String CATEGORY = "name";
        public static final String TYPE = "type";
    }
}
