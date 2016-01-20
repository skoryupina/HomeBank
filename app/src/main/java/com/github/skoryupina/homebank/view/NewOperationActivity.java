package com.github.skoryupina.homebank.view;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skoryupina.homebank.model.Category;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Account;


public class NewOperationActivity extends AppCompatActivity {
    private TextView mStartDate;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String[] mType = {"INCOME", "OUTLAY", "ORDER"};
    private Spinner mTypeSpinner;
    private Spinner mToSpinner;
    private Spinner mCategorySpinner;
    private int idFrom;//selected account
    private String nameFrom;
    private String nameTo;
    private String typeOfOperation;
    private int balance;
    private long lastOperationID;
    public static Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_operation);
        Intent i = getIntent();
        if (i != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                idFrom = extras.getInt("id");
                nameFrom = extras.getString("name");
                balance = extras.getInt("balance");
            }
            if (idFrom ==-1){
                DatabaseHelper dbHelper=new DatabaseHelper(this);
                long id = dbHelper.insertAccount(nameFrom, balance, "Test");
                account = dbHelper.getAccountByName(nameFrom);
                account.setName(nameFrom);
                idFrom = account.getId();
                balance = account.getBalance();
                dbHelper.insertCategory("TestCategory","INCOME");
            }
        }
        prepareActivityStuff();
    }

    public int getIdFrom() {
        return idFrom;
    }

    public long getLastOperationID() {
        return lastOperationID;
    }

    public void setLastOperationID(long lastOperationID) {
        this.lastOperationID = lastOperationID;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public void prepareActivityStuff() {
        try {
            //date
            mStartDate = (TextView) findViewById(R.id.inputStartDate);
            final Calendar c = Calendar.getInstance();
            mStartDate.setText(new SimpleDateFormat(DATE_FORMAT).format(c.getTime()));

            //spinners
            mTypeSpinner = (Spinner) findViewById(R.id.spinnerType);
            setAdapter(mTypeSpinner, mType);
            mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    mCategorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
                    typeOfOperation = mType[position];
                    if (position == 2) {
                        //hide to
                        mToSpinner.setEnabled(true);
                        mCategorySpinner.setEnabled(false);
                    } else {
                        mToSpinner.setEnabled(false);
                        DatabaseHelper dbHelper = new DatabaseHelper(NewOperationActivity.this);
                        ArrayList<String> listOfCategories = dbHelper.getCategories(typeOfOperation);
                        String[] listString = new String[listOfCategories.size()];
                        setAdapter(mCategorySpinner, listOfCategories.toArray(listString));
                    }
                }

                @Override
                public void onNothingSelected
                        (AdapterView<?> arg0) {
                }
            });

            mToSpinner = (Spinner) findViewById(R.id.spinnerTo);
            settingToSpinner(mToSpinner, idFrom);
        } catch (Exception e) {
            e.getMessage();
        }

    }


    private void settingToSpinner(final Spinner toSpinner, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ArrayList<String> list = dbHelper.getAccounts(id);
        if (list.size() > 0) {//another accounts
            String[] listString = new String[list.size()];
            list.toArray(listString);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listString);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            toSpinner.setAdapter(adapterSpinner);
            toSpinner.setSelection(0);
            toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    nameTo = (String) toSpinner.getSelectedItem();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    private void setAdapter(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelButtonClicked(View view) {
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    public void createNewOperation(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String strValue = ((EditText) findViewById(R.id.etValue)).getText().toString();
        if (strValue.equals("")) {
            Toast.makeText(this, "Incorrect value.", Toast.LENGTH_SHORT).show();
        } else {
            int value = Integer.valueOf(strValue);
            if (value > 0) {
                String description = ((EditText) findViewById(R.id.etDescription)).getText().toString();
                String datetime = ((TextView) findViewById(R.id.inputStartDate)).getText().toString();
                String typeOfOperation = mTypeSpinner.getSelectedItem().toString();

                if (typeOfOperation.equals(Category.INCOME)) {
                    if (mCategorySpinner.getAdapter().getCount() == 0) {
                        Toast.makeText(this, "Please create categories first.", Toast.LENGTH_SHORT).show();
                    } else {
                        Category category = dbHelper.getCategoryType(mCategorySpinner.getSelectedItem().toString());
                        lastOperationID = dbHelper.insertOperation(idFrom, datetime, value, category.getId(), description);
                        dbHelper.updateBalanceAccount(idFrom, balance, value,false);
                        Toast.makeText(this, "Income operation added.", Toast.LENGTH_SHORT).show();
                    }
                } else if (balance < value) {
                    Toast.makeText(this, "Insufficient funds: " + balance + "$ only.", Toast.LENGTH_SHORT).show();
                } else {
                    if (typeOfOperation.equals(Category.ORDER)) {
                        if (nameTo == null) {
                            Toast.makeText(this, "Please create another account.", Toast.LENGTH_SHORT).show();
                        } else {
                            lastOperationID = dbHelper.insertOperation(idFrom, nameTo, datetime, value, description);
                            dbHelper.updateBalanceAccount(idFrom, balance, -value,false);
                            Account toAccount = dbHelper.getAccountByName(nameTo);
                            dbHelper.updateBalanceAccount(toAccount.getId(), toAccount.getBalance(), value,false);
                            Toast.makeText(getBaseContext(), "Order operation added.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (mCategorySpinner.getAdapter().getCount() == 0) {
                            Toast.makeText(this, "Please create categories first.", Toast.LENGTH_SHORT).show();
                        } else {
                            Category category = dbHelper.getCategoryType(mCategorySpinner.getSelectedItem().toString());
                            lastOperationID = dbHelper.insertOperation(idFrom, datetime, value, category.getId(), description);
                            dbHelper.updateBalanceAccount(idFrom, balance, -value,false);
                            Toast.makeText(this, "Outlay operation added.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (account == null) {
                    setResult(Activity.RESULT_OK, null);
                    finish();
                }
            }
        }
    }
}
