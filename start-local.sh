#!/bin/bash

# 設定顏色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 顯示開始訊息
echo -e "${GREEN}開始設定本機開發環境...${NC}"

# 檢查必要的目錄是否存在
if [ ! -d "/Users/fredericli/app/vpms/logs" ]; then
    echo -e "${YELLOW}建立日誌目錄...${NC}"
    mkdir -p /Users/fredericli/app/vpms/logs
fi

# 設定環境變數
echo -e "${GREEN}設定環境變數...${NC}"

# 日誌設定
export LOGGING_FILE_NAME=vpms.log
export LOGGING_FILE_PATH=/Users/fredericli/app/vpms/logs

# 郵件設定
export SPRING_MAIL_HOST=chtspps.org
export SPRING_MAIL_PORT=587

# 資料庫設定
export SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.4.158:5432/moda_vpms
export SPRING_DATASOURCE_USERNAME=moda
export SPRING_DATASOURCE_PASSWORD=1qaz@WSX3edc

# TWDIW 設定
export TWDIW_ISSUER_API_BASE_URL=https://issuer-sandbox.wallet.gov.tw
export TWDIW_ISSUER_API_ACCESS_TOKEN=8kp4RlF4k0VJBnn3XqwIW8X0fib0FAtU
export TWDIW_ISSUER_VISITOR_CARD_VC_ID=418853
export TWDIW_ISSUER_VISITOR_CARD_VC_CID=1234567890
export TWDIW_API_BASE_URL=https://verifier-sandbox.wallet.gov.tw
export TWDIW_API_KEY=fKX11yWfp5mxbWKUlhzVBf7EZNuJk9KP
export TWDIW_DEFAULT_REF=00000000_vpms_20250506_1303

# 檢查 Maven 是否安裝
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}錯誤：找不到 Maven 命令，請確認 Maven 是否已安裝${NC}"
    exit 1
fi

# 檢查 settings.xml 是否存在
if [ ! -f "settings.xml" ]; then
    echo -e "${RED}錯誤：找不到 settings.xml 檔案${NC}"
    exit 1
fi

# 清理 Maven 本地倉庫中的問題檔案
echo -e "${YELLOW}清理 Maven 本地倉庫...${NC}"
rm -rf ~/.m2/repository/org/slf4j/jul-to-slf4j/2.0.9

# 啟動應用程式
echo -e "${GREEN}啟動應用程式...${NC}"
echo -e "${YELLOW}使用以下設定：${NC}"
echo "日誌檔案：$LOGGING_FILE_PATH/$LOGGING_FILE_NAME"
echo "資料庫：$SPRING_DATASOURCE_URL"
echo "TWDIW API：$TWDIW_API_BASE_URL"

# 使用 local profile 啟動應用程式，並指定 Maven 設定檔
mvn spring-boot:run -Dspring-boot.run.profiles=local -s settings.xml

# 檢查執行結果
if [ $? -eq 0 ]; then
    echo -e "${GREEN}應用程式已成功啟動${NC}"
else
    echo -e "${RED}應用程式啟動失敗${NC}"
fi 
