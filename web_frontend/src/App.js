import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
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
import NotAvailablePage from "./pages/NotAvailablePage";

// Delivery Agent
// Delivery Page
import DeliveryPage from "./pages/DeliveryAgent/DeliveryPage";
import PendingDeliveries from "./components/DeliveryAgent/PendingDeliveries";
import TodayDeliveries from "./components/DeliveryAgent/TodayDeliveries";
// Manage Availability Page
import Availability from "./pages/DeliveryAgent/Availability";
import SavedAvailability from "./pages/DeliveryAgent/SavedAvailability";
// Update status page
import UpdateStatus from "./pages/DeliveryAgent/UpdateStatus";
// Payout page
import DeliveryAgentPayout from "./pages/DeliveryAgent/DeliveryAgentPayout";
import AllPayouts from "./components/DeliveryAgent/AllPayouts";
import PendingPayouts from "./components/DeliveryAgent/PendingPayouts";
import PaidPayouts from "./components/DeliveryAgent/PaidPayouts";
// Feedback Page
import DeliveryAgentFeedback from "./pages/DeliveryAgent/DeliveryAgentFeedback";
import DeliveryAgentFeedbackList from "./components/DeliveryAgent/DeliveryAgentFeedbackList";
// Profile Page
import CompleteDeliveryAgentProfilePage from "./pages/DeliveryAgent/CompleteDeliveryAgentProfilePage";
import DeliveryAgentProfile from "./pages/DeliveryAgent/DeliveryAgentProfile";
import EditAgentProfilePage from "./pages/DeliveryAgent/EditAgentProfile";
import ChangeAgentPasswordPage from "./pages/DeliveryAgent/ChnageAgentPasswordPage";

// Admin
import SetupAdmin from './pages/Admin/SetupAdmin';
// Revenue Page
import RevenueSummaryPage from "./pages/Admin/RevenueSummaryPage";
import TotalRevenuePage from "./pages/Admin/TotalRevenuePage";
import ProviderRevenuePage from "./pages/Admin/ProviderRevenuePage";
import RevenueBreakdownPage from "./pages/Admin/RevenueBreakdownPage";
import RevenueTrendsPage from "./pages/Admin/RevenueTrendsPage";
import AgentRevenuePage from "./pages/Admin/AgentRevenuePage";
// Report Page
import OrderReportPage from "./pages/Admin/OrderReportPage";
import UserWiseOrderReportPage from "./pages/Admin/UserWiseOrderReportPage";
import UserwiseGraphReportPage from "./pages/Admin/UserwiseGraphReportPage";
// Request Page
import ServiceProviderRequestPage from "./pages/Admin/ServiceProviderRequestPage";
import DeliveryAgentRequestPage from "./pages/Admin/DeliveryAgentRequestPage";
// User Page
import CustomerGraphPage from "./pages/Admin/CustomerGraphPage";
import ServiceProviderGraphPage from "./pages/Admin/ServiceProviderGraphPage";
import DeliveryAgentGraphPage from "./pages/Admin/DeliveryAgentGraphPage";
import CustomerTablePage from "./pages/Admin/CustomerTablePage";
import ServiceProviderTablePage from "./pages/Admin/ServiceProviderTablePage";
import DeliveryAgentTablePage from "./pages/Admin/DeliveryAgentTablePage";
import DeliveryAgentTableMoreDetailPage from "./pages/Admin/DeliveryAgentTableMoreDetailPage";
// Service Page
import ServiceSummaryPage from "./pages/Admin/ServiceSummaryPage";
import ItemPage from "./pages/Admin/ItemPage";
import SubServicePage from "./pages/Admin/SubServicePage";
import ServicePage from "./pages/Admin/ServicePage";
// Profile Page
import AdminProfile from "./pages/Admin/AdminProfile";
import EditProfilePage from "./pages/Admin/EditProfilePage";
import ChangePasswordPage from "./pages/Admin/ChangePasswordPage";
// Configuration Page
import RevenueBreakdownConfigPage from "./pages/Admin/RevenueBreakdownConfigPage";
import AgentEarningConfigPage from "./pages/Admin/AgentEarningConfigPage";
import GeoCodingSetting from "./pages/Admin/GeoCodingSetting";

// Protected Routes
import PrivateRoute from "./components/PrivateRoute";
import RoleProtectedRoute from "./components/RoleProtectedRoute";
import ServiceProviderTableMoreDetailPage from "./pages/Admin/ServiceProviderTableMoreDetailPage";
import CustomerTableMoreDetailPage from "./pages/Admin/CustomerTableMoreDetailPage";

function App() {

  const [setupRequired, setSetupRequired] = useState(null);

  useEffect(() => {
    fetch('/setup-admin')
      .then(res => res.json())
      .then(setup => setSetupRequired(setup))
      .catch(err => {
        console.error("Setup check failed", err);
        setSetupRequired(false); 
      });
  }, []);

  if (setupRequired === null) return <div>Loading...</div>;

  return (
      <Routes>

        {setupRequired ? (

          <>
            <Route path="/setup-admin" element={<SetupAdmin />} />
            <Route path="*" element={<Navigate to="/setup-admin" replace />} />
          </>

          ) : (

          <>
          
            <Route path="/" element={<Login />} />
            <Route path="/setup-admin" element={<Navigate to="/" replace />} />
            
            {/* other routes */}

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

            {/* Manage Availability */}
            <Route path="/availability/manage" element={
              <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                <Availability />
              </RoleProtectedRoute>
            }/>
            <Route path="/availability/saved" element={
              <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                <SavedAvailability />
              </RoleProtectedRoute>
            }/>
            <Route path="/availability/manage/delete/:id" element={
              <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                <TodayDeliveries />
              </RoleProtectedRoute>
            }/>
            <Route path="/availability/manage/edit/:id" element={
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

            {/* Profile Page */}
            <Route path="/profile/complete" element={
              <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                <CompleteDeliveryAgentProfilePage />
              </RoleProtectedRoute>
            }/> 
            <Route path="/profile/detail" element={
              <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                <DeliveryAgentProfile />
              </RoleProtectedRoute>
            }/> 
            <Route path="/profile/detail/edit" element={
              <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                <EditAgentProfilePage />
              </RoleProtectedRoute>
            }/> 
            <Route path="/profile/detail/change-password" element={
              <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
                <ChangeAgentPasswordPage />
              </RoleProtectedRoute>
            }/> 
            
            {/* Feature Not Available Page */}
            <Route path="/not-available" element={<NotAvailablePage />} />

            {/* Admin */}
            <Route path="/setup-admin" element={<SetupAdmin />} />

            {/* Revenue Page */}
            <Route path="/revenue/summary" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <RevenueSummaryPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/revenue/total-revenue" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <TotalRevenuePage />
              </RoleProtectedRoute>
            }/>
            <Route path="/revenue/provider-analytics-list" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ProviderRevenuePage />
              </RoleProtectedRoute>
            }/>
            <Route path="/revenue/breakdown/graph" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <RevenueBreakdownPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/revenue/trends" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <RevenueTrendsPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/revenue/provider-analytics-list" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ProviderRevenuePage />
              </RoleProtectedRoute>
            }/>
            <Route path="/revenue/agent-analytics-list" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <AgentRevenuePage />
              </RoleProtectedRoute>
            }/>

            {/* Report Page */}
            <Route path="/reports/order/trend" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <OrderReportPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/reports/order/user-report-list" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <UserWiseOrderReportPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/reports/order/user-report-list/graph/:id" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <UserwiseGraphReportPage />
              </RoleProtectedRoute>
            }/>

            {/* Request Page */}
            <Route path="/provider-requests" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ServiceProviderRequestPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/provider/:type/:userID" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ServiceProviderRequestPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/agent-requests" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <DeliveryAgentRequestPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/agent/:type/:userID" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <DeliveryAgentRequestPage />
              </RoleProtectedRoute>
            }/>
            
            {/* User Page */}
            <Route path="/users/customer/graphs" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <CustomerGraphPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/users/customers/table" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <CustomerTablePage />
              </RoleProtectedRoute>
            }/> 
            <Route path="/users/service-providers/graphs" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ServiceProviderGraphPage />
              </RoleProtectedRoute>
            }/> 
            <Route path="/users/service-providers/table" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ServiceProviderTablePage />
              </RoleProtectedRoute>
            }/> 
            <Route path="/users/delivery-agents/graphs" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <DeliveryAgentGraphPage />
              </RoleProtectedRoute>
            }/>  
            <Route path="/users/delivery-agents/table" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <DeliveryAgentTablePage />
              </RoleProtectedRoute>
            }/> 
            <Route path="/users/delivery-agents/table/more" element={<DeliveryAgentTableMoreDetailPage />} />
            <Route path="/users/service-providers/table/more" element={<ServiceProviderTableMoreDetailPage />} />
            <Route path="/users/customer/table/more" element={<CustomerTableMoreDetailPage />} />

            {/* Service Page */}
            <Route path="/service/summary" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ServiceSummaryPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/service/add-items" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ItemPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/service/add-services" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ServicePage />
              </RoleProtectedRoute>
            }/>
            <Route path="/service/add-subservices" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <SubServicePage />
              </RoleProtectedRoute>
            }/>

            {/* Profile Page */}
            <Route path="/admin-profile" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <AdminProfile />
              </RoleProtectedRoute>
            }/>
            <Route path="/admin-profile/edit" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <EditProfilePage />
              </RoleProtectedRoute>
            }/>
            <Route path="/admin-profile/change-password" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <ChangePasswordPage />
              </RoleProtectedRoute>
            }/>

            {/* Configuration Page */}
            <Route path="/configurations/providers" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <GeoCodingSetting />
              </RoleProtectedRoute>
            }/>
            <Route path="/configurations/revenue-breakdown" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <RevenueBreakdownConfigPage />
              </RoleProtectedRoute>
            }/>
            <Route path="/configurations/agent-earnings" element={
              <RoleProtectedRoute allowedRoles={["ADMIN"]}>
                <AgentEarningConfigPage />
              </RoleProtectedRoute>
            }/>

            <Route path="*" element={<h1>404 Not Found</h1>} />

          </>
        )}

      </Routes>
   
  );
}

export default App;
