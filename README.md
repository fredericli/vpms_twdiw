# VPMS (Visitor Pass Management System)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

## 目錄

- [專案簡介](#專案簡介)
- [功能特點](#功能特點)
- [技術棧](#技術棧)
- [系統需求](#系統需求)
- [安裝與設定](#安裝與設定)
- [使用說明](#使用說明)
- [開發指南](#開發指南)
- [專案文檔](#專案文檔)
- [授權](#授權)
- [貢獻指南](#貢獻指南)
- [聯絡方式](#聯絡方式)

## 專案簡介

VPMS (Visitor Pass Management System) 是一個基於 Spring Boot 的訪客管理系統，提供完整的訪客登記、證件發放和管理功能。本專案是基於[數位憑證皮夾](https://www.wallet.gov.tw/)的發行端與驗證端沙盒系統進行開發，整合了數位憑證皮夾服務，支援數位訪客證的發放和驗證。

## 功能特點

- 訪客登記與管理
- 數位訪客證發放
- 訪客證驗證
- 報表生成
- 郵件通知
- 角色基礎的權限管理
- RESTful API 支援

## 技術棧

- **後端框架**: Spring Boot 3.1.5
- **資料庫**: PostgreSQL 15
- **ORM**: Spring Data JPA
- **資料庫遷移**: Liquibase
- **安全框架**: Spring Security
- **模板引擎**: Thymeleaf
- **API 文件**: OpenAPI 3.0
- **容器化**: Docker
- **CI/CD**: GitHub Actions

## 系統需求

- Java 17 或更高版本
- PostgreSQL 15 或更高版本
- Maven 3.8 或更高版本
- Docker 和 Docker Compose（可選）

## 安裝與設定

### 必要環境變數

建立 `.env` 檔案並設定以下環境變數：

```env
# 資料庫設定
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vpms
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# 郵件伺服器設定
SPRING_MAIL_HOST=smtp.example.com
SPRING_MAIL_USERNAME=your_email
SPRING_MAIL_PASSWORD=your_password

# 數位憑證皮夾設定
TWDIW_API_BASE_URL=https://verifier-sandbox.wallet.gov.tw/api
TWDIW_API_KEY=your_api_key
TWDIW_ISSUER_API_BASE_URL=https://issuer-sandbox.wallet.gov.tw
TWDIW_ISSUER_API_ACCESS_TOKEN=your_access_token
```

### 使用 Docker 部署

1. 複製 `.env.example` 到 `.env` 並設定環境變數
2. 執行以下指令：

```bash
docker-compose up -d
```

### 本地部署

1. 確保 PostgreSQL 已安裝並運行
2. 執行以下指令：

```bash
mvn clean package
java -jar target/vpms-0.0.1-SNAPSHOT.jar
```

## 使用說明

系統提供以下主要功能：

1. **訪客登記**
   - 訪客基本資料登記
   - 訪客證發放
   - 訪客證驗證

2. **管理功能**
   - 使用者管理
   - 角色管理
   - 報表生成

3. **API 整合**
   - RESTful API 支援
   - 數位憑證皮夾整合

## 開發指南

詳細的開發指南請查看 [DEVELOPMENT.md](DEVELOPMENT.md) 文件。

### 專案結構

```
src/main/java/example/vpms/
├── config/         # 配置類
├── controller/     # 控制器
├── dto/           # 資料傳輸物件
├── model/         # 實體類
├── repository/    # 資料訪問層
├── service/       # 業務邏輯層
├── twdiw/         # 數位憑證皮夾整合
└── util/          # 工具類
```

### 編碼風格

本專案遵循 [Google Java 程式碼風格指南](https://google.github.io/styleguide/javaguide.html)。詳細的編碼風格指南請查看 [CODING_STYLE.md](CODING_STYLE.md) 文件。

## 專案文檔

- [編碼風格指南](CODING_STYLE.md) - 編碼風格和最佳實踐
- [開發設定指南](DEVELOPMENT.md) - 開發環境設定和工作流程
- [貢獻指南](CONTRIBUTING.md) - 如何貢獻代碼
- [行為準則](CODE_OF_CONDUCT.md) - 社區行為準則
- [專案治理](GOVERNANCE.md) - 專案決策和管理流程
- [安全政策](SECURITY.md) - 安全漏洞報告和處理流程
- [依賴授權資訊](DEPENDENCIES.md) - 第三方依賴庫及其授權
- [變更日誌](CHANGELOG.md) - 版本變更記錄

## 授權

本專案採用 MIT 授權條款。詳見 [LICENSE](LICENSE) 檔案。

## 貢獻指南

我們歡迎所有形式的貢獻，包括代碼、文檔、問題報告和功能請求。請先閱讀我們的 [貢獻指南](CONTRIBUTING.md) 和 [行為準則](CODE_OF_CONDUCT.md)。

## 聯絡方式

如有任何問題或建議，請開立 [GitHub Issue](https://github.com/fredericli/vpms_twdiw/issues) 或提交 [Pull Request](https://github.com/fredericli/vpms_twdiw/pulls)。

---

© 2024 [fredericli](https://github.com/fredericli)