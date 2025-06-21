// // axiosInstance.js
// import axios from "axios";

// const instance = axios.create({
//   baseURL: "http://localhost:8080",
// });

// instance.interceptors.request.use((config) => {
//   const token = localStorage.getItem("token");
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// });

// export default instance;
// axiosInstance.js
import axios from "axios";

const instance = axios.create({
  baseURL: "http://localhost:8080",
});

// ✅ Add public route whitelist logic here
const publicEndpoints = [
  "/customer/serviceProviders",
  "/customer/serviceProviders/nearby",
  "/customer/location/resolve-pin",
  "/customer/serviceProviders/", // for /customer/serviceProviders/{id}
  // axiosInstance.js or similar

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
