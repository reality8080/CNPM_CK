package com.example.storemanager.controller;

import com.example.storemanager.entity.Invoice;
import com.example.storemanager.repository.InvoiceRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;


@RestController
@RequestMapping("/api/manager/reports")
public class ReportController {
    private final InvoiceRepository invoiceRepo;

    public ReportController(InvoiceRepository invoiceRepo) {
        this.invoiceRepo = invoiceRepo;
    }

    @GetMapping("/revenue")
    public List<Map<String, Object>> getRevenue(@RequestParam String from,
                                                @RequestParam String to) {
        LocalDate start = LocalDate.parse(from);
        LocalDate end = LocalDate.parse(to);

        List<Invoice> invoices = invoiceRepo.findAll();

        Map<LocalDate, Double> revenueByDate = invoices.stream()
            .filter(inv -> {
                LocalDate date = inv.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate();
                return !date.isBefore(start) && !date.isAfter(end);
            })
            .collect(Collectors.groupingBy(
                inv -> inv.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                Collectors.summingDouble(inv -> inv.getTotalAmount().doubleValue())
            ));

        return revenueByDate.entrySet().stream()
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("date", entry.getKey());
                item.put("revenue", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/revenue.csv")
    public ResponseEntity<byte[]> exportRevenueCsv(@RequestParam String from,
                                                   @RequestParam String to) {
        List<Map<String, Object>> rows = getRevenue(from, to);
        StringBuilder sb = new StringBuilder();
        sb.append("date,revenue\n");
        for (Map<String, Object> r : rows) {
            sb.append(r.get("date")).append(',').append(r.get("revenue")).append('\n');
        }
        byte[] bytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenues.csv");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
