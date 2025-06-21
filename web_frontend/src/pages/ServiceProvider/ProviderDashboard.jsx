import React from "react";
import { useNavigate } from "react-router-dom";
import { FaClock, FaRunning, FaCheckCircle, FaBoxOpen } from "react-icons/fa";
import { FaKey } from "react-icons/fa";
export default function ProviderDashboard() {
  const navigate = useNavigate();

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
      title: "Ready for Delivery",
      description: "Mark prepared orders as ready",
      icon: <FaBoxOpen size={28} />,
      route: "/serviceprovider/select-ready-order",
      color: "bg-orange-100 text-orange-700",
    },
    {
      title: "Verify OTPs",
      description: "Pickup & Delivery OTP verification",
      icon: <FaKey size={28} />,
      route: "/serviceprovider/otp-verification-orders",
      color: "bg-orange-100 text-orange-800",
    },
  ];

  return (
    <div className="min-h-screen bg-orange-50">
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
  );
}
