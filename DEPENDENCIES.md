# 依賴授權資訊

VPMS 專案使用了多個開源元件，本文件列出了主要的依賴庫及其授權資訊，以確保合規使用這些元件。

## 主要依賴

| 依賴名稱 | 版本 | 授權 | 用途 |
|----------|------|------|------|
| Spring Boot | 3.1.5 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 應用程式框架 |
| Spring Security | 6.1.5 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 安全框架 |
| Spring Data JPA | 3.1.5 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 資料存取框架 |
| Thymeleaf | 3.1.2 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 模板引擎 |
| PostgreSQL JDBC Driver | 42.6.0 | [BSD 2-Clause](https://jdbc.postgresql.org/about/license/) | 資料庫驅動程式 |
| Liquibase | 4.23.2 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 資料庫遷移工具 |
| Swagger Annotations | 2.2.15 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | API 文檔 |
| Jackson | 2.15.2 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | JSON 處理 |
| Apache HttpClient | 5.2.1 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | HTTP 客戶端 |

## 測試依賴

| 依賴名稱 | 版本 | 授權 | 用途 |
|----------|------|------|------|
| JUnit Jupiter | 5.9.3 | [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0/) | 單元測試框架 |
| Mockito | 5.3.1 | [MIT License](https://github.com/mockito/mockito/blob/main/LICENSE) | 模擬測試框架 |
| Spring Boot Test | 3.1.5 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 測試工具 |

## 開發工具依賴

| 依賴名稱 | 版本 | 授權 | 用途 |
|----------|------|------|------|
| Spring Boot DevTools | 3.1.5 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 開發工具 |
| impsort-maven-plugin | 1.9.0 | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) | 代碼風格工具 |

## 授權相容性分析

本專案採用 MIT 授權釋出，這與上述依賴的授權條款相容。以下是主要授權的相容性分析：

- **Apache License 2.0**：與 MIT 授權相容，允許在 MIT 授權的專案中使用。Apache License 2.0 是一個寬鬆的授權，但要求保留版權和授權聲明。

- **Eclipse Public License (EPL) 2.0**：EPL 與 MIT 授權在某些方面有所不同，但在非專利條款方面基本相容。由於 JUnit 只用於測試，不會包含在最終的生產代碼中，因此不會引起授權問題。

- **BSD 2-Clause**：與 MIT 授權高度相容，都是寬鬆授權。

- **MIT License**：與本專案的 MIT 授權完全相容。

## 授權聲明

依據各開源元件的授權要求，我們在此確認：

1. 本專案清楚標示了使用的開源元件及其授權資訊
2. 保留了所有相關的版權聲明和授權聲明
3. 在適用的情況下提供了原始碼獲取方式

## 第三方授權文字

本專案的分發版本包含了部分第三方元件。以下是這些元件的完整授權文字連結：

- Apache License 2.0: [https://www.apache.org/licenses/LICENSE-2.0.txt](https://www.apache.org/licenses/LICENSE-2.0.txt)
- MIT License: [https://opensource.org/licenses/MIT](https://opensource.org/licenses/MIT)
- Eclipse Public License 2.0: [https://www.eclipse.org/legal/epl-2.0/](https://www.eclipse.org/legal/epl-2.0/)
- BSD 2-Clause License: [https://opensource.org/licenses/BSD-2-Clause](https://opensource.org/licenses/BSD-2-Clause)

## 免責聲明

本文件所提供的資訊僅供參考，不構成法律建議。如有特定授權問題，請諮詢專業法律顧問。 