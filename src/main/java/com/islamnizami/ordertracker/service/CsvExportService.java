package com.islamnizami.ordertracker.service;


import com.islamnizami.ordertracker.model.dto.OrderResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvExportService {

    public ByteArrayInputStream exportOrdersToCsv(List<OrderResponse> orders) {
        final CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("Order ID", "Total Amount", "Status", "Created At", "Updated At")
                .build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            for (OrderResponse order : orders) {
                List<String> data = Arrays.asList(
                        String.valueOf(order.getId()),
                        String.valueOf(order.getTotalAmount()),
                        order.getStatus(),
                        order.getCreatedAt().toString(),
                        order.getUpdatedAt().toString()
                );
                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to CSV file: " + e.getMessage());
        }
    }
}