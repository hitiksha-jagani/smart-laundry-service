import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import axios from "../../utils/axiosInstance";

export default function VerifyDeliveryOtp() {
  const { orderId } = useParams();
  const { userId } = useAuth(); // ✅ Get userId from context
  const navigate = useNavigate();

  const [otp, setOtp] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleVerify = async () => {
    setMessage("");
    setError("");

    if (!otp.trim()) {
      setError("Please enter the OTP.");
      return;
    }

    if (!userId) {
      setError("User not logged in. Please re-login.");
      return;
    }

    try {
      await axios.post("/emailotp/verify-delivery", {
        orderId,
        otp,
        verifierId: userId, // ✅ You can remove this if backend gets it from JWT
      });

      setMessage("OTP verified successfully. Redirecting...");
      setTimeout(() => navigate("/provider/orders/verify-otps"), 1500);
    } catch (err) {
      console.error("OTP verification error:", err);
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        "Verification failed. Please try again.";
      setError(msg);
    }
  };

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded shadow mt-8">
      <h2 className="text-2xl font-semibold mb-4 text-center">Verify Delivery OTP</h2>

      <input
        type="text"
        value={otp}
        onChange={(e) => setOtp(e.target.value)}
        placeholder="Enter OTP"
        className="w-full p-2 border rounded mb-4"
      />

      <button
        onClick={handleVerify}
        className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
      >
        Verify OTP
      </button>

      {message && <p className="text-green-600 mt-4 text-center">{message}</p>}
      {error && <p className="text-red-600 mt-4 text-center">{error}</p>}
    </div>
  );
}
