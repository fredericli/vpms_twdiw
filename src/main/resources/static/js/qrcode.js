/**
 * 數位憑證皮夾QR碼掃描頁面JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // 初始化各模組
    QRCodeModule.init();
});

/**
 * QR碼主模組
 */
const QRCodeModule = {
    // DOM元素
    elements: {
        qrcodeDisplay: null,
        qrcodeArea: null,
        refreshButton: null,
        countdownText: null,
        countdownProgress: null,
        appOpenButton: null,
        welcomeMessage: null
    },
    
    // 預設參數
    config: {
        defaultRef: '00000000_demo_01', // 初始預設值，稍後將從後端獲取
        qrCodeExpireMinutes: 5, // QR碼有效時間（分鐘）
        resultDisplaySeconds: 5, // 掃描結果顯示時間（秒）
        contextPath: '', // 應用程式的context path
        welcomeMessage: '歡迎來林次長室！123' // 預設歡迎訊息
    },
    
    // 狀態
    state: {
        checkResultTimer: null,
        currentTransactionId: null,
        countdownTimer: null,
        resultDisplayTimer: null,
        qrCodeGeneratedTime: null
    },
    
    /**
     * 初始化QR碼模組
     */
    init: function() {
        this.initElements();
        this.initConfig();
        this.getContextPath();
        this.bindEvents();
        this.fetchDefaultRef();
        this.fetchWelcomeMessage();
    },
    
    /**
     * 初始化DOM元素
     */
    initElements: function() {
        this.elements.qrcodeDisplay = document.getElementById('qrcode-display');
        this.elements.qrcodeArea = document.getElementById('qrcode-area');
        this.elements.refreshButton = document.getElementById('refresh-btn');
        this.elements.countdownText = document.getElementById('countdown-text');
        this.elements.countdownProgress = document.getElementById('countdown-progress');
        this.elements.appOpenButton = document.getElementById('app-open-btn');
        this.elements.welcomeMessage = document.getElementById('welcome-message');
    },
    
    /**
     * 初始化配置（將分鐘轉換為毫秒等）
     */
    initConfig: function() {
        this.config.qrCodeExpireMs = this.config.qrCodeExpireMinutes * 60 * 1000;
    },
    
    /**
     * 獲取應用程式的context path
     */
    getContextPath: function() {
        // 從meta標籤中獲取context path (將在HTML中設置)
        const metaContextPath = document.querySelector('meta[name="context-path"]');
        if (metaContextPath) {
            this.config.contextPath = metaContextPath.getAttribute('content') || '';
            console.log('從meta標籤獲取到context path:', this.config.contextPath);
        } else {
            // 從當前URL中提取context path
            const pathParts = window.location.pathname.split('/');
            if (pathParts.length > 1) {
                this.config.contextPath = '/' + pathParts[1] + '/';
                console.log('從URL中提取context path:', this.config.contextPath);
            }
        }
    },
    
    /**
     * 綁定事件
     */
    bindEvents: function() {
        if (this.elements.refreshButton) {
            this.elements.refreshButton.addEventListener('click', this.handleRefreshClick.bind(this));
        }
    },
    
    /**
     * 處理重新整理按鈕點擊
     */
    handleRefreshClick: function() {
        // 顯示載入中的動畫
        if (this.elements.qrcodeDisplay) {
            this.elements.qrcodeDisplay.innerHTML = '<div class="loading"></div>';
        }
        
        // 重置 authUri 顯示
        this.displayAuthUri(null, '重新生成中...');
        
        // 隱藏 APP 開啟按鈕
        if (this.elements.appOpenButton) {
            this.elements.appOpenButton.style.display = 'none';
        }
        
        // 停止先前的查詢定時器與倒數計時器
        TimerModule.stopAllTimers();
        
        // 移除已有的掃描結果顯示
        UIHelper.removeExistingScanResult();
        
        // 顯示QR碼區域
        UIHelper.showQRCodeArea(this.elements.qrcodeArea, this.elements.qrcodeDisplay);
        
        // 顯示倒數計時區域
        UIHelper.showCountdownArea();
        
        // 產生新的QR碼
        this.generateQRCode();
    },
    
    /**
     * 從後端獲取默認證件類型
     */
    fetchDefaultRef: function() {
        // 顯示載入中的動畫
        if (this.elements.qrcodeDisplay) {
            this.elements.qrcodeDisplay.innerHTML = '<div class="loading"></div>';
        }
        
        // 呼叫後端API獲取默認證件類型
        APIModule.fetchDefaultRef()
            .then(data => {
                if (data && data.defaultRef) {
                    // 使用後端提供的預設值
                    this.config.defaultRef = data.defaultRef;
                    console.log('從後端獲取的默認證件類型:', this.config.defaultRef);
                }
                // 生成QR碼
                this.generateQRCode();
            })
            .catch(error => {
                console.error('獲取默認證件類型失敗:', error);
                // 出錯時使用本地預設值並繼續生成QR碼
                this.generateQRCode();
            });
    },
    
    /**
     * 從後端獲取歡迎訊息
     */
    fetchWelcomeMessage: function() {
        APIModule.fetchWelcomeMessage()
            .then(data => {
                if (data && data.message) {
                    console.log('從後端獲取的歡迎訊息:', data.message);
                    this.config.welcomeMessage = data.message;
                    if (this.elements.welcomeMessage) {
                        this.elements.welcomeMessage.textContent = data.message;
                    }
                }
            })
            .catch(error => {
                console.error('獲取歡迎訊息失敗:', error);
                // 使用預設歡迎訊息
                if (this.elements.welcomeMessage) {
                    this.elements.welcomeMessage.textContent = this.config.welcomeMessage;
                }
            });
    },
    
    /**
     * 向後端API請求產生QR碼
     */
    generateQRCode: function() {
        // 顯示載入中的動畫
        if (this.elements.qrcodeDisplay) {
            this.elements.qrcodeDisplay.innerHTML = '<div class="loading"></div>';
        }
        
        // 產生隨機交易ID
        this.state.currentTransactionId = this.generateTransactionId();
        
        // 記錄QR碼產生時間
        this.state.qrCodeGeneratedTime = Date.now();
        
        // 呼叫後端API
        APIModule.generateQRCode(this.config.defaultRef, this.state.currentTransactionId)
            .then(data => {
                this.handleQRCodeResponse(data);
            })
            .catch(error => {
                this.handleQRCodeError(error);
            });
    },
    
    /**
     * 重置 QR 碼和相關狀態
     */
    reset: function() {
        // 停止所有定時器
        TimerModule.stopAllTimers();
        
        // 清除掃描結果顯示
        UIHelper.removeExistingScanResult();
        
        // 顯示載入中的動畫
        if (this.elements.qrcodeDisplay) {
            this.elements.qrcodeDisplay.innerHTML = '<div class="loading"></div>';
        }
        
        // 重置 authUri 顯示
        this.displayAuthUri(null, '重新生成中...');
        
        // 隱藏 APP 開啟按鈕
        if (this.elements.appOpenButton) {
            this.elements.appOpenButton.style.display = 'none';
        }
        
        // 重新顯示 QR 碼區域
        UIHelper.showQRCodeArea(this.elements.qrcodeArea, this.elements.qrcodeDisplay);
        
        // 重新生成 QR 碼
        this.generateQRCode();
    },
    
    /**
     * 處理QR碼回應
     */
    handleQRCodeResponse: function(data) {
        console.log('API回應資料:', data);
        
        // 檢查各種可能的資料格式
        if (data && data.data && data.data.qrcode) {
            // 格式1: {data: {qrcode: "base64"}} (舊格式)
            UIHelper.displayQRCode(data.data.qrcode, this.elements.qrcodeDisplay);
            
            // 顯示 authUri
            this.displayAuthUri(data);
            
            // 開始定時檢查結果與倒數計時
            this.startScanStatusChecking();
        } else if (data && data.qrcode_image) {
            // 格式2: {qrcode_image: "data:image/png;base64,..."} (新格式)
            UIHelper.displayQRCode(data.qrcode_image, this.elements.qrcodeDisplay);
            
            // 顯示 authUri
            this.displayAuthUri(data);
            
            // 開始定時檢查結果與倒數計時
            this.startScanStatusChecking();
        } else if (data && data.error) {
            // 錯誤回應格式
            console.error('API回傳錯誤:', data.error);
            this.displayAuthUri(null, '發生錯誤: ' + data.error);
            throw new Error(data.error);
        } else {
            console.error('QR碼資料格式錯誤:', data);
            this.displayAuthUri(null, 'QR碼資料格式錯誤或無效');
            throw new Error('QR碼資料格式錯誤或無效');
        }
    },
    
    /**
     * 處理QR碼生成錯誤
     */
    handleQRCodeError: function(error) {
        console.error('獲取QR碼失敗:', error);
        if (this.elements.qrcodeDisplay) {
            this.elements.qrcodeDisplay.innerHTML = `
                <div class="alert alert-danger">
                    獲取QR碼失敗，請點擊重新產生按鈕再試一次。<br>
                    <small>錯誤: ${error.message}</small>
                </div>
            `;
        }
        
        // 顯示錯誤信息
        this.displayAuthUri(null, '獲取QR碼失敗: ' + error.message);
        
        // 重置倒數計時
        CountdownModule.resetCountdown(this.elements.countdownText, this.elements.countdownProgress, this.config.qrCodeExpireMinutes);
    },
    
    /**
     * 顯示 authUri
     */
    displayAuthUri: function(data, errorMessage) {
        const authUriDisplay = document.getElementById('auth-uri-display');
        if (!authUriDisplay) return;
        
        // 獲取 APP 開啟按鈕
        const appOpenButton = this.elements.appOpenButton;
        
        if (errorMessage) {
            authUriDisplay.innerHTML = `<span class="text-danger">${errorMessage}</span>`;
            if (appOpenButton) {
                appOpenButton.style.display = 'none';
            }
            return;
        }
        
        if (!data) {
            authUriDisplay.innerHTML = `<span class="text-warning">未收到數據</span>`;
            if (appOpenButton) {
                appOpenButton.style.display = 'none';
            }
            return;
        }
        
        // 嘗試從數據中獲取 auth_uri
        let authUri = null;
        
        // 從頂層查詢
        if (data.auth_uri) {
            authUri = data.auth_uri;
        } 
        // 從 data 對象中查詢
        else if (data.data && data.data.auth_uri) {
            authUri = data.data.auth_uri;
        }
        // 從舊版屬性名稱查詢 (兼容性)
        else if (data.authUri) {
            authUri = data.authUri;
        }
        else if (data.data && data.data.authUri) {
            authUri = data.data.authUri;
        }
        
        // 設置 APP 開啟按鈕
        if (authUri && appOpenButton) {
            appOpenButton.href = authUri;
            appOpenButton.style.display = 'inline-block';
        } else if (appOpenButton) {
            appOpenButton.style.display = 'none';
        }
        
        // 顯示 auth_uri 或提示缺失（現在隱藏在頁面上但代碼保留以便調試）
        if (authUri) {
            // 顯示帶有連結的 auth_uri，但不顯示詳細資訊
            authUriDisplay.innerHTML = `
                <a href="${authUri}" class="btn btn-sm btn-outline-primary mt-1" target="_blank">使用數位憑證皮夾APP開啟</a>
            `;
        } else {
            // 顯示錯誤提示，但不顯示詳細資訊
            authUriDisplay.innerHTML = `
                <span class="text-warning">無法建立深度連結，請掃描QR碼</span>
            `;
        }
    },
    
    /**
     * 開始掃描狀態檢查
     */
    startScanStatusChecking: function() {
        // 開始倒數計時
        CountdownModule.startCountdown(
            this.elements.countdownText, 
            this.elements.countdownProgress, 
            this.state.qrCodeGeneratedTime, 
            this.config.qrCodeExpireMs,
            this.config.qrCodeExpireMinutes,
            this.handleQRCodeExpired.bind(this)
        );
        
        // 開始檢查掃描結果
        TimerModule.startResultCheck(
            this.state.currentTransactionId,
            this.handleScanResult.bind(this)
        );
    },
    
    /**
     * 處理QR碼過期
     */
    handleQRCodeExpired: function() {
        // 停止定時檢查
        TimerModule.stopResultCheck();
        
        // 提示用戶QR碼已過期
        if (this.elements.qrcodeDisplay && this.elements.qrcodeDisplay.querySelector('img')) {
            this.elements.qrcodeDisplay.innerHTML = `
                <div class="alert alert-warning">
                    QR碼已過期，請點擊下方按鈕重新產生。
                </div>
            `;
        }
    },
    
    /**
     * 處理掃描結果
     */
    handleScanResult: function(data) {
        console.log('掃描結果資料:', data);
        
        // 隱藏QR碼區域（包含標題）
        UIHelper.hideQRCodeArea(this.elements.qrcodeArea);
        
        // 顯示掃描結果
        UIHelper.displayScanResult(data, this.elements.qrcodeArea, this.elements.qrcodeDisplay);
        
        // 停止定時檢查和倒數計時
        TimerModule.stopResultCheck();
        CountdownModule.stopCountdown();
        
        // 啟動結果顯示倒數計時器（3秒後重新載入頁面）
        TimerModule.startResultDisplayTimer(
            this.config.resultDisplaySeconds,
            this.resetScanPage.bind(this)
        );
    },
    
    /**
     * 重置掃描頁面
     */
    resetScanPage: function() {
        // 移除已有的掃描結果顯示
        UIHelper.removeExistingScanResult();
        
        // 顯示QR碼區域（包含標題）
        UIHelper.showQRCodeArea(this.elements.qrcodeArea, this.elements.qrcodeDisplay);
        
        // 顯示倒數計時區域
        UIHelper.showCountdownArea();
        
        // 產生新的QR碼
        this.generateQRCode();
    },
    
    /**
     * 產生隨機交易ID
     */
    generateTransactionId: function() {
        // 生成唯一交易ID，格式: qr_[隨機字符串]_[時間戳]
        return 'qr_' + Math.random().toString(36).substring(2, 15) + 
               Math.random().toString(36).substring(2, 15) + 
               '_' + Date.now();
    }
};

/**
 * API模組，處理所有API請求
 */
const APIModule = {
    /**
     * 從後端獲取默認證件類型
     */
    fetchDefaultRef: function() {
        return fetch(QRCodeModule.config.contextPath + 'api/oidvp/default-ref')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP狀態: ${response.status}`);
                }
                return response.json();
            });
    },
    
    /**
     * 產生QR碼
     */
    generateQRCode: function(ref, transactionId) {
        const url = QRCodeModule.config.contextPath + 'api/oidvp/qr-code?ref=' + encodeURIComponent(ref) + 
                    '&transaction_id=' + encodeURIComponent(transactionId);
        
        console.log('QR碼請求URL:', url);  // 新增日誌
        
        return fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP狀態: ${response.status}`);
                }
                return response.json();
            });
    },
    
    /**
     * 檢查掃描結果
     */
    checkScanResult: function(transactionId) {
        const url = QRCodeModule.config.contextPath + 'api/oidvp/result?transaction_id=' + 
                    encodeURIComponent(transactionId);
        
        return fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP狀態: ${response.status}`);
                }
                return response.json();
            });
    },
    
    /**
     * 從後端獲取歡迎訊息
     */
    fetchWelcomeMessage: function() {
        return fetch(QRCodeModule.config.contextPath + 'api/oidvp/welcome-message')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP狀態: ${response.status}`);
                }
                return response.json();
            });
    }
};

/**
 * 倒數計時模組
 */
const CountdownModule = {
    // 倒數計時器
    countdownTimer: null,
    
    /**
     * 開始倒數計時
     */
    startCountdown: function(countdownText, countdownProgress, startTime, totalMs, minutes, onExpireCallback) {
        // 確保只有一個定時器在運行
        this.stopCountdown();
        
        // 立即更新倒數顯示
        this.updateCountdown(countdownText, countdownProgress, startTime, totalMs, minutes, onExpireCallback);
        
        // 每秒更新一次倒數顯示
        this.countdownTimer = setInterval(() => {
            this.updateCountdown(countdownText, countdownProgress, startTime, totalMs, minutes, onExpireCallback);
        }, 1000);
    },
    
    /**
     * 停止倒數計時
     */
    stopCountdown: function() {
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer);
            this.countdownTimer = null;
        }
    },
    
    /**
     * 重置倒數計時顯示
     */
    resetCountdown: function(countdownText, countdownProgress, minutes) {
        if (countdownText) {
            countdownText.textContent = `QR碼有效期限：${minutes}:00`;
        }
        if (countdownProgress) {
            countdownProgress.style.width = '100%';
        }
    },
    
    /**
     * 更新倒數計時顯示
     */
    updateCountdown: function(countdownText, countdownProgress, startTime, totalMs, minutes, onExpireCallback) {
        if (!startTime) {
            this.resetCountdown(countdownText, countdownProgress, minutes);
            return;
        }
        
        // 計算經過的時間（毫秒）
        const elapsedMs = Date.now() - startTime;
        
        // 計算剩餘時間（毫秒）
        const remainingMs = totalMs - elapsedMs;
        
        if (remainingMs <= 0) {
            // QR碼已過期
            this.stopCountdown();
            
            if (countdownText) {
                countdownText.textContent = '此QR碼已失效，請重新產生';
            }
            if (countdownProgress) {
                countdownProgress.style.width = '0%';
            }
            
            // 呼叫過期回調
            if (typeof onExpireCallback === 'function') {
                onExpireCallback();
            }
            
            return;
        }
        
        // 計算剩餘分鐘和秒數
        const remainingMinutes = Math.floor(remainingMs / (60 * 1000));
        const remainingSeconds = Math.floor((remainingMs % (60 * 1000)) / 1000);
        
        // 更新倒數文字
        if (countdownText) {
            countdownText.textContent = `QR碼有效期限：${remainingMinutes}:${remainingSeconds.toString().padStart(2, '0')}`;
        }
        
        // 更新進度條寬度（百分比）
        if (countdownProgress) {
            const progressPercentage = (remainingMs / totalMs) * 100;
            countdownProgress.style.width = `${progressPercentage}%`;
        }
    }
};

/**
 * 定時器模組 - 管理各種計時器
 */
const TimerModule = {
    // 查詢結果的定時器
    checkResultTimer: null,
    
    // 結果顯示倒數計時器
    resultDisplayTimer: null,
    
    /**
     * 開始定時檢查QR碼掃描結果
     */
    startResultCheck: function(transactionId, onResultCallback) {
        // 確保只有一個定時器在運行
        this.stopResultCheck();
        
        // 每3秒檢查一次結果
        this.checkResultTimer = setInterval(() => {
            this.checkScanResult(transactionId, onResultCallback);
        }, 3000);
    },
    
    /**
     * 停止檢查QR碼掃描結果
     */
    stopResultCheck: function() {
        if (this.checkResultTimer) {
            clearInterval(this.checkResultTimer);
            this.checkResultTimer = null;
        }
    },
    
    /**
     * 檢查QR碼掃描結果
     */
    checkScanResult: function(transactionId, onResultCallback) {
        if (!transactionId) {
            console.warn('無法檢查結果: 交易ID不存在');
            this.stopResultCheck();
            return;
        }
        
        APIModule.checkScanResult(transactionId)
            .then(data => {
                if (!data) return; // 尚未掃描，繼續等待
                
                // 呼叫結果回調
                if (typeof onResultCallback === 'function') {
                    onResultCallback(data);
                }
            })
            .catch(error => {
                console.error('查詢掃描結果時發生錯誤:', error);
            });
    },
    
    /**
     * 啟動結果顯示倒數計時器
     */
    startResultDisplayTimer: function(seconds, onCompleteCallback) {
        // 停止先前的計時器
        this.stopResultDisplayTimer();
        
        // 創建顯示倒數元素
        const countdownElement = document.createElement('div');
        countdownElement.className = 'result-countdown alert alert-info mt-3';
        countdownElement.innerHTML = `<span id="result-countdown-value">${seconds}</span> 秒後將重新顯示QR碼掃描頁面...`;
        
        // 添加到掃描結果下方
        const scanResult = document.querySelector('.scan-result');
        if (scanResult) {
            scanResult.appendChild(countdownElement);
        }
        
        // 更新倒數顯示
        let secondsLeft = seconds;
        const updateResultCountdown = () => {
            const countdownValue = document.getElementById('result-countdown-value');
            if (countdownValue) {
                secondsLeft--;
                countdownValue.textContent = secondsLeft;
                
                if (secondsLeft <= 0) {
                    // 執行完成回調
                    if (typeof onCompleteCallback === 'function') {
                        onCompleteCallback();
                    }
                } else {
                    // 繼續倒數
                    this.resultDisplayTimer = setTimeout(updateResultCountdown, 1000);
                }
            }
        };
        
        // 開始倒數
        this.resultDisplayTimer = setTimeout(updateResultCountdown, 1000);
    },
    
    /**
     * 停止結果顯示倒數計時器
     */
    stopResultDisplayTimer: function() {
        if (this.resultDisplayTimer) {
            clearTimeout(this.resultDisplayTimer);
            this.resultDisplayTimer = null;
        }
    },
    
    /**
     * 停止所有計時器
     */
    stopAllTimers: function() {
        this.stopResultCheck();
        this.stopResultDisplayTimer();
        CountdownModule.stopCountdown();
    }
};

/**
 * UI幫助類 - 處理UI相關操作
 */
const UIHelper = {
    /**
     * 顯示QR碼區域
     */
    showQRCodeArea: function(qrcodeArea, qrcodeDisplay) {
        if (qrcodeArea) {
            qrcodeArea.style.display = 'block';
        }
        
        if (qrcodeDisplay) {
            qrcodeDisplay.style.display = 'flex';
        }
    },
    
    /**
     * 隱藏QR碼區域
     */
    hideQRCodeArea: function(qrcodeArea) {
        if (qrcodeArea) {
            qrcodeArea.style.display = 'none';
        }
    },
    
    /**
     * 顯示倒數計時區域
     */
    showCountdownArea: function() {
        const countdownWrapper = document.querySelector('.countdown-wrapper');
        if (countdownWrapper) {
            countdownWrapper.style.display = 'block';
        }
    },
    
    /**
     * 移除已存在的掃描結果顯示
     */
    removeExistingScanResult: function() {
        const existingResult = document.querySelector('.scan-result');
        if (existingResult) {
            existingResult.remove();
        }
    },
    
    /**
     * 顯示QR碼圖片
     */
    displayQRCode: function(qrcodeData, qrcodeDisplay) {
        try {
            // 創建圖片元素
            const img = document.createElement('img');
            
            // 檢查是否已經是完整的data URL
            if (qrcodeData.startsWith('data:image')) {
                img.src = qrcodeData;
            } else {
                img.src = `data:image/png;base64,${qrcodeData}`;
            }
            
            img.alt = '數位憑證皮夾QR碼';
            img.className = 'img-fluid'; // 確保圖片自適應大小
            
            img.onerror = function() {
                console.error('QR碼圖片載入失敗');
                if (qrcodeDisplay) {
                    qrcodeDisplay.innerHTML = `
                        <div class="alert alert-danger">
                            QR碼圖片載入失敗，請點擊重新產生按鈕再試一次。
                        </div>
                    `;
                }
            };
            
            // 清空載入動畫，顯示QR碼圖片
            if (qrcodeDisplay) {
                qrcodeDisplay.innerHTML = '';
                qrcodeDisplay.appendChild(img);
            }
        } catch (error) {
            console.error('顯示QR碼時發生錯誤:', error);
            if (qrcodeDisplay) {
                qrcodeDisplay.innerHTML = `
                    <div class="alert alert-danger">
                        顯示QR碼時發生錯誤，請點擊重新產生按鈕再試一次。
                    </div>
                `;
            }
        }
    },
    
    /**
     * 顯示QR碼掃描結果
     */
    displayScanResult: function(resultData, qrcodeArea, qrcodeDisplay) {
        // 建立結果顯示元素
        const resultElement = document.createElement('div');
        resultElement.className = 'scan-result alert alert-success mt-4';
        
        // 根據返回的資料格式來顯示結果
        let resultHTML = '<h4>QR碼已被掃描!</h4>';
        
        // 格式化並顯示結果資料
        if (typeof resultData === 'object') {
            resultHTML += this.formatScanResultHTML(resultData);
        } else {
            resultHTML += `<p>回應資料: ${resultData}</p>`;
        }
        
        // 添加感謝訊息
        resultHTML += '<p class="mt-3"><strong>已認證，' + QRCodeModule.config.welcomeMessage + '</strong></p>';
        
        resultElement.innerHTML = resultHTML;
        
        // 隱藏倒數計時
        const countdownWrapper = document.querySelector('.countdown-wrapper');
        if (countdownWrapper) {
            countdownWrapper.style.display = 'none';
        }
        
        // 將掃描結果插入到QR碼區域之後
        if (qrcodeArea && qrcodeArea.parentNode) {
            qrcodeArea.parentNode.insertBefore(resultElement, qrcodeArea.nextSibling);
        } else if (qrcodeDisplay) {
            // 備用方案：插入到QR碼顯示區域之後
            qrcodeDisplay.parentNode.insertBefore(resultElement, qrcodeDisplay.nextSibling);
        }
    },
    
    /**
     * 格式化掃描結果HTML
     */
    formatScanResultHTML: function(resultData) {
        let html = '<p>從數位憑證皮夾取得的資料:</p>';
        
        // 檢查標準驗證結果欄位
        if (resultData.verifyResult !== undefined) {
            html += `<p><strong>驗證結果:</strong> ${resultData.verifyResult ? '成功' : '失敗'}</p>`;
        }
        
        if (resultData.resultDescription) {
            html += `<p><strong>結果描述:</strong> ${resultData.resultDescription}</p>`;
        }
        
        // 如果有憑證資料，顯示詳細資訊
        if (resultData.data && Array.isArray(resultData.data)) {
            resultData.data.forEach((credential, index) => {
                html += `<div class="credential-data mt-3">`;
                
                if (credential.claims && Array.isArray(credential.claims)) {
                    html += '<ul class="text-start">';
                    credential.claims.forEach(claim => {
                        html += `<li><strong>${claim.cname || claim.ename || '欄位'}:</strong> ${claim.value || '無值'}</li>`;
                    });
                    html += '</ul>';
                }
                
                html += `</div>`;
            });
        } else {
            // 一般資料顯示
            html += '<ul class="text-start">';
            for (const key in resultData) {
                if (resultData.hasOwnProperty(key) && key !== 'data') {
                    const value = resultData[key];
                    if (typeof value !== 'object' || value === null) {
                        html += `<li><strong>${key}:</strong> ${value}</li>`;
                    }
                }
            }
            html += '</ul>';
        }
        
        return html;
    }
}; 