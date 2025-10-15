
// Giả định: API_BASE_URL, getAuthHeader, showAlert đã được định nghĩa và có thể truy cập
// từ custom.js (hoặc file dùng chung)
//const ADMIN_API_BASE_URL = '/api/v1/admin';
//const CUSTOMER_API = '${ADMIN_API_BASE_URL}/customers';


// Hàm tải bảng users
async function loadAdminTable() {
    // Kiểm tra xem phần tử bảng có tồn tại không
    if (!document.getElementById('userTableBody')) {
        return;
    }

    // Hủy bỏ DataTable hiện có nếu có
    if ($.fn.DataTable.isDataTable('#adminTable')) {
        $('#adminTable').DataTable().destroy();
    }

    try {
        const headers = await getAuthHeader(); // Lấy từ custom.js
        const response = await fetch(CUSTOMER_API, { headers: headers });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '../login.html';
                return;
            }
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const users = await response.json();
        const tableBody = $('#userTableBody');
        tableBody.empty();

        users.forEach(user => {
            const fullName = user.fullname || 'N/A';
            const role = user.roleId || 'CUSTOMER';
            const createdAt = new Date(user.createdAt).toLocaleDateString('en-US', {
                year: 'numeric', month: 'short', day: 'numeric'
            });

            const row = `
                <tr>
                    <td>${user.email}</td>
                    <td>${fullName}</td>
                    <td>${role}</td>
                    <td>${createdAt}</td>
                    <td>
                        <button class="btn btn-sm btn-info edit-btn" data-email="${user.email}" title="Chỉnh sửa"><i class="bi bi-pencil-square"></i></button>
                        <button class="btn btn-sm btn-danger delete-btn" data-email="${user.email}" title="Xóa"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `;
            tableBody.append(row);
        });

        // Khởi tạo DataTable sau khi load dữ liệu
        $('#adminTable').DataTable({
            "columnDefs": [
                { "orderable": false, "targets": 4 } // Cột Actions không cho sắp xếp
            ]
        });

        // Gắn lại sự kiện cho các nút
        tableBody.off('click').on('click', '.edit-btn', function() {
            const email = $(this).data('email');
            editUser(email);
        }).on('click', '.delete-btn', function() {
            const email = $(this).data('email');
            deleteUser(email);
        });

    } catch (error) {
        showAlert('userAlert', `Error loading user list: ${error.message}`, 'danger'); // showAlert từ custom.js
    }
}

// Hàm Chỉnh sửa User (Đổ dữ liệu vào modal)
async function editUser(email) {
    try {
        const headers = await getAuthHeader();
        const response = await fetch(`${CUSTOMER_API}/${email}`, { headers: headers });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const user = await response.json();
        $('#userModalLabel').text('Edit User');
        $('#newEmail').val(user.email).prop('disabled', true);
        $('#newFullName').val(user.fullname || '');
        $('#newPassword').val('').prop('required', false);
        $('#newRole').val(user.roleId || 'CUSTOMER');

        // Clear alert
        $('#modalAlert').addClass('d-none').removeClass('alert-danger alert-success').text('');

        const modal = new bootstrap.Modal(document.getElementById('userModal'));
        modal.show();
    } catch (error) {
        showAlert('userAlert', `Error loading user info: ${error.message}`, 'danger');
    }
}

// Hàm Xóa User
async function deleteUser(email) {
    if (!confirm('Bạn có chắc chắn muốn xóa user này không?')) return;

    try {
        const headers = await getAuthHeader();
        const response = await fetch(`${CUSTOMER_API}/${email}`, {
            method: 'DELETE',
            headers: headers
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        showAlert('userAlert', 'User deleted successfully.', 'success');
        loadAdminTable();
    } catch (error) {
        showAlert('userAlert', `Error deleting user: ${error.message}`, 'danger');
    }
}

// Xử lý sự kiện submit form (Thêm/Sửa)
$('#userForm').submit(async function(e) {
    e.preventDefault();
    const email = $('#newEmail').val();
    const fullName = $('#newFullName').val();
    const password = $('#newPassword').val();

    const isEdit = $('#userModalLabel').text() === 'Edit User';

    if (!isEdit && !password) {
        showAlert('modalAlert', 'Password is required for new user.', 'danger');
        return;
    }

    const userData = {
        email: email,
        password: isEdit && !password ? undefined : password,
        fullname: fullName,
        roleId: $('#newRole').val() // Fixed as CUSTOMER
    };

    try {
        const method = isEdit ? 'PUT' : 'POST';
        const url = isEdit ? `${CUSTOMER_API}/${email}` : CUSTOMER_API;
        const headers = { 'Content-Type': 'application/json', ...await getAuthHeader() };

        const response = await fetch(url, {
            method: method,
            headers: headers,
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            showAlert('userAlert', `User ${isEdit ? 'updated' : 'added'} successfully.`, 'success');
            const modal = bootstrap.Modal.getInstance(document.getElementById('userModal'));
            if (modal) modal.hide();
            loadAdminTable();
        } else {
            const error = await response.text();
            showAlert('modalAlert', error || 'Error saving user. Check if email already exists or required fields are missing.', 'danger');
        }
    } catch (error) {
        showAlert('modalAlert', `Server connection error: ${error.message}`, 'danger');
    }
});

// Xử lý sự kiện nút "Add New User"
$('#addNewUserBtn').click(() => {
    if (!document.getElementById('userForm')) return;
    $('#userModalLabel').text('Add New User');
    $('#userForm')[0].reset();
    $('#newEmail').prop('disabled', false);
    $('#newPassword').prop('required', true);
    $('#newRole').val('CUSTOMER');
    // Clear alert
    $('#modalAlert').addClass('d-none').removeClass('alert-danger alert-success').text('');
});

// Khởi tạo khi DOM đã sẵn sàng
$(document).ready(function() {
    // Chỉ chạy logic loadAdminTable trên trang admin-users.html
    if (window.location.pathname.includes('admin-users.html')) {
        loadAdminTable();
    }
});