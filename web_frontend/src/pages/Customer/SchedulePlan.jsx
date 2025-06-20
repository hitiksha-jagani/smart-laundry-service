import React, { useState } from "react";
import axios from "../../utils/axiosInstance";

export default function SchedulePlan({ dummyOrderId, onNext, onPrev }) {
  const [schedulePlan, setSchedulePlan] = useState("");
  const [paymentOption, setPaymentOption] = useState(""); // "EACH" or "LAST"
  const [error, setError] = useState("");

 const handleSubmit = async () => {
  if (!schedulePlan) {
    setError("Please select a schedule plan.");
    return;
  }

  const payEachDelivery = paymentOption === "EACH";
  const payLastDelivery = paymentOption === "LAST";

  // XOR validation: Only one should be true
  if (payEachDelivery === payLastDelivery) {
    setError("Please select exactly one payment option.");
    return;
  }

  try {
    const response = await axios.post(
      `/customer/schedule-plan/${dummyOrderId}`,
      {
        schedulePlan,
        payEachDelivery,
        payLastDelivery,
      }
    );

    if (response.status === 200) {
      onNext();
    }
  } catch (err) {
    console.error("Error saving schedule plan:", err);
    setError("Failed to save schedule plan. Please try again.");
  }
};


  return (
    <div className="p-6 max-w-xl mx-auto bg-white shadow rounded">
      <h2 className="text-xl font-semibold mb-4">Step 2: Select Schedule Plan</h2>

      <div className="space-y-4">
        {/* Schedule Plan Selection */}
        <div>
          <label className="block font-medium mb-1">Schedule Plan:</label>
          <select
            value={schedulePlan}
            onChange={(e) => setSchedulePlan(e.target.value)}
            className="w-full p-2 border rounded"
          >
            <option value="">-- Select --</option>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
          </select>
        </div>

        {/* Payment Option Radio Buttons */}
        <div>
          <label className="block font-medium mb-1">Payment Option:</label>

          <label className="flex items-center space-x-2">
            <input
              type="radio"
              name="paymentOption"
              value="EACH"
              checked={paymentOption === "EACH"}
              onChange={() => setPaymentOption("EACH")}
            />
            <span>Pay After Each Delivery</span>
          </label>

          <label className="flex items-center space-x-2 mt-2">
            <input
              type="radio"
              name="paymentOption"
              value="LAST"
              checked={paymentOption === "LAST"}
              onChange={() => setPaymentOption("LAST")}
            />
            <span>Pay After Last Delivery</span>
          </label>
        </div>

        {/* Error Message */}
        {error && <p className="text-red-600 font-medium">{error}</p>}

        {/* Buttons */}
        <div className="flex justify-between pt-4">
          <button
            onClick={onPrev}
            className="px-4 py-2 bg-gray-300 text-black rounded hover:bg-gray-400"
          >
            Previous
          </button>

          <button
            onClick={handleSubmit}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Save & Continue
          </button>
        </div>
      </div>
    </div>
  );
}
