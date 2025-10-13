// /js/suppliers.js

const SUPPLIER_API = '/api/v1/admin/suppliers';
const supplierModal = new bootstrap.Modal(document.getElementById('supplierModal'));
let isEditMode = false;

// Hàm tải và hiển thị danh sách nhà cung cấp
function loadSuppliers(name = '') {
    const apiUrl = name ? `${SUPPLIER_API}/search?name=${encodeURIComponent(name)}` : SUPPLIER_API;

    $.ajax({
        url: apiUrl,
        type: 'GET',

        success: function(suppliers) {
            const $tableBody = $('#supplierTableBody');
            $tableBody.empty();

            if (suppliers.length === 0) {
                $tableBody.html('<tr><td colspan="8" class="text-center">Không tìm thấy nhà cung cấp nào.</td></tr>');
                return;
            }

            suppliers.forEach(supplier => {
                const row = `
                    <tr>
                        <td>${supplier.supplierId || 'N/A'}</td>
                        <td>${supplier.supplierName || ''}</td>
                        <td>${supplier.address || ''}</td>
                        <td>${supplier.phoneNumber || ''}</td>
                        <td>${supplier.email || ''}</td>
                        <td>${supplier.description || ''}</td>
                        <td><span class="badge bg-${supplier.status === 'Active' ? 'success' : 'secondary'}">${supplier.status}</span></td>
                        <td class="action-buttons">
                            <button class="btn btn-sm btn-info edit-btn" data-id="${supplier.supplierId}">Sửa</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${supplier.supplierId}">Xóa</button>
                        </td>
                    </tr>
                `;
                $tableBody.append(row);
            });

            // Gán event listener cho các nút mới thêm
            $('.edit-btn').off('click').on('click', handleEditClick);
            $('.delete-btn').off('click').on('click', handleDeleteClick);
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || 'Lỗi khi tải danh sách nhà cung cấp.';
            showAlert('supplierAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý submit form Thêm/Sửa
$('#supplierForm').submit(function(e) {
    e.preventDefault();
    const supplierId = $('#supplierId').val();
    const supplierName = $('#supplierName').val().trim();
    const address = $('#address').val().trim();
    const phoneNumber = $('#phoneNumber').val().trim();
    const email = $('#email').val().trim();
    const description = $('#supplierDescription').val().trim();
    const status = $('#status').val();
    const $saveBtn = $('#saveSupplierBtn');

    // Validation cơ bản
    if (!supplierName || !address || !phoneNumber || !email || !status) {
        showAlert('modalAlert', 'Vui lòng điền đầy đủ thông tin bắt buộc.', 'danger');
        return;
    }

    const supplierData = {
        supplierName,
        address,
        phoneNumber,
        email,
        description,
        status
    };

    const method = isEditMode ? 'PUT' : 'POST';
    const url = isEditMode ? `${SUPPLIER_API}/${supplierId}` : SUPPLIER_API;

    toggleSpinner($saveBtn, true);

    $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(supplierData),

        success: function() {
            showAlert('supplierAlert', `Nhà cung cấp đã được ${isEditMode ? 'cập nhật' : 'thêm mới'} thành công.`, 'success');
            supplierModal.hide();
            loadSuppliers();
            $('#supplierForm')[0].reset(); // Reset form
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || `Lỗi khi ${isEditMode ? 'cập nhật' : 'thêm mới'} nhà cung cấp.`;
            showAlert('modalAlert', errorMessage, 'danger');
        },
        complete: function() {
            toggleSpinner($saveBtn, false);
        }
    });
});

// Xử lý click nút Thêm Mới
$('#addNewSupplierBtn').click(function() {
    isEditMode = false;
    $('#supplierModalLabel').text('Thêm Nhà Cung Cấp Mới');
    $('#supplierForm')[0].reset();
    $('#supplierId').val('');
    $('#modalAlert').addClass('d-none').empty();
    supplierModal.show();
});

// Xử lý click nút Sửa
function handleEditClick() {
    isEditMode = true;
    const supplierId = $(this).data('id');
    $('#supplierModalLabel').text('Cập Nhật Nhà Cung Cấp');
    $('#modalAlert').addClass('d-none').empty();

    $.ajax({
        url: `${SUPPLIER_API}/${supplierId}`,
        type: 'GET',

        success: function(supplier) {
            $('#supplierId').val(supplier.supplierId);
            $('#supplierName').val(supplier.supplierName);
            $('#address').val(supplier.address);
            $('#phoneNumber').val(supplier.phoneNumber);
            $('#email').val(supplier.email);
            $('#supplierDescription').val(supplier.description);
            $('#status').val(supplier.status);
            supplierModal.show();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || 'Không thể tải chi tiết nhà cung cấp.';
            showAlert('supplierAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý click nút Xóa
function handleDeleteClick() {
    const supplierId = $(this).data('id');
    if (confirm('Bạn có chắc chắn muốn xóa nhà cung cấp này?')) {
        $.ajax({
            url: `${SUPPLIER_API}/${supplierId}`,
            type: 'DELETE',

            success: function() {
                showAlert('supplierAlert', 'Nhà cung cấp đã được xóa thành công.', 'success');
                loadSuppliers(); // Tải lại danh sách
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || 'Lỗi khi xóa nhà cung cấp.';
                showAlert('supplierAlert', errorMessage, 'danger');
            }
        });
    }
}

// Xử lý form tìm kiếm
$('#supplierSearchForm').submit(function(e) {
    e.preventDefault();
    const searchName = $('#searchName').val().trim();
    loadSuppliers(searchName || ''); // Nếu rỗng, tải tất cả
});

// Khởi tạo
$(document).ready(function() {
    loadSuppliers();
});