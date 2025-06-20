$(document).ready(function() {
    // 獲取當前應用的context path
    const contextPath = document.querySelector('meta[name="context-path"]').getAttribute('content');
    
    // 全局計時器變量，用於清除定時器
    window.cardTimers = {
        pollStatusTimer: null,
        countdownTimer: null
    };
    
    // 定義輪詢相關變數
    let pollInterval = null;
    let transactionId = null;
    const POLL_DELAY = 5000; // 5秒輪詢一次
    
    // 表單提交處理
    $('#visitorCardForm').on('submit', function(e) {
        e.preventDefault();
        
        // 重置驗證狀態
        $(this).find('.is-invalid').removeClass('is-invalid');
        
        // 獲取表單數據
        const formData = {
            companyName: $('#cardCompanyName').val().trim(),
            name: $('#cardName').val().replace(/\s+/g, '_'),
            email: $('#cardEmail').val().trim(),                    
            jobTitle: $('#cardJobTitle').val().trim(),
            cellphone: $('#cardCellphone').val().trim()
        };
        
        // 簡單的前端驗證
        let isValid = true;
        
        // 檢查必填欄位
        if (!formData.companyName) {
            $('#cardCompanyName').addClass('is-invalid');
            isValid = false;
        }
        
        if (!formData.name) {
            $('#cardName').addClass('is-invalid');
            isValid = false;
        }
        
        // 只有在有輸入電子郵件時才進行驗證
        if (formData.email && !isValidEmail(formData.email)) {
            $('#cardEmail').addClass('is-invalid');
            isValid = false;
        }
        
        if (!formData.jobTitle) {
            $('#cardJobTitle').addClass('is-invalid');
            isValid = false;
        }
        
        if (!formData.cellphone) {
            $('#cardCellphone').addClass('is-invalid');
            isValid = false;
        }
        
        if (!isValid) return;
        
        // 在提交前顯示載入中訊息
        showResult('info', '處理中，請稍候...');
        
        // 發送AJAX請求，加上context path
        $.ajax({
            url: contextPath + 'api/visitor/card',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                showResult('success', '訪客領卡申請已提交成功！');
                
                // 如果返回卡片信息，顯示卡片結果
                if (response.cardInfo) {
                    showCardResult(response.cardInfo);
                    
                    // 儲存 transactionId 並開始輪詢掃描狀態
                    if (response.cardInfo.transactionId) {
                        transactionId = response.cardInfo.transactionId;
                        startPolling(transactionId);
                    }
                }
            },
            error: function(xhr) {
                let errorMessage = '提交失敗，請稍後再試。';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                showResult('danger', errorMessage);
            }
        });
    });
    
    // 開始輪詢 QR 碼掃描狀態
    function startPolling(id) {
        // 清除可能存在的先前輪詢
        if (pollInterval) {
            clearInterval(pollInterval);
        }
        
        // 設置狀態提示
        $('#scan-status-message').html('<span class="text-muted">等待掃描 QR 碼...</span>');
        
        // 設置輪詢間隔
        pollInterval = setInterval(function() {
            checkScanStatus(id);
        }, POLL_DELAY);
    }
    
    // 停止輪詢
    function stopPolling() {
        if (pollInterval) {
            clearInterval(pollInterval);
            pollInterval = null;
        }
    }
    
    // 檢查掃描狀態
    function checkScanStatus(id) {
        $.ajax({
            url: contextPath + 'api/visitor/card/scan-status/' + id,
            type: 'GET',
            success: function(response) {
                console.log('掃描狀態:', response);
                
                if (response.status === 'scanned') {
                    // 掃描成功，停止輪詢
                    stopPolling();
                    
                    // 更新狀態提示
                    $('#scan-status-message').html('<span class="text-success"><strong>✓ QR 碼已成功掃描！</strong></span>');
                    
                    // 加亮 QR 碼區域
                    $('#card-qrcode-container').addClass('border-success');
                    
                    // 顯示成功訊息
                    showResult('success', '訪客卡已成功掃描並發行！');
                    
                    // 短暫延遲後自動跳轉到訪客登記標籤並重置領卡表單
                    setTimeout(function() {
                        // 切換到訪客登記標籤
                        $('#register-tab').tab('show');
                        
                        // 重置訪客領卡表單
                        resetCardForm();
                        
                        // 顯示資料在訪客登記頁
                        // displayScanResultData(response);
                        
                        // 顯示成功訊息提示
                        showResult('success', '訪客卡已成功發行並領取！');
                        
                        // 2秒後重新初始化 QR 碼
                        setTimeout(function() {
                            // 使用重置方法重新生成 QR Code
                            if (QRCodeModule && typeof QRCodeModule.reset === 'function') {
                                QRCodeModule.reset();
                            }
                        }, 2000);
                    }, 2000);  // 2秒後跳轉
                } else if (response.status === 'error') {
                    // 發生錯誤，停止輪詢
                    stopPolling();
                    
                    // 更新狀態提示
                    $('#scan-status-message').html('<span class="text-danger"><strong>✗ 檢查掃描狀態時發生錯誤</strong></span>');
                    
                    // 顯示錯誤訊息
                    showResult('danger', response.message || '檢查掃描狀態時發生錯誤');
                }
                // 如果是 pending 狀態，繼續輪詢，不做特別處理
            },
            error: function(xhr) {
                console.error('檢查掃描狀態錯誤:', xhr);
                
                // 不停止輪詢，繼續嘗試
                $('#scan-status-message').html('<span class="text-warning"><strong>! 檢查掃描狀態時暫時無法連接</strong></span>');
            }
        });
    }
    
    // 重設訪客領卡表單
    function resetCardForm() {
        // 停止掃描狀態輪詢
        stopPolling();
        
        // 隱藏卡片結果
        $('#cardResult').addClass('d-none');
        
        // 重置 QR 碼容器的樣式
        $('#card-qrcode-container').removeClass('border-success');
        
        // 清空掃描狀態訊息
        $('#scan-status-message').empty();
        
        // 重置訪客領卡表單
        $('#visitorCardForm')[0].reset();
        
        // 顯示表單
        $('#visitorCardForm').show();
    }
    
    // 重設表單按鈕點擊處理
    $('#resetFormBtn').on('click', function() {
        resetCardForm();
    });
    
    // 顯示結果訊息
    function showResult(type, message) {
        const resultDiv = $('#formResult');
        resultDiv.removeClass('d-none alert-success alert-danger alert-info')
                 .addClass('alert-' + type)
                 .text(message)
                 .show();
        
        // 如果不是資訊類型的訊息，3秒後自動隱藏
        if (type !== 'info') {
            setTimeout(function() {
                resultDiv.fadeOut('slow', function() {
                    $(this).addClass('d-none');
                });
            }, 3000);
        }
    }
    
    // 顯示卡片結果
    function showCardResult(cardInfo) {
        // 隱藏表單
        $('#visitorCardForm').hide();
        
        // 清空並顯示二維碼
        const qrcodeDiv = $('#card-qrcode');
        qrcodeDiv.empty();
        
        // 如果有QR碼，顯示QR碼
        if (cardInfo.qrCode) {
            const img = $('<img>')
                .attr('src', cardInfo.qrCode)  // 直接使用服務器返回的 QR 碼數據
                .attr('alt', '訪客卡QR碼')
                .addClass('img-fluid')
                .css({
                    'max-width': '200px', 
                    'max-height': '200px',
                    'width': 'auto',
                    'height': 'auto'
                });
            qrcodeDiv.append(img);
        } else {
            qrcodeDiv.html('<div class="alert alert-warning">無法生成二維碼</div>');
        }
        
        // 如果有深度連結，設置深度連結按鈕
        if (cardInfo.deepLink) {
            $('#deepLinkBtn').attr('href', cardInfo.deepLink);
        } else {
            $('#deepLinkBtn').attr('disabled', true).text('深度連結不可用');
        }
        
        // 顯示結果區域
        $('#cardResult').removeClass('d-none');
    }
    
    // 電子郵件格式驗證
    function isValidEmail(email) {
        const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        return emailRegex.test(email);
    }
    
    // 顯示掃描結果資料
    function displayScanResultData(data) {
        const resultDataDiv = $('#result-data-content');
        resultDataDiv.empty();
        
        // 顯示資料區域
        $('#scan-result-data').removeClass('d-none');
        
        // 創建結果卡片
        const resultCard = $('<div class="scan-result-card"></div>');
        
        // 卡片標題
        resultCard.append('<h4>QR碼已被掃描！</h4>');
        resultCard.append('<p>從數位憑證皮夾獲取的資料：</p>');
        
        // 卡片內容
        const contentDiv = $('<div class="scan-result-content"></div>');
        
        // 收集需要顯示的資料項目
        const displayItems = [];
        
        // 檢查憑證資料
        if (data.resultInfo && data.resultInfo.credential) {
            // 解析憑證對象
            let credential = data.resultInfo.credential;
            if (typeof credential === 'string') {
                try {
                    credential = JSON.parse(credential);
                } catch (e) {
                    // 保持原始字串
                }
            }
            
            // 如果是物件且有 credentialType 屬性
            if (typeof credential === 'object' && credential !== null) {
                if (credential.credentialType) {
                    displayItems.push({
                        label: '憑證類型',
                        value: credential.credentialType
                    });
                }
                
                // 處理 claims 陣列
                if (credential.claims && Array.isArray(credential.claims)) {
                    credential.claims.forEach(claim => {
                        if (claim.cname && claim.value) {
                            displayItems.push({
                                label: claim.cname,
                                value: claim.value
                            });
                        }
                    });
                }
            }
        }
        
        // 如果沒有找到憑證資料，顯示狀態和訊息
        if (displayItems.length === 0) {
            if (data.status) {
                displayItems.push({
                    label: '狀態',
                    value: data.status
                });
            }
            
            if (data.message) {
                displayItems.push({
                    label: '訊息',
                    value: data.message
                });
            }
        }
        
        // 創建資料列表
        if (displayItems.length > 0) {
            const list = $('<ul class="credential-list"></ul>');
            
            displayItems.forEach(item => {
                list.append(`<li><strong>${item.label}:</strong> ${item.value}</li>`);
            });
            
            contentDiv.append(list);
        } else {
            contentDiv.append('<p>無法解析憑證資料</p>');
        }
        
        // 將內容添加到卡片
        resultCard.append(contentDiv);
        
        // 添加底部感謝訊息
        resultCard.append('<div class="scan-result-footer">感謝您的使用！</div>');
        
        // 添加倒數計時提示
        resultCard.append(`<div class="alert alert-info mt-3">
            <span id="redirect-countdown">2</span> 秒後將重新顯示QR碼掃描頁面...
        </div>`);
        
        // 將卡片添加到結果區域
        resultDataDiv.append(resultCard);
        
        // 倒數計時
        let countdown = 2;
        const countdownInterval = setInterval(() => {
            countdown--;
            $('#redirect-countdown').text(countdown);
            if (countdown <= 0) {
                clearInterval(countdownInterval);
            }
        }, 1000);
    }
    
    // 標籤頁切換處理
    const registerTab = document.getElementById('register-tab');
    const cardTab = document.getElementById('card-tab');
    
    // 記錄當前活動的標籤
    let activeTab = 'register';  // 預設是訪客登記標籤
    
    // 監聽訪客登記標籤點擊
    if (registerTab) {
        registerTab.addEventListener('click', function() {
            console.log('切換到訪客登記標籤');
            
            // 如果當前已經是這個標籤則不做處理
            if (activeTab === 'register') return;
            
            // 更新當前活動標籤
            activeTab = 'register';
            
            // 停止訪客領卡頁面的所有定時查詢和檢查
            stopAllCardProcesses();
            
            // 清除掃描結果顯示區域
            $('#scan-result-data').addClass('d-none');
            $('#result-data-content').empty();
            
            // 重置 QR 碼顯示區域，顯示載入中動畫
            const qrcodeDisplay = document.getElementById('qrcode-display');
            if (qrcodeDisplay) {
                qrcodeDisplay.innerHTML = '<div class="loading"></div>';
            }
            
            // 初始化訪客登記頁面
            if (QRCodeModule && typeof QRCodeModule.reset === 'function') {
                // 使用重置方法重新生成 QR Code
                setTimeout(() => {
                    QRCodeModule.reset();
                }, 100);
            }
        });
    }
    
    // 監聽訪客領卡標籤點擊
    if (cardTab) {
        cardTab.addEventListener('click', function() {
            console.log('切換到訪客領卡標籤');
            
            // 如果當前已經是這個標籤則不做處理
            if (activeTab === 'card') return;
            
            // 更新當前活動標籤
            activeTab = 'card';
            
            // 停止訪客登記頁面的所有定時查詢和檢查
            if (TimerModule && typeof TimerModule.stopAllTimers === 'function') {
                TimerModule.stopAllTimers();
            }
            
            // 重置QR碼顯示區域
            const qrcodeDisplay = document.getElementById('qrcode-display');
            if (qrcodeDisplay) {
                qrcodeDisplay.innerHTML = '<div class="loading"></div>';
            }
        });
    }
    
    // 函數：停止訪客領卡頁面的所有進程
    function stopAllCardProcesses() {
        // 停止卡片QR碼的輪詢和計時器
        if (window.cardTimers.pollStatusTimer) {
            clearInterval(window.cardTimers.pollStatusTimer);
            window.cardTimers.pollStatusTimer = null;
        }
        if (window.cardTimers.countdownTimer) {
            clearInterval(window.cardTimers.countdownTimer);
            window.cardTimers.countdownTimer = null;
        }
        
        // 重置訪客領卡表單狀態
        const cardForm = document.getElementById('visitorCardForm');
        if (cardForm) {
            cardForm.reset();
            cardForm.style.display = 'block';
        }
        
        // 隱藏卡片結果區域
        const cardResult = document.getElementById('cardResult');
        if (cardResult) {
            cardResult.classList.add('d-none');
        }
        
        // 隱藏表單結果提示
        const formResult = document.getElementById('formResult');
        if (formResult) {
            formResult.classList.add('d-none');
            formResult.textContent = '';
        }
    }
}); 