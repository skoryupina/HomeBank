package com.github.skoryupina.homebank.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.skoryupina.homebank.R;
import com.github.skoryupina.homebank.model.Category;
import com.github.skoryupina.homebank.model.Operation;

import java.util.ArrayList;

public class OperationsAdapter extends ArrayAdapter<Operation> {
    private final Context context;
    public ArrayList<Operation> operationItems;

    public OperationsAdapter(Context context, ArrayList<Operation> values) {
        super(context, R.layout.item_operation, values);
        this.context = context;
        this.operationItems = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_operation, parent, false);


        TextView dateTextView = (TextView) rowView.findViewById(R.id.dateOfOperation);
        TextView descriptionTextView = (TextView) rowView.findViewById(R.id.descriptionOfOperation);
        TextView valueTextView = (TextView) rowView.findViewById(R.id.value);
        TextView categoryTextView = (TextView) rowView.findViewById(R.id.categoryOfOperation);
        ImageView icon = (ImageView) rowView.findViewById(R.id.operationCategoryIcon);

        Operation operation = operationItems.get(position);

        dateTextView.setText(operation.getDate());
        descriptionTextView.setText(operation.getDescription());
        valueTextView.setText(operation.getValue());
        categoryTextView.setText(operation.getCategory());
        if (operation.getTypeOfCategory().equals(Category.INCOME)) {
            icon.setImageResource(R.drawable.income);
        } else if (operation.getTypeOfCategory().equals(Category.OUTLAY)) {
            icon.setImageResource(R.drawable.outlay);
        }
        else {
            icon.setImageResource(R.drawable.order);
        }
        return rowView;
    }
}
