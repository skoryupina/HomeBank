package com.github.skoryupina.homebank.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Operation;
import com.github.skoryupina.homebank.model.dbstaff.DatabaseHelper;
import com.github.skoryupina.homebank.model.Account;
import com.github.skoryupina.homebank.view.ListOfAccountOperationsActivity;
import com.github.skoryupina.homebank.view.MainActivity;
import com.github.skoryupina.homebank.view.NewOperationActivity;

import java.util.ArrayList;

public class AccountListAdapter extends ArrayAdapter<Account> {
    private int layoutResource;
    private ListView listView;
    public ArrayList<Account> accounts;
    private MainActivity context;
    private static final String LOG = "LOG";
    private String selectedAccountTo;
    private int idTo;
    private Account accountToDelete;
    public static final int CREATE_OPERATION = 1;
    public static final int VIEW_OPERATIONS = 2;

    public static class ItemHolder {
        public TextView name;
        public TextView balance;
        RelativeLayout mainView;
    }

    public AccountListAdapter(Context context, int layoutResource, ArrayList<Account> accounts) {
        super(context, layoutResource, accounts);
        this.accounts = accounts;
        this.layoutResource = layoutResource;
        this.context = (MainActivity) context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ItemHolder viewHolder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.item_account, null);
            viewHolder = new ItemHolder();
            viewHolder.mainView = (RelativeLayout) view.findViewById(R.id.item_task_mainview);
            viewHolder.balance = (TextView) view.findViewById(R.id.tvBalance);
            viewHolder.name = (TextView) view.findViewById(R.id.tvAccount);
            view.setTag(viewHolder);
        } else viewHolder = (ItemHolder) view.getTag();

        Account account = accounts.get(position);

        if (account != null) {
            viewHolder.name.setText(account.getName());
            viewHolder.balance.setText(String.valueOf(account.getBalance()));
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.mainView.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        viewHolder.mainView.setLayoutParams(params);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, position);
            }
        });

        return view;
    }

    private void showPopup(View v, final int pos) {
        final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.inflate(R.menu.menu_context);
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete: {
                                accountToDelete = getItem(pos);
                                showDeleteBillDialog(pos);
                                return true;
                            }
                            case R.id.descript: {
                                Account account = getItem(pos);
                                showDescription(account.getDescription());
                                return true;
                            }
                            case R.id.add_operat: {
                                Account account = getItem(pos);
                                Intent intent = new Intent(context, NewOperationActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("id", account.getId());
                                intent.putExtra("name", account.getName());
                                intent.putExtra("balance", account.getBalance());
                                context.startActivityForResult(intent, CREATE_OPERATION);
                            }
                            break;
                            case R.id.view_operations: {
                                Account account = getItem(pos);
                                Intent intent = new Intent(context, ListOfAccountOperationsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("id", account.getId());
                                intent.putExtra("name", account.getName());
                                context.startActivityForResult(intent, VIEW_OPERATIONS);
                            }
                            break;
                        }
                        return true;
                    }
                });

        popupMenu.show();
    }

    private void showDescription(String description) {
        if (description != null && description.length() > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }

                    });
            dialog.setTitle("Description")
                    .setMessage(description);
            dialog.show();
        } else {
            Toast.makeText(context, "Empty description.", Toast.LENGTH_SHORT).show();
        }

    }

    private void showDeleteBillDialog(final int pos) {
        //first - check has operations this account or not
        if (accountToDelete.getTotalIncome() > 0 || accountToDelete.getTotalOutlay() < 0) {
            //account has operations
            //check if there any accounts to transfer all operations
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View billDialogView = factory.inflate(R.layout.delete_account_dialog, null);
            Spinner accountToSpinner = (Spinner) billDialogView.findViewById(R.id.spinner);
            final int code = settingSpinner(accountToSpinner, accountToDelete.getId(), accountToDelete.getTotalOutlay());
            if (code == 0) {
                Toast.makeText(getContext(), "No available account to transfer operations.", Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog.Builder billDialog = new AlertDialog.Builder(getContext());
                billDialog.setTitle("Delete account")
                        .setMessage("Choose account to move operations")
                        .setView(billDialogView)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAccount(pos);
                            }
                        });

                billDialog.show();
            }
        } else {//no operations for the account
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.deleteAccount(accountToDelete.getId());
            context.updateListView();
            Toast.makeText(getContext(), R.string.delete_account_toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAccount(int pos) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            //transfer all operations
            dbHelper.moveOperations(accountToDelete.getId(), idTo);
            remove(getItem(pos));
            notifyDataSetChanged();
            context.updateListView();
            Toast.makeText(getContext(), "Account deleted.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private int settingSpinner(final Spinner toSpinner, int id, int outlay) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        ArrayList<String> list = dbHelper.getAccounts(id, outlay);
        if (list.size() > 0) {//another accounts
            String[] listString = new String[list.size()];
            list.toArray(listString);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listString);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            toSpinner.setAdapter(adapterSpinner);
            toSpinner.setSelection(0);
            toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedAccountTo = (String) toSpinner.getSelectedItem();
                    for (Account a : accounts) {
                        if (a.getName().equals(selectedAccountTo))
                            idTo = a.getId();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            return 1;
        } else {
            //if no avalable accounts to transfer operations
            return 0;
        }
    }


   /* private ItemHolder getAccountHolder(View workingView, int position) {
        Object tag = workingView.getTag();
        ItemHolder holder = null;

        if (tag == null || !(tag instanceof ItemHolder)) {
            holder = new ItemHolder();

            AccountTable taskItem = getItem(position);

            holder.name = (TextView) workingView.findViewById(R.id.tvCategory);
            holder.balance = (TextView) workingView.findViewById(R.id.tvTime);
            workingView.setTag(holder);

            // Populate the data into the template view using the data object
            //holder.name.setText(taskItem.category);
            //String description = taskItem.description;
        } else {
            holder = (ItemHolder) tag;
        }
        return holder;
    }*/


    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Account getItem(int position) {
        return accounts.get(position);
    }


    public void setListView(ListView view) {
        listView = view;
        /*LongClickDetector longClickDetector = new LongClickDetector();
        listView.setOnItemLongClickListener(longClickDetector);*/
    }
/*
    public class LongClickDetector implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
            TaskItem taskItem = getItem(pos);
            Intent intent = new Intent(getContext(), ListOfAccountOperationsActivity.class);
            intent.putExtra(context.getString(R.string.task_id_details), taskItem.id);
            intent.putExtra(context.getString(R.string.task_caterory_details), taskItem.category);
            intent.putExtra(context.getString(R.string.task_duration_details), taskItem.duration);
            intent.putExtra(context.getString(R.string.task_summary_details), taskItem.description);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getContext().startActivity(intent);
            return false;
        }
    }*/
}
