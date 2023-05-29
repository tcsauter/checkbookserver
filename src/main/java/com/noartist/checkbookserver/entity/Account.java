package com.noartist.checkbookserver.entity;

public class Account implements Comparable<Account>{
    private String _id;
    private String name;
    private String type;
    private int lastFour;

    public String get_Id() {
        return _id;
    }

    public void set_Id(String _id) {
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

    public int getLastFour() {
        return lastFour;
    }

    public void setLastFour(int lastFour) {
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
        if(obj instanceof Account){
            return ((Account) obj).get_Id().equals(this.get_Id());
        }
        return false;
    }

    @Override
    public int compareTo(Account o) {
        return this.getName().compareTo(o.getName());
    }

    class InvalidTypeException extends Exception {
        public InvalidTypeException(String message){
            super(message);
        }
    }
}
