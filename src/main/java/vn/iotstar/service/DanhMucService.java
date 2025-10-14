package vn.iotstar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.DanhMuc;
import vn.iotstar.repository.DanhMucRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DanhMucService {
    @Autowired
    private DanhMucRepository repository;

    public List<DanhMuc> getAllCategories() {
        return repository.findAll();
    }

    public Optional<DanhMuc> getCategoryById(String maDM) {
        return repository.findById(maDM);
    }

    public DanhMuc saveCategory(DanhMuc danhMuc) {
        return repository.save(danhMuc);
    }

    public void deleteCategory(String maDM) {
        repository.deleteById(maDM);
    }
}
