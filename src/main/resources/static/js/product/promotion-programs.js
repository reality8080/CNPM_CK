// /js/promotion-programs.js

const PROMOTION_API = '/api/v1/admin/promotion-programs';
const promotionModal = new bootstrap.Modal(document.getElementById('promotionModal'));
let isEditMode = false;

// Hàm tải và hiển thị danh sách chương trình khuyến mãi
function loadPromotions(type = '') {
    const apiUrl = type ? `${PROMOTION_API}/type/${encodeURIComponent(type)}` : PROMOTION_API;

    $.ajax({
        url: apiUrl,
        type: 'GET',

        success: function(promotions) {
            const $tableBody = $('#promotionTableBody');
            $tableBody.empty();

            if (promotions.length === 0) {
                $tableBody.html('<tr><td colspan="10" class="text-center">Không tìm thấy chương trình nào.</td></tr>');
                return;
            }

            promotions.forEach(promotion => {
                const startDate = new Date(promotion.startDate).toLocaleString('vi-VN');
                const endDate = new Date(promotion.endDate).toLocaleString('vi-VN');
                const productsCount = promotion.products ? promotion.products.length : 0;
                const row = `
                    <tr>
                        <td>${promotion.promotionId || 'N/A'}</td>
                        <td>${promotion.promotionName || ''}</td>
                        <td>${promotion.promotionType || ''}</td>
                        <td>${promotion.discountValue ? promotion.discountValue.toFixed(2) + '%' : 'N/A'}</td>
                        <td>${startDate}</td>
                        <td>${endDate}</td>
                        <td>${promotion.applyCondition || ''}</td>
                        <td><span class="badge bg-${promotion.status === 'Active' ? 'success' : 'secondary'}">${promotion.status}</span></td>
                        <td>${productsCount}</td>
                        <td class="action-buttons">
                            <button class="btn btn-sm btn-info edit-btn" data-id="${promotion.promotionId}">Sửa</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${promotion.promotionId}">Xóa</button>
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
            const errorMessage = xhr.responseJSON?.message || 'Lỗi khi tải danh sách chương trình.';
            showAlert('promotionAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý submit form Thêm/Sửa
$('#promotionForm').submit(function(e) {
    e.preventDefault();
    const promotionId = $('#promotionId').val();
    const promotionName = $('#promotionName').val().trim();
    const promotionType = $('#promotionType').val();
    const discountValue = parseFloat($('#discountValue').val());
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    const applyCondition = $('#applyCondition').val().trim();
    const status = $('#status').val();
    const $saveBtn = $('#savePromotionBtn');

    // Validation cơ bản
    if (!promotionName || !promotionType || isNaN(discountValue) || !startDate || !endDate || !status) {
        showAlert('modalAlert', 'Vui lòng điền đầy đủ thông tin bắt buộc.', 'danger');
        return;
    }

    const promotionData = {
        promotionName,
        promotionType,
        discountValue,
        startDate: new Date(startDate).toISOString(),
        endDate: new Date(endDate).toISOString(),
        applyCondition,
        status
        // products sẽ được xử lý riêng nếu cần
    };

    const method = isEditMode ? 'PUT' : 'POST';
    const url = isEditMode ? `${PROMOTION_API}/${promotionId}` : PROMOTION_API;

    toggleSpinner($saveBtn, true);

    $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(promotionData),

        success: function() {
            showAlert('promotionAlert', `Chương trình đã được ${isEditMode ? 'cập nhật' : 'thêm mới'} thành công.`, 'success');
            promotionModal.hide();
            loadPromotions();
            $('#promotionForm')[0].reset();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || `Lỗi khi ${isEditMode ? 'cập nhật' : 'thêm mới'} chương trình.`;
            showAlert('modalAlert', errorMessage, 'danger');
        },
        complete: function() {
            toggleSpinner($saveBtn, false);
        }
    });
});

// Xử lý click nút Thêm Mới
$('#addNewPromotionBtn').click(function() {
    isEditMode = false;
    $('#promotionModalLabel').text('Thêm Chương Trình Khuyến Mãi Mới');
    $('#promotionForm')[0].reset();
    $('#promotionId').val('');
    $('#modalAlert').addClass('d-none').empty();
    promotionModal.show();
});

// Xử lý click nút Sửa
function handleEditClick() {
    isEditMode = true;
    const promotionId = $(this).data('id');
    $('#promotionModalLabel').text('Cập Nhật Chương Trình Khuyến Mãi');
    $('#modalAlert').addClass('d-none').empty();

    $.ajax({
        url: `${PROMOTION_API}/${promotionId}`,
        type: 'GET',

        success: function(promotion) {
            $('#promotionId').val(promotion.promotionId);
            $('#promotionName').val(promotion.promotionName);
            $('#promotionType').val(promotion.promotionType);
            $('#discountValue').val(promotion.discountValue);
            $('#startDate').val(new Date(promotion.startDate).toISOString().slice(0, 16));
            $('#endDate').val(new Date(promotion.endDate).toISOString().slice(0, 16));
            $('#applyCondition').val(promotion.applyCondition);
            $('#status').val(promotion.status);
            promotionModal.show();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || 'Không thể tải chi tiết chương trình.';
            showAlert('promotionAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý click nút Xóa
function handleDeleteClick() {
    const promotionId = $(this).data('id');
    if (confirm('Bạn có chắc chắn muốn xóa chương trình này?')) {
        $.ajax({
            url: `${PROMOTION_API}/${promotionId}`,
            type: 'DELETE',

            success: function() {
                showAlert('promotionAlert', 'Chương trình đã được xóa thành công.', 'success');
                loadPromotions(); // Tải lại danh sách
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || 'Lỗi khi xóa chương trình.';
                showAlert('promotionAlert', errorMessage, 'danger');
            }
        });
    }
}

// Xử lý form tìm kiếm
$('#promotionSearchForm').submit(function(e) {
    e.preventDefault();
    const searchType = $('#searchType').val().trim();
    loadPromotions(searchType || ''); // Nếu rỗng, tải tất cả
});

// Khởi tạo
$(document).ready(function() {
    loadPromotions();
});