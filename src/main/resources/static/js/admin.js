/**
 * 訪客登記系統管理後台 JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // 初始化各模組
    DateTimeModule.init();
    FormValidationModule.init();
    DeleteConfirmModule.init();
});

/**
 * 日期時間處理模組
 */
const DateTimeModule = {
    // 時間輸入元素
    startTimeInput: null,
    endTimeInput: null,
    
    /**
     * 初始化日期時間模組
     */
    init: function() {
        this.startTimeInput = document.getElementById('startTime');
        this.endTimeInput = document.getElementById('endTime');
        
        if (this.startTimeInput || this.endTimeInput) {
            this.setupDefaultValues();
            this.addTimeRangeValidation();
        }
    },
    
    /**
     * 設置開始和結束時間的默認值
     */
    setupDefaultValues: function() {
        if (this.startTimeInput && !this.startTimeInput.value) {
            // 開始時間：當前時間往前一小時
            const oneHourAgo = new Date();
            oneHourAgo.setHours(oneHourAgo.getHours() - 1);
            this.startTimeInput.value = this.formatDateTimeForInput(oneHourAgo);
        }
        
        if (this.endTimeInput && !this.endTimeInput.value) {
            // 結束時間：當前時間
            const now = new Date();
            this.endTimeInput.value = this.formatDateTimeForInput(now);
        }
    },
    
    /**
     * 格式化日期時間為輸入欄位格式
     */
    formatDateTimeForInput: function(date) {
        return date.getFullYear() + '-' + 
               String(date.getMonth() + 1).padStart(2, '0') + '-' +
               String(date.getDate()).padStart(2, '0') + 'T' +
               String(date.getHours()).padStart(2, '0') + ':' +
               String(date.getMinutes()).padStart(2, '0') + ':' +
               String(date.getSeconds()).padStart(2, '0');
    },
    
    /**
     * 添加時間範圍驗證
     */
    addTimeRangeValidation: function() {
        // 添加表單提交驗證
        const searchForm = document.querySelector('form[action*="/admin/search"]');
        if (searchForm) {
            searchForm.addEventListener('submit', this.validateTimeRangeOnSubmit.bind(this));
        }
        
        // 添加時間輸入變更事件監聽器
        if (this.startTimeInput && this.endTimeInput) {
            this.startTimeInput.addEventListener('change', this.validateTimeRange.bind(this));
            this.endTimeInput.addEventListener('change', this.validateTimeRange.bind(this));
        }
    },
    
    /**
     * 表單提交時驗證時間範圍
     */
    validateTimeRangeOnSubmit: function(event) {
        if (this.startTimeInput && this.endTimeInput) {
            const startTime = new Date(this.startTimeInput.value);
            const endTime = new Date(this.endTimeInput.value);
            
            // 確保開始時間小於結束時間
            if (startTime > endTime) {
                event.preventDefault();
                alert('開始時間必須小於結束時間！');
                return false;
            }
        }
    },
    
    /**
     * 即時驗證時間範圍
     */
    validateTimeRange: function() {
        if (this.startTimeInput && this.endTimeInput && 
            this.startTimeInput.value && this.endTimeInput.value) {
            
            const startTime = new Date(this.startTimeInput.value);
            const endTime = new Date(this.endTimeInput.value);
            
            if (startTime > endTime) {
                UIHelper.showError(this.startTimeInput, '開始時間必須小於結束時間');
                UIHelper.showError(this.endTimeInput, '結束時間必須大於開始時間');
            } else {
                UIHelper.clearError(this.startTimeInput);
                UIHelper.clearError(this.endTimeInput);
            }
        }
    }
};

/**
 * 表單驗證模組
 */
const FormValidationModule = {
    /**
     * 初始化表單驗證
     */
    init: function() {
        this.setupRecipientFormValidation();
    },
    
    /**
     * 設置收件人表單驗證
     */
    setupRecipientFormValidation: function() {
        const recipientForm = document.querySelector('form[action*="/recipients/add"]');
        if (recipientForm) {
            recipientForm.addEventListener('submit', this.validateRecipientForm);
        }
    },
    
    /**
     * 驗證收件人表單
     */
    validateRecipientForm: function(event) {
        let isValid = true;
        
        // 姓名驗證
        const nameInput = document.getElementById('name');
        if (nameInput && nameInput.value.trim() === '') {
            UIHelper.showError(nameInput, '請輸入姓名');
            isValid = false;
        } else if (nameInput) {
            UIHelper.clearError(nameInput);
        }
        
        // 電子郵件驗證
        const emailInput = document.getElementById('email');
        if (emailInput) {
            if (emailInput.value.trim() === '') {
                UIHelper.showError(emailInput, '請輸入電子郵件');
                isValid = false;
            } else if (!ValidationUtil.isValidEmail(emailInput.value)) {
                UIHelper.showError(emailInput, '請輸入有效的電子郵件地址');
                isValid = false;
            } else {
                UIHelper.clearError(emailInput);
            }
        }
        
        if (!isValid) {
            event.preventDefault();
        }
    }
};

/**
 * 刪除確認模組
 */
const DeleteConfirmModule = {
    /**
     * 初始化刪除確認功能
     */
    init: function() {
        const deleteButtons = document.querySelectorAll('button[onclick*="confirm"]');
        deleteButtons.forEach(button => {
            button.addEventListener('click', this.confirmDelete);
        });
    },
    
    /**
     * 確認刪除操作
     */
    confirmDelete: function(event) {
        if (!confirm('確定要執行此操作嗎？')) {
            event.preventDefault();
        }
    }
};

/**
 * 驗證工具
 */
const ValidationUtil = {
    /**
     * 驗證電子郵件
     */
    isValidEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
};

/**
 * UI 幫助類
 */
const UIHelper = {
    /**
     * 顯示錯誤訊息
     */
    showError: function(input, message) {
        const formGroup = input.closest('.form-group');
        const errorElement = formGroup.querySelector('small');
        
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        } else {
            const small = document.createElement('small');
            small.className = 'text-danger';
            small.textContent = message;
            formGroup.appendChild(small);
        }
        
        input.classList.add('is-invalid');
    },
    
    /**
     * 清除錯誤訊息
     */
    clearError: function(input) {
        const formGroup = input.closest('.form-group');
        const errorElement = formGroup.querySelector('small');
        
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }
        
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
    }
};