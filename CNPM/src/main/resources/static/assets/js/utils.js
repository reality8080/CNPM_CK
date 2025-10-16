// src/main/resources/static/assets/js/utils.js

const Format = {
    currency(value) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(value || 0);
    },

    date(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        return date.toLocaleDateString('vi-VN');
    },

    status(status) {
        const statusMap = {
            'DangXuLy': '<span class="badge bg-warning">Đang xử lý</span>',
            'DaNhap': '<span class="badge bg-success">Đã nhập</span>',
            'DaXuat': '<span class="badge bg-success">Đã xuất</span>',
            'DangKiem': '<span class="badge bg-info">Đang kiểm</span>',
            'HoanThanh': '<span class="badge bg-success">Hoàn thành</span>',
            'ChuaXuLy': '<span class="badge bg-warning">Chưa xử lý</span>',
            'DaXuLy': '<span class="badge bg-success">Đã xử lý</span>'
        };
        return statusMap[status] || status;
    }
};

const Table = {
    render(tableId, data, columns) {
        const tbody = $(`#${tableId} tbody`);
        tbody.empty();

        if (data.length === 0) {
            tbody.html(`<tr><td colspan="${columns.length}" class="text-center">Không có dữ liệu</td></tr>`);
            return;
        }

        data.forEach(row => {
            const tr = $('<tr>');
            columns.forEach(col => {
                const td = $('<td>');
                if (col.field) {
                    td.text(row[col.field] || '');
                } else if (col.render) {
                    td.html(col.render(row));
                }
                tr.append(td);
            });
            tbody.append(tr);
        });
    }
};

const Toast = {
    show(message, type = 'info') {
        const toastHtml = `
            <div class="toast align-items-center text-white bg-${type} border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `;
        
        let container = $('.toast-container');
        if (container.length === 0) {
            container = $('<div class="toast-container"></div>');
            $('body').append(container);
        }
        
        const toastEl = $(toastHtml);
        container.append(toastEl);
        
        const toast = new bootstrap.Toast(toastEl[0]);
        toast.show();
        
        toastEl.on('hidden.bs.toast', () => toastEl.remove());
    },

    success(message) {
        this.show(message, 'success');
    },

    error(message) {
        this.show(message, 'danger');
    },

    warning(message) {
        this.show(message, 'warning');
    },

    info(message) {
        this.show(message, 'info');
    }
};

const Modal = {
    confirm(title, message, onConfirm) {
        const modalHtml = `
            <div class="modal fade" id="confirmModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${title}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">${message}</div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="button" class="btn btn-primary" id="confirmBtn">Xác nhận</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#confirmModal').remove();
        $('body').append(modalHtml);
        
        const modal = new bootstrap.Modal($('#confirmModal')[0]);
        modal.show();
        
        $('#confirmBtn').on('click', () => {
            modal.hide();
            onConfirm();
        });
        
        $('#confirmModal').on('hidden.bs.modal', () => $('#confirmModal').remove());
    },

    showImage(src, title) {
        if (!src) {
            Toast.warning('Không có hình ảnh');
            return;
        }
        
        const modalHtml = `
            <div class="modal fade" id="imageModal" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${title || 'Hình ảnh'}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body text-center">
                            <img src="${src}" class="img-fluid">
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('#imageModal').remove();
        $('body').append(modalHtml);
        
        const modal = new bootstrap.Modal($('#imageModal')[0]);
        modal.show();
        
        $('#imageModal').on('hidden.bs.modal', () => $('#imageModal').remove());
    }
};