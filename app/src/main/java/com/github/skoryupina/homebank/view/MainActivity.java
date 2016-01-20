package com.github.skoryupina.homebank.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.viewmodel.AccountListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        /*implements NavigationDrawerFragment.NavigationDrawerCallbacks, PopupMenu.OnMenuItemClickListener */{
    private static final String LOG = "LOG";
    public static final int CREATE_OPERATION = 1;
    public static final int VIEW_CATEGORIES = 2;
    //private NavigationDrawerFragment mNavigationDrawerFragment;
    public ArrayList<Account> accountItems;
    public AccountListAdapter adapter;
    private CharSequence mTitle;
    //private ActionMode mode;
    public static final int VIEW_OPERATIONS = 2;
    public static ArrayList<Operation> foundOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);*/
        mTitle = getTitle();
        /*mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));*/

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.plan);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
     /*   mode = startSupportActionMode(new TestActionMode());

        ((Button) findViewById(R.id.action_add)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mode = startSupportActionMode(new TestActionMode());
                    }
                });
        ((Button) findViewById(R.id.action_help)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mode != null) {
                            mode.finish();
                        }
                    }
                });*/
        updateListView();
    }



  /*  private final class TestActionMode implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //setResult(item.getTitle());
            onOptionsItemSelected(item);
            return true;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {}
    }*/


   /* @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }*/

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* if (!mNavigationDrawerFragment.isDrawerOpen()) {*/
            getMenuInflater().inflate(R.menu.main_menu, menu);
            restoreActionBar();
            return true;
      //  }
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_category:{
                startCategoriesActivity(item);
            }break;
            case R.id.action_about: {
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage(R.string.about_toast)
                        .setPositiveButton("OK", null)
                        .show();
            }
            break;

            case R.id.action_exit: {
                //Ask the user if he want to quit
                createExitDialog();
            }
            case R.id.action_add: {
                startCreateAccount();
            }
            break;
            case R.id.find_operations: {
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this)
                        .setTitle("Search by description")
                        .setMessage("Input description");
                final EditText input = new EditText(this);
                input.setText("Default description");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                dialog.setView(input);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mDescription = input.getText().toString().trim();
                        if (!mDescription.equals("")) {
                            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                            foundOperations = dbHelper.getOperationsByDescription(mDescription);
                            if (foundOperations.size()==0){
                                Toast.makeText(MainActivity.this, "Nothing found.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent intent = new Intent(MainActivity.this, ListOfAccountOperationsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("id", 0);
                                MainActivity.this.startActivityForResult(intent, VIEW_OPERATIONS);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Empty search field.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
            break;
        }
        return true;
    }

    private void startCreateAccount() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View meetingDialogView = factory.inflate(
                R.layout.new_account_dialog, null);
        final android.app.AlertDialog.Builder newAccountDialog = new android.app.AlertDialog.Builder(this);
        final EditText name = (EditText) meetingDialogView.findViewById(R.id.nameText);
        final EditText balance = (EditText) meetingDialogView.findViewById(R.id.balanceText);
        final EditText description = (EditText) meetingDialogView.findViewById(R.id.descriptionText);

        newAccountDialog.setView(meetingDialogView)
                .setTitle("New account")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String acc_name = name.getText().toString().trim();
                        int acc_bal = new Integer(balance.getText().toString().trim());
                        String acc_desc = description.getText().toString().trim();

                        if (acc_name.length()>0 && acc_bal>=0) {
                            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                            long id = dbHelper.insertAccount(acc_name,acc_bal,acc_desc);
                            updateListView();
                        }
                        else
                            Toast.makeText(MainActivity.this,"Incorrect data.", Toast.LENGTH_SHORT).show();
                    }
                });
        newAccountDialog.show();
    }



    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            super.onActivityResult(reqCode, resultCode, intent);
            if (reqCode == CREATE_OPERATION) {
                updateListView();
                //Toast.makeText(this, R.string.task_created, Toast.LENGTH_SHORT).show();
            }else if (reqCode == VIEW_CATEGORIES){
                updateListView();
            }

        }
    }

    private void startCategoriesActivity(MenuItem item) {
        Intent intent;
        intent = new Intent(this, CategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent,VIEW_CATEGORIES);
    }

    /***
     * Update view of the scheduler items list
     */
    public void updateListView() {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        accountItems = dbHelper.getAccounts();
        // Create the adapter to convert the array to views
        adapter = new AccountListAdapter(MainActivity.this, R.layout.item_account, accountItems);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvAccounts);
        listView.setAdapter(adapter);
        adapter.setListView(listView);
    }

  /*  public void showPopupHelp() {
        View menuItemView = findViewById(R.id.action_help);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.global, popup.getMenu());
        popup.show();

    }*/
/*
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about: {
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage(R.string.about_toast)
                        .setPositiveButton("OK", null)
                        .show();
            }
            break;

            case R.id.action_exit: {
                //Ask the user if he want to quit
                createExitDialog();
            }
            case R.id.action_add: {
                startCreateAccount();
            }
            break;
            case R.id.find_operations: {
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this)
                        .setTitle("Search by description")
                        .setMessage("Input description");
                final EditText input = new EditText(this);
                input.setText("Default description");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                dialog.setView(input);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mDescription = input.getText().toString().trim();
                        if (!mDescription.equals("")) {
                            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                            foundOperations = dbHelper.getOperationsByDescription(mDescription);
                            if (foundOperations.size()==0){
                                Toast.makeText(MainActivity.this, "Nothing found.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent intent = new Intent(MainActivity.this, ListOfAccountOperationsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("id", 0);
                                MainActivity.this.startActivityForResult(intent, VIEW_OPERATIONS);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Empty search field.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
            break;
        }
        return true;
    }*/

    public void createExitDialog() {
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.exit_confirmation)
                .setMessage(R.string.exit_question)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Finish activity
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();

    }



    /**
 * A placeholder fragment containing a simple view.
 */
public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
         /*   ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));*/
    }

}

}
