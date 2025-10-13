// /js/products.js

const PRODUCT_API = '/api/v1/admin/products';
const productModal = new bootstrap.Modal(document.getElementById('productModal'));
let isEditMode = false;

// Hàm tải và hiển thị danh sách sản phẩm
function loadProducts(filterType = 'all', filterValue = '') {
    let apiUrl = PRODUCT_API;
    if (filterType === 'category' && filterValue) {
        apiUrl = `${PRODUCT_API}/category/${encodeURIComponent(filterValue)}`;
    } else if (filterType === 'supplier' && filterValue) {
        apiUrl = `${PRODUCT_API}/supplier/${encodeURIComponent(filterValue)}`;
    }

    $.ajax({
        url: apiUrl,
        type: 'GET',

        success: function(products) {
            const $tableBody = $('#productTableBody');
            $tableBody.empty();

            if (products.length === 0) {
                $tableBody.html('<tr><td colspan="10" class="text-center">Không tìm thấy sản phẩm nào.</td></tr>');
                return;
            }

            products.forEach(product => {
                const createdDate = product.createdDate ? new Date(product.createdDate).toLocaleDateString('vi-VN') : 'N/A';
                const row = `
                    <tr>
                        <td>${product.productId || 'N/A'}</td>
                        <td>${product.productName || ''}</td>
                        <td>${product.price ? product.price.toLocaleString('vi-VN') + ' VND' : 'N/A'}</td>
                        <td>${product.description || ''}</td>
                        <td>${product.categoryId || ''}</td>
                        <td>${product.supplierId || ''}</td>
                        <td><span class="badge bg-${product.status === 'Active' ? 'success' : 'secondary'}">${product.status}</span></td>
                        <td>${product.ratingScore ? product.ratingScore.toFixed(1) : 'N/A'}</td>
                        <td>${product.soldQuantity || 0}</td>
                        <td class="action-buttons">
                            <button class="btn btn-sm btn-info edit-btn" data-id="${product.productId}">Sửa</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${product.productId}">Xóa</button>
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
            const errorMessage = xhr.responseJSON?.message || 'Lỗi khi tải danh sách sản phẩm.';
            showAlert('productAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý submit form Thêm/Sửa
$('#productForm').submit(function(e) {
    e.preventDefault();
    const productId = $('#productId').val();
    const productName = $('#productName').val().trim();
    const description = $('#description').val().trim();
    const price = parseFloat($('#productPrice').val());
    const imageUrl = $('#imageUrl').val().trim();
    const categoryId = $('#categoryId').val().trim();
    const supplierId = $('#supplierId').val().trim();
    const status = $('#status').val();
    const $saveBtn = $('#saveProductBtn');

    // Validation cơ bản
    if (!productName || isNaN(price) || !imageUrl || !categoryId || !supplierId || !status) {
        showAlert('modalAlert', 'Vui lòng điền đầy đủ thông tin bắt buộc.', 'danger');
        return;
    }

    const productData = {
        productName,
        description,
        price,
        imageUrl,
        status,
        categoryId,
        supplierId
        // Các trường khác như soldQuantity, createdDate, ratingScore, favoriteCount sẽ được backend xử lý
    };

    const method = isEditMode ? 'PUT' : 'POST';
    const url = isEditMode ? `${PRODUCT_API}/${productId}` : PRODUCT_API;

    toggleSpinner($saveBtn, true);

    $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(productData),

        success: function() {
            showAlert('productAlert', `Sản phẩm đã được ${isEditMode ? 'cập nhật' : 'thêm mới'} thành công.`, 'success');
            productModal.hide();
            loadProducts();
            $('#productForm')[0].reset();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || `Lỗi khi ${isEditMode ? 'cập nhật' : 'thêm mới'} sản phẩm.`;
            showAlert('modalAlert', errorMessage, 'danger');
        },
        complete: function() {
            toggleSpinner($saveBtn, false);
        }
    });
});

// Xử lý click nút Thêm Mới
$('#addNewProductBtn').click(function() {
    isEditMode = false;
    $('#productModalLabel').text('Thêm Sản Phẩm Mới');
    $('#productForm')[0].reset();
    $('#productId').val('');
    $('#modalAlert').addClass('d-none').empty();
    productModal.show();
});

// Xử lý click nút Sửa
function handleEditClick() {
    isEditMode = true;
    const productId = $(this).data('id');
    $('#productModalLabel').text('Cập Nhật Sản Phẩm');
    $('#modalAlert').addClass('d-none').empty();

    $.ajax({
        url: `${PRODUCT_API}/${productId}`,
        type: 'GET',

        success: function(product) {
            $('#productId').val(product.productId);
            $('#productName').val(product.productName);
            $('#description').val(product.description);
            $('#productPrice').val(product.price);
            $('#imageUrl').val(product.imageUrl);
            $('#categoryId').val(product.categoryId);
            $('#supplierId').val(product.supplierId);
            $('#status').val(product.status);
            productModal.show();
        },
        error: function(xhr) {
            const errorMessage = xhr.responseJSON?.message || 'Không thể tải chi tiết sản phẩm.';
            showAlert('productAlert', errorMessage, 'danger');
        }
    });
}

// Xử lý click nút Xóa
function handleDeleteClick() {
    const productId = $(this).data('id');
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
        $.ajax({
            url: `${PRODUCT_API}/${productId}`,
            type: 'DELETE',

            success: function() {
                showAlert('productAlert', 'Sản phẩm đã được xóa thành công.', 'success');
                loadProducts(); // Tải lại danh sách
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || 'Lỗi khi xóa sản phẩm.';
                showAlert('productAlert', errorMessage, 'danger');
            }
        });
    }
}

// Xử lý form tìm kiếm
$('#productSearchForm').submit(function(e) {
    e.preventDefault();
    const searchCategoryId = $('#searchCategoryId').val().trim();
    const searchSupplierId = $('#searchSupplierId').val().trim();

    if (searchCategoryId && searchSupplierId) {
        showAlert('productAlert', 'Vui lòng chỉ chọn một loại lọc (danh mục hoặc nhà cung cấp).', 'warning');
        return;
    } else if (searchCategoryId) {
        loadProducts('category', searchCategoryId);
    } else if (searchSupplierId) {
        loadProducts('supplier', searchSupplierId);
    } else {
        loadProducts(); // Tải tất cả nếu không có lọc
    }
});

// Khởi tạo
$(document).ready(function() {
    loadProducts();
});