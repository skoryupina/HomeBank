package com.github.skoryupina.homebank.model;

public class Operation {
    private int id;
    private int fromAccount;
    private int toAccount;
    private int value;
    private String date;
    private String description;
    private String category;
    private String typeOfCategory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(int fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String  getValue() {
        return String.valueOf( value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeOfCategory() {
        return typeOfCategory;
    }

    public void setTypeOfCategory(String typeOfCategory) {
        this.typeOfCategory = typeOfCategory;
    }

    public int getToAccount() {
        return toAccount;
    }

    public void setToAccount(int toAccount) {
        this.toAccount = toAccount;
    }
}
