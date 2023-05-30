package com.noartist.checkbookserver.controller;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.noartist.checkbookserver.entity.Account;
import com.noartist.checkbookserver.entity.Expense;
import com.noartist.checkbookserver.exception.InvalidTypeException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CheckbookController {

    String connectUri = "mongodb+srv://traviscsauter:yxQzYrphFAPDxbLT@cluster0.fe4mczu.mongodb.net/?retryWrites=true&w=majority";

    @GetMapping("/get/expenses")
    public List<Expense> getExpenses(){
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            FindIterable<Document> cursor = expensesTable.find();

            List<Expense> results = new ArrayList<>();

            try(final MongoCursor<Document> cursorIterator = cursor.cursor()){
                while(cursorIterator.hasNext()){
                    Document doc = cursorIterator.next();
                    Expense expense = new Expense();
                    expense.set_id(doc.get("_id").toString());
                    expense.setAccountId(doc.get("accountId").toString());
                    expense.setAmount(Double.parseDouble(doc.get("amount").toString()));
                    expense.setDate(doc.get("date").toString());


                    results.add(expense);
                }
                return results;
            }
        }
    }

    @GetMapping("/get/accounts")
    public List<Account> getAccounts() {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountsTable = db.getCollection("accounts");

            FindIterable<Document> cursor = accountsTable.find();

            List<Account> results = new ArrayList<>();

            try(final MongoCursor<Document> cursorIterator = cursor.cursor()){
                while(cursorIterator.hasNext()){
                    Document doc = cursorIterator.next();
                    Account account = new Account();
                    account.set_id(doc.get("_id").toString());
                    account.setName(doc.get("name").toString());
                    account.setType(doc.get("type").toString());
                    if(doc.containsKey("lastFour")){
                        account.setLastFour(doc.get("lastFour").toString());
                    }

                    results.add(account);
                }
                return results;
            }catch(InvalidTypeException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("/add/expense")
    public List<Expense> addExpense(@RequestBody Expense expense){
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            Document newExpenseDoc = new Document("_id", expense.get_id())
                    .append("accountId", expense.getAccountId())
                    .append("amount", expense.getAmount())
                    .append("date", expense.getDate());

            try {
                expensesTable.insertOne(newExpenseDoc);
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getExpenses();
    }

    @PostMapping("/add/account")
    public List<Account> addAccount(@RequestBody Account account) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountsTable = db.getCollection("accounts");

            Document newAccountDoc = new Document("_id", account.get_id())
                    .append("name", account.getName())
                    .append("type", account.getType());

            if(account.getLastFour() != null){
                newAccountDoc.append("lastFour", account.getLastFour());
            }

            try {
                accountsTable.insertOne(newAccountDoc);
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getAccounts();
    }

    @PutMapping("/update/expense/{expenseId}")
    public List<Expense> updateExpense(@RequestBody Expense update, @PathVariable("expenseId") String expenseId) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            Bson query = Filters.eq("_id", expenseId);
            Document expenseDoc = new Document("accountId", update.getAccountId())
                    .append("amount", update.getAmount())
                    .append("date", update.getDate());

            try {
                expensesTable.replaceOne(query, expenseDoc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getExpenses();
    }

    @PutMapping("/update/account/{accountId}")
    public List<Account> updateAccount(@RequestBody Account update, @PathVariable("accountId") String accountId) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountsTable = db.getCollection("accounts");

            Bson query = Filters.eq("_id", accountId);
            Document accountDoc = new Document("name", update.getName())
                    .append("type", update.getType())
                    .append("lastFour", update.getLastFour());

            try {
                accountsTable.replaceOne(query, accountDoc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getAccounts();
    }

    @DeleteMapping("/delete/expense/{expenseId}")
    public List<Expense> deleteExpense(@PathVariable String expenseId) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            Bson query = Filters.eq("_id", expenseId);

            try {
                expensesTable.deleteOne(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getExpenses();
    }

    @DeleteMapping("/delete/account/{accountId}")
    public List<Account> deleteAccount(@PathVariable("accountId") String accountId) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountsTable = db.getCollection("accounts");

            Bson query = Filters.eq("_id", accountId);

            try {
                accountsTable.deleteOne(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getAccounts();
    }
}
