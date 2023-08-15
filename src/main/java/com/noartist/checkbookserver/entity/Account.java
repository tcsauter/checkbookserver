package com.noartist.checkbookserver.entity;

import com.noartist.checkbookserver.exception.InvalidTypeException;
import org.bson.Document;

public class Account implements Comparable<Account> {
    private String _id;
    private String name;
    private String type;
    private String lastFour;

    public Account() {
    }

    public Account(Document doc) throws InvalidTypeException {
        set_id(doc.get("_id").toString());
        setName(doc.get("name").toString());
        setType(doc.get("type").toString());
        if (doc.containsKey("lastFour")) {
            setLastFour(doc.get("lastFour").toString());
        }
    }

    public Document toDocument() {
        Document newAccountDoc = new Document("_id", get_id())
                .append("name", getName())
                .append("type", getType());

        if (getLastFour() != null) {
            newAccountDoc.append("lastFour", getLastFour());
        }

        return newAccountDoc;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws InvalidTypeException {
        if (type.equalsIgnoreCase("credit")) {
            this.type = "Credit";
        } else if (type.equalsIgnoreCase("cash")) {
            this.type = "Cash";
        } else {
            throw new InvalidTypeException("Account.type can only be Credit or Cash.");
        }
    }

    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    @Override
    public String toString() {
        return "Account{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", lastFour=" + lastFour +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Account) {
            return ((Account) obj).get_id().equals(this.get_id());
        }
        return false;
    }

    @Override
    public int compareTo(Account o) {
        return this.getName().compareTo(o.getName());
    }
}
