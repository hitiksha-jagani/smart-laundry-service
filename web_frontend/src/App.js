import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./pages/Customer/Register";
import Login from "./pages/Customer/login";
import CustomerHomePage from './pages/Customer/CustomerHomePage';
import OrderBookingWizard from "./pages/Customer/OrderBookingWizard";
import NearbyServiceProviders from "./pages/Customer/NearbyServiceProviders"; 
import ProviderDetail from "./pages/Customer/ProviderDetail";
import CompleteServiceProviderForm from "./pages/ServiceProvider/CompleteServiceProviderForm";
import PendingOrders from "./pages/ServiceProvider/PendingOrders";
import ActiveOrders from "./pages/ServiceProvider/ActiveOrders";
import DeliveredOrders from "./pages/ServiceProvider/DeliveredOrders"; 
import ProviderDashboard from "./pages/ServiceProvider/ProviderDashboard";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./components/PrivateRoute";
import PickupOtpVerify from "./pages/ServiceProvider/PickupOtpVerify";
import DeliveryOtpVerify from "./pages/ServiceProvider/DeliveryOtpVerify";
import ReadyForDelivery from "./pages/ServiceProvider/ReadyForDelivery";
import OtpVerificationOrders from "./pages/ServiceProvider/OtpVerificationOrders";

import ProtectedRoute from "./components/ProtectedRoute";
import RoleProtectedRoute from "./components/RoleProtectedRoute";

// Delivery Agent
import DeliveryPage from "./pages/DeliveryAgent/DeliveryPage";
import UpdateStatus from "./pages/DeliveryAgent/UpdateStatus";

// Optional placeholder dashboards
const AgentDashboard = () => <h2>Delivery Agent Dashboard</h2>;
const AdminDashboard = () => <h2>Admin Dashboard</h2>;

function App() {
  return (
    // <AuthProvider>
      
        <Routes>

          {/* Customer */}
          <Route path="/" element={<CustomerHomePage />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route
            path="/order/book"
            element={
              <PrivateRoute>
                <OrderBookingWizard />
              </PrivateRoute>
            }
          />
          <Route path="/service-providers" element={<NearbyServiceProviders />} />
          <Route path="/provider/:providerId" element={<ProviderDetail />} />
          <Route path="/customer/dashboard" element={<CustomerHomePage />} />

          {/* Service Provider */}
          <Route path="/provider/dashboard" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><ProviderDashboard /></PrivateRoute>} />
          <Route path="/provider/pending-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><PendingOrders /></PrivateRoute>} />
          <Route path="/provider/active-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><ActiveOrders /></PrivateRoute>} />
          <Route path="/provider/delivered-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><DeliveredOrders /></PrivateRoute>} />
          <Route path="/sp/completeprofile" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><CompleteServiceProviderForm /></PrivateRoute>} />

          {/* Service Provider */}
          <Route path="/sp/completeprofile" element={<CompleteServiceProviderForm />} />

          {/* Order OTP Verification – Unified Component */}
          <Route path="/serviceprovider/verify-pickup-otp" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><PickupOtpVerify /></PrivateRoute>} />
          <Route path="/serviceprovider/verify-delivery-otp" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><DeliveryOtpVerify /></PrivateRoute>}/>
          <Route path="/serviceprovider/mark-ready" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><ReadyForDelivery /></PrivateRoute>}/>
          <Route path="/serviceprovider/otp-verification-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><OtpVerificationOrders /></PrivateRoute>}/>


          {/* Delivery Agent */}
          <Route path="/deliveries/summary" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <DeliveryPage />
            </RoleProtectedRoute>
          }/>
          <Route path="/update-status" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <UpdateStatus />
            </RoleProtectedRoute>
          }/>
          <Route path="/emailotp/verify-pickup" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <UpdateStatus />
            </RoleProtectedRoute>
          }/>   
          <Route path="/emailotp/verify-handover" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <UpdateStatus />
            </RoleProtectedRoute>
          }/> 
          <Route path="/emailotp/verify-delivery" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <UpdateStatus />
            </RoleProtectedRoute>
          }/>
          <Route path="/emailotp/verify-confirm-for-cloths" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <UpdateStatus />
            </RoleProtectedRoute>
          }/>          

          {/* Other Dashboards */}
          <Route path="/agent/dashboard" element={<AgentDashboard />} />
          <Route path="/admin/dashboard" element={<AdminDashboard />} />

          {/* <Route path="/unauthorized" element={<Unauthorized />} /> */}
          {/* <Route path="*" element={<NotFound />} /> */}

        </Routes>

    // </AuthProvider>
  );
}

export default App;
