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

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private final Context context;
    public ArrayList<Category> categoryItems;

    public CategoryAdapter(Context context, ArrayList<Category> values) {
        super(context, R.layout.item_category, values);
        this.context = context;
        this.categoryItems = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_category, parent, false);
        TextView categoryTextView = (TextView) rowView.findViewById(R.id.categoryName);
        ImageView icon = (ImageView) rowView.findViewById(R.id.categoryIcon);
        Category category = categoryItems.get(position);
        categoryTextView.setText(category.name);
        if (category.type.equals(Category.INCOME)) {
            icon.setImageResource(R.drawable.income);
        }
        else {
            icon.setImageResource(R.drawable.outlay);
        }
        return rowView;
    }
}
