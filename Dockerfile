## 建置階段：使用 Node 建置前端
FROM node:20-alpine AS build

WORKDIR /app

# 目前只需要 package.json 就好
COPY package.json ./
RUN npm install

# 複製其餘原始碼
COPY . .

# 建置 Vite 專案
RUN npm run build

## 執行階段：用 Nginx 提供靜態檔案
FROM nginx:1.27-alpine

# 將 dist 放到 Nginx 預設靜態目錄
COPY --from=build /app/dist /usr/share/nginx/html

# 使用自訂 Nginx 設定，支援 SPA 路由 fallback
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]