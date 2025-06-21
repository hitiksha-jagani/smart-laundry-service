import React, { useState, useEffect } from "react";
import axios from "../../utils/axiosInstance";
import { useNavigate, useSearchParams } from "react-router-dom";

const PickupOtpVerify = () => {
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const orderId = searchParams.get("orderId");
  const agentId = searchParams.get("agentId");

  useEffect(() => {
    if (!orderId) {
      setError("Order ID is missing in the URL.");
    }
    console.log("Pickup OTP Params:", { orderId, agentId });
  }, [orderId, agentId]);

  const handleVerify = async () => {
    if (!orderId || !otp) {
      setError("Order ID or OTP is missing.");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const res = await axios.post("/emailotp/verify-pickup", null, {
        params: { orderId, otp, agentId },
      });

      setSuccess(res.data || "OTP verified successfully");

      setTimeout(() => {
        navigate("/provider/active-orders");
      }, 2000);
    } catch (err) {
      const msg = err?.response?.data || "Something went wrong";
      setError(typeof msg === "string" ? msg : msg.message || "OTP verification failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-orange-50 px-4">
      <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md border border-orange-300">
        <h2 className="text-2xl font-bold text-orange-600 mb-4 text-center">
          Pickup OTP Verification
        </h2>
        <p className="text-sm text-gray-500 text-center mb-6">
          Please enter the 6-digit OTP sent to your registered email.
        </p>

        <input
          type="text"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
          maxLength={6}
          placeholder="Enter OTP"
          className="w-full px-4 py-2 border rounded-xl focus:outline-none focus:ring-2 focus:ring-orange-400 mb-4"
        />

        {error && <p className="text-red-600 text-sm mb-3 text-center">{error}</p>}
        {success && <p className="text-green-600 text-sm mb-3 text-center">{success}</p>}

        <button
          onClick={handleVerify}
          disabled={loading || !otp}
          className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold py-2 rounded-xl transition duration-200"
        >
          {loading ? "Verifying..." : "Verify OTP"}
        </button>
      </div>
    </div>
  );
};

export default PickupOtpVerify;
