package com.noartist.checkbookserver.entity;

import java.time.LocalDate;

public class Expense implements Comparable<Expense> {
    private int id;
    private double amount;
    private int acctId;
    private LocalDate date;

    public Expense() {
    }

    public Expense(int id, double amount, int acctId, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.acctId = acctId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getAcctId() {
        return acctId;
    }

    public void setAcctId(int acctId) {
        this.acctId = acctId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", acctId=" + acctId +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Expense){
            return ((Expense) obj).getId() == this.id;
        }
        return false;
    }

    @Override
    public int compareTo(Expense o) {
        return this.getDate().compareTo(o.getDate());
    }
}
