package com.noartist.checkbookserver.controller;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.noartist.checkbookserver.entity.Account;
import com.noartist.checkbookserver.entity.Bill;
import com.noartist.checkbookserver.entity.BudgetPeriod;
import com.noartist.checkbookserver.entity.Expense;
import com.noartist.checkbookserver.exception.InvalidTypeException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin()
@RestController
public class CheckbookController {

    String connectUri = "mongodb+srv://traviscsauter:yxQzYrphFAPDxbLT@cluster0.fe4mczu.mongodb.net/?retryWrites=true&w=majority";

    @GetMapping("/get/expenses")
    public List<Expense> getExpenses(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate){
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            FindIterable<Document> cursor;

            if(startDate != null && endDate != null){
                Bson filter = Filters.and(Filters.gte("date", startDate), Filters.lte("date", endDate));
                cursor = expensesTable.find(filter);
            }else{
                cursor = expensesTable.find();
            }

            List<Expense> results = new ArrayList<>();

            try(final MongoCursor<Document> cursorIterator = cursor.cursor()){
                while(cursorIterator.hasNext()){
                    Document doc = cursorIterator.next();

                    results.add(new Expense(doc));
                }
                results.sort(null);
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

                    results.add(new Account(doc));
                }
                results.sort(null);
                return results;
            }catch(InvalidTypeException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @GetMapping("/get/budgetperiods")
    public List<BudgetPeriod> getBudgetPeriods(){
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountsTable = db.getCollection("budgetPeriods");

            FindIterable<Document> cursor = accountsTable.find();

            List<BudgetPeriod> results = new ArrayList<>();

            try(final MongoCursor<Document> cursorIterator = cursor.cursor()){
                while(cursorIterator.hasNext()){
                    Document doc = cursorIterator.next();

                    results.add(new BudgetPeriod(doc));
                }
                results.sort(null);
                return results;
            }
        }
    }

    @GetMapping("/get/bills")
    public List<Bill> getBills() {
        try (MongoClient client = MongoClients.create(connectUri)) {
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> billsTable = db.getCollection("bills");

            FindIterable<Document> cursor = billsTable.find();

            List<Bill> results = new ArrayList<>();

            try (final MongoCursor<Document> cursorIterator = cursor.cursor()) {
                while(cursorIterator.hasNext()) {
                    Document doc = cursorIterator.next();

                    results.add(new Bill(doc));
                }

                results.sort(null);
                return results;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @PostMapping("/add/expense")
    public List<Expense> addExpense(@RequestBody Expense expense, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate){
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            try {
                expensesTable.insertOne(expense.createDocumentFromExpense());
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getExpenses(startDate, endDate);
    }

    @PostMapping("/add/account")
    public List<Account> addAccount(@RequestBody Account account) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountsTable = db.getCollection("accounts");

            try {
                accountsTable.insertOne(account.toDocument());
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getAccounts();
    }

    @PostMapping("/add/budgetperiod")
    public List<BudgetPeriod> addBudgetPeriod(@RequestBody BudgetPeriod budgetPeriod) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> budgetPeriodsTable = db.getCollection("budgetPeriods");

            try {
                budgetPeriodsTable.insertOne(budgetPeriod.toDocument());
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getBudgetPeriods();
    }

    @PostMapping("/add/bill")
    public List<Bill> addBill(@RequestBody Bill bill) {
        try(MongoClient client = MongoClients.create(connectUri)) {
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> billsTable = db.getCollection("bills");

            try {
                billsTable.insertOne(bill.createdDocumentFromBill());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return getBills();
    }

    @PutMapping("/update/expense/{expenseId}")
    public List<Expense> updateExpense(@RequestBody Expense update,
                                       @PathVariable("expenseId") String expenseId,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate
    ) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            Bson query = Filters.eq("_id", expenseId);

            try {
                expensesTable.replaceOne(query, update.createDocumentFromExpense());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getExpenses(startDate, endDate);
    }

    @PutMapping("/update/account/{accountId}")
    public List<Account> updateAccount(@RequestBody Account update, @PathVariable("accountId") String accountId) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountsTable = db.getCollection("accounts");

            Bson query = Filters.eq("_id", accountId);

            try {
                accountsTable.replaceOne(query, update.toDocument());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getAccounts();
    }

    @PutMapping("/update/budgetperiod/{budgetPeriodId}")
    public List<BudgetPeriod> updateBudgetPeriod(@RequestBody BudgetPeriod update, @PathVariable("budgetPeriodId") String budgetPeriodId){
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> bpTable = db.getCollection("budgetPeriods");

            Bson query = Filters.eq("_id", budgetPeriodId);

            try {
                bpTable.replaceOne(query, update.toDocument());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getBudgetPeriods();
    }

    @PutMapping("/update/bill/{billId}")
    public List<Bill> updateBill(@RequestBody Bill bill, @PathVariable("billId") String billId) {
        try(MongoClient client = MongoClients.create(connectUri)) {
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> billsTable = db.getCollection("bills");

            Bson query = Filters.eq("_id", billId);

            try {
                billsTable.replaceOne(query, bill.createdDocumentFromBill());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return getBills();
    }

    @DeleteMapping("/delete/expense/{expenseId}")
    public List<Expense> deleteExpense(@PathVariable String expenseId,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate
    ) {
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
        return getExpenses(startDate, endDate);
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

    @DeleteMapping("/delete/budgetperiod/{budgetPeriodId}")
    public List<BudgetPeriod> deleteBudgetPeriod(@PathVariable("budgetPeriodId") String budgetPeriodId) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> bpsTable = db.getCollection("budgetPeriods");

            Bson query = Filters.eq("_id", budgetPeriodId);

            try {
                bpsTable.deleteOne(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getBudgetPeriods();
    }

    @DeleteMapping("/delete/bill/{billId}")
    public List<Bill> deleteBill(@PathVariable("billId") String billId) {
        try(MongoClient client = MongoClients.create(connectUri)) {
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> billsTable = db.getCollection("bills");

            Bson query = Filters.eq("_id", billId);

            try {
                billsTable.deleteOne(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getBills();
    }

    @PostMapping("/bulkadd/expenses")
    public List<Expense> bulkAddExpenses(@RequestBody Expense[] expenses) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            List<Document> newExpenseDocs = new ArrayList<>(expenses.length);

            for (Expense expense : expenses) {
                newExpenseDocs.add(expense.createDocumentFromExpense());
            }

            try {
                expensesTable.insertMany(newExpenseDocs);
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getExpenses(null, null);
    }

    @PostMapping("/bulkadd/accounts")
    public List<Account> bulkAddAccounts(@RequestBody Account[] accounts) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> accountTable = db.getCollection("accounts");

            List<Document> newAccountsDoc = new ArrayList<>(accounts.length);

            for (Account account : accounts) {
                newAccountsDoc.add(account.toDocument());
            }

            try {
                accountTable.insertMany(newAccountsDoc);
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getAccounts();
    }

    @PostMapping("/bulkadd/budgetperiods")
    public List<BudgetPeriod> bulkAddBudgetPeriods(@RequestBody BudgetPeriod[] bps){
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> budgetPeriodsTable = db.getCollection("budgetPeriods");

            List<Document> newBpsDoc = new ArrayList<>(bps.length);

            for (BudgetPeriod bp : bps) {
                Document newBpDoc = new Document("_id", bp.get_id())
                        .append("budgetStart", bp.getBudgetStart())
                        .append("budgetEnd", bp.getBudgetEnd())
                        .append("payDate", bp.getPayDate())
                        .append("startingAmt", bp.getStartingAmt());

                newBpsDoc.add(newBpDoc);
            }

            try {
                budgetPeriodsTable.insertMany(newBpsDoc);
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return getBudgetPeriods();
    }

    @PostMapping("/bulkadd/bills")
    public List<Bill> bulkAddBills(@RequestBody Bill[] bills) {
        try(MongoClient client = MongoClients.create(connectUri)) {
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> billsTable = db.getCollection("bills");

            List<Document> newBillsDoc = new ArrayList<>(bills.length);

            for(Bill bill : bills) {
                newBillsDoc.add(bill.createdDocumentFromBill());
            }

            try {
                billsTable.insertMany(newBillsDoc);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return getBills();
    }

    @DeleteMapping("/delete/expenses/{accountId}")
    public List<Expense> deleteExpensesByAccount(@PathVariable("accountId") String accountId,
                                            @RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate
    ) {
        try(MongoClient client = MongoClients.create(connectUri)){
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            Bson query = Filters.eq("accountId", accountId);

            try {
                expensesTable.deleteMany(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getExpenses(startDate, endDate);
    }

    @DeleteMapping("/clear/expenses")
    public boolean clearExpenses(){
        boolean expensesCleared = false;

        try(MongoClient client = MongoClients.create(connectUri)) {
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> expensesTable = db.getCollection("expenses");

            try {
                expensesCleared = expensesTable.deleteMany(Filters.empty()).wasAcknowledged();
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return expensesCleared;
    }

    @DeleteMapping("/clear/budgetperiods")
    public boolean clearBudgetPeriods(){
        boolean bpsCleared = false;

        try(MongoClient client = MongoClients.create(connectUri)) {
            MongoDatabase db = client.getDatabase("checkbook");

            MongoCollection<Document> bpTable = db.getCollection("budgetPeriods");

            try {
                bpsCleared = bpTable.deleteMany(Filters.empty()).wasAcknowledged();
            } catch (Exception e) {
                //todo: institute error handling
                e.printStackTrace();
            }
        }

        return bpsCleared;
    }
}
