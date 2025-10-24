/** Helper function để lấy dữ liệu từ form (yêu cầu input có thuộc tính 'name') */
function getSignupFormData(form) {
    const data = {};
    const inputs = form.querySelectorAll('input');

    inputs.forEach(input => {
        if (input.name) {
            data[input.name] = input.value;
        }
    });
    return data;
}

function openUserModal() {
    console.log("Opening user modal...");
    const modal = document.getElementById("userModal");
    const content = document.getElementById("modalContent");

    if (!modal) {
        console.log("Modal not found!");
        return;
    }

    modal.classList.remove("hidden", "modal-hidden");
    modal.classList.add("modal-visible");
    modal.style.display = "flex";

    setTimeout(() => {
        content.classList.remove("scale-95", "opacity-0", "modal-content-hidden");
        content.classList.add("scale-100", "opacity-100", "modal-content-visible");
    }, 10);

    console.log("User modal opened");
}

function closeUserModal() {
    console.log("Closing user modal...");
    const modal = document.getElementById("userModal");
    const content = document.getElementById("modalContent");
    content.classList.remove("scale-100", "opacity-100", "modal-content-visible");
    content.classList.add("scale-95", "opacity-0", "modal-content-hidden");
    setTimeout(() => {
        modal.classList.remove("modal-visible");
        modal.classList.add("hidden", "modal-hidden");
        console.log("User modal fully closed");
    }, 150);
}

function openSignupModal() {
    console.log("Opening signup modal...");
    const modal = document.getElementById("signupModal");
    const content = document.getElementById("signupModalContent");
    modal.classList.remove("hidden", "modal-hidden");
    modal.classList.add("modal-visible");
    modal.style.display = "flex";
    setTimeout(() => {
        content.classList.remove("scale-95", "opacity-0", "modal-content-hidden");
        content.classList.add("scale-100", "opacity-100", "modal-content-visible");
    }, 10);
}

function closeSignupModal() {
    console.log("Closing signup modal...");
    const modal = document.getElementById("signupModal");
    const content = document.getElementById("signupModalContent");
    content.classList.remove("scale-100", "opacity-100", "modal-content-visible");
    content.classList.add("scale-95", "opacity-0", "modal-content-hidden");
    setTimeout(() => {
        modal.classList.remove("modal-visible");
        modal.classList.add("hidden", "modal-hidden");
        console.log("Signup modal fully closed");
    }, 150);
}

function openProfileModal() {
    console.log("Opening profile modal...");
    const modal = document.getElementById("profileModal");
    const content = document.getElementById("profileModalContent");

    if (!modal) {
        console.log("Profile modal not found!");
        return;
    }

    // Lấy user từ storage
    const user = JSON.parse(localStorage.getItem('currentUser') || sessionStorage.getItem('currentUser') || 'null');
    if (user) {
        document.getElementById("profileName").textContent = user.name || 'Không có thông tin';
        document.getElementById("profileEmail").textContent = user.email || 'Không có thông tin';
        document.getElementById("profileRole").textContent = user.role?.name || 'USER';
    }

    modal.classList.remove("hidden", "modal-hidden");
    modal.classList.add("modal-visible");
    modal.style.display = "flex";

    setTimeout(() => {
        content.classList.remove("scale-95", "opacity-0", "modal-content-hidden");
        content.classList.add("scale-100", "opacity-100", "modal-content-visible");
    }, 10);

    console.log("Profile modal opened");
}

function closeProfileModal() {
    console.log("Closing profile modal...");
    const modal = document.getElementById("profileModal");
    const content = document.getElementById("profileModalContent");
    content.classList.remove("scale-100", "opacity-100", "modal-content-visible");
    content.classList.add("scale-95", "opacity-0", "modal-content-hidden");
    setTimeout(() => {
        modal.classList.remove("modal-visible");
        modal.classList.add("hidden", "modal-hidden");
        console.log("Profile modal fully closed");
    }, 150);
}

function switchToSignup() {
    console.log("Switching to signup...");
    const userContent = document.getElementById("modalContent");
    userContent.classList.remove("scale-100", "opacity-100", "modal-content-visible");
    userContent.classList.add("scale-95", "opacity-0", "modal-content-hidden");

    setTimeout(() => {
        const userModal = document.getElementById("userModal");
        const signupModal = document.getElementById("signupModal");
        const signupContent = document.getElementById("signupModalContent");

        userModal.classList.remove("modal-visible");
        userModal.classList.add("hidden", "modal-hidden");

        signupModal.classList.remove("hidden", "modal-hidden");
        signupModal.classList.add("modal-visible");
        signupModal.style.display = "flex";

        setTimeout(() => {
            signupContent.classList.remove("scale-95", "opacity-0", "modal-content-hidden");
            signupContent.classList.add("scale-100", "opacity-100", "modal-content-visible");
        }, 10);
    }, 100);
}

function switchToLogin() {
    console.log("Switching to login...");
    const signupContent = document.getElementById("signupModalContent");
    signupContent.classList.remove("scale-100", "opacity-100", "modal-content-visible");
    signupContent.classList.add("scale-95", "opacity-0", "modal-content-hidden");

    setTimeout(() => {
        const signupModal = document.getElementById("signupModal");
        const userModal = document.getElementById("userModal");
        const userContent = document.getElementById("modalContent");

        signupModal.classList.remove("modal-visible");
        signupModal.classList.add("hidden", "modal-hidden");

        userModal.classList.remove("hidden", "modal-hidden");
        userModal.classList.add("modal-visible");
        userModal.style.display = "flex";

        setTimeout(() => {
            userContent.classList.remove("scale-95", "opacity-0", "modal-content-hidden");
            userContent.classList.add("scale-100", "opacity-100", "modal-content-visible");
        }, 10);
    }, 100);
}

// ========================
// DOMContentLoaded - FULL SETUP
// ========================
document.addEventListener('DOMContentLoaded', function () {
    console.log("DOM loaded, setting up modals...");

    // === USER ICON CLICK: Login or Profile ===
    const userIcon = document.getElementById('userIcon');
    if (userIcon) {
        userIcon.addEventListener('click', () => {
            const token = localStorage.getItem('refreshToken') || sessionStorage.getItem('refreshToken');
            if (token) {
                openProfileModal();
            } else {
                openUserModal();
            }
        });
        console.log("User icon event bound");
    }

    // === BACKDROP CLICK TO CLOSE MODAL ===
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                const closeFn = {
                    userModal: closeUserModal,
                    signupModal: closeSignupModal,
                    profileModal: closeProfileModal,
                    forgotPasswordModal: closeForgotPasswordModal,
                    otpModal: closeOtpModal
                }[modal.id];
                if (closeFn) closeFn();
            }
        });
    });
    console.log("Modal backdrop events bound");

    // === CLOSE BUTTONS ===
    const closeButtons = document.querySelectorAll('.close');
    closeButtons.forEach(button => {
        button.addEventListener('click', () => {
            const modal = button.closest('.modal');
            const closeFn = {
                userModal: closeUserModal,
                signupModal: closeSignupModal,
                profileModal: closeProfileModal,
                forgotPasswordModal: closeForgotPasswordModal,
                otpModal: closeOtpModal
            }[modal.id];
            if (closeFn) closeFn();
        });
    });
    console.log("Close buttons events bound");

    // === SWITCH BETWEEN LOGIN & SIGNUP ===
    const switchToSignupBtn = document.getElementById('switchToSignup');
    if (switchToSignupBtn) {
        switchToSignupBtn.addEventListener('click', switchToSignup);
        console.log("Switch to signup event bound");
    }

    const switchToLoginBtn = document.getElementById('switchToLogin');
    if (switchToLoginBtn) {
        switchToLoginBtn.addEventListener('click', switchToLogin);
        console.log("Switch to login event bound");
    }

    // === LOGIN FORM ===
    // const loginForm = document.getElementById('loginForm');
    // if (loginForm) {
    //     loginForm.addEventListener('submit', async function (e) {
    //         e.preventDefault();
    //         console.log("Login form submitted");
    //
    //         const emailOrPhone = document.querySelector('input[name="emailOrPhone"]').value.trim();
    //         const password = document.querySelector('input[name="password"]').value;
    //         const rememberMe = document.querySelector('input[name="rememberMe"]').checked;
    //
    //         if (!emailOrPhone || !password) {
    //             alert("Vui lòng nhập đầy đủ thông tin!");
    //             return;
    //         }
    //
    //         const credentials = { emailOrPhone, password, rememberMe };
    //
    //         try {
    //             const response = await fetch('/api/auth/login', {
    //                 method: 'POST',
    //                 headers: { 'Content-Type': 'application/json' },
    //                 body: JSON.stringify(credentials)
    //             });
    //
    //             if (!response.ok) {
    //                 const error = await response.text();
    //                 throw new Error(error || 'Đăng nhập thất bại');
    //             }
    //
    //             const result = await response.json();
    //             const user = result.data;
    //
    //             if (!user || !user.refreshToken) {
    //                 throw new Error("Không nhận được thông tin người dùng");
    //             }
    //
    //             // Lưu vào storage phù hợp
    //             const storage = rememberMe ? localStorage : sessionStorage;
    //             storage.setItem('refreshToken', user.refreshToken);
    //             storage.setItem('currentUser', JSON.stringify(user));
    //
    //             alert("Đăng nhập thành công!");
    //
    //             closeUserModal();
    //
    //             // CHUYỂN HƯỚNG THEO VAI TRÒ
    //             const roleName = user.role?.name?.toUpperCase();
    //             if (roleName === 'ADMIN') {
    //                 window.location.href = '/admin/dashboard';
    //             } else {
    //                 window.location.href = '/';
    //             }
    //
    //         } catch (error) {
    //             console.error('Login error:', error);
    //             alert('Đăng nhập thất bại: ' + error.message);
    //         }
    //     });
    //     console.log("Login form event bound");
    // }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            console.log("Login form submitted");

            const emailOrPhone = document.querySelector('input[name="emailOrPhone"]').value.trim();
            const password = document.querySelector('input[name="password"]').value;
            const rememberMe = document.querySelector('input[name="rememberMe"]').checked;

            if (!emailOrPhone || !password) {
                alert("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            const credentials = { emailOrPhone, password, rememberMe };

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(credentials)
                });

                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error || 'Đăng nhập thất bại');
                }

                const result = await response.json();
                const data = result.data;

                const user = data.user;
                const redirectUrl = data.redirectUrl || '/';

                if (!user || !user.refreshToken) {
                    throw new Error("Không nhận được thông tin người dùng");
                }

                // Lưu vào storage
                const storage = rememberMe ? localStorage : sessionStorage;
                storage.setItem('refreshToken', user.refreshToken);
                storage.setItem('currentUser', JSON.stringify(user));

                alert("Đăng nhập thành công!");
                closeUserModal();

                // Backend quyết định redirect
                window.location.href = redirectUrl;

            } catch (error) {
                console.error('Login error:', error);
                alert('Đăng nhập thất bại: ' + error.message);
            }
        });
    }

    // === SIGNUP FORM ===
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            console.log("Signup form submitted");

            const formData = getSignupFormData(signupForm);

            if (formData.password !== formData.confirmPassword) {
                alert("Mật khẩu xác nhận không khớp!");
                return;
            }

            try {
                const response = await fetch('/api/auth/signup', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });

                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error || 'Đăng ký thất bại');
                }

                alert("Đăng ký thành công! Vui lòng đăng nhập.");
                switchToLogin();

            } catch (error) {
                console.error('Signup error:', error);
                alert('Đăng ký thất bại: ' + error.message);
            }
        });
        console.log("Signup form event bound");
    }

    // === LOGOUT ===
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function () {
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('currentUser');
            sessionStorage.removeItem('refreshToken');
            sessionStorage.removeItem('currentUser');

            alert("Đăng xuất thành công!");
            closeProfileModal();
            window.location.href = '/';
        });
        console.log("Logout event bound");
    }

    // === FORGOT PASSWORD FLOW ===
    // const forgotPasswordBtn = document.getElementById('forgotPassword');
    // if (forgotPasswordBtn) {
    //     forgotPasswordBtn.addEventListener('click', function (e) {
    //         e.preventDefault();
    //         switchModal("userModal", "modalContent", "forgotPasswordModal", "forgotPasswordModalContent");
    //     });
    //     console.log("Forgot password button event bound");
    // }

    function closeForgotPasswordModal() {
        console.log("Closing forgot password modal...");
        closeModal("forgotPasswordModal", "forgotPasswordModalContent");
    }

    function closeOtpModal() {
        console.log("Closing OTP modal...");
        closeModal("otpModal", "otpModalContent");
        if (window.otpTimer) clearInterval(window.otpTimer);
    }

    function closeModal(modalId, contentId) {
        const modal = document.getElementById(modalId);
        const content = document.getElementById(contentId);
        content.classList.remove("scale-100", "opacity-100", "modal-content-visible");
        content.classList.add("scale-95", "opacity-0", "modal-content-hidden");
        setTimeout(() => {
            modal.classList.remove("modal-visible");
            modal.classList.add("hidden", "modal-hidden");
            console.log(`${modalId} fully closed`);
        }, 150);
    }

    function switchModal(fromModalId, fromContentId, toModalId, toContentId) {
        const fromContent = document.getElementById(fromContentId);
        fromContent.classList.remove("scale-100", "opacity-100", "modal-content-visible");
        fromContent.classList.add("scale-95", "opacity-0", "modal-content-hidden");

        setTimeout(() => {
            const fromModal = document.getElementById(fromModalId);
            const toModal = document.getElementById(toModalId);
            const toContent = document.getElementById(toContentId);

            fromModal.classList.remove("modal-visible");
            fromModal.classList.add("hidden", "modal-hidden");

            toModal.classList.remove("hidden", "modal-hidden");
            toModal.classList.add("modal-visible");
            toModal.style.display = "flex";

            setTimeout(() => {
                toContent.classList.remove("scale-95", "opacity-0", "modal-content-hidden");
                toContent.classList.add("scale-100", "opacity-100", "modal-content-visible");
            }, 10);
        }, 100);
    }

    // === OTP TIMER ===
    function startOtpTimer() {
        let timeLeft = 60;
        const resendBtn = document.getElementById("resendOtp");
        resendBtn.disabled = true;
        resendBtn.textContent = `Gửi lại sau (${timeLeft}s  )`;

        window.otpTimer = setInterval(() => {
            timeLeft--;
            resendBtn.textContent = `Gửi lại sau (${timeLeft}s  )`;
            if (timeLeft <= 0) {
                clearInterval(window.otpTimer);
                resendBtn.disabled = false;
                resendBtn.textContent = "Gửi lại OTP";
            }
        }, 1000);
    }

    // === FORGOT PASSWORD FORM ===
    const forgotPasswordForm = document.getElementById("forgotPasswordForm");
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const emailOrPhone = document.getElementById("forgotEmailOrPhone").value.trim();
            if (!emailOrPhone) {
                alert("Vui lòng nhập email hoặc số điện thoại!");
                return;
            }
            switchModal("forgotPasswordModal", "forgotPasswordModalContent", "otpModal", "otpModalContent");
            startOtpTimer();
            console.log("OTP sent to:", emailOrPhone);
        });
        console.log("Forgot password form event bound");
    }

    // === BACK TO LOGIN FROM FORGOT ===
    const backToLoginBtn = document.getElementById("backToLogin");
    if (backToLoginBtn) {
        backToLoginBtn.addEventListener("click", function (e) {
            e.preventDefault();
            switchModal("forgotPasswordModal", "forgotPasswordModalContent", "userModal", "modalContent");
        });
    }

    // === RESEND OTP ===
    const resendOtpBtn = document.getElementById("resendOtp");
    if (resendOtpBtn) {
        resendOtpBtn.addEventListener("click", function (e) {
            e.preventDefault();
            if (!resendOtpBtn.disabled) {
                startOtpTimer();
                alert("Mã OTP mới đã được gửi!");
            }
        });
    }

    // === CHANGE EMAIL/PHONE ===
    const changeEmailOrPhoneBtn = document.getElementById("changeEmailOrPhone");
    if (changeEmailOrPhoneBtn) {
        changeEmailOrPhoneBtn.addEventListener("click", function (e) {
            e.preventDefault();
            switchModal("otpModal", "otpModalContent", "forgotPasswordModal", "forgotPasswordModalContent");
            if (window.otpTimer) clearInterval(window.otpTimer);
        });
    }

    // === OTP FORM ===
    const otpForm = document.getElementById("otpForm");
    if (otpForm) {
        otpForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const otpCode = document.getElementById("otpCode").value.trim();

            if (!otpCode || otpCode.length !== 6) {
                alert("Vui lòng nhập đầy đủ 6 chữ số OTP!");
                return;
            }

            if (otpCode === "123456") {
                alert("Xác thực OTP thành công! Mật khẩu mới đã được gửi đến email/SMS của bạn.");
                switchModal("otpModal", "otpModalContent", "userModal", "modalContent");
                if (window.otpTimer) clearInterval(window.otpTimer);
            } else {
                alert("Mã OTP không đúng! Vui lòng thử lại.");
            }
        });
        console.log("OTP form event bound");
    }
// Thêm sự kiện cho nút "Đăng ký ngay"
    const signupBtn = document.getElementById('signupBtn');
    if (signupBtn) {
        signupBtn.addEventListener('click', switchToSignup);
        console.log("Signup button event bound (patched)");
    }

// Thêm sự kiện cho nút "Quên mật khẩu"
    const forgotPasswordBtn = document.getElementById('forgotPasswordBtn');
    if (forgotPasswordBtn) {
        forgotPasswordBtn.addEventListener('click', function (e) {
            e.preventDefault();
            switchModal("userModal", "modalContent", "forgotPasswordModal", "forgotPasswordModalContent");
            console.log("Forgot password button event bound (patched)");
        });
    }

    // Thêm sự kiện cho nút đóng modal Login
    const closeUserModalBtn = document.getElementById('closeUserModal');
    if (closeUserModalBtn) {
        closeUserModalBtn.addEventListener('click', closeUserModal);
        console.log("Close user modal button event bound (patched)");
    }

// Thêm sự kiện cho nút đóng modal Register
    const closeSignupModalBtn = document.getElementById('closeSignupModal');
    if (closeSignupModalBtn) {
        closeSignupModalBtn.addEventListener('click', closeSignupModal);
        console.log("Close signup modal button event bound (patched)");
    }

// Thêm sự kiện cho nút đóng modal Forgot Password
    const closeForgotPasswordModalBtn = document.getElementById('closeForgotPasswordModal');
    if (closeForgotPasswordModalBtn) {
        closeForgotPasswordModalBtn.addEventListener('click', closeForgotPasswordModal);
        console.log("Close forgot password modal button event bound (patched)");
    }

    console.log("Modal setup completed! (Full version loaded)");
});

