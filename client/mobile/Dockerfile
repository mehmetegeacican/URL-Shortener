# Stage 1: Build the Expo Web app
FROM node:20-alpine AS builder

WORKDIR /app

# 1. Install dependencies
COPY package.json package-lock.json* ./
RUN npm ci

# 2. Copy the frontend source code
COPY . .

# 3. Build for production web target
RUN npx expo export --platform web

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Copy static assets from the builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]