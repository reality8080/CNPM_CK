// custom.js - Đã loại bỏ logic quản lý nhân viên và các hàm trùng lặp

const API_BASE_URL = '/api/v1';
const EMPLOYEE_API = `${API_BASE_URL}/admin/employees`; // Giữ lại hằng số này

async function getAuthHeader() {
    let token = localStorage.getItem('accessToken');
    if (!token) {
        console.warn('No access token found, redirecting to login.');
        window.location.href = '../login.html';
        return {};
    }
    return { 'Authorization': `Bearer ${token}` };
}

function showAlert(elementId, message, type = 'success') {
    const alertElement = document.getElementById(elementId);
    if (!alertElement) return;
    alertElement.className = `alert alert-${type}`;
    alertElement.textContent = message;
    alertElement.classList.remove('d-none');
    setTimeout(() => {
        alertElement.classList.add('d-none');
        alertElement.textContent = '';
    }, 5000);
}

function checkAuthentication() {
    const protectedPaths = [
        '/admin/',
        '/product/',
        '/admin/profile.html',
        '/admin/manage-employees.html',
        '/product/app-discounts.html',
        '/admin/admin-dashboard.html',
        '/product/categories.html',
        '/product/products.html',
        '/product/promotion-programs.html',
        '/product/suppliers.html'];
    if (!localStorage.getItem('accessToken') && protectedPaths.some(path => window.location.pathname.includes(path))) {
        window.location.href = '../login.html';
    }
}

$(document).ready(function() {
    $("#navbar-placeholder").load("/views/layouts/admin-navbar.html", function(response, status) {
        // Logic load navbar
    });
    checkAuthentication(); // Kiểm tra token khi trang tải
});

// Sidebar Toggle
document.getElementById('sidebar-toggle')?.addEventListener('click', () => {
    // ... (Logic toggle sidebar) ...
});

// Logout
function handleLogout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = '../logout.html';
}

document.getElementById('logout-btn')?.addEventListener('click', handleLogout);
document.getElementById('logout-dropdown-btn')?.addEventListener('click', handleLogout);




// Initialize

// $(document).ready(function() {
//     console.log('Access Token:', localStorage.getItem('accessToken'));
//     const headers = getAuthHeader();
//     console.log('Headers:', headers);
//     if (!headers.Authorization) {
//         window.location.href = '../login.html';
//         return;
//     }
//     loadDiscounts();
// });
// custom.js - THÊM VÀO CUỐI FILE HOẶC SỬA ĐOẠN CODE LIÊN QUAN ĐẾN PASSWORD
$(document).ready(function() {
    // ... (Giữ lại tất cả các hàm và logic hiện có: getAuthHeader, showAlert, checkAuthentication, v.v.) ...

    // START: LOGIC CHUNG CHO TÍNH NĂNG SHOW/HIDE PASSWORD
    // Gắn sự kiện click cho TẤT CẢ các nút có class .toggle-password
    // Dùng .on('click', ...) để đảm bảo hoạt động cả trong Modal (như manage-employees.html)
    $(document).on('click', '.toggle-password', function() {
        // Lấy ID của trường input mục tiêu từ thuộc tính data-target (data-target="INPUT_ID")
        const targetId = $(this).data('target');
        const input = $(`#${targetId}`);
        const icon = $(this).find('i');

        if (input.attr('type') === 'password') {
            // Chuyển sang dạng text (hiển thị)
            input.attr('type', 'text');
            icon.removeClass('bi-eye').addClass('bi-eye-slash'); // Thay icon mắt mở thành mắt đóng
        } else {
            // Chuyển lại dạng password (ẩn)
            input.attr('type', 'password');
            icon.removeClass('bi-eye-slash').addClass('bi-eye'); // Thay icon mắt đóng thành mắt mở
        }
    });
    // END: LOGIC CHUNG CHO TÍNH NĂNG SHOW/HIDE PASSWORD

    // ... (Giữ lại các logic khác của custom.js) ...
});