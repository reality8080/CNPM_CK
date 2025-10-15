async function loadPromotions() {
  const listEl = document.getElementById("promoList");
  if (!listEl) return;
  try {
    const res = await fetch(`${API_BASE}/promotions`, { headers: authHeaders() });
    if (res.status === 401) {
      alert("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || "Không tải được khuyến mãi");
    }
    const data = await res.json();
    if (!Array.isArray(data) || data.length === 0) {
      listEl.innerHTML = `<div class="text-muted">Chưa có khuyến mãi nào.</div>`;
      return;
    }
    listEl.innerHTML = data.map(p => `
      <div class="border rounded p-3 mb-3">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h6 class="mb-1">${p.name} ${p.active ? '<span class="badge bg-success ms-2">Active</span>' : ''}</h6>
            <div class="text-muted small">${p.type} - Giá trị: ${p.value}</div>
          </div>
          ${p.active ? '' : `<button class="btn btn-sm btn-outline-success" onclick="activatePromo('${p.id}')">Kích hoạt</button>`}
        </div>
      </div>`).join("");
  } catch (err) {
    console.error(err);
    listEl.innerHTML = `<div class="text-danger">${err.message || 'Lỗi tải khuyến mãi'}</div>`;
  }
}

window.activatePromo = async id => {
  await fetch(`${API_BASE}/promotions/${id}/activate`, { method: "POST", headers: authHeaders() });
  loadPromotions();
};

const promoModalEl = document.getElementById("promoModal");
const btnNewPromo = document.getElementById("btnNewPromo");
const btnSavePromo = document.getElementById("btnSavePromo");

btnNewPromo.onclick = () => {
  document.getElementById("promoId").value = "";
  document.getElementById("promoName").value = "";
  document.getElementById("promoType").value = "PERCENT";
  document.getElementById("promoValue").value = 0;
  document.getElementById("promoStart").value = "";
  document.getElementById("promoEnd").value = "";
  document.getElementById("promoMin").value = 0;
  document.getElementById("promoMax").value = 1;
  const modal = bootstrap.Modal.getOrCreateInstance(promoModalEl);
  modal.show();
};

btnSavePromo.onclick = async () => {
  try {
    const name = document.getElementById("promoName").value.trim();
    const type = document.getElementById("promoType").value;
    const valueStr = document.getElementById("promoValue").value;
    const startStr = document.getElementById("promoStart").value;
    const endStr = document.getElementById("promoEnd").value;
    const minStr = document.getElementById("promoMin").value;
    const maxStr = document.getElementById("promoMax").value;

    if (!name) return alert("Tên khuyến mãi không được để trống");

    const body = {
      name,
      type,
      value: valueStr ? parseFloat(valueStr) : null,
      startDate: startStr ? new Date(startStr).toISOString() : null,
      endDate: endStr ? new Date(endStr).toISOString() : null,
      minOrderValue: minStr ? parseFloat(minStr) : null,
      maxUsage: maxStr ? parseInt(maxStr, 10) : null
    };

    const res = await fetch(`${API_BASE}/promotions`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...authHeaders() },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const msg = await res.text();
      return alert(msg || "Không tạo được khuyến mãi");
    }
    bootstrap.Modal.getInstance(promoModalEl).hide();
    loadPromotions();
  } catch (err) {
    console.error(err);
    alert("Lỗi đầu vào khuyến mãi. Vui lòng kiểm tra lại.");
  }
};

// Load ngay khi mở trang Promotions
loadPromotions();

// Tự động reload khi quay lại tab Promotions
if (!window.__promotions_listener_bound__) {
  window.__promotions_listener_bound__ = true;
  window.addEventListener('page:loaded', (e) => {
    if (e.detail && e.detail.page === 'promotions') {
      loadPromotions();
    }
  });
}
