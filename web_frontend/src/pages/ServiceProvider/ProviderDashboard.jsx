// src/pages/ServiceProvider/ProviderDashboard.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaClock, FaRunning, FaCheckCircle, FaKey } from "react-icons/fa";
import ProviderSidebar from "./ServiceProviderSidebar";

export default function ProviderDashboard() {
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);

  const toggleSidebar = () => setCollapsed((prev) => !prev);

  const dashboardItems = [
    {
      title: "Pending Orders",
      description: "Orders waiting for your confirmation",
      icon: <FaClock size={28} />,
      route: "/provider/pending-orders",
      color: "bg-orange-100 text-orange-600",
    },
    {
      title: "Active Orders",
      description: "Orders in progress or picked up",
      icon: <FaRunning size={28} />,
      route: "/provider/active-orders",
      color: "bg-orange-200 text-orange-700",
    },
    {
      title: "Completed / Delivered",
      description: "All completed and delivered orders",
      icon: <FaCheckCircle size={28} />,
      route: "/provider/completed-orders",
      color: "bg-orange-300 text-orange-800",
    },
    {
      title: "Verify OTPs",
      description: "Pickup & Delivery OTP verification",
      icon: <FaKey size={28} />,
      route: "/provider/orders/verify-otps",
      color: "bg-orange-100 text-orange-800",
    },
  ];

  return (
    <div className="flex">
      {/* Sidebar */}
      <ProviderSidebar collapsed={collapsed} toggleSidebar={toggleSidebar} />

      {/* Dashboard Content */}
      <div
  className={`flex-1 min-h-screen bg-orange-50 transition-all duration-300 ${
    collapsed ? "ml-20" : "ml-64"
  }`}
>

        <header className="bg-orange-600 text-white py-4 px-6 text-center text-3xl font-bold shadow">
          Service Provider Dashboard
        </header>

        <div className="p-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {dashboardItems.map((item, idx) => (
            <div
              key={idx}
              onClick={() => navigate(item.route)}
              className={`rounded-2xl p-6 shadow cursor-pointer hover:shadow-xl transition duration-200 ${item.color}`}
            >
              <div className="flex items-center gap-4 mb-4">
                <div className="text-3xl">{item.icon}</div>
                <h3 className="text-xl font-semibold">{item.title}</h3>
              </div>
              <p className="text-sm">{item.description}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
