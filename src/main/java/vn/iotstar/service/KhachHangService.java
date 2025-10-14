package vn.iotstar.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.KhachHang;
import vn.iotstar.repository.KhachHangRepository;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository repository;

    public List<KhachHang> getAllCustomers() {
        return repository.findAll();
    }

    public List<KhachHang> searchCustomers(String keyword) {
        return repository.findByHoTenContainingIgnoreCase(keyword);
    }

    public Optional<KhachHang> getCustomerById(String maKH) {
        return repository.findById(maKH);
    }

    public KhachHang saveCustomer(KhachHang khachHang) {
        return repository.save(khachHang);
    }

    public void deleteCustomer(String maKH) {
        repository.deleteById(maKH);
    }
}