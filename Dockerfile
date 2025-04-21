# 多階段構建：階段1 - 使用官方Maven鏡像進行構建
FROM maven:3.8-eclipse-temurin-17 AS build

# 設置工作目錄
WORKDIR /app

# 複製Maven配置文件
COPY pom.xml .

# 複製源代碼
COPY src ./src

# 構建應用程序
RUN mvn package -DskipTests

# 多階段構建：階段2 - 使用精簡的JRE運行環境
FROM eclipse-temurin:17-jre

# 工作目錄
WORKDIR /app

# 設置台灣時區
ENV TZ=Asia/Taipei
ENV LANG=zh_TW.UTF-8
ENV LANGUAGE=zh_TW:zh
ENV LC_ALL=zh_TW.UTF-8

# 安裝必要的套件
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    tzdata \
    chrony \
    locales && \
    # 設定時區
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    # 設定語言環境
    locale-gen zh_TW.UTF-8 && \
    # 清理暫存檔
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 設置時間同步配置
RUN echo "server tock.stdtime.gov.tw iburst" >> /etc/chrony/chrony.conf && \
    echo "server watch.stdtime.gov.tw iburst" >> /etc/chrony/chrony.conf && \
    echo "server time.stdtime.gov.tw iburst" >> /etc/chrony/chrony.conf

# 從構建階段複製構建好的JAR文件
COPY --from=build /app/target/*.jar app.jar

# 創建日誌目錄並設置權限
RUN mkdir -p /logs && \
    chmod 777 /logs

# 暴露應用程序的埠
EXPOSE 8080

# 創建啟動腳本
RUN echo '#!/bin/sh\nchronyd -d &\njava -jar app.jar' > /app/start.sh && \
    chmod +x /app/start.sh

# 使用啟動腳本啟動應用程序
ENTRYPOINT ["/app/start.sh"] 