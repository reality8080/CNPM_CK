package com.example.storemanager.repository;

import com.example.storemanager.entity.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository cho hóa đơn bán hàng.
 */
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
}
