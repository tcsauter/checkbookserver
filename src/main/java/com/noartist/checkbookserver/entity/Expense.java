package com.noartist.checkbookserver.entity;

import org.bson.Document;

public class Expense implements Comparable<Expense> {
    private String _id;
    private double amount;
    private String accountId;
    private String date;

    public Expense() {
    }

    public Expense(Document doc) {
        set_id(doc.get("_id").toString());
        setAccountId(doc.get("accountId").toString());
        setAmount(Double.parseDouble(doc.get("amount").toString()));
        setDate(doc.get("date").toString());
    }

    public Document createDocumentFromExpense() {
        return new Document("_id", get_id())
                .append("accountId", getAccountId())
                .append("amount", getAmount())
                .append("date", getDate());
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "amount=" + amount +
                ", accountId=" + accountId +
                ", date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Expense) {
            return ((Expense) obj).get_id().equals(this._id);
        }
        return false;
    }

    @Override
    public int compareTo(Expense o) {
        return this.getDate().compareTo(o.getDate());
    }
}
