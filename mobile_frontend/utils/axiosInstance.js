import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { BASE_URL } from '../config'; 
const instance = axios.create({
  baseURL: BASE_URL, 
});

// ✅ Public routes (no auth token required)
const publicEndpoints = [
  '/customer/serviceProviders',
  '/customer/serviceProviders/nearby',
  '/customer/location/resolve-pin',
  '/customer/serviceProviders/',  
  '/orders/user/',                
  '/orders/user/',                
  //'/orders/summary-from-redis'  
];

// Add a request interceptor
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
  (error) => {
    return Promise.reject(error);
  }
);

export default instance;
