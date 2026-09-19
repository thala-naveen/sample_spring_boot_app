package com.practice.samplespringboot.web;

import com.practice.samplespringboot.entity.Transaction;
import com.practice.samplespringboot.kafka.TransactionProducer;
import com.practice.samplespringboot.service.CsvIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Data Ingestion API", description = "Endpoints for batch CSV file ingestion and streaming transaction ingestion")
public class IngestionController {

    private final CsvIngestionService csvIngestionService;
    private final TransactionProducer transactionProducer;

    public IngestionController(CsvIngestionService csvIngestionService, TransactionProducer transactionProducer) {
        this.csvIngestionService = csvIngestionService;
        this.transactionProducer = transactionProducer;
    }

    @PostMapping(value = "/ingest/customers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ingest Customers CSV", description = "Upload and import customers.csv dataset into PostgreSQL")
    public ResponseEntity<Map<String, Object>> ingestCustomers(@RequestParam("file") MultipartFile file) {
        try {
            int count = csvIngestionService.ingestCustomers(file);
            return ResponseEntity.ok(Map.of("message", "Customers ingested successfully", "count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/ingest/accounts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ingest Accounts CSV", description = "Upload and import accounts.csv dataset into PostgreSQL")
    public ResponseEntity<Map<String, Object>> ingestAccounts(@RequestParam("file") MultipartFile file) {
        try {
            int count = csvIngestionService.ingestAccounts(file);
            return ResponseEntity.ok(Map.of("message", "Accounts ingested successfully", "count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/ingest/transactions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ingest Transactions CSV", description = "Upload transactions CSV and stream records to Kafka real-time detection engine")
    public ResponseEntity<Map<String, Object>> ingestTransactions(@RequestParam("file") MultipartFile file) {
        try {
            int count = csvIngestionService.ingestTransactions(file);
            return ResponseEntity.ok(Map.of("message", "Transactions streamed to Kafka pipeline successfully", "count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/transactions")
    @Operation(summary = "Post Single Streaming Transaction", description = "Ingest a single transaction record into Kafka pipeline for real-time evaluation")
    public ResponseEntity<Map<String, Object>> postTransaction(@RequestBody Transaction transaction) {
        if (transaction.getTransactionId() == null || transaction.getTransactionId().isEmpty()) {
            transaction.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
        }
        if (transaction.getNormalizedAmount() == null) {
            transaction.setNormalizedAmount(transaction.getAmount());
        }
        transactionProducer.sendTransaction(transaction);
        return ResponseEntity.ok(Map.of("message", "Transaction submitted to Kafka topic", "transactionId", transaction.getTransactionId()));
    }
}
