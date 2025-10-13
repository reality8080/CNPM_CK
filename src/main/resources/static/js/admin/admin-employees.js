// /js/admin-employees.js

// Giả định: API_BASE_URL, getAuthHeader, showAlert đã được định nghĩa và có thể truy cập
// từ custom.js (hoặc file dùng chung)
// const API_BASE_URL = '/api/v1';
// const EMPLOYEE_API = `${API_BASE_URL}/admin/employees`;

// Hàm tải bảng nhân viên
async function loadAdminTable() {
    // Kiểm tra xem phần tử bảng có tồn tại không
    if (!document.getElementById('employeeTableBody')) {
        return;
    }

    // Hủy bỏ DataTable hiện có nếu có
    if ($.fn.DataTable.isDataTable('#adminTable')) {
        $('#adminTable').DataTable().destroy();
    }

    try {
        const headers = await getAuthHeader(); // Lấy từ custom.js
        const response = await fetch(EMPLOYEE_API, { headers: headers });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '../login.html';
                return;
            }
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const employees = await response.json();
        const tableBody = $('#employeeTableBody');
        tableBody.empty();

        employees.forEach(emp => {
            const fullName = emp.userProfile && emp.userProfile.fullName ? emp.userProfile.fullName : 'N/A';
            const department = emp.department || 'N/A';
            const isActive = emp.isActive ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-danger">Inactive</span>';

            const row = `
                <tr>
                    <td>${emp.email}</td>
                    <td>${fullName}</td>
                    <td>${department}</td>
                    <td>${emp.role || 'Employee'}</td>
                    <td>${isActive}</td>
                    <td>
                        <button class="btn btn-sm btn-info edit-btn" data-email="${emp.email}" title="Chỉnh sửa"><i class="bi bi-pencil-square"></i></button>
                        <button class="btn btn-sm btn-danger delete-btn" data-email="${emp.email}" title="Xóa"><i class="bi bi-trash"></i></button>
                    </td>
                </tr>
            `;
            tableBody.append(row);
        });

        // Khởi tạo DataTable sau khi load dữ liệu
        $('#adminTable').DataTable({
            "columnDefs": [
                { "orderable": false, "targets": 5 } // Cột Actions không cho sắp xếp
            ]
        });

        // Gắn lại sự kiện cho các nút
        tableBody.off('click').on('click', '.edit-btn', function() {
            const email = $(this).data('email');
            editEmployee(email);
        }).on('click', '.delete-btn', function() {
            const email = $(this).data('email');
            deleteEmployee(email);
        });

    } catch (error) {
        showAlert('employeeAlert', `Error loading employee list: ${error.message}`, 'danger'); // showAlert từ custom.js
    }
}

// Hàm Chỉnh sửa Employee (Đổ dữ liệu vào modal)
async function editEmployee(email) {
    try {
        const headers = await getAuthHeader();
        const response = await fetch(`${EMPLOYEE_API}/${email}`, { headers: headers });

        // ... (Logic xử lý phản hồi và đổ dữ liệu vào modal) ...

        const emp = await response.json();
        $('#employeeModalLabel').text('Edit Employee');
        $('#newEmail').val(emp.email).prop('disabled', true);
        $('#newFullName').val(emp.userProfile ? emp.userProfile.fullName : '');
        $('#newPassword').val('').prop('required', false);
        $('#newDepartment').val(emp.department || '');
        $('#newActive').prop('checked', emp.isActive);

        // Clear alert
        $('#modalAlert').addClass('d-none').removeClass('alert-danger alert-success').text('');

        const modal = new bootstrap.Modal(document.getElementById('employeeModal'));
        modal.show();
    } catch (error) {
        showAlert('employeeAlert', `Error loading employee info: ${error.message}`, 'danger');
    }
}

// Hàm Xóa Employee
async function deleteEmployee(email) {
    if (!confirm('Bạn có chắc chắn muốn xóa nhân viên này không?')) return;

    // ... (Logic xử lý xóa API) ...
    try {
        const headers = await getAuthHeader();
        const response = await fetch(`${EMPLOYEE_API}/${email}`, {
            method: 'DELETE',
            headers: headers
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        showAlert('employeeAlert', 'Employee deleted successfully.', 'success');
        loadAdminTable();
    } catch (error) {
        showAlert('employeeAlert', `Error deleting employee: ${error.message}`, 'danger');
    }
}

// Xử lý sự kiện submit form (Thêm/Sửa)
$('#employeeForm').submit(async function(e) {
    e.preventDefault();
    const email = $('#newEmail').val();
    const fullName = $('#newFullName').val();
    const password = $('#newPassword').val();

    // ... (Logic kiểm tra required và gửi dữ liệu) ...
    const isEdit = $('#employeeModalLabel').text() === 'Edit Employee';

    if (!isEdit && !password) {
        showAlert('modalAlert', 'Password is required for new employee.', 'danger');
        return;
    }

    const employeeData = {
        email: email,
        password: isEdit && !password ? undefined : password,
        department: $('#newDepartment').val(),
        role: 'Employee', // Đặt role mặc định là Employee
        isActive: $('#newActive').prop('checked'),
        userProfile: {
            fullName: fullName
        }
    };

    try {
        const method = isEdit ? 'PUT' : 'POST';
        const url = isEdit ? `${EMPLOYEE_API}/${email}` : EMPLOYEE_API;
        const headers = { 'Content-Type': 'application/json', ...await getAuthHeader() };

        const response = await fetch(url, {
            method: method,
            headers: headers,
            body: JSON.stringify(employeeData)
        });

        if (response.ok) {
            showAlert('employeeAlert', `Employee ${isEdit ? 'updated' : 'added'} successfully.`, 'success');
            const modal = bootstrap.Modal.getInstance(document.getElementById('employeeModal'));
            if (modal) modal.hide();
            loadAdminTable();
        } else {
            const error = await response.text();
            showAlert('modalAlert', error || 'Error saving employee. Check if email already exists or required fields are missing.', 'danger');
        }
    } catch (error) {
        showAlert('modalAlert', `Server connection error: ${error.message}`, 'danger');
    }
});

// Xử lý sự kiện nút "Add New Employee"
$('#addNewEmployeeBtn').click(() => {
    if (!document.getElementById('employeeForm')) return;
    $('#employeeModalLabel').text('Add New Employee');
    $('#employeeForm')[0].reset();
    $('#newEmail').prop('disabled', false);
    $('#newPassword').prop('required', true);
    $('#newActive').prop('checked', true);
    // Clear alert
    $('#modalAlert').addClass('d-none').removeClass('alert-danger alert-success').text('');
});


// Khởi tạo khi DOM đã sẵn sàng
$(document).ready(function() {
    // Chỉ chạy logic loadAdminTable trên trang manage-employees.html
    if (window.location.pathname.includes('manage-employees.html')) {
        loadAdminTable();
    }
});