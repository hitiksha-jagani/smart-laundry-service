import React, { useState } from "react";
import axios from "../../utils/axiosInstance";
import { useSearchParams, useNavigate } from "react-router-dom";

const ReadyForDelivery = () => {
  const [statusMessage, setStatusMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const orderId = searchParams.get("orderId"); 

  const handleMarkReady = async () => {
    setLoading(true);
    setStatusMessage("");
    setErrorMessage("");

    try {
      await axios.put(`/provider/orders/${orderId}/ready-for-delivery`);
      setStatusMessage("Order marked as READY_FOR_DELIVERY. OTP sent accordingly.");
      setTimeout(() => navigate("/serviceprovider/active-orders"), 2500);
    } catch (error) {
      const msg = error?.response?.data || "An error occurred.";
      setErrorMessage(typeof msg === "string" ? msg : msg.message || "Failed to update order.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-orange-50 px-4">
      <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-md border border-orange-300">
        <h2 className="text-2xl font-bold text-orange-600 mb-4 text-center">
          Mark Order Ready
        </h2>

        <p className="text-sm text-gray-600 mb-6 text-center">
          Are you ready to mark this order as{" "}
          <span className="font-semibold text-orange-700">READY_FOR_DELIVERY</span>?
        </p>

        {statusMessage && (
          <p className="text-green-600 text-sm text-center mb-4">{statusMessage}</p>
        )}
        {errorMessage && (
          <p className="text-red-600 text-sm text-center mb-4">{errorMessage}</p>
        )}

        <button
          onClick={handleMarkReady}
          disabled={loading}
          className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold py-2 rounded-xl transition duration-200"
        >
          {loading ? "Processing..." : "Confirm Ready for Delivery"}
        </button>
      </div>
    </div>
  );
};

export default ReadyForDelivery;
