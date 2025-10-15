package com.example.storemanager.service;

import com.example.storemanager.entity.Category;
import com.example.storemanager.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public List<Category> findAll() { return repo.findAll(); }

    public Category create(Category cat) { return repo.save(cat); }

    public Category update(String id, Category newCat) {
        return repo.findById(id).map(c -> {
            c.setName(newCat.getName());
            c.setDescription(newCat.getDescription());
            return repo.save(c);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
    }

    public void delete(String id) { repo.deleteById(id); }
}
