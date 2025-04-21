#!/bin/bash

# 設置顏色輸出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 設置變數
IMAGE_NAME="vpms"
IMAGE_VERSION="1.0.0"
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_VERSION}"
OUTPUT_DIR="./docker-images"
TAR_FILENAME="${IMAGE_NAME}_${IMAGE_VERSION}_multiarch.tar"

# 顯示腳本資訊
echo -e "${GREEN}====================================================${NC}"
echo -e "${GREEN}       訪客管理系統 Docker 多平台構建腳本         ${NC}"
echo -e "${GREEN}====================================================${NC}"

# 檢查 Docker 是否已安裝
if ! command -v docker &> /dev/null; then
    echo -e "${RED}錯誤: Docker 未安裝，請先安裝 Docker${NC}"
    exit 1
fi

# 檢查 Docker Buildx 是否可用
if ! docker buildx version &> /dev/null; then
    echo -e "${RED}錯誤: Docker Buildx 未安裝或未啟用${NC}"
    echo -e "${YELLOW}請確保 Docker Desktop 已安裝或執行: docker buildx install${NC}"
    exit 1
fi

# 創建輸出目錄（如果不存在）
if [ ! -d "$OUTPUT_DIR" ]; then
    echo -e "${YELLOW}建立輸出目錄: $OUTPUT_DIR ${NC}"
    mkdir -p "$OUTPUT_DIR"
fi

# 檢查檔案是否已存在
if [ -f "$OUTPUT_DIR/$TAR_FILENAME" ]; then
    echo -e "${YELLOW}警告: 輸出檔案 $OUTPUT_DIR/$TAR_FILENAME 已存在，將被覆蓋${NC}"
fi

# 開始計時
START_TIME=$(date +%s)

# 步驟 1: 清理現有映像（如果需要）
echo -e "\n${GREEN}步驟 1: 清理現有映像...${NC}"
if docker image inspect $FULL_IMAGE_NAME >/dev/null 2>&1; then
    echo -e "${YELLOW}移除現有映像: $FULL_IMAGE_NAME ${NC}"
    docker rmi $FULL_IMAGE_NAME
else
    echo -e "${GREEN}沒有找到現有映像，繼續...${NC}"
fi

# 步驟 2: 創建多平台構建器
echo -e "\n${GREEN}步驟 2: 創建多平台構建器...${NC}"
docker buildx create --name multiarch --use

# 步驟 3: 構建多平台映像
echo -e "\n${GREEN}步驟 3: 構建多平台映像...${NC}"
echo -e "${YELLOW}支援平台: linux/amd64,linux/arm64 ${NC}"

# 構建多平台映像並直接載入到本地 Docker
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    -t $FULL_IMAGE_NAME \
    --load \
    .

if [ $? -ne 0 ]; then
    echo -e "${RED}錯誤: Docker 多平台構建失敗${NC}"
    exit 1
fi

# 步驟 4: 保存映像到檔案
echo -e "\n${GREEN}步驟 4: 保存映像到檔案...${NC}"
echo -e "${YELLOW}保存到: $OUTPUT_DIR/$TAR_FILENAME ${NC}"

# 使用 docker save 保存映像
docker save -o "$OUTPUT_DIR/$TAR_FILENAME" $FULL_IMAGE_NAME

if [ $? -ne 0 ]; then
    echo -e "${RED}錯誤: 保存映像失敗${NC}"
    exit 1
fi

# 步驟 5: 顯示構建結果
echo -e "\n${GREEN}步驟 5: 顯示構建結果...${NC}"
echo -e "${GREEN}映像名稱: $FULL_IMAGE_NAME ${NC}"
echo -e "${GREEN}支援平台: linux/amd64,linux/arm64 ${NC}"
echo -e "${GREEN}輸出檔案: $OUTPUT_DIR/$TAR_FILENAME ${NC}"

# 計算並顯示總耗時
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))
echo -e "\n${GREEN}總耗時: $DURATION 秒${NC}"

# 步驟 6: 顯示映像資訊
echo -e "\n${GREEN}步驟 6: 顯示映像資訊...${NC}"
docker buildx imagetools inspect $FULL_IMAGE_NAME

echo -e "\n${GREEN}多平台構建完成！${NC}"

# 使用提示
echo -e "\n${YELLOW}使用提示:${NC}"
echo -e "${YELLOW}在目標機器上匯入映像:${NC}"
echo -e "${GREEN}docker load -i $TAR_FILENAME${NC}"
echo -e "\n${YELLOW}啟動容器:${NC}"
echo -e "${GREEN}docker run -d -p 8080:8080 \\ ${NC}"
echo -e "${GREEN}  -v /path/to/logs:/logs \\ ${NC}"
echo -e "${GREEN}  --name vpms $FULL_IMAGE_NAME${NC}"
echo -e "\n${YELLOW}建議與 PostgreSQL 一起使用 docker-compose 進行部署${NC}"

exit 0 