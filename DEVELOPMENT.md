# 開發設定指南

本文檔提供 VPMS (Visitor Pass Management System) 專案的開發環境設定指南，幫助開發者快速建立開發環境並開始貢獻。

## 系統需求

- Java 17 或更高版本
- Maven 3.8 或更高版本
- PostgreSQL 15 或更高版本
- Git
- IDE (推薦 IntelliJ IDEA 或 Eclipse)
- Docker 和 Docker Compose (可選，用於容器化開發)

## 設定開發環境

### 1. 安裝基礎工具

#### Java 17
```bash
# 使用 SDKMAN (推薦)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.7-tem

# 或使用 Homebrew (macOS)
brew install openjdk@17

# 或從 Oracle/OpenJDK 官方網站下載
```

#### Maven
```bash
# 使用 SDKMAN
sdk install maven

# 或使用 Homebrew (macOS)
brew install maven

# 或從 Maven 官方網站下載
```

#### PostgreSQL
```bash
# 使用 Homebrew (macOS)
brew install postgresql@15

# 或使用 Docker
docker run --name vpms-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=vpms -p 5432:5432 -d postgres:15
```

### 2. 克隆專案

```bash
git clone https://github.com/fredericli/vpms_twdiw.git
cd vpms_twdiw
```

### 3. 配置資料庫

```bash
# 本地安裝的 PostgreSQL
createdb vpms

# 設定資料庫使用者和密碼
# 根據您的 PostgreSQL 設定方式不同，創建使用者的方法可能會有所不同
```

### 4. 配置環境變數

複製示例環境檔案並根據您的環境進行設定：

```bash
cp .env.example .env
```

編輯 `.env` 檔案，設定以下必要的環境變數：

```
# 資料庫設定
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vpms
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# 日誌設定
LOGGING_FILE_PATH=./logs
LOGGING_FILE_NAME=vpms

# 數位憑證皮夾設定
TWDIW_API_BASE_URL=https://verifier-sandbox.wallet.gov.tw/api
TWDIW_API_KEY=your_api_key
TWDIW_ISSUER_API_BASE_URL=https://issuer-sandbox.wallet.gov.tw
TWDIW_ISSUER_API_ACCESS_TOKEN=your_access_token
TWDIW_DEFAULT_REF=your_default_ref
TWDIW_ISSUER_VISITOR_CARD_VC_ID=your_vc_id
TWDIW_ISSUER_VISITOR_CARD_VC_CID=your_vc_cid
```

### 5. 建置專案

```bash
mvn clean install
```

### 6. 運行專案

```bash
# 使用 Maven
mvn spring-boot:run

# 或直接執行 JAR 檔案
java -jar target/vpms-0.0.1-SNAPSHOT.jar
```

應用程式將在 http://localhost:8080 上運行。

您可以訪問以下頁面：
- 主頁：http://localhost:8080/
- 管理員儀表板：http://localhost:8080/admin/dashboard

## 使用 Docker 開發

若要使用 Docker 進行開發，您可以使用提供的 Docker Compose 配置：

```bash
# 啟動開發環境
docker-compose up -d

# 停止開發環境
docker-compose down
```

## 開發工作流程

### 分支管理

- `main`: 穩定的發布版本
- `dev`: 開發分支，所有功能和修復都合併到此分支
- 功能分支: `feature/feature-name`
- 修復分支: `fix/issue-name`

### 提交代碼

1. 從最新的 `dev` 分支建立您的功能分支：
   ```bash
   git checkout dev
   git pull
   git checkout -b feature/your-feature-name
   ```

2. 進行更改並提交：
   ```bash
   git add .
   git commit -m "feat: add your feature"
   ```

3. 確保您的代碼符合專案規範並通過測試：
   ```bash
   mvn verify
   ```

4. 將您的分支推送到遠程倉庫：
   ```bash
   git push origin feature/your-feature-name
   ```

5. 創建一個 Pull Request 到 `dev` 分支

### 代碼風格

本專案遵循 [Google Java 風格指南](https://google.github.io/styleguide/javaguide.html)。您可以使用以下命令來格式化您的代碼：

```bash
mvn impsort:sort
```

### 資料庫遷移

本專案使用 Liquibase 進行資料庫遷移管理。遷移腳本位於 `src/main/resources/db/changelog` 目錄中。

添加新的遷移腳本時，請遵循現有的命名規範：`YYYYMMDD_HHMM_description.xml`。


## 常見問題

### Q: 資料庫連接錯誤
確保 PostgreSQL 服務正在運行，並且連接字符串、使用者名稱和密碼正確。

### Q: 如何重置資料庫
```bash
# 刪除資料庫
dropdb vpms

# 創建新的資料庫
createdb vpms

# 應用程式將在下次啟動時自動應用所有遷移
```

### Q: 如何模擬數位憑證皮夾功能
請參考[數位憑證皮夾](https://www.wallet.gov.tw/)。

## 聯絡支援

如有開發相關問題，請通過 [GitHub Issues](https://github.com/fredericli/vpms_twdiw/issues) 提問或聯絡維護者。 