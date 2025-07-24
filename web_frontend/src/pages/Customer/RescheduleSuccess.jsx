import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function RescheduleSuccess() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => navigate("/customer/dashboard"), 5000);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-white text-gray-900 px-6">
      <h1 className="text-3xl font-bold text-green-600 mb-4">Order Rescheduled!</h1>
      <p className="text-lg text-gray-700">
        Your order has been successfully rescheduled.
      </p>
      <p className="mt-2 text-gray-500">Redirecting to your dashboard in 5 seconds...</p>
    </div>
  );
}
