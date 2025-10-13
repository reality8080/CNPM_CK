$(document).ready(function() {
    // Common function: Toggle spinner with improved animation
    function toggleSpinner(button, show) {
        const spinner = button.find('.spinner-border');
        if (show) {
            spinner.removeClass('d-none');
            button.prop('disabled', true).addClass('disabled').text('Processing...');
        } else {
            spinner.addClass('d-none');
            button.prop('disabled', false).removeClass('disabled').text(button.data('original-text'));
        }
    }

    // Login
    $('#loginForm').submit(function(e) {
        e.preventDefault();
        const button = $(this).find('button[type="submit"]');
        if (!button.data('original-text')) button.data('original-text', button.text());
        toggleSpinner(button, true);
        const data = { email: $('#email').val(), password: $('#password').val() };
        $.ajax({
            url: '/api/v1/auth/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                localStorage.setItem('accessToken', response.accessToken);
                localStorage.setItem('refreshToken', response.refreshToken);
                $('#loginMessage').html('<div class="alert alert-success fade show">Login successful! Redirecting...</div>');
                setTimeout(() => { window.location.href = '/views/admin/admin-dashboard.html'; }, 1500);
            },
            error: function(xhr) {
                $('#loginMessage').html('<div class="alert alert-danger fade show">Error: ' + (xhr.responseJSON ? xhr.responseJSON.message : 'Login failed') + '</div>');
            },
            complete: function() { toggleSpinner(button, false); }
        });
    });

    // Register Request OTP
    $('#registerRequestForm').submit(function(e) {
        e.preventDefault();
        const button = $(this).find('button[type="submit"]');
        if (!button.data('original-text')) button.data('original-text', button.text());
        toggleSpinner(button, true);
        const password = $('#regPassword').val();
        const confirmPassword = $('#regConfirmPassword').val();
        if (password !== confirmPassword) {
            $('#registerMessage').html('<div class="alert alert-danger fade show">Passwords do not match.</div>');
            toggleSpinner(button, false);
            return;
        }
        const data = {
            email: $('#regEmail').val(),
            password: password,
            userProfile: { fullName: $('#regFullName').val() }
        };
        $.ajax({
            url: '/api/v1/auth/register/request',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                $('#registerMessage').html('<div class="alert alert-success fade show">' + response + '</div>');
                $('#registerRequestForm').fadeOut(300, function() { $('#registerConfirmForm').fadeIn(300); });
            },
            error: function(xhr) {
                $('#registerMessage').html('<div class="alert alert-danger fade show">Error: ' + (xhr.responseText || 'Failed to request OTP') + '</div>');
            },
            complete: function() { toggleSpinner(button, false); }
        });
    });

    // Register Confirm OTP
    $('#registerConfirmForm').submit(function(e) {
        e.preventDefault();
        const button = $(this).find('button[type="submit"]');
        if (!button.data('original-text')) button.data('original-text', button.text());
        toggleSpinner(button, true);
        const data = { email: $('#regEmail').val(), otp: $('#regOtp').val() };
        $.ajax({
            url: '/api/v1/auth/register/confirm',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                localStorage.setItem('accessToken', response.accessToken);
                localStorage.setItem('refreshToken', response.refreshToken);
                $('#registerMessage').html('<div class="alert alert-success fade show">Registration successful! Redirecting...</div>');
                setTimeout(() => { window.location.href = '/views/admin/admin-dashboard.html'; }, 1500);
            },
            error: function(xhr) {
                $('#registerMessage').html('<div class="alert alert-danger fade show">Error: ' + (xhr.responseJSON ? xhr.responseJSON.message : 'Invalid OTP') + '</div>');
            },
            complete: function() { toggleSpinner(button, false); }
        });
    });

    // Forgot Request OTP
    $('#forgotRequestForm').submit(function(e) {
        e.preventDefault();
        const button = $(this).find('button[type="submit"]');
        if (!button.data('original-text')) button.data('original-text', button.text());
        toggleSpinner(button, true);
        const email = $('#forgotEmail').val();
        $.ajax({
            url: '/api/v1/auth/reset-password/request?email=' + email,
            type: 'POST',
            success: function(response) {
                $('#forgotMessage').html('<div class="alert alert-success fade show">' + response + '</div>');
                $('#forgotRequestForm').fadeOut(300, function() { $('#forgotConfirmForm').fadeIn(300); });
            },
            error: function(xhr) {
                $('#forgotMessage').html('<div class="alert alert-danger fade show">Error: ' + (xhr.responseText || 'Failed to request OTP') + '</div>');
            },
            complete: function() { toggleSpinner(button, false); }
        });
    });

    // Forgot Confirm
    $('#forgotConfirmForm').submit(function(e) {
        e.preventDefault();
        const button = $(this).find('button[type="submit"]');
        if (!button.data('original-text')) button.data('original-text', button.text());
        toggleSpinner(button, true);
        const data = { email: $('#forgotEmail').val(), otp: $('#forgotOtp').val(), newPassword: $('#newPassword').val() };
        $.ajax({
            url: '/api/v1/auth/reset-password/confirm',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                $('#forgotMessage').html('<div class="alert alert-success fade show">' + response + '</div>');
                setTimeout(() => { window.location.href = '/views/login.html'; }, 1500);
            },
            error: function(xhr) {
                $('#forgotMessage').html('<div class="alert alert-danger fade show">Error: ' + (xhr.responseJSON ? xhr.responseJSON.message : 'Invalid OTP') + '</div>');
            },
            complete: function() { toggleSpinner(button, false); }
        });
    });
});