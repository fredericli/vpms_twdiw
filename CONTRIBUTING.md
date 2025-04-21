# 貢獻指南

感謝您對 VPMS (Visitor Pass Management System) 專案的興趣！我們歡迎各種形式的貢獻，從錯誤報告到功能實作，從文檔改進到使用者體驗增強。本文檔概述了貢獻流程和指南。

## 行為準則

參與本專案的所有貢獻者都需要遵守我們的 [行為準則](CODE_OF_CONDUCT.md)。請花點時間閱讀，了解我們對社區成員的期望。

## 如何貢獻

### 報告錯誤

如果您發現錯誤，請通過 [GitHub Issues](https://github.com/fredericli/vpms_twdiw/issues) 報告，包含以下資訊：

1. 問題的簡短描述
2. 重現問題的步驟
3. 預期的行為
4. 實際的行為
5. 環境詳情（作業系統、瀏覽器等）
6. 如可能，請附上截圖或錯誤日誌

### 建議功能

欲建議新功能，請通過 [GitHub Issues](https://github.com/fredericli/vpms_twdiw/issues) 提交，標題前加上 `[Feature Request]`，包含：

1. 功能的清晰描述
2. 使用案例和潛在受益
3. 如可能，提供實作建議

### 提交程式碼變更

1. **分叉專案**：首先分叉 [VPMS 儲存庫](https://github.com/fredericli/vpms_twdiw)。

2. **建立分支**：為您的變更建立新分支：
   ```bash
   git checkout -b feature/your-feature-name
   # 或
   git checkout -b fix/your-bugfix-name
   ```

3. **編寫程式碼**：進行更改，遵循[程式碼風格指南](CODING_STYLE.md)。

4. **撰寫測試**：為您的變更新增適當的測試。

5. **確保所有測試通過**：
   ```bash
   mvn test
   ```

6. **提交變更**：
   ```bash
   git add .
   git commit -m "你的變更描述"
   ```

   提交訊息格式應遵循 [約定式提交](https://www.conventionalcommits.org/)：
   - `feat: 新功能描述`
   - `fix: 修復問題描述`
   - `docs: 文檔更新描述`
   - `style: 程式碼樣式變更描述`
   - `refactor: 重構描述`
   - `test: 測試相關變更描述`
   - `chore: 維護性任務描述`

7. **推送變更**：
   ```bash
   git push origin feature/your-feature-name
   ```

8. **建立 Pull Request**：前往 GitHub 儲存庫，建立新的 Pull Request。

### Pull Request 流程

1. 確保 PR 描述中清楚說明了變更的目的和實作方法。
2. 連結相關的 Issue（如果有）。
3. 等待維護者的審核。如需修改，請在同一分支上進行更改並推送。
4. 一旦審核通過，您的 PR 將被合併到主分支。

## 開發環境設定

請參閱 [DEVELOPMENT.md](DEVELOPMENT.md) 獲取關於如何設定開發環境的詳細說明。

## 程式碼風格

請參閱 [CODING_STYLE.md](CODING_STYLE.md) 了解專案的程式碼規範。

## 測試指南

- 為所有新功能和錯誤修復撰寫測試。
- 當修復錯誤時，首先撰寫測試以捕捉該錯誤。
- 使用 JUnit 5 和 Mockito 進行單元測試。
- 測試應涵蓋正常情況和邊緣情況。

測試執行：
```bash
# 執行所有測試
mvn test

# 執行特定測試
mvn test -Dtest=TestClassName
```

## 文檔貢獻

文檔改進也是有價值的貢獻。如果您發現文檔不完整或不准確，請通過與程式碼相同的 PR 流程提交變更。

## 社區成員和角色

請參閱 [GOVERNANCE.md](GOVERNANCE.md) 了解專案的治理結構和角色。

## 許可證

通過貢獻，您同意您的貢獻將在 [MIT 許可證](LICENSE) 下提供。

## 謝謝！

再次感謝您對 VPMS 專案的貢獻。您的努力有助於提高系統的品質和功能。 