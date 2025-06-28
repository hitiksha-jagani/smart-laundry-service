import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import AutoRedirect from "./components/AutoRedirect";

// Auth & Customer Pages
import Register from "./pages/Customer/Register";
import Login from "./pages/Customer/login";
import CustomerHomePage from './pages/Customer/CustomerHomePage';
import OrderBookingWizard from "./pages/Customer/OrderBookingWizard";
import NearbyServiceProviders from "./pages/Customer/NearbyServiceProviders";
import ProviderDetail from "./pages/Customer/ProviderDetail";
import OrderBill from "./pages/Customer/OrderBill";
import OrderDetails from "./pages/Customer/OrderDetails";
import AvailablePromotions from "./pages/Customer/AvailablePromotions";
import CustomerOrderHistory from "./pages/Customer/CustomerOrderHistory";
import OrderSummary from "./pages/Customer/OrderSummary";
import PromotionSelector from "./pages/Customer/PromotionSelector";
import PaymentSuccess from "./pages/PaymentSuccess";
import PaymentCancel from "./pages/PaymentCancel";
import OrderSuccess from "./pages/Customer/OrderSuccess";
import FeedbackForm from "./pages/Customer/FeedbackForm";
import RaiseTicketForm from "./pages/Customer/RaiseTicketForm";
import TermsAndConditions from "./pages/Customer/TermsAndConditions";
import CancelOrder from "./pages/Customer/CancelOrder";
import RescheduleOrder from "./pages/Customer/RescheduleOrder";
import RescheduleSuccess from "./pages/Customer/RescheduleSuccess";
import UpdateProfile from "./pages/Customer/UpdateProfile";
import MyProfile from "./pages/Customer/MyProfile";
import TrackOrder from "./pages/Customer/TrackOrder";

// Service Provider
import CompleteServiceProviderForm from "./pages/ServiceProvider/CompleteServiceProviderForm";
import PendingOrders from "./pages/ServiceProvider/PendingOrders";
import ActiveOrders from "./pages/ServiceProvider/ActiveOrders";
import DeliveredOrders from "./pages/ServiceProvider/DeliveredOrders";
import ProviderDashboard from "./pages/ServiceProvider/ProviderDashboard";
import EditServiceProviderProfile from "./pages/ServiceProvider/EditServiceProviderProfile";
import PickupOtpVerify from "./pages/ServiceProvider/PickupOtpVerify";
import VerifyDeliveryOtp from "./pages/ServiceProvider/VerifyDeliveryOtp";
import OtpVerificationOrders from "./pages/ServiceProvider/OtpVerificationOrders";

// Delivery Agent
import DeliveryPage from "./pages/DeliveryAgent/DeliveryPage";
import PendingDeliveries from "./components/DeliveryAgent/PendingDeliveries";
import TodayDeliveries from "./components/DeliveryAgent/TodayDeliveries";
import UpdateStatus from "./pages/DeliveryAgent/UpdateStatus";
import DeliveryAgentPayout from "./pages/DeliveryAgent/DeliveryAgentPayout";
import AllPayouts from "./components/DeliveryAgent/AllPayouts";
import PendingPayouts from "./components/DeliveryAgent/PendingPayouts";
import PaidPayouts from "./components/DeliveryAgent/PaidPayouts";
import DeliveryAgentFeedback from "./pages/DeliveryAgent/DeliveryAgentFeedback";
import DeliveryAgentFeedbackList from "./components/DeliveryAgent/DeliveryAgentFeedbackList";

// Admin
import RevenuePage from "./pages/Admin/RevenuePage";
import Reports from "./pages/Admin/Reports";
import Requests from "./pages/Admin/Requests";
import UsersPage from "./pages/Admin/UsersPage";
import SendMessage from "./pages/Admin/SendMessage";
import Complaints from "./pages/Admin/Complaints";
import ManageServiceListing from "./pages/Admin/ManageServiceListing";
import Promotion from "./pages/Admin/PromotionPage";
import AdminProfile from "./pages/Admin/AdminProfile";
import Configurations from "./pages/Admin/Configurations";
import GeoCodingSetting from "./pages/Admin/GeoCodingSetting";

// Protected Routes
import PrivateRoute from "./components/PrivateRoute";
import RoleProtectedRoute from "./components/RoleProtectedRoute";

// Dummy dashboards
const AgentDashboard = () => <h2>Delivery Agent Dashboard</h2>;
const AdminDashboard = () => <h2>Admin Dashboard</h2>;

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Common */}
        <Route path="/" element={<AutoRedirect />} />
        <Route path="*" element={<h2>404 - Page Not Found</h2>} />

        {/* Auth */}
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

        {/* Customer */}
        <Route path="/customer/dashboard" element={<CustomerHomePage />} />
        <Route path="/order/book" element={<PrivateRoute><OrderBookingWizard /></PrivateRoute>} />
        <Route path="/service-providers" element={<NearbyServiceProviders />} />
        <Route path="/provider/:providerId" element={<ProviderDetail />} />
        <Route path="/orders/:orderId/bill" element={<OrderDetails />} />
        <Route path="/orders/:orderId/promotions" element={<AvailablePromotions />} />
        <Route path="/customer/Orderhistory" element={<CustomerOrderHistory />} />
        <Route path="/orders/:orderId/summary" element={<OrderSummary />} />
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
        <Route path="/provider/completed-orders" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><DeliveredOrders /></PrivateRoute>} />
        <Route path="/provider/completeprofile" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><CompleteServiceProviderForm /></PrivateRoute>} />
        <Route path="/sp/edit-profile" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><EditServiceProviderProfile /></PrivateRoute>} />
        <Route path="/provider/otp/verify/pickup/:orderId" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><PickupOtpVerify /></PrivateRoute>} />
        <Route path="/provider/otp/verify/delivery/:orderId" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><VerifyDeliveryOtp /></PrivateRoute>} />
        <Route path="/provider/orders/verify-otps" element={<PrivateRoute roles={["SERVICE_PROVIDER"]}><OtpVerificationOrders /></PrivateRoute>} />

        {/* Delivery Agent */}
        <Route path="/deliveries/summary" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><DeliveryPage /></RoleProtectedRoute>} />
        <Route path="/deliveries/pending" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><PendingDeliveries /></RoleProtectedRoute>} />
        <Route path="/deliveries/today" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><TodayDeliveries /></RoleProtectedRoute>} />
        <Route path="/update-status" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><UpdateStatus /></RoleProtectedRoute>} />
        <Route path="/emailotp/verify-pickup" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><UpdateStatus /></RoleProtectedRoute>} />
        <Route path="/emailotp/verify-handover" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><UpdateStatus /></RoleProtectedRoute>} />
        <Route path="/emailotp/verify-delivery" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><UpdateStatus /></RoleProtectedRoute>} />
        <Route path="/emailotp/verify-confirm-for-cloths" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><UpdateStatus /></RoleProtectedRoute>} />
        <Route path="/payouts/summary" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><DeliveryAgentPayout /></RoleProtectedRoute>} />
        <Route path="/payouts/all" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><AllPayouts /></RoleProtectedRoute>} />
        <Route path="/payouts/pending" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><PendingPayouts /></RoleProtectedRoute>} />
        <Route path="/payouts/paid" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><PaidPayouts /></RoleProtectedRoute>} />
        <Route path="/feedback/summary" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><DeliveryAgentFeedback /></RoleProtectedRoute>} />
        <Route path="/feedback/list" element={<RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}><DeliveryAgentFeedbackList /></RoleProtectedRoute>} />

        {/* Admin */}
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        <Route path="/revenue/summary" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><RevenuePage /></RoleProtectedRoute>} />
        <Route path="/reports/order/summary" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><Reports /></RoleProtectedRoute>} />
        <Route path="/provider-requests" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><Requests /></RoleProtectedRoute>} />
        <Route path="/users/customer/graphs" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><UsersPage /></RoleProtectedRoute>} />
        <Route path="/send-message" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><SendMessage /></RoleProtectedRoute>} />
        <Route path="/complaints/summary" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><Complaints /></RoleProtectedRoute>} />
        <Route path="/admin/manage-listing" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><ManageServiceListing /></RoleProtectedRoute>} />
        <Route path="/admin/promotions" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><Promotion /></RoleProtectedRoute>} />
        <Route path="/admin-profile" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><AdminProfile /></RoleProtectedRoute>} />
        <Route path="/configurations/save-google-key" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><Configurations /></RoleProtectedRoute>} />
        <Route path="/configurations/save" element={<RoleProtectedRoute allowedRoles={["ADMIN"]}><GeoCodingSetting /></RoleProtectedRoute>} />

        {/* Placeholder */}
        <Route path="/agent/dashboard" element={<AgentDashboard />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
