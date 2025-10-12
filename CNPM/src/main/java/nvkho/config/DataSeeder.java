package nvkho.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nvkho.entity.DanhMuc;
import nvkho.repository.DanhMucRepository;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final DanhMucRepository danhMucRepository;
    
    @Override
    public void run(String... args) {
        if (danhMucRepository.count() == 0) {
            danhMucRepository.saveAll(Arrays.asList(
                new DanhMuc("DM001", "Áo"),
                new DanhMuc("DM002", "Quần")
            ));
        }
    }
}