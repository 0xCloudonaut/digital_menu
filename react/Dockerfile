# Stage 1: Build React App
FROM node:18-alpine AS build
WORKDIR /app
ARG VITE_API_BASE_URL=http://localhost:8080
ARG VITE_TENANT_ID=demo-restaurant
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_TENANT_ID=${VITE_TENANT_ID}
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
# Note: If your build folder is 'dist' (Vite), change 'build' to 'dist' below
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
