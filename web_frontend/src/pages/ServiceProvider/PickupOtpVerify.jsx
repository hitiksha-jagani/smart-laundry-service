
import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../../utils/axiosInstance";

export default function VerifyPickupOtp() {
  const { orderId } = useParams();
  const [otp, setOtp] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleVerify = async () => {
    try {
      await axios.post("/emailotp/verify-pickup", { orderId, otp });
      setMessage("Pickup OTP verified! Redirecting...");
      setTimeout(() => navigate("/provider/orders/verify-otps"), 1500); 
    } catch (err) {
      setMessage("Verification failed: " + (err.response?.data || "Try again."));
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-semibold mb-4">Verify Pickup OTP</h2>
      <input
        className="border p-2 mr-2"
        placeholder="Enter OTP"
        value={otp}
        onChange={(e) => setOtp(e.target.value)}
      />
      <button className="bg-blue-600 text-white px-4 py-2" onClick={handleVerify}>
        Verify
      </button>
      {message && <p className="mt-4">{message}</p>}
    </div>
  );
}
