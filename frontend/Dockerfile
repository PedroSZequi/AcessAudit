# syntax=docker/dockerfile:1.7
# =========================================================================
# Stage 1 — build do bundle Vite
# =========================================================================
FROM node:20-alpine AS build

WORKDIR /app

# Cache de deps
COPY package*.json ./
RUN npm ci

# Código + build
COPY . .

# Em produção, o frontend é servido pelo Nginx no mesmo host do backend
# via reverse proxy. As chamadas viram same-origin (/api/...), sem CORS.
ENV VITE_API_BASE_URL=""
RUN npm run build

# =========================================================================
# Stage 2 — runtime: Nginx servindo o bundle estático
# =========================================================================
FROM nginx:1.27-alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost/ >/dev/null 2>&1 || exit 1
