package com.github.skoryupina.homebank.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Category;
import com.github.skoryupina.homebank.viewmodel.CategoryAdapter;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {
    private CategoryAdapter mCategoriesListAdapter;
    private ListView mLvCategories;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        mLvCategories = (ListView) findViewById(R.id.lvCategories);
        createOrUpdateListView();
        mLvCategories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long arg3) {
                mPosition = pos;
                new AlertDialog.Builder(CategoriesActivity.this)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle(R.string.delete_confirmation)
                        .setMessage(R.string.delete_question)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(), "Category: " + mLvCategories.getItemAtPosition(mPosition).toString() + " deleted.", Toast.LENGTH_SHORT).show();
                                removeItemFromList(mPosition);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();

                return false;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.category);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_categories, menu);
        return true;
    }

    public void createOrUpdateListView() {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<Category> listOfCategories = dbHelper.getCategories();
        if (listOfCategories.size() > 0) {
            mCategoriesListAdapter = new CategoryAdapter(this, listOfCategories);
            mLvCategories.setAdapter(mCategoriesListAdapter);
        }
    }


    public void insertCategory(View v) {
        EditText editTextNewCategory = (EditText) findViewById(R.id.newCategoryName);
        String newCategory = editTextNewCategory.getText().toString();
        if (newCategory.length() > 0) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            RadioGroup rad = (RadioGroup) findViewById(R.id.radioPriority);
            StringBuilder type = new StringBuilder();
            int checkedRadioButtonId = rad.getCheckedRadioButtonId();
            switch (checkedRadioButtonId) {
                case R.id.incomeRadio:
                    type.append(Category.INCOME);
                    break;
                case R.id.outlayRadio:
                    type.append(Category.OUTLAY);
                    break;
            }
            long id = dbHelper.insertCategory(newCategory, type.toString());
            if (id != -1) {
                createOrUpdateListView();
            } else {
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
            }
            editTextNewCategory.setText("", null);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editTextNewCategory.getWindowToken(), 0);
        } else {
            Toast.makeText(this, R.string.empty_category, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeItemFromList(int pPosition) {
        Category categoryToDelete = mCategoriesListAdapter.getItem(pPosition);
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.deleteCategory(categoryToDelete.getId());
        createOrUpdateListView();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, null);
        finish();
    }
}
