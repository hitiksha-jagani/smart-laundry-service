import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { BASE_URL } from '../config'; // ✅ Correct

const instance = axios.create({
  baseURL: BASE_URL,
});

// ✅ Public endpoints (no auth token required)
const publicEndpoints = [
  '/customer/serviceProviders',
  '/customer/serviceProviders/nearby',
  '/customer/location/resolve-pin',
  '/customer/serviceProviders/',   // for /customer/serviceProviders/{id}
  '/orders/user/',                 // for /orders/user/{userId}
  '/orders/summary-from-redis'     // summary from redis
];

instance.interceptors.request.use(
  async (config) => {
    const isPublic = publicEndpoints.some((url) =>
      config.url?.startsWith(url)
    );

    if (!isPublic) {
      const token = await AsyncStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

export default instance;
