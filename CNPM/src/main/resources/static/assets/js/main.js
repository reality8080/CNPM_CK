// src/main/resources/static/assets/js/main.js

$(document).ready(function() {
    loadComponents();
});

function loadComponents() {
    $('#navbar').load('/components/navbar.html');
    $('#sidebar').load('/components/sidebar.html', function() {
        setActiveMenu();
    });
}


function setActiveMenu() {
    const path = window.location.pathname;
    $('.sidebar .nav-link').removeClass('active');
    $(`.sidebar .nav-link[href="${path}"]`).addClass('active');
}

function logout() {
    Modal.confirm('Đăng xuất', 'Bạn có chắc muốn đăng xuất?', () => {
        Storage.remove('currentUser');
        window.location.href = '/';
    });
}