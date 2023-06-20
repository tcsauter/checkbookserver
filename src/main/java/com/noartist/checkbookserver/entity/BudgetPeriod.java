package com.noartist.checkbookserver.entity;

public class BudgetPeriod implements Comparable<BudgetPeriod> {
    private String _id;
    private String payDate;
    private String budgetStart;
    private String budgetEnd;
    private double startingAmt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getBudgetStart() {
        return budgetStart;
    }

    public void setBudgetStart(String budgetStart) {
        this.budgetStart = budgetStart;
    }

    public String getBudgetEnd() {
        return budgetEnd;
    }

    public void setBudgetEnd(String budgetEnd) {
        this.budgetEnd = budgetEnd;
    }

    public double getStartingAmt() {
        return startingAmt;
    }

    public void setStartingAmt(double startingAmt) {
        this.startingAmt = startingAmt;
    }

    @Override
    public String toString() {
        return "BudgetPeriod{" +
                "_id='" + _id + '\'' +
                ", payDate='" + payDate + '\'' +
                ", budgetStart='" + budgetStart + '\'' +
                ", budgetEnd='" + budgetEnd + '\'' +
                ", startingAmt=" + startingAmt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BudgetPeriod){
            return ((BudgetPeriod) obj).getPayDate().equals(this.getPayDate());
        }
        return false;
    }


    @Override
    public int compareTo(BudgetPeriod o) {
        return this.getPayDate().compareTo(o.getPayDate());
    }
}
