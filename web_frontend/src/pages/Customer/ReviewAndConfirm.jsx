import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";

export default function ReviewAndConfirm({ dummyOrderId, onOrderCreated }) {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // Fetch order summary from Redis
  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const res = await axios.get("/order/summary-from-redis", {
          params: { dummyOrderId },
        });
        setSummary(res.data);
      } catch (err) {
        console.error(err);
        setError("Failed to load order summary. Please try again.");
      }
    };

    fetchSummary();
  }, [dummyOrderId]);

  const handleConfirm = async () => {
    setError("");
    setLoading(true);
    try {
      const res = await axios.post(`/orders/place/${dummyOrderId}`);
      onOrderCreated(res.data);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to create order. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  if (!summary) return <p className="p-4 text-gray-700">Loading order summary...</p>;

  return (
    <div className="p-4">
      <h2 className="text-xl font-semibold mb-4">Step 4: Review & Confirm</h2>

      <div className="bg-gray-100 dark:bg-gray-800 rounded-lg p-4 mb-4">
        <h3 className="text-lg font-bold mb-2">Service Provider</h3>
        <p>{summary.serviceProviderName || "N/A"}</p>
        <p className="text-sm text-gray-500">{summary.serviceProviderContact || "N/A"}</p>
      </div>

      <div className="bg-gray-100 dark:bg-gray-800 rounded-lg p-4 mb-4">
        <h3 className="text-lg font-bold mb-2">Items</h3>
        {summary.items?.length > 0 ? (
          <ul className="list-disc pl-5">
            {summary.items.map((item, i) => (
              <li key={i}>
                {item.itemName} × {item.quantity} — ₹
                {item.finalPrice ? item.finalPrice.toFixed(2) : "0.00"}
              </li>
            ))}
          </ul>
        ) : (
          <p>No items found.</p>
        )}
      </div>

      <div className="bg-gray-100 dark:bg-gray-800 rounded-lg p-4 mb-4">
        <h3 className="text-lg font-bold mb-2">Contact Info</h3>
        <p><strong>Name:</strong> {summary.contactName}</p>
        <p><strong>Phone:</strong> {summary.contactPhone}</p>
        <p><strong>Address:</strong> {summary.contactAddress}</p>
      </div>

      <div className="flex justify-between items-center text-lg font-semibold mt-4">
        <span>Total Amount:</span>
        <span>₹{summary.totalAmount ? summary.totalAmount.toFixed(2) : "0.00"}</span>
      </div>

      {error && <p className="text-red-600 mt-3 font-medium">{error}</p>}

      <button
        onClick={handleConfirm}
        disabled={loading}
        className={`mt-4 bg-green-600 text-white py-2 px-6 rounded transition ${
          loading ? "opacity-60 cursor-not-allowed" : "hover:bg-green-700"
        }`}
      >
        {loading ? "Placing Order..." : "Confirm Order"}
      </button>
    </div>
  );
}
