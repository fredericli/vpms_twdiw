/**
 * 訪客登記系統前台 JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // 初始化模組
    FormValidationModule.init();
});

/**
 * 表單驗證模組
 */
const FormValidationModule = {
    /**
     * 初始化表單驗證
     */
    init: function() {
        this.setupRegistrationFormValidation();
    },
    
    /**
     * 設置訪客登記表單驗證
     */
    setupRegistrationFormValidation: function() {
        const registrationForm = document.querySelector('form');
        if (registrationForm) {
            registrationForm.addEventListener('submit', this.validateRegistrationForm);
        }
    },
    
    /**
     * 驗證訪客登記表單
     */
    validateRegistrationForm: function(event) {
        let isValid = true;
        
        // 公司或部門驗證
        isValid = FormValidationModule.validateCompanyName() && isValid;
        
        // 部門驗證
        isValid = FormValidationModule.validateDepartment() && isValid;
        
        // 姓名驗證
        isValid = FormValidationModule.validateName() && isValid;
        
        // 電話驗證
        isValid = FormValidationModule.validatePhone() && isValid;
        
        if (!isValid) {
            event.preventDefault();
        }
    },
    
    /**
     * 驗證公司或部門
     */
    validateCompanyName: function() {
        const companyNameInput = document.getElementById('companyName');
        if (companyNameInput && companyNameInput.value.trim() === '') {
            UIHelper.showError(companyNameInput, '請輸入公司或部門');
            return false;
        } else if (companyNameInput) {
            UIHelper.clearError(companyNameInput);
            return true;
        }
        return true;
    },
    
    /**
     * 驗證部門
     */
    validateDepartment: function() {
        const departmentInput = document.getElementById('department');
        if (departmentInput && departmentInput.value.trim() === '') {
            UIHelper.showError(departmentInput, '請輸入部門');
            return false;
        } else if (departmentInput) {
            UIHelper.clearError(departmentInput);
            return true;
        }
        return true;
    },
    
    /**
     * 驗證姓名
     */
    validateName: function() {
        const nameInput = document.getElementById('name');
        if (nameInput && nameInput.value.trim() === '') {
            UIHelper.showError(nameInput, '請輸入姓名');
            return false;
        } else if (nameInput) {
            UIHelper.clearError(nameInput);
            return true;
        }
        return true;
    },
    
    /**
     * 驗證電話
     */
    validatePhone: function() {
        const phoneInput = document.getElementById('phone');
        if (phoneInput) {
            if (phoneInput.value.trim() === '') {
                UIHelper.showError(phoneInput, '請輸入聯絡電話');
                return false;
            } else if (!ValidationUtil.isValidPhone(phoneInput.value)) {
                UIHelper.showError(phoneInput, '請輸入有效的電話號碼');
                return false;
            } else {
                UIHelper.clearError(phoneInput);
                return true;
            }
        }
        return true;
    }
};

/**
 * 驗證工具
 */
const ValidationUtil = {
    /**
     * 驗證電話號碼
     */
    isValidPhone: function(phone) {
        // 允許數字、+、-、()和空格，長度在8到20之間
        const phoneRegex = /^[0-9+\-\s()]{8,20}$/;
        return phoneRegex.test(phone);
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