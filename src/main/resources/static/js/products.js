function getProdModalEl() { return document.getElementById("prodModal"); }
const prodId = document.getElementById("prodId");
const prodName = document.getElementById("prodName");
const prodDesc = document.getElementById("prodDesc");
const prodPrice = document.getElementById("prodPrice");
const prodQty = document.getElementById("prodQty");
const prodCat = document.getElementById("prodCat");
const prodImg = document.getElementById("prodImg");
const imgPreview = document.getElementById("imgPreview");
const saveProdBtn = document.getElementById("saveProd");

let categoryMap = {};

async function ensureCategoryMap() {
  if (Object.keys(categoryMap).length > 0) return categoryMap;
  try {
    const res = await fetch(`${API_BASE}/categories`, { headers: authHeaders() });
    if (res.ok) {
      const data = await res.json();
      categoryMap = Object.fromEntries((data || []).map(c => [c.id, c.name]));
    }
  } catch {}
  return categoryMap;
}

async function loadProducts() {
  const prodList = document.getElementById("prodList");
  const prodStatus = document.getElementById("prodStatus");
  if (!prodList) return;
  try {
    if (prodStatus) prodStatus.innerHTML = "Đang tải sản phẩm...";
    await ensureCategoryMap();
    const res = await fetch(`${API_BASE}/products`, { headers: authHeaders() });
    if (res.status === 401) {
      alert("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || "Không tải được danh sách sản phẩm");
    }
    const data = await res.json();
    if (!Array.isArray(data) || data.length === 0) {
      prodList.innerHTML = `<div class="text-muted">Chưa có sản phẩm nào.</div>`;
      if (prodStatus) prodStatus.innerHTML = "";
      return;
    }
    prodList.innerHTML = data.map(p => `
    <div class="col-md-4">
      <div class="card p-3 mb-3">
        <img src="${p.images?.[0] || 'https://via.placeholder.com/200'}" class="img-fluid rounded mb-2" />
        <h6>${p.name} <span class="badge bg-success ms-1">${(p.price ?? 0).toLocaleString('vi-VN')}₫</span></h6>
        <div class="d-flex justify-content-between align-items-center">
          <div class="small text-muted">${p.description || ''}</div>
          <span class="badge text-bg-light border">${categoryMap[p.categoryId] || 'Chưa phân loại'}</span>
        </div>
        <div class="d-flex justify-content-between mt-2">
          <button class="btn btn-sm btn-outline-primary" onclick="editProd('${p.id}')">Sửa</button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteProd('${p.id}')">Xóa</button>
        </div>
      </div>
    </div>`).join("");
    if (prodStatus) prodStatus.innerHTML = `Hiển thị ${data.length} sản phẩm`;
  } catch (err) {
    console.error(err);
    prodList.innerHTML = `<div class="text-danger">${err.message || 'Lỗi tải sản phẩm'}</div>`;
    if (prodStatus) prodStatus.innerHTML = "Thử lại sau 2s...";
    setTimeout(() => { loadProducts().catch(() => {}); }, 2000);
  }
}

async function loadCategoriesForProduct() {
  const res = await fetch(`${API_BASE}/categories`, { headers: authHeaders() });
  const data = await res.json();
  prodCat.innerHTML = data.map(c => `<option value="${c.id}">${c.name}</option>`).join("");
}

document.getElementById("btnNewProd").onclick = async () => {
  prodId.value = "";
  prodName.value = "";
  prodDesc.value = "";
  prodQty.value = 0;
  imgPreview.innerHTML = "";
  prodImg.value = "";
  const el = getProdModalEl();
  if (el) { bootstrap.Modal.getOrCreateInstance(el).show(); }
  try {
    await loadCategoriesForProduct();
  } catch (e) {
    console.warn("Failed to load categories:", e);
    prodCat.innerHTML = "";
  }
};

// Fallback: uỷ quyền sự kiện phòng khi handler chưa gắn kịp
if (!window.__products_delegate_bound__) {
  window.__products_delegate_bound__ = true;
  document.addEventListener('click', (e) => {
    if (e.target && e.target.id === 'btnNewProd') {
      const el = getProdModalEl();
      if (el) bootstrap.Modal.getOrCreateInstance(el).show();
    }
  });
}

prodImg.onchange = e => {
  const files = Array.from(e.target.files);
  imgPreview.innerHTML = files.map(f => `<img src="${URL.createObjectURL(f)}" class="rounded" height="60">`).join("");
};

saveProdBtn.onclick = async () => {
  const id = prodId.value;
  const name = prodName.value.trim();
  const desc = prodDesc.value.trim();
  const price = Number(prodPrice.value || 0);
  const qty = +prodQty.value;
  const cat = prodCat.value;
  const files = prodImg.files;

  if (!name || !cat) return alert("Tên sản phẩm và danh mục không được để trống");

  const form = new FormData();
  form.append("product", new Blob([JSON.stringify({ name, description: desc, quantity: qty, categoryId: cat, price })], { type: "application/json" }));
  for (let f of files) form.append("images", f);

  const method = id ? "PUT" : "POST";
  const url = id ? `${API_BASE}/products/${id}` : `${API_BASE}/products`;

  const res = await fetch(url, { method, headers: authHeaders(), body: form });
  if (res.ok) {
    const el = getProdModalEl();
    if (el) (bootstrap.Modal.getInstance(el) || bootstrap.Modal.getOrCreateInstance(el)).hide();
    loadProducts();
  } else {
    const msg = await res.text();
    alert(msg || "Có lỗi xảy ra khi lưu sản phẩm!");
  }
};

window.editProd = async id => {
  const res = await fetch(`${API_BASE}/products/${id}`, { headers: authHeaders() });
  const p = await res.json();
  prodId.value = p.id;
  prodName.value = p.name;
  prodDesc.value = p.description;
  prodPrice.value = p.price || 0;
  prodQty.value = p.quantity;
  await loadCategoriesForProduct();
  prodCat.value = p.categoryId || "";
  imgPreview.innerHTML = (p.images || []).map(i => `<img src="${i}" class="rounded" height="60">`).join("");
  const el = getProdModalEl();
  if (el) bootstrap.Modal.getOrCreateInstance(el).show();
};

window.deleteProd = async id => {
  if (!confirm("Xóa sản phẩm này?")) return;
  await fetch(`${API_BASE}/products/${id}`, { method: "DELETE", headers: authHeaders() });
  loadProducts();
};

// Load khi lần đầu mở trang Products
loadProducts();

// Tự động reload khi quay lại tab Products
window.addEventListener('page:loaded', (e) => {
  if (e.detail && e.detail.page === 'products') {
    loadProducts();
  }
});
