// /views/product/categories.js

const CATEGORY_API = '/api/v1/admin/categories';
const categoryModal = new bootstrap.Modal(document.getElementById('categoryModal'));
let isEditMode = false;

// Hàm tải và hiển thị danh sách danh mục
function loadCategories(name = '') {
    const apiUrl = name ? `${CATEGORY_API}/search?name=${encodeURIComponent(name)}` : CATEGORY_API;

    $.ajax({
        url: apiUrl,
        type: 'GET',
        success: function(categories) {
            const $tableBody = $('#categoryTableBody');
            $tableBody.empty();

            if (categories.length === 0) {
                $tableBody.html('<tr><td colspan="4" class="text-center">Không tìm thấy danh mục nào.</td></tr>');
                return;
            }

            categories.forEach(category => {
                const row = `
                    <tr>
                        <td>${category.categoryId || 'N/A'}</td>
                        <td>${category.categoryName || ''}</td>
                        <td>${category.description || ''}</td>
                        <td class="action-buttons">
                            <button class="btn btn-sm btn-info edit-btn" data-id="${category.categoryId}">Sửa</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${category.categoryId}">Xóa</button>
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
            const errorMessage = xhr.responseJSON?.message || 'Lỗi khi tải danh sách danh mục.';
            showAlert('categoryAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý submit form Thêm/Sửa
$('#categoryForm').submit(function(e) {
    e.preventDefault();
    const categoryId = $('#categoryId').val();
    const categoryName = $('#categoryName').val().trim();
    const categoryDescription = $('#categoryDescription').val().trim();
    const $saveBtn = $('#saveCategoryBtn');

    // Validation cơ bản
    if (!categoryName) {
        showAlert('modalAlert', 'Tên danh mục không được để trống.', 'danger');
        return;
    }

    const categoryData = {
        categoryName: categoryName,
        description: categoryDescription
        // Không gửi categoryId khi create (backend tự generate)
    };

    const method = isEditMode ? 'PUT' : 'POST';
    const url = isEditMode ? `${CATEGORY_API}/${categoryId}` : CATEGORY_API;

    toggleSpinner($saveBtn, true);

    $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(categoryData),
        success: function() {
            showAlert('categoryAlert', `Danh mục đã được ${isEditMode ? 'cập nhật' : 'thêm mới'} thành công.`, 'success');
            categoryModal.hide();
            loadCategories(); // Tải lại danh sách
            $('#categoryForm')[0].reset(); // Reset form sau khi thành công
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || `Lỗi khi ${isEditMode ? 'cập nhật' : 'thêm mới'} danh mục.`;
            showAlert('modalAlert', errorMessage, 'danger');
        },
        complete: function() {
            toggleSpinner($saveBtn, false);
        }
    });
});

// Xử lý click nút Thêm Mới
$('#addNewCategoryBtn').click(function() {
    isEditMode = false;
    $('#categoryModalLabel').text('Thêm Danh Mục Mới');
    $('#categoryForm')[0].reset();
    $('#categoryId').val('');
    $('#modalAlert').addClass('d-none').empty();
    categoryModal.show();
});

// Xử lý click nút Sửa (sử dụng function riêng để delegation)
function handleEditClick() {
    isEditMode = true;
    const categoryId = $(this).data('id');
    $('#categoryModalLabel').text('Cập Nhật Danh Mục');
    $('#modalAlert').addClass('d-none').empty();

    $.ajax({
        url: `${CATEGORY_API}/${categoryId}`,
        type: 'GET',
        success: function(category) {
            $('#categoryId').val(category.categoryId);
            $('#categoryName').val(category.categoryName);
            $('#categoryDescription').val(category.description);
            categoryModal.show();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || 'Không thể tải chi tiết danh mục.';
            showAlert('categoryAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý click nút Xóa (sử dụng function riêng để delegation)
function handleDeleteClick() {
    const categoryId = $(this).data('id');
    if (confirm('Bạn có chắc chắn muốn xóa danh mục này?')) {
        $.ajax({
            url: `${CATEGORY_API}/${categoryId}`,
            type: 'DELETE',
            success: function() {
                showAlert('categoryAlert', 'Danh mục đã được xóa thành công.', 'success');
                loadCategories(); // Tải lại danh sách
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || 'Lỗi khi xóa danh mục.';
                showAlert('categoryAlert', errorMessage, 'danger');
            }
        });
    }
}

// Xử lý form tìm kiếm
$('#categorySearchForm').submit(function(e) {
    e.preventDefault();
    const searchName = $('#searchName').val().trim();
    loadCategories(searchName || ''); // Nếu rỗng, tải tất cả
});

// Khởi tạo
$(document).ready(function() {
    // Tải danh sách khi trang load
    loadCategories();
});