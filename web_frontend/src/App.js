import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import AutoRedirect from "./components/AutoRedirect";
import Register from "./pages/Customer/Register";
import Login from "./pages/Customer/login";
import CustomerHomePage from './pages/Customer/CustomerHomePage';
import OrderBookingWizard from "./pages/Customer/OrderBookingWizard";
import NearbyServiceProviders from "./pages/Customer/NearbyServiceProviders";
import ProviderDetail from "./pages/Customer/ProviderDetail";
import CompleteServiceProviderForm from "./pages/ServiceProvider/CompleteServiceProviderForm";
import PendingOrders from "./pages/ServiceProvider/PendingOrders";
import ActiveOrders from "./pages/ServiceProvider/ActiveOrders";
import ProviderDashboard from "./pages/ServiceProvider/ProviderDashboard";
import { AuthProvider } from "./context/AuthContext";
import PrivateRoute from "./components/PrivateRoute";
import PickupOtpVerify from "./pages/ServiceProvider/PickupOtpVerify";
import VerifyDeliveryOtp from "./pages/ServiceProvider/VerifyDeliveryOtp"; 
import OtpVerificationOrders from "./pages/ServiceProvider/OtpVerificationOrders";
import OrderBill from "./pages/Customer/OrderBill";
import OrderDetails from "./pages/Customer/OrderDetails";
import ProtectedRoute from "./components/ProtectedRoute";
import AvailablePromotions from "./pages/Customer/AvailablePromotions";
import RoleProtectedRoute from "./components/RoleProtectedRoute";
import CustomerOrderHistory from "./pages/Customer/CustomerOrderHistory";
import OrderSummary from "./pages/Customer/OrderSummary";
import PromotionSelector from "./pages/Customer/PromotionSelector";
import PaymentSuccess from "./pages/PaymentSuccess";
import PaymentCancel from "./pages/PaymentCancel";
import OrderSuccess from "./pages/Customer/OrderSuccess";
import FeedbackForm from "./pages/Customer/FeedbackForm";
import EditServiceProviderProfile from "./pages/ServiceProvider/EditServiceProviderProfile";
import TrackOrder from "./pages/Customer/TrackOrder";
import RaiseTicketForm from "./pages/Customer/RaiseTicketForm";
import TermsAndConditions from "./pages/Customer/TermsAndConditions";
import CancelOrder from "./pages/Customer/CancelOrder";
import RescheduleOrder from "./pages/Customer/RescheduleOrder";
import RescheduleSuccess from "./pages/Customer/RescheduleSuccess";
import UpdateProfile from "./pages/Customer/UpdateProfile";
import MyProfile from "./pages/Customer/MyProfile";
// Delivery Agent
import DeliveryPage from "./pages/DeliveryAgent/DeliveryPage";
import UpdateStatus from "./pages/DeliveryAgent/UpdateStatus";

// ✅ New import for completed orders page

import DeliveredOrders from "./pages/ServiceProvider/DeliveredOrders";

// Optional placeholder dashboards
const AgentDashboard = () => <h2>Delivery Agent Dashboard</h2>;
const AdminDashboard = () => <h2>Admin Dashboard</h2>;

function App() {
  return (
    <Routes>
      <Route path="/" element={<AutoRedirect />} />
      <Route path="*" element={<h2>404 - Page Not Found</h2>} />

      {/* Customer */}
      <Route path="/customer/dashboard" element={<CustomerHomePage />} />
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
      {/* <Route path="/customer/dashboard" element={<CustomerHomePage />} /> */}
      <Route path="/orders/:orderId/bill" element={<OrderDetails />} />
      <Route path="/orders/:orderId/promotions" element={<AvailablePromotions />} />
      <Route path="/customer/Orderhistory" element={<CustomerOrderHistory />} />
      <Route path="/orders/:orderId/summary" element={<OrderSummary />} />
      <Route path="/orders/:orderId/promotions" element={<PromotionSelector />} />
      <Route path="/orders/:orderId/track" element={<TrackOrder />} />
      <Route path="/payment/success" element={<PaymentSuccess />} />
      <Route path="/payment/cancel" element={<PaymentCancel />} />
      <Route path="/order/success" element={<OrderSuccess />} />
      <Route path="/feedback/:orderId" element={<FeedbackForm />} />
      <Route path="/ticket/raise" element={<PrivateRoute><RaiseTicketForm /></PrivateRoute>} />
      <Route path="/terms" element={<TermsAndConditions />} />
      <Route path="/orders/:orderId/cancel" element={<CancelOrder />} />
      <Route path="/orders/:orderId/reschedule" element={<RescheduleOrder />} />
      <Route path="/order/reschedule-success" element={<RescheduleSuccess />} />
      <Route path="/update-profile" element={<UpdateProfile />} />
      <Route path="/my-profile" element={<MyProfile />} />

      {/* Service Provider */}
      <Route path="/provider/dashboard" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><ProviderDashboard /></PrivateRoute>} />
      <Route path="/provider/pending-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><PendingOrders /></PrivateRoute>} />
      <Route path="/provider/active-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><ActiveOrders /></PrivateRoute>} />
      <Route path="/provider/completed-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><DeliveredOrders /></PrivateRoute>} /> {/* ✅ Added this route */}
      <Route path="/provider/completeprofile" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><CompleteServiceProviderForm /></PrivateRoute>} />
      <Route path="/sp/edit-profile" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><EditServiceProviderProfile /></PrivateRoute>} />
      {/* Order OTP Verification – Unified Component */}
      <Route path="/provider/otp/verify/pickup/:orderId" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><PickupOtpVerify /></PrivateRoute>} />
      <Route path="/provider/otp/verify/delivery/:orderId" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><VerifyDeliveryOtp /></PrivateRoute>} />
      <Route path="/provider/orders/verify-otps" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><OtpVerificationOrders /></PrivateRoute>} />

      {/* Delivery Agent */}
      <Route path="/deliveries/summary" element={
        <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
          <DeliveryPage />
        </RoleProtectedRoute>
      } />
      <Route path="/update-status" element={
        <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
          <UpdateStatus />
        </RoleProtectedRoute>
      } />
      <Route path="/emailotp/verify-pickup" element={
        <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
          <UpdateStatus />
        </RoleProtectedRoute>
      } />
      <Route path="/emailotp/verify-handover" element={
        <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
          <UpdateStatus />
        </RoleProtectedRoute>
      } />
      <Route path="/emailotp/verify-delivery" element={
        <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
          <UpdateStatus />
        </RoleProtectedRoute>
      } />
      <Route path="/emailotp/verify-confirm-for-cloths" element={
        <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
          <UpdateStatus />
        </RoleProtectedRoute>
      } />

      {/* Other Dashboards */}
      <Route path="/agent/dashboard" element={<AgentDashboard />} />
      <Route path="/admin/dashboard" element={<AdminDashboard />} />

    </Routes>
  );
}

export default App;
