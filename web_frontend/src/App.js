import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./pages/Customer/Register";
import Login from "./pages/Customer/login";
import CustomerHomePage from './pages/Customer/CustomerHomePage';
import OrderBookingWizard from "./pages/Customer/OrderBookingWizard";
import NearbyServiceProviders from "./pages/Customer/NearbyServiceProviders"; 
import ProviderDetail from "./pages/Customer/ProviderDetail";
import CompleteServiceProviderForm from "./pages/ServiceProvider/CompleteServiceProviderForm";

import ProtectedRoute from "./components/ProtectedRoute";
import RoleProtectedRoute from "./components/RoleProtectedRoute";

// Delivery Agent
import DeliveryPage from "./pages/DeliveryAgent/DeliveryPage";


// Placeholder dashboards
const ProviderDashboard = () => <h2>Service Provider Dashboard</h2>;
const AgentDashboard = () => <h2>Delivery Agent Dashboard</h2>;
const AdminDashboard = () => <h2>AdminDashboard</h2>;

function App() {
  return (
    // <BrowserRouter>
      <Routes>
         {/* Customer */}
        <Route path="/" element={<CustomerHomePage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/order/book" element={<OrderBookingWizard />} />
        <Route path="/service-providers" element={<NearbyServiceProviders />} />
        <Route path="/provider/:providerId" element={<ProviderDetail />} />

        {/* Dashboards */}
        <Route path="/customer/dashboard" element={<CustomerHomePage />} />
        <Route path="/provider/dashboard" element={<ProviderDashboard />} />
        <Route path="/agent/dashboard" element={<AgentDashboard />} />
        <Route path="/admin/dashboard" element={<AdminDashboard />} />

       {/* Service Provider */}
       <Route path="/sp/completeprofile" element={<CompleteServiceProviderForm />} />

        {/* Delivery Agent */}
        {/* <Route path="/deliveries/summary" element={<DeliveryPage />} /> */}

        <Route path="/deliveries/summary" element={
          <RoleProtectedRoute allowedRoles={["DELIVERY_AGENT"]}>
            <DeliveryPage />
          </RoleProtectedRoute>
        }/>

        {/* <Route path="/unauthorized" element={<Unauthorized />} /> */}
        {/* <Route path="*" element={<NotFound />} /> */}

      </Routes>
  );
}

export default App;
