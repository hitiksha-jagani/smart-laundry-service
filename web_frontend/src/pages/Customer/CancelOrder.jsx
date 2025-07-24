import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../../utils/axiosInstance";

export default function CancelOrder() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [message, setMessage] = useState("Cancelling your order...");
  const [error, setError] = useState(null);

  useEffect(() => {
    const cancelOrder = async () => {
      try {
        const res = await axios.post(`/orders/cancel/${orderId}`);
        setMessage("Order cancelled successfully. Redirecting to home page...");
        setTimeout(() => navigate("/customer/dashboard"), 5000);
      } catch (err) {
        setError(
          err.response?.data || "Failed to cancel order. Please try again later."
        );
        setTimeout(() => navigate("/customer/dashboard"), 5000);
      }
    };

    cancelOrder();
  }, [orderId, navigate]);

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-white text-gray-800 px-4">
      <h1 className="text-3xl font-bold text-[#FF4774] mb-4">Cancel Order</h1>
      <div className="text-lg text-center">
        {error ? (
          <p className="text-red-600">{error}</p>
        ) : (
          <p className="text-green-600">{message}</p>
        )}
        <p className="mt-2 text-gray-600">You’ll be redirected in 5 seconds...</p>
      </div>
    </div>
  );
}
