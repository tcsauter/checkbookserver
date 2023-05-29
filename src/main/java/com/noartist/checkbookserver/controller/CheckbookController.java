package com.noartist.checkbookserver.controller;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.noartist.checkbookserver.entity.Expense;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CheckbookController {

    //todo: Figure out an id strategy; the one provided by mongoDb is not going to work

    String connectUri = "mongodb+srv://traviscsauter:yxQzYrphFAPDxbLT@cluster0.fe4mczu.mongodb.net/?retryWrites=true&w=majority";

    @GetMapping("/expenses")
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
}
