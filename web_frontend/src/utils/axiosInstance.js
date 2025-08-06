import axios from "axios";
import { BASE_URL } from "./config";
const instance = axios.create({
  baseURL: BASE_URL,
  withCredentials: true
});

// ✅ Public routes (no auth token required)
const publicEndpoints = [
  "/customer/serviceProviders",
  "/customer/serviceProviders/nearby",
  "/customer/location/resolve-pin",
  "/customer/serviceProviders/",   // for /customer/serviceProviders/{id}
  "/orders/user/",                 // for /orders/user/{userId}
  "/orders/user/",                 // for /orders/user/{userId}/stats
  //"/orders/summary-from-redis"     // summary from redis
];

instance.interceptors.request.use((config) => {
  const isPublic = publicEndpoints.some((url) =>
    config.url?.startsWith(url)
  );

  const token = localStorage.getItem("token");

  if (token && !isPublic) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default instance;