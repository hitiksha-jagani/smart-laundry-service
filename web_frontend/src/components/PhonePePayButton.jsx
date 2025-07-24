import React from "react";
import axios from "../utils/axiosInstance";

export default function PhonePePayButton({ orderId, billId, finalPrice }) {
  const handlePayment = async () => {
    try {
      const res = await axios.post(
        `/payment/phonepe/initiate?orderId=${orderId}`
      );

      if (res.data.redirectUrl) {
        window.location.href = res.data.redirectUrl;
      } else {
        alert("Payment URL not received from PhonePe.");
      }
    } catch (err) {
      console.error("❌ PhonePe payment initiation failed:", err);
      alert("Payment initiation failed. Please try again.");
    }
  };

  return (
    <button
      onClick={handlePayment}
      className="px-6 py-2 bg-purple-600 text-white rounded hover:bg-purple-700"
    >
      Pay with PhonePe
    </button>
  );
}
