document.addEventListener('DOMContentLoaded', function() {
    console.log('Contact form script loaded');

    // Khởi tạo EmailJS
    try {
        emailjs.init('9fZWqEKZOraoSN7nC'); // Thay bằng Public Key thực tế
        console.log('EmailJS initialized');
    } catch (error) {
        console.error('Failed to initialize EmailJS:', error);
        alert('Không thể khởi tạo dịch vụ gửi email. Vui lòng thử lại sau.');
        return;
    }

    const form = document.querySelector('form');
    if (!form) {
        console.error('Form not found!');
        alert('Không tìm thấy form liên hệ!');
        return;
    }
    console.log('Form found');

    form.addEventListener('submit', async function(event) {
        console.log('Submit event triggered');
        event.preventDefault();
        console.log('Prevented default form submission');

        const name = form.querySelector('input[type="text"]').value.trim();
        const email = form.querySelector('input[type="email"]').value.trim();
        const phone = form.querySelector('input[type="tel"]').value.trim();
        const message = form.querySelector('textarea').value.trim();

        if (!name || !email || !message) {
            console.log('Validation failed: Missing required fields');
            alert('Vui lòng điền đầy đủ họ tên, email và tin nhắn');
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            console.log('Validation failed: Invalid email');
            alert('Vui lòng nhập email hợp lệ');
            return;
        }

        const formData = { name, email, phone, message };
        console.log('Form data:', formData);

        const submitButton = form.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        submitButton.textContent = 'Đang gửi...';

        try {
            const response = await emailjs.send('service_s4e7wph', 'template_35io8x1', formData);
            console.log('Email sent successfully:', response.status, response.text);
            alert('Tin nhắn của bạn đã được gửi thành công!');
            form.reset();
        } catch (error) {
            console.error('Failed to send email:', error);
            alert('Đã có lỗi xảy ra khi gửi tin nhắn: ' + error.text || error.message);
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Gửi tin nhắn';
        }
    });
    console.log('Form event listener attached');
});