import React from "react";
import { Routes, Route } from "react-router-dom";
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
import PrivateRoute from "./components/PrivateRoute";
import PickupOtpVerify from "./pages/ServiceProvider/PickupOtpVerify";
import DeliveryOtpVerify from "./pages/ServiceProvider/DeliveryOtpVerify";
import ReadyForDelivery from "./pages/ServiceProvider/ReadyForDelivery";
import OtpVerificationOrders from "./pages/ServiceProvider/OtpVerificationOrders";

import RoleProtectedRoute from "./components/RoleProtectedRoute";

// Delivery Agent

// Delivery Page
import DeliveryPage from "./pages/DeliveryAgent/DeliveryPage";
import PendingDeliveries from "./components/DeliveryAgent/PendingDeliveries";
import TodayDeliveries from "./components/DeliveryAgent/TodayDeliveries";

// Update Status Page
import UpdateStatus from "./pages/DeliveryAgent/UpdateStatus";

// Payout Page
import DeliveryAgentPayout from "./pages/DeliveryAgent/DeliveryAgentPayout";
import AllPayouts from "./components/DeliveryAgent/AllPayouts";
import PendingPayouts from "./components/DeliveryAgent/PendingPayouts";

// Feedback Page
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
import PaidPayouts from "./components/DeliveryAgent/PaidPayouts";

function App() {
  return (

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

          {/* Delivery Page  */}
          <Route path="/deliveries/summary" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <DeliveryPage />
            </RoleProtectedRoute>
          }/>
          <Route path="/deliveries/pending" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <PendingDeliveries />
            </RoleProtectedRoute>
          }/>
          <Route path="/deliveries/today" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <TodayDeliveries />
            </RoleProtectedRoute>
          }/>

          {/* Update Status Page  */}
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

          {/* Payout Page   */}
          <Route path="/payouts/summary" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <DeliveryAgentPayout />
            </RoleProtectedRoute>
          }/> 
          <Route path="/payouts/all" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <AllPayouts />
            </RoleProtectedRoute>
          }/>
          <Route path="/payouts/paid" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <PaidPayouts />
            </RoleProtectedRoute>
          }/>   
          <Route path="/payouts/pending" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <PendingPayouts />
            </RoleProtectedRoute>
          }/> 
          <Route path="/payouts/pending" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <PendingPayouts />
            </RoleProtectedRoute>
          }/>  

          {/* Feedback Page  */}
          <Route path="/feedback/summary" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <DeliveryAgentFeedback />
            </RoleProtectedRoute>
          }/>
          <Route path="/feedback/list" element={
            <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
              <DeliveryAgentFeedbackList />
            </RoleProtectedRoute>
          }/> 



          {/* Admin */}
          <Route path="/revenue/summary" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <RevenuePage />
            </RoleProtectedRoute>
          }/>
          <Route path="/reports/order/summary" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <Reports />
            </RoleProtectedRoute>
          }/>
          <Route path="/provider-requests" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <Requests />
            </RoleProtectedRoute>
          }/>
          <Route path="/users/customer/graphs" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <UsersPage />
            </RoleProtectedRoute>
          }/>
          <Route path="/revenue/summary" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <SendMessage />
            </RoleProtectedRoute>
          }/>
          <Route path="/complaints/summary" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <Complaints />
            </RoleProtectedRoute>
          }/>
          <Route path="/admin-profile" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <ManageServiceListing />
            </RoleProtectedRoute>
          }/>
          <Route path="/admin-profile" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <Promotion />
            </RoleProtectedRoute>
          }/>
          <Route path="/admin-profile" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <AdminProfile />
            </RoleProtectedRoute>
          }/>
          <Route path="/configurations/save-google-key" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <Configurations />
            </RoleProtectedRoute>
          }/>
          <Route path="/configurations/save" element={
            <RoleProtectedRoute allowedRoles={["ADMIN"]}>
              <GeoCodingSetting />
            </RoleProtectedRoute>
          }/>

          {/* <Route path="/unauthorized" element={<Unauthorized />} /> */}
          {/* <Route path="*" element={<NotFound />} /> */}

        </Routes>

  );
}

export default App;
