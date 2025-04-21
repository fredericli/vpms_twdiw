# 編碼風格指南

本文檔提供 VPMS 專案的編碼風格指南，以確保代碼庫的一致性和可維護性。所有貢獻者都應遵循這些指南。

## 通用指南

### 基本原則

- **可讀性優先**：代碼應該容易閱讀和理解。
- **保持簡潔**：避免不必要的複雜性和重複。
- **有意義的命名**：變數、方法和類名應明確表達其目的和功能。
- **一致性**：遵循既定模式和慣例。
- **適當注釋**：為複雜邏輯提供必要的文檔和注釋。

### 代碼格式

- 使用 UTF-8 編碼。
- 使用 LF (Unix) 換行符。
- 刪除結尾空格和空行。
- 文件應以空行結尾。
- 避免過長的行（超過120個字符）。

## Java 編碼風格

我們遵循 [Google Java 風格指南](https://google.github.io/styleguide/javaguide.html)，具體要點如下：

### 格式化

- 縮排使用4個空格，不使用Tab。
- 大括號使用 K&R 風格（左大括號在行尾）。
- 所有花括號都使用大括號，即使只有一條語句。
- 在控制語句（if, for, while等）的條件周圍加上括號，並在控制語句關鍵字和開括號之間加一個空格。
- 在每個邏輯運算符前後各加一個空格。
- 在逗號和分號後面加一個空格。
- 不在方法名與開括號之間加空格。

```java
// 正確
if (condition) {
    doSomething();
}

// 錯誤
if(condition){
    doSomething();
}
```

### 命名慣例

- **類名**：使用 PascalCase (大駝峰命名法)，如 `VisitorService`。
- **方法和變數**：使用 camelCase (小駝峰命名法)，如 `createVisitor`。
- **常量**：使用大寫字母和下劃線，如 `MAX_VISITORS`。
- **包名**：全小寫，使用點分隔，如 `example.vpms.controller`。

### 註釋

- 使用 Javadoc 為公共類和方法提供文檔。
- 每個類都應有類級別的 Javadoc，描述其用途和責任。
- 公共方法應有 Javadoc，說明：
  - 方法的作用
  - 參數描述和約束
  - 返回值描述
  - 可能拋出的異常
- 避免不必要的註釋（如描述明顯行為的註釋）。
- 使用 `// TODO: 說明` 標記待完成工作。

```java
/**
 * 訪客服務，處理訪客相關操作。
 */
public class VisitorService {

    /**
     * 創建新訪客。
     *
     * @param visitor 訪客資訊
     * @return 創建後的訪客，包含生成的ID
     * @throws IllegalArgumentException 如果訪客資訊不完整或無效
     */
    public Visitor createVisitor(Visitor visitor) {
        // 實現邏輯
    }
}
```

### 導入語句

- 不使用萬用字符導入 (`import java.util.*`)，除非是靜態導入。
- 按以下順序組織導入：
  1. 標準 Java 包 (`java.*`, `javax.*`)
  2. 第三方包
  3. 專案自身的包
- 在每組導入之間添加一個空行。
- 使用 `impsort-maven-plugin` 自動整理導入順序。

```java
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.vpms.model.Visitor;
import example.vpms.repository.VisitorRepository;
```

### 類結構

- 遵循以下順序排列類成員：
  1. 靜態變數
  2. 實例變數
  3. 構造函數
  4. 公共方法
  5. 私有/保護方法
  6. 內部類
- 相關方法應該分組在一起。
- 保持方法簡短，功能單一。

### 最佳實踐

- 遵循 SOLID 原則。
- 避免深度嵌套的代碼（超過3-4層）。
- 使用合適的設計模式，但不要過度設計。
- 優先使用 Java 8+ 的功能（如 Stream API, Optional, Lambda）。
- 使用明確的比較，不使用隱式轉換。
- 適當處理異常，提供有用的錯誤信息。

## 資料庫相關

### SQL 腳本

- 關鍵字使用大寫（SELECT, INSERT, UPDATE 等）。
- 表名和列名使用小寫，用下劃線分隔（snake_case）。
- 每個語句結束後使用分號。
- 縮排 JOIN, WHERE, GROUP BY, HAVING, ORDER BY 子句。

```sql
SELECT user_id, username, email
FROM users
WHERE active = true
  AND created_at > '2023-01-01'
ORDER BY created_at DESC;
```

### Liquibase 變更集

- 每個變更集都應該有明確的 ID 和作者。
- 變更集應該是原子性的，專注於一個邏輯變更。
- 使用適當的變更集，如 `createTable`, `addColumn`, `createIndex` 等。
- 為表和列提供有意義的名稱和註釋。

## 前端代碼風格

### HTML

- 使用4個空格縮排。
- 使用小寫標籤名。
- 屬性使用雙引號。
- 避免不必要的嵌套標籤。

### CSS/SCSS

- 使用類選擇器，避免使用 ID 選擇器和標籤選擇器。
- 使用 kebab-case 命名類（如 `.visitor-card`）。
- 使用縮寫屬性（如 `margin: 0 10px;` 而不是分別設置四個方向）。
- 保持選擇器的簡單性，避免過深的嵌套。

### JavaScript

- 使用駝峰命名法命名變數和函數。
- 使用單引號作為字符串的默認引號。
- 在適當的地方使用箭頭函數。
- 使用 `===` 和 `!==` 進行比較。
- 使用 `const` 和 `let`，避免使用 `var`。

## 代碼審查清單

在提交 PR 前，請檢查：

- [ ] 代碼遵循風格指南
- [ ] 注釋和文檔已更新
- [ ] 沒有編譯警告
- [ ] 所有測試通過
- [ ] 沒有重複代碼
- [ ] 沒有暴露敏感信息
- [ ] 沒有未使用的導入或變數

## 代碼檢查工具

我們使用以下工具來幫助維護代碼質量：

- Maven 的 `impsort-maven-plugin` 整理導入順序
- Google Java Format 工具
- SonarQube/SonarCloud 靜態代碼分析
- JUnit 和 Mockito 進行單元測試

## 參考資源

- [Google Java 風格指南](https://google.github.io/styleguide/javaguide.html)
- [Spring Framework 貢獻指南](https://github.com/spring-projects/spring-framework/blob/main/CONTRIBUTING.md)
- [Effective Java (Joshua Bloch)](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
- [Clean Code (Robert C. Martin)](https://www.oreilly.com/library/view/clean-code-a/9780136083238/) 