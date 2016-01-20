package com.github.skoryupina.homebank.model;


public class Account {
    private static int incID=0;
    private int id;
    private String name;
    private String description;
    private int balance;
    private int totalIncome;
    private int totalOutlay;
    {
        id = ++incID;
    }
    public Account() {
    }
    /*public AccountTable(String name, String description, int balance) {
        id = ++incID;
        this.name = name;
        this.description = description;
        this.balance = balance;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    public int getTotalOutlay() {
        return totalOutlay;
    }

    public void setTotalOutlay(int totalOutlay) {
        this.totalOutlay = totalOutlay;
    }

    public void changeBalance(int value){
        balance = balance + value;
        if (value>0){
            totalIncome = totalIncome + value;
        }
        else{
            totalOutlay = totalOutlay + value;
        }
    }
}
