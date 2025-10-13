// /js/app-discounts.js

const DISCOUNT_API = '/api/v1/admin/app-discounts';
const discountModal = new bootstrap.Modal(document.getElementById('discountModal'));
let isEditMode = false;

$(document).ready(function() {
    loadDiscounts(); // Chỉ gọi loadDiscounts(), để checkAuthentication() trong custom.js xử lý
});

// Hàm tải và hiển thị danh sách giảm giá
function loadDiscounts(shopId = '') {
    const apiUrl = shopId ? `${DISCOUNT_API}/shop/${encodeURIComponent(shopId)}` : DISCOUNT_API;

    $.ajax({
        url: apiUrl,
        type: 'GET',
        success: function(discounts) {
            const $tableBody = $('#discountTableBody');
            $tableBody.empty();

            if (discounts.length === 0) {
                $tableBody.html('<tr><td colspan="7" class="text-center">Không tìm thấy giảm giá nào.</td></tr>');
                return;
            }

            discounts.forEach(discount => {
                const startDate = new Date(discount.startDate).toLocaleString('vi-VN');
                const endDate = new Date(discount.endDate).toLocaleString('vi-VN');
                const row = `
                    <tr>
                        <td>${discount.discountId || 'N/A'}</td>
                        <td>${discount.shopId || ''}</td>
                        <td>${discount.discountPercentage ? discount.discountPercentage.toFixed(2) + '%' : 'N/A'}</td>
                        <td>${startDate}</td>
                        <td>${endDate}</td>
                        <td><span class="badge bg-${discount.status === 'Active' ? 'success' : 'secondary'}">${discount.status}</span></td>
                        <td class="action-buttons">
                            <button class="btn btn-sm btn-info edit-btn" data-id="${discount.discountId}">Sửa</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${discount.discountId}">Xóa</button>
                        </td>
                    </tr>
                `;
                $tableBody.append(row);
            });

            // Gán event listener cho các nút mới thêm (sử dụng delegation)
            $('.edit-btn').off('click').on('click', handleEditClick);
            $('.delete-btn').off('click').on('click', handleDeleteClick);
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || 'Lỗi khi tải danh sách giảm giá.';
            showAlert('discountAlert', errorMessage, 'danger');
        }
    });
}

// (Giữ nguyên các hàm khác: submit, handleEditClick, handleDeleteClick, discountSearchForm)

// Xử lý submit form Thêm/Sửa
$('#discountForm').submit(function(e) {
    e.preventDefault();
    const discountId = $('#discountId').val();
    const shopId = $('#shopId').val().trim();
    const discountPercentage = parseFloat($('#discountPercentage').val());
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    const status = $('#status').val();
    const $saveBtn = $('#saveDiscountBtn');

    // Validation cơ bản
    if (!shopId || isNaN(discountPercentage) || !startDate || !endDate || !status) {
        showAlert('modalAlert', 'Vui lòng điền đầy đủ thông tin bắt buộc.', 'danger');
        return;
    }

    const discountData = {
        shopId,
        discountPercentage,
        startDate: new Date(startDate).toISOString(),
        endDate: new Date(endDate).toISOString(),
        status
    };

    const method = isEditMode ? 'PUT' : 'POST';
    const url = isEditMode ? `${DISCOUNT_API}/${discountId}` : DISCOUNT_API;

    toggleSpinner($saveBtn, true);

    $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(discountData),

        success: function() {
            showAlert('discountAlert', `Giảm giá đã được ${isEditMode ? 'cập nhật' : 'thêm mới'} thành công.`, 'success');
            discountModal.hide();
            loadDiscounts();
            $('#discountForm')[0].reset();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || `Lỗi khi ${isEditMode ? 'cập nhật' : 'thêm mới'} giảm giá.`;
            showAlert('modalAlert', errorMessage, 'danger');
        },
        complete: function() {
            toggleSpinner($saveBtn, false);
        }
    });
});

// Xử lý click nút Thêm Mới
$('#addNewDiscountBtn').click(function() {
    isEditMode = false;
    $('#discountModalLabel').text('Thêm Giảm Giá Mới');
    $('#discountForm')[0].reset();
    $('#discountId').val('');
    $('#modalAlert').addClass('d-none').empty();
    discountModal.show();
});

// Xử lý click nút Sửa
function handleEditClick() {
    isEditMode = true;
    const discountId = $(this).data('id');
    $('#discountModalLabel').text('Cập Nhật Giảm Giá');
    $('#modalAlert').addClass('d-none').empty();

    $.ajax({
        url: `${DISCOUNT_API}/${discountId}`,
        type: 'GET',

        success: function(discount) {
            $('#discountId').val(discount.discountId);
            $('#shopId').val(discount.shopId);
            $('#discountPercentage').val(discount.discountPercentage);
            $('#startDate').val(new Date(discount.startDate).toISOString().slice(0, 16)); // Format for datetime-local
            $('#endDate').val(new Date(discount.endDate).toISOString().slice(0, 16));
            $('#status').val(discount.status);
            discountModal.show();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || 'Không thể tải chi tiết giảm giá.';
            showAlert('discountAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý click nút Xóa
function handleDeleteClick() {
    const discountId = $(this).data('id');
    if (confirm('Bạn có chắc chắn muốn xóa giảm giá này?')) {
        $.ajax({
            url: `${DISCOUNT_API}/${discountId}`,
            type: 'DELETE',

            success: function() {
                showAlert('discountAlert', 'Giảm giá đã được xóa thành công.', 'success');
                loadDiscounts(); // Tải lại danh sách
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || 'Lỗi khi xóa giảm giá.';
                showAlert('discountAlert', errorMessage, 'danger');
            }
        });
    }
}

// Xử lý form tìm kiếm
$('#discountSearchForm').submit(function(e) {
    e.preventDefault();
    const searchShopId = $('#searchShopId').val().trim();
    loadDiscounts(searchShopId || ''); // Nếu rỗng, tải tất cả
});

// Khởi tạo
$(document).ready(function() {
    // Tải danh sách khi trang load
    loadDiscounts();
});
