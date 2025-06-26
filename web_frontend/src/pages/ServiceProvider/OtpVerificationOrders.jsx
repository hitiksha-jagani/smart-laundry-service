import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { useNavigate } from "react-router-dom";

const OtpVerificationOrders = () => {
  const [orders, setOrders] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOtpOrders = async () => {
      try {
        const token = localStorage.getItem("token");

        const response = await axios.get(
          "http://localhost:8080/provider/orders/pending-otp-verification",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setOrders(response.data);
      } catch (err) {
        console.error("Failed to fetch OTP orders", err);
      }
    };

    fetchOtpOrders();
  }, []);

  return (
    <div className="min-h-screen bg-orange-50 px-6 py-8">
      <h2 className="text-3xl font-bold text-orange-600 mb-6 text-center">
        Orders Requiring OTP Verification
      </h2>
      {orders.length === 0 ? (
        <p className="text-center text-gray-500">No OTP verifications pending.</p>
      ) : (
        <div className="grid md:grid-cols-2 gap-6">
          {orders.map((order) => (
            <div
              key={order.orderId}
              className="bg-white p-6 shadow rounded-2xl border border-orange-200"
            >
              <h3 className="text-xl font-semibold text-orange-700 mb-2">
                Order ID: {order.orderId}
              </h3>
              <p className="text-sm text-gray-600 mb-3">
                Customer: {order.customerName}
              </p>
              <div className="flex gap-3">
                {order.requiresPickupOtp && (
                  <button
                    onClick={() =>
                      navigate(
                        `/serviceprovider/verify-pickup-otp?orderId=${order.orderId}&agentId=${order.agentId}`
                      )
                    }
                    className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-xl text-sm"
                  >
                    Verify Pickup OTP
                  </button>
                )}
                {order.requiresDeliveryOtp && (
                  <button
                    onClick={() =>
                      navigate(
                        `/serviceprovider/verify-delivery-otp?orderId=${order.orderId}&verifierId=${order.providerId}`
                      )
                    }
                    className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-xl text-sm"
                  >
                    Verify Delivery OTP
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default OtpVerificationOrders;

