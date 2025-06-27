import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { useNavigate } from "react-router-dom";
import ProviderSidebar from "./ServiceProviderSidebar";

export default function OtpVerificationOrders() {
  const [orders, setOrders] = useState([]);
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);

  const toggleSidebar = () => setCollapsed(prev => !prev);

  useEffect(() => {
    axios
      .get("/provider/orders/pending-otp-verification")
      .then(res => setOrders(res.data))
      .catch(console.error);
  }, []);

  return (
    <div className="flex bg-orange-50 min-h-screen">
      {/* Fixed Sidebar */}
      <div className="fixed top-0 left-0 h-full z-40">
        <ProviderSidebar collapsed={collapsed} toggleSidebar={toggleSidebar} />
      </div>

      {/* Main Content wrapper with left margin to account for fixed sidebar */}
     
        <div
          className={`flex-1 transition-all duration-300 ${
            collapsed ? "ml-20" : "ml-64"
          }`}
        >

        {/* Sticky Header */}
        <header className="bg-orange-600 text-white py-4 px-6 text-center text-2xl font-bold shadow sticky top-0 z-30">
          Pending OTP Verifications
        </header>

        {/* Scrollable Main Content */}
        <main className="p-6 space-y-4">
          {orders.length === 0 ? (
            <p className="text-center text-gray-600">No OTP verifications pending.</p>
          ) : (
            orders.map(order => (
              <div
                key={order.orderId}
                className="bg-white p-4 rounded-2xl shadow-md border border-orange-200"
              >
                <p className="text-sm text-gray-700">
                  <strong>Order ID:</strong> {order.orderId}
                </p>
                <p className="text-sm text-gray-700">
                  <strong>Customer:</strong> {order.customerName}
                </p>
                <p className="text-sm text-gray-700">
                  <strong>Status:</strong>{" "}
                  <span className="font-medium text-orange-600">{order.status}</span>
                </p>

                <div className="mt-3 flex gap-3">
                  {order.status === "ACCEPTED_BY_PROVIDER" && (
                    <button
                      onClick={() => navigate(`/provider/otp/verify/pickup/${order.orderId}`)}
                      className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md text-sm"
                    >
                      Verify Pickup OTP
                    </button>
                  )}
                  {order.status === "READY_FOR_DELIVERY" && (
                    <button
                      onClick={() => navigate(`/provider/otp/verify/delivery/${order.orderId}`)}
                      className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-md text-sm"
                    >
                      Verify Delivery OTP
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </main>
      </div>
    </div>
  );
}
