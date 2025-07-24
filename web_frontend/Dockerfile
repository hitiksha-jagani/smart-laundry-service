
# Build React app
FROM node:24 AS react-builder
WORKDIR /app
COPY . .

# Copy environment variables
COPY .env.production .env
RUN npm install && npm run build

# Remove default nginx website
FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/*

# Copy built React app
COPY --from=react-builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]


