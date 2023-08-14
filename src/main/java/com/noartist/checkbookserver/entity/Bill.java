package com.noartist.checkbookserver.entity;

import com.noartist.checkbookserver.exception.InvalidDueDateException;
import com.noartist.checkbookserver.exception.InvalidFrequencyException;
import org.bson.Document;

public class Bill implements Comparable<Bill> {
    private String _id;
    private String description;
    private double amount;
    private Frequency frequency;
    private String due;
    private boolean isPaidInInstallments;
    private double paidSoFar;
    private boolean isPaidFromBudget;
    private String comment;

    public Document createdDocumentFromBill() {
        Document doc = new Document("_id", this.get_id())
                .append("description", this.getDescription())
                .append("amount", this.getAmount())
                .append("frequency", this.getFrequency())
                .append("due", this.getDue())
                .append("isPaidInInstallments", this.isPaidInInstallments())
                .append("paidSoFar", this.getPaidSoFar())
                .append("isPaidFromBudget", this.isPaidFromBudget());

        if (getComment() != null) {
            doc.append("comment", this.getComment());
        }

        return doc;
    }

    public Bill() {}

    public Bill(Document doc) throws NumberFormatException, InvalidFrequencyException, InvalidDueDateException {
        this.set_id(doc.get("_id").toString());
        this.setDescription(doc.get("description").toString());
        this.setAmount(Double.parseDouble(doc.get("amount").toString()));
        this.setFrequency(doc.get("frequency").toString());
        this.setDue(doc.get("due").toString());
        this.setPaidInInstallments(Boolean.parseBoolean(doc.get("isPaidInInstallments").toString()));
        this.setPaidSoFar(Double.parseDouble(doc.get("paidSoFar").toString()));
        this.setPaidFromBudget(Boolean.parseBoolean(doc.get("isPaidFromBudget").toString()));

        if (doc.containsKey("comment")) {
            this.setComment(doc.get("comment").toString());
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return this.frequency.toString();
    }

    public void setFrequency(String frequency) throws InvalidFrequencyException {
        switch (frequency.toLowerCase()) {
            case "yearly" -> this.frequency = Frequency.Y;
            case "quarterly" -> this.frequency = Frequency.Q;
            case "monthly" -> this.frequency = Frequency.M;
            case "bi-weekly" -> this.frequency = Frequency.B;
            default ->
                    throw new InvalidFrequencyException("Bill frequency must be Yearly, Quarterly, Monthly, or Bi-Weekly.");
        }
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) throws InvalidDueDateException {
        switch (this.frequency) {
            case Y -> {
                boolean isValidated = true;
                if (due.contains("-")) {
                    String[] dueSplit = due.split("-");
                    if (dueSplit.length == 2) {
                        try {
                            int month = Integer.parseInt(dueSplit[0]);
                            int day = Integer.parseInt(dueSplit[1]);
                            if (month > 0 && month < 13) {
                                switch (month) {
                                    case 4, 6, 9, 11 -> {
                                        if (day > 0 && day < 31) {
                                            this.due = due;
                                        } else {
                                            isValidated = false;
                                        }
                                    }
                                    case 2 -> {
                                        if (day > 0 && day < 29) {
                                            this.due = due;
                                        } else {
                                            isValidated = false;
                                        }
                                    }
                                    default -> {
                                        if (day > 0 && day < 32) {
                                            this.due = due;
                                        } else {
                                            isValidated = false;
                                        }
                                    }
                                }
                            } else {
                                isValidated = false;
                            }
                        } catch (NumberFormatException e) {
                            isValidated = false;
                        }
                    } else {
                        isValidated = false;
                    }
                } else {
                    isValidated = false;
                }

                if (!isValidated) {
                    throw new InvalidDueDateException("Due Date must be in mm-dd format for Yearly bills.");
                }
            }
            case M -> {
                boolean isValidated = true;
                try {
                    int day = Integer.parseInt(due);
                    if (day > 0 && day < 32) {
                        this.due = due;
                    } else {
                        isValidated = false;
                    }
                } catch (NumberFormatException e) {
                    isValidated = false;
                }

                if (!isValidated) {
                    throw new InvalidDueDateException("Due Date must be a valid day of the month for Monthly bills.");
                }
            }
            case B -> this.due = "Bi-Weekly";
            case Q -> this.due = "Quarterly";
        }
    }

    public boolean isPaidInInstallments() {
        return isPaidInInstallments;
    }

    public void setPaidInInstallments(boolean paidInInstallments) {
        isPaidInInstallments = paidInInstallments;
    }

    public double getPaidSoFar() {
        return paidSoFar;
    }

    public void setPaidSoFar(double paidSoFar) {
        this.paidSoFar = paidSoFar;
    }

    public boolean isPaidFromBudget() {
        return isPaidFromBudget;
    }

    public void setPaidFromBudget(boolean paidFromBudget) {
        isPaidFromBudget = paidFromBudget;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(Bill o) {
        //Ordering of bills is: Bills paid in installments at the top, then
        //                      By Frequency
        //                      By Due Date
        //                      By Description
        if (this.isPaidInInstallments && o.isPaidInInstallments) {
            return this.description.compareTo(o.description);
        } else if (this.isPaidInInstallments) {
            return -1;
        } else if (o.isPaidInInstallments) {
            return 1;
        }

        if (!(this.frequency.equals(o.frequency))) {
            switch (this.frequency) {
                case Y -> {
                    return -1;
                }
                case Q -> {
                    if (o.frequency.equals(Frequency.Y)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
                case M -> {
                    if (o.frequency.equals(Frequency.Y) || o.frequency.equals(Frequency.Q)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
                default -> {
                    return 1;
                }
            }
        }

        if (!(this.due.equals(o.due))) {
            return this.due.compareTo(o.due);
        }

        return this.description.compareTo(o.description);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "_id='" + _id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", frequency=" + frequency +
                ", due='" + due + '\'' +
                ", isPaidInInstallments=" + isPaidInInstallments +
                ", paidSoFar=" + paidSoFar +
                ", isPaidFromBudget=" + isPaidFromBudget +
                ", comment=" + comment +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bill) {
            return this.compareTo((Bill) obj) == 0;
        } else {
            return false;
        }
    }

    private enum Frequency {
        B("Bi-weekly"),
        M("Monthly"),
        Q("Quarterly"),
        Y("Yearly");

        private final String frequencyString;

        Frequency(String freqString) {
            this.frequencyString = freqString;
        }

        @Override
        public String toString() {
            return frequencyString;
        }
    }
}
