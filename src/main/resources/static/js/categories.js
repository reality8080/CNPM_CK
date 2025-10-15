// categories.js

// API_BASE lấy từ index.html

// Tránh đăng ký lặp lại khi chuyển tab nhiều lần
if (!window.__categories_once__) {
  window.__categories_once__ = true;

  // Lắng nghe sự kiện quay lại trang categories 1 lần duy nhất
  window.addEventListener('page:loaded', (e) => {
    if (e.detail && e.detail.page === 'categories') {
      initCategoryPage();
      loadCategories();
    }
  });
}

// Hàm header kèm token nếu đăng nhập
function authHeaders() {
  return { "Authorization": "Bearer " + localStorage.getItem("token") };
}

// Hàm load danh mục vào table
let __cat_loading = false;
async function loadCategories() {
  if (__cat_loading) return; // debounce đơn giản
  __cat_loading = true;
  const categoryTableBody = document.getElementById("categoryTableBody");
  if (!categoryTableBody) return;

  try {
    const res = await fetch(`${API_BASE}/categories`, { headers: authHeaders() });
    if (res.status === 401) {
      alert("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || "Không tải được danh mục");
    }
    const data = await res.json();

    if (!Array.isArray(data) || data.length === 0) {
      categoryTableBody.innerHTML = `<tr><td colspan="3" class="text-muted">Chưa có danh mục</td></tr>`;
      return;
    }

    categoryTableBody.innerHTML = data.map(c => `
      <tr>
        <td>${c.name}</td>
        <td>${c.description || ""}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary me-1" onclick="editCategory('${c.id}')">Sửa</button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteCategory('${c.id}')">Xóa</button>
        </td>
      </tr>
    `).join("");
  } catch (err) {
    console.error("Lỗi load danh mục:", err);
    categoryTableBody.innerHTML = `<tr><td colspan="3" class="text-danger">${err.message || 'Lỗi tải danh mục'}</td></tr>`;
  }
  __cat_loading = false;
}

// Gắn sự kiện modal sau khi DOM được load
function initCategoryPage() {
  const btnAddCategory = document.getElementById("btnAddCategory");
  const btnSaveCategory = document.getElementById("btnSaveCategory");
  const categoryModalEl = document.getElementById("categoryModal");
  const categoryId = document.getElementById("categoryId");
  const categoryName = document.getElementById("categoryName");
  const categoryDesc = document.getElementById("categoryDesc");

  if (!btnSaveCategory) return;

  const showCategoryModal = () => {
    if (!categoryModalEl) return;
    categoryId.value = "";
    categoryName.value = "";
    categoryDesc.value = "";
    const modal = window.bootstrap && bootstrap.Modal ? bootstrap.Modal.getOrCreateInstance(categoryModalEl) : null;
    if (modal) modal.show();
  };

  // mở modal thêm mới
  if (btnAddCategory) {
    btnAddCategory.onclick = showCategoryModal;
  }
  // fallback: uỷ quyền sự kiện nếu nút được render muộn (đăng ký 1 lần toàn cục)
  if (!window.__categories_delegate_bound__) {
    window.__categories_delegate_bound__ = true;
    document.addEventListener("click", (e) => {
      if (e.target && e.target.id === "btnAddCategory") {
        showCategoryModal();
      }
    });
  }

  // lưu danh mục
  btnSaveCategory.onclick = async () => {
    const id = categoryId.value;
    const name = categoryName.value.trim();
    const desc = categoryDesc.value.trim();

    if (!name) return alert("Tên danh mục không được để trống");

    const body = JSON.stringify({ name, description: desc });
    const method = id ? "PUT" : "POST";
    const url = id ? `${API_BASE}/categories/${id}` : `${API_BASE}/categories`;

    try {
      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json", ...authHeaders() },
        body
      });
      if (!res.ok) {
        const msg = await res.text();
        return alert(msg || "Có lỗi khi lưu danh mục!");
      }
      const modal = bootstrap.Modal.getInstance(categoryModalEl) || bootstrap.Modal.getOrCreateInstance(categoryModalEl);
      modal.hide();
      loadCategories();
    } catch (err) {
      console.error("Lỗi lưu danh mục:", err);
    }
  };
}

// sửa danh mục
window.editCategory = async (id) => {
  const categoryModalEl = document.getElementById("categoryModal");
  const categoryId = document.getElementById("categoryId");
  const categoryName = document.getElementById("categoryName");
  const categoryDesc = document.getElementById("categoryDesc");

  try {
    const res = await fetch(`${API_BASE}/categories/${id}`, { headers: authHeaders() });
    const c = await res.json();
    categoryId.value = c.id;
    categoryName.value = c.name;
    categoryDesc.value = c.description || "";
    const modal = bootstrap.Modal.getOrCreateInstance(categoryModalEl);
    modal.show();
  } catch (err) {
    console.error("Lỗi load danh mục để sửa:", err);
  }
};

// xóa danh mục
window.deleteCategory = async (id) => {
  if (!confirm("Bạn có chắc muốn xóa danh mục này?")) return;
  try {
    await fetch(`${API_BASE}/categories/${id}`, { method: "DELETE", headers: authHeaders() });
    loadCategories();
  } catch (err) {
    console.error("Lỗi xóa danh mục:", err);
  }
};

// Khởi tạo ngay khi script được chèn
initCategoryPage();
loadCategories();
