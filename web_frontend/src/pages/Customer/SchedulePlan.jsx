import React, { useState, useEffect } from "react";
import axios from "../../utils/axiosInstance";
import PrimaryButton from "../../components/PrimaryButton";

export default function SchedulePlan({
  dummyOrderId,
  onNext,
  onPrev,
  providerId,
  initialOrderData,
  setInitialOrderData,
}) {
  const [error, setError] = useState("");
  const [availablePlans, setAvailablePlans] = useState([]);
  const [loading, setLoading] = useState(false);

  const { schedulePlan, paymentOption } = initialOrderData;

  useEffect(() => {
    const fetchAvailablePlans = async () => {
      try {
        const res = await axios.get(`/schedule-plans/${providerId}`);
        setAvailablePlans(res.data || []);
      } catch (err) {
        console.error("Error fetching schedule plans:", err);
        setError("Could not load schedule plan options.");
      }
    };

    if (providerId) fetchAvailablePlans();
  }, [providerId]);

  const setField = (field, value) => {
    setInitialOrderData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async () => {
    if (!schedulePlan) {
      setError("Please select a schedule plan.");
      return;
    }

    const payEachDelivery = paymentOption === "EACH";
    const payLastDelivery = paymentOption === "LAST";

    if (payEachDelivery === payLastDelivery) {
      setError("Please select exactly one payment option.");
      return;
    }

    try {
      setLoading(true);
      const response = await axios.post(`/orders/schedule-plan/${dummyOrderId}`, {
        schedulePlan,
        payEachDelivery,
        payLastDelivery,
      });

      if (response.status === 200) {
        onNext();
      }
    } catch (err) {
      console.error("Error saving schedule plan:", err);
      setError("Failed to save schedule plan. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-xl mx-auto bg-white shadow rounded">
      <h2 className="text-xl font-semibold mb-4">Step 2: Select Schedule Plan</h2>

      <div className="space-y-4">
        <div>
          <label className="block font-medium mb-1">Schedule Plan:</label>
          <select
            value={schedulePlan}
            onChange={(e) => setField("schedulePlan", e.target.value)}
            className="w-full p-2 border border-gray-300 rounded"
          >
            <option value="">-- Select --</option>
            {availablePlans.map((plan) => (
              <option key={plan} value={plan}>
                {plan.charAt(0) + plan.slice(1).toLowerCase().replace("_", " ")}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block font-medium mb-1">Payment Option:</label>
          <label className="flex items-center space-x-2">
            <input
              type="radio"
              name="paymentOption"
              value="EACH"
              checked={paymentOption === "EACH"}
              onChange={() => setField("paymentOption", "EACH")}
            />
            <span>Pay After Each Delivery</span>
          </label>
          <label className="flex items-center space-x-2 mt-2">
            <input
              type="radio"
              name="paymentOption"
              value="LAST"
              checked={paymentOption === "LAST"}
              onChange={() => setField("paymentOption", "LAST")}
            />
            <span>Pay After Last Delivery</span>
          </label>
        </div>

        {error && <p className="text-red-600 font-medium">{error}</p>}

        <div className="flex justify-between pt-4">
          <button
            onClick={onPrev}
            className="px-6 py-3 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition"
          >
            Previous
          </button>

          <PrimaryButton onClick={handleSubmit} disabled={loading}>
            {loading ? "Saving..." : "Next"}
          </PrimaryButton>
        </div>
      </div>
    </div>
  );
}
