import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../../utils/axiosInstance";

export default function RescheduleOrder() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [date, setDate] = useState("");
  const [slot, setSlot] = useState("");
  const [error, setError] = useState("");

  const handleReschedule = async (e) => {
    e.preventDefault();
    if (!date || !slot) {
      return setError("Please provide both date and time slot.");
    }

    try {
      await axios.post(`/orders/reschedule/${orderId}`, { date, slot });
      navigate("/order/reschedule-success");
    } catch (err) {
      setError(err.response?.data || "Failed to reschedule the order.");
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-center items-center bg-white text-gray-900 p-6">
      <h1 className="text-2xl font-bold mb-4 text-[#4B00B5]">Reschedule Order</h1>

      <form onSubmit={handleReschedule} className="space-y-4 w-full max-w-md">
        <div>
          <label className="block mb-1 font-medium">New Pickup Date</label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="w-full border border-gray-300 px-4 py-2 rounded-md"
          />
        </div>

        <div>
          <label className="block mb-1 font-medium">New Time Slot (HH:mm)</label>
          <input
            type="time"
            value={slot}
            onChange={(e) => setSlot(e.target.value)}
            className="w-full border border-gray-300 px-4 py-2 rounded-md"
          />
        </div>

        {error && <p className="text-red-600">{error}</p>}

        <button
          type="submit"
          className="w-full bg-[#4B00B5] text-white py-2 rounded hover:bg-[#360088]"
        >
          Submit Reschedule Request
        </button>
      </form>
    </div>
  );
}
