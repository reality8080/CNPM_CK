// admin.js - Fixed version with no duplicates, correct IDs, and proper 404 handling

// Define toggleSpinner (only one definition)
function toggleSpinner(button, show) {
    const spinner = button.find('.spinner-border');
    const originalText = button.data('original-text') || button.html();

    if (show) {
        button.data('original-text', originalText);
        button.html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Processing...');
        button.prop('disabled', true);
    } else {
        button.html(originalText);
        button.prop('disabled', false);
    }
}

// Global for profile picture
let currentBlobUrl = null;

// Load profile picture with retry and 404 handling
function loadProfilePicture(retryCount = 0) {
    console.log(`Calling loadProfilePicture (retry ${retryCount})`);
    const pictureUrl = '/api/v1/admin/me/profile/picture';
    const $imgElement = $('#profilePicture');
    const $deleteBtn = $('#deletePictureBtn');

    if (currentBlobUrl) {
        URL.revokeObjectURL(currentBlobUrl);
        currentBlobUrl = null;
    }

    $.ajax({
        url: pictureUrl,
        type: 'GET',
        xhrFields: { responseType: 'blob' },
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` },
        success: function(blob) {
            console.log('GET success: blob size=', blob.size, 'type=', blob.type);
            if (blob.size === 0 || !blob.type.startsWith('image/')) {
                console.warn('Invalid blob:', blob);
                $imgElement.attr('src', '/images/default-user.jpg');
                $deleteBtn.hide();
                return;
            }
            currentBlobUrl = URL.createObjectURL(blob);
            $imgElement.attr('src', currentBlobUrl);
            $deleteBtn.show();
        },
        error: function(xhr) {
            console.error('GET error:', xhr.status, xhr.responseText || 'Unknown error');
            $imgElement.attr('src', '/images/default-user.jpg');
            $deleteBtn.hide();
            if (retryCount < 3 && xhr.status >= 500) { // Retry only for server errors (500+), not 404
                console.log(`Retrying GET in 500ms... (attempt ${retryCount + 1})`);
                setTimeout(() => loadProfilePicture(retryCount + 1), 500);
            } else if (xhr.status === 404) {
                console.log('No profile picture found, using default.');
            }
        }
    });
}

$(document).ready(function() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        window.location.href = '../../views/login.html';
        return;
    }

    $.ajaxSetup({
        headers: { 'Authorization': `Bearer ${token}` }
    });

    // Load profile picture on page load if element exists
    if ($('#profilePicture').length) {
        loadProfilePicture();
    }

    // Update Profile Form
    $('#updateProfileForm').submit(function(e) {
        e.preventDefault();
        const $button = $(this).find('button[type="submit"]');
        toggleSpinner($button, true);

        const profileData = {
            fullName: $('#fullName').val(),
            phoneNumber: $('#phoneNumber').val(),
            address: $('#address').val(),
            dateOfBirth: $('#dateOfBirth').val() ? new Date($('#dateOfBirth').val()).toISOString() : null
        };

        $.ajax({
            url: '/api/v1/admin/me/profile',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(profileData),
            success: function() {
                $('#profileMessage').html('<div class="alert alert-success">Profile updated successfully!</div>');
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || 'Failed to update profile';
                $('#profileMessage').html('<div class="alert alert-danger">Error: ' + errorMessage + '</div>');
            },
            complete: function() {
                toggleSpinner($button, false);
            }
        });
    });

    // Update Password Form
    $('#updatePasswordForm').submit(function(e) {
        e.preventDefault();
        const $button = $(this).find('button[type="submit"]');
        toggleSpinner($button, true);

        const passwordData = {
            password: $('#newPassword').val()
        };

        if ($('#newPassword').val() !== $('#confirmPassword').val()) {
            $('#passwordMessage').html('<div class="alert alert-danger">Passwords do not match.</div>');
            toggleSpinner($button, false);
            return;
        }

        $.ajax({
            url: '/api/v1/admin/me/details',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(passwordData),
            success: function() {
                $('#passwordMessage').html('<div class="alert alert-success">Password updated successfully!</div>');
                $('#updatePasswordForm')[0].reset();
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || 'Failed to update password';
                $('#passwordMessage').html('<div class="alert alert-danger">Error: ' + errorMessage + '</div>');
            },
            complete: function() {
                toggleSpinner($button, false);
            }
        });
    });

    // Upload Profile Picture
    $('#pictureForm').submit(function(e) {
        e.preventDefault();
        const $button = $(this).find('button[type="submit"]');
        toggleSpinner($button, true);

        const fileInput = $('#pictureFile')[0].files[0];
        if (!fileInput || !fileInput.type.startsWith('image/')) {
            $('#pictureMessage').html('<div class="alert alert-danger">Only image files are allowed.</div>');
            toggleSpinner($button, false);
            return;
        }

        const formData = new FormData(this);
        console.log('Uploading file:', fileInput.name, 'size:', fileInput.size, 'type:', fileInput.type);

        $.ajax({
            url: '/api/v1/admin/me/profile/picture',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` },
            success: function(response) {
                console.log('POST success:', response);
                $('#pictureMessage').html('<div class="alert alert-success">Picture uploaded successfully!</div>');
                $('#pictureFile').val('');
                setTimeout(() => {
                    loadProfilePicture();
                }, 300); // Delay for DB sync
            },
            error: function(xhr) {
                const errorMessage = xhr.responseJSON?.message || xhr.responseText || 'Failed to upload picture';
                console.error('POST error:', xhr.status, errorMessage);
                $('#pictureMessage').html('<div class="alert alert-danger">Error: ' + errorMessage + '</div>');
            },
            complete: function() {
                toggleSpinner($button, false);
            }
        });
    });

    // Delete picture
    $('#deletePictureBtn').click(function() {
        if (!confirm('Are you sure you want to remove your profile picture?')) return;

        $.ajax({
            url: '/api/v1/admin/me/profile/picture',
            type: 'DELETE',
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` },
            success: function() {
                $('#pictureMessage').html('<div class="alert alert-success">Picture removed successfully!</div>');
                loadProfilePicture();
            },
            error: function(xhr) {
                const errorMessage = xhr.responseText || 'Failed to remove picture';
                $('#pictureMessage').html('<div class="alert alert-danger">Error: ' + errorMessage + '</div>');
            }
        });
    });
});

async function loadAdminInfo() {
    if (!window.location.pathname.includes('profile.html')) {
        console.log('Not on profile page, skipping loadAdminInfo.');
        return;
    }

    const adminNameElement = document.getElementById('adminFullName'); // Fixed: Use 'adminFullName'
    if (!adminNameElement) {
        console.log('Element #adminFullName not found, skipping loadAdminInfo.');
        return;
    }

    try {
        const response = await fetch('/api/v1/admin/me', {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });

        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                window.location.href = '../../views/login.html';
                return;
            }
            if (response.status === 404) {
                showAlert('profileMessage', 'Admin not found.', 'danger');
                return;
            }
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const admin = await response.json();

        if (!admin.userProfile) {
            showAlert('profileMessage', 'User profile not found.', 'danger');
            return;
        }

        // Update profile information
        adminNameElement.textContent = `Xin chào, ${admin.userProfile.fullName}`;        document.getElementById('adminEmail').textContent = admin.email;
        document.getElementById('fullName').value = admin.userProfile.fullName;
        document.getElementById('phoneNumber').value = admin.userProfile.phoneNumber || '';
        document.getElementById('address').value = admin.userProfile.address || '';
        const dob = admin.userProfile.dateOfBirth;
        document.getElementById('dateOfBirth').value = dob && !isNaN(new Date(dob).getTime())
            ? new Date(dob).toISOString().slice(0, 10)
            : '';

        // Load profile picture
        loadProfilePicture();

    } catch (error) {
        console.error('Error in loadAdminInfo:', error);
        showAlert('profileMessage', `Error loading admin info: ${error.message}`, 'danger');
    }
}

// Initialize on DOMContentLoaded
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('profile.html')) {
        loadAdminInfo();
    }
});

// admin.js - Phần thêm cho dashboard

// Hàm load dashboard data (chỉ chạy nếu trên trang dashboard)
async function loadDashboardData() {
    // Kiểm tra xem trang có phải dashboard không (dựa trên element tồn tại)
    if (!document.getElementById('total-employees')) {
        console.log('Not on dashboard page, skipping loadDashboardData.');
        return;
    }

    try {
        const headers = await getAuthHeader(); // Từ custom.js
        const response = await fetch('/api/v1/admin/employees', { headers: headers });

        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                window.location.href = '../../views/login.html';
                return;
            }
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const employees = await response.json();

        // 1. Tính toán metrics
        const totalEmployees = employees.length;
        const activeEmployees = employees.filter(emp => emp.isActive).length;
        const inactiveEmployees = totalEmployees - activeEmployees; // Dùng cho Alerts
        const uniqueDepartments = new Set(employees.map(emp => emp.department || 'N/A')).size; // Unique departments

        // 2. Cập nhật DOM
        document.getElementById('total-employees').textContent = totalEmployees;
        document.getElementById('active-employees').textContent = activeEmployees;
        document.getElementById('departments').textContent = uniqueDepartments;
        document.getElementById('alerts').textContent = inactiveEmployees; // Alerts = inactive count (có thể thay đổi)

        // 3. Recent Activities: Sort by createdAt desc, lấy top 5
        const recentActivitiesList = document.getElementById('recent-activities');
        recentActivitiesList.innerHTML = ''; // Clear default
        if (employees.length === 0) {
            recentActivitiesList.innerHTML = '<li class="list-group-item text-muted">No recent activities.</li>';
        } else {
            // Sort desc by createdAt (giả định createdAt là ISO string hoặc timestamp)
            employees.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            employees.slice(0, 5).forEach(emp => {
                const fullName = emp.userProfile?.fullName || emp.email;
                const timeAgo = calculateTimeAgo(emp.createdAt); // Hàm helper bên dưới
                const activityItem = `
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-person-plus text-success"></i> New employee added: ${fullName}</span>
                        <small class="text-muted">${timeAgo}</small>
                    </li>
                `;
                recentActivitiesList.innerHTML += activityItem;
            });
        }

        // 4. Department Distribution Chart (Pie chart using Chart.js)
        const departmentCounts = employees.reduce((acc, emp) => {
            const dept = emp.department || 'N/A';
            acc[dept] = (acc[dept] || 0) + 1;
            return acc;
        }, {});

        const labels = Object.keys(departmentCounts);
        const data = Object.values(departmentCounts);
        const backgroundColors = generateColors(labels.length); // Hàm helper bên dưới

        const ctx = document.getElementById('departmentChart').getContext('2d');
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: backgroundColors,
                    borderColor: backgroundColors.map(color => color.replace('0.2', '1')), // Darker borders
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Department Distribution'
                    }
                }
            }
        });

    } catch (error) {
        console.error('Error loading dashboard data:', error);
        // Có thể add alert: showAlert('dashboardAlert', `Error: ${error.message}`, 'danger'); nhưng HTML chưa có element
    }
}

// Helper: Tính time ago (e.g., "2 hours ago")
function calculateTimeAgo(dateString) {
    const now = new Date();
    const created = new Date(dateString);
    const diffMs = now - created;
    const diffSecs = Math.floor(diffMs / 1000);
    const diffMins = Math.floor(diffSecs / 60);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffDays > 0) return `${diffDays} days ago`;
    if (diffHours > 0) return `${diffHours} hours ago`;
    if (diffMins > 0) return `${diffMins} minutes ago`;
    return 'Just now';
}

// Helper: Generate random colors for chart (rgba with alpha)
function generateColors(count) {
    const colors = [];
    for (let i = 0; i < count; i++) {
        const r = Math.floor(Math.random() * 255);
        const g = Math.floor(Math.random() * 255);
        const b = Math.floor(Math.random() * 255);
        colors.push(`rgba(${r}, ${g}, ${b}, 0.6)`); // Semi-transparent
    }
    return colors;
}

// Gọi hàm khi DOM ready (thêm vào phần document.addEventListener('DOMContentLoaded') hiện có)
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('admin-dashboard.html')) {
        loadDashboardData();
    }
    // ... code khác ...
});