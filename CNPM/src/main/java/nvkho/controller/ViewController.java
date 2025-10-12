package nvkho.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/sanpham")
    public String sanPham() {
        return "sanpham";
    }
    
    @GetMapping("/nhapkho")
    public String nhapKho() {
        return "nhapkho";
    }
    
    @GetMapping("/xuatkho")
    public String xuatKho() {
        return "xuatkho";
    }
    
    @GetMapping("/tonkho")
    public String tonKho() {
        return "tonkho";
    }
    
    @GetMapping("/kiemke")
    public String kiemKe() {
        return "kiemke";
    }
    
    @GetMapping("/doitra")
    public String doiTra() {
        return "doitra";
    }
    
    @GetMapping("/baocao")
    public String baoCao() {
        return "baocao";
    }
}