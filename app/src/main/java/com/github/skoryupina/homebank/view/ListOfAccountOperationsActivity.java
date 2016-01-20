package com.github.skoryupina.homebank.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.viewmodel.AccountListAdapter;
import com.github.skoryupina.homebank.viewmodel.OperationsAdapter;

import java.util.ArrayList;

public class ListOfAccountOperationsActivity extends AppCompatActivity {
    private OperationsAdapter mOperationsAdapter;
    private ListView lvOperations;
    private int mPosition;
    private int id;
    private long lastId;
    private String name;
    public static DatabaseHelper dbHelper;
    public static Account account;
    public static Operation operation;
    public static final String DEFAULT_NAME = "main";
    private ArrayList<Operation> listOfOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_details);
        Intent i = getIntent();
        if (i.getAction() == Intent.ACTION_MAIN) {//launched from the
            dbHelper = new DatabaseHelper(this);
            account = dbHelper.getAccountByName(DEFAULT_NAME);
            lastId = dbHelper.insertOperation(account.getId(), "2016-01-09", 1000, 1, "Description");
            id = account.getId();
            name = DEFAULT_NAME;
        } else {
            Bundle extras = i.getExtras();
            if (extras != null) {
                id = extras.getInt("id");
                name = extras.getString("name");
            }
            if (id == -1) {
                String desc = extras.getString("description");
                dbHelper = new DatabaseHelper(this);
                account = dbHelper.getAccountByName(name);
                operation = dbHelper.getOperationByDescription(desc);
                account.setName(name);
                id = account.getId();
            }
        }
        lvOperations = (ListView) findViewById(R.id.lvOperations);
        if (id == 0) {
            createOrUpdateListView(MainActivity.foundOperations);
        } else {
            createOrUpdateListView(id);
            lvOperations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long arg3) {
                    mPosition = pos;
                    new AlertDialog.Builder(ListOfAccountOperationsActivity.this)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setTitle(R.string.delete_confirmation)
                            .setMessage(R.string.delete_question)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getApplicationContext(), "Operation deleted.", Toast.LENGTH_SHORT).show();
                                    removeItemFromList(mPosition);
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();

                    return false;
                }
            });
        }
    }

    public long getLastId() {
        return lastId;
    }

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    public void removeItemFromList(int pPosition) {
        Operation operationToDelete = mOperationsAdapter.getItem(pPosition);
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.deleteOperation(operationToDelete, false);
        mOperationsAdapter.remove(operationToDelete);
        mOperationsAdapter.notifyDataSetChanged();
        if (mOperationsAdapter.operationItems.size() == 0) {
            Toast.makeText(this, "Account has no operations.", Toast.LENGTH_SHORT).show();
            if (account == null) {
                setResult(Activity.RESULT_OK, null);
                finish();
            }
        }
    }

    public void createOrUpdateListView(int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        listOfOperations = dbHelper.getOperationsForAccount(id);
        if (listOfOperations.size() > 0) {
            mOperationsAdapter = new OperationsAdapter(this, listOfOperations);
            lvOperations.setAdapter(mOperationsAdapter);
        } else {
            Toast.makeText(this, "Account has no operations.", Toast.LENGTH_SHORT).show();
            if (account == null) {
                setResult(Activity.RESULT_OK, null);
                finish();
            }
        }
    }

    public void createOrUpdateListView(ArrayList<Operation> listOfFoundOperations) {
        mOperationsAdapter = new OperationsAdapter(this, listOfFoundOperations);
        lvOperations.setAdapter(mOperationsAdapter);
        Toast.makeText(this, "Search finished.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
