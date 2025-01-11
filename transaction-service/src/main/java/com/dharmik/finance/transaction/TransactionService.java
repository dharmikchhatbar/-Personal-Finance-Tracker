package com.dharmik.finance.transaction;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Create a new transaction
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // Update an existing transaction
    public Transaction updateTransaction(Long id, Transaction transaction) {
        Optional<Transaction> existingTransaction = transactionRepository.findById(id);
        if (existingTransaction.isPresent()) {
            Transaction updatedTransaction = existingTransaction.get();
            updatedTransaction.setDescription(transaction.getDescription());
            updatedTransaction.setAmount(transaction.getAmount());
            updatedTransaction.setDate(transaction.getDate());
            updatedTransaction.setType(transaction.getType());
            return transactionRepository.save(updatedTransaction);
        } else {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
    }

    // Delete a transaction by ID
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        // Fetch all transactions and filter by date range
        return transactionRepository.findAll().stream()
                .filter(transaction -> {
                    LocalDate date = transaction.getDate();
                    return (startDate == null || !date.isBefore(startDate)) &&
                            (endDate == null || !date.isAfter(endDate));
                })
                .collect(Collectors.toList());
    }
}