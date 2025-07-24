import React, { useEffect, useState } from "react";
// import axios from "../../utils/axiosInstance"; // uses your interceptor config
import PrimaryButton from "../../components/PrimaryButton";
import axiosInstance from '../../utils/axiosInstance';
export default function ReviewAndConfirm({ dummyOrderId, onOrderCreated }) {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

useEffect(() => {
  const fetchSummary = async () => {
    try {
      const res = await axiosInstance.get(`/orders/summary-from-redis`, {
        params: {
          dummyOrderId: dummyOrderId,
        },
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
      const res = await axiosInstance.post(`/orders/place/${dummyOrderId}`);

      onOrderCreated(res.data);
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || "Failed to create order. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  if (!summary) return <p className="p-4 text-muted">Loading order summary...</p>;

  return (
    <div className="p-6 max-w-xl mx-auto bg-white shadow rounded">
      <h2 className="text-xl font-semibold mb-4">Step 4: Review & Confirm</h2>

      {/* Pickup Info */}
      <div className="bg-light p-4 rounded-lg mb-4">
        <h3 className="font-semibold text-lg mb-2">Pickup Info</h3>
        <p><strong>Date:</strong> {summary.pickupDate}</p>
        <p><strong>Time:</strong> {summary.pickupTime}</p>
      </div>

      {/* Items */}
      <div className="bg-light p-4 rounded-lg mb-4">
        <h3 className="font-semibold text-lg mb-2">Items</h3>
        {summary.bookingItems?.length > 0 ? (
          <ul className="list-disc pl-5 space-y-1">
            {summary.bookingItems.map((item, i) => (
              <li key={i}>
                {item.itemName} × {item.quantity}
              </li>
            ))}
          </ul>
        ) : (
          <p>No items found.</p>
        )}
      </div>

      {/* Contact Info */}
      <div className="bg-light p-4 rounded-lg mb-4">
        <h3 className="font-semibold text-lg mb-2">Contact Info</h3>
        <p><strong>Name:</strong> {summary.contactName}</p>
        <p><strong>Phone:</strong> {summary.contactPhone}</p>
        <p><strong>Address:</strong> {summary.contactAddress}</p>
      </div>

      {/* Schedule Plan */}
      {summary.schedulePlan && (
        <div className="bg-light p-4 rounded-lg mb-4">
          <h3 className="font-semibold text-lg mb-2">Schedule Plan</h3>
          <p><strong>Plan:</strong> {summary.schedulePlan.plan}</p>
          <p><strong>Pay Each Delivery:</strong> {summary.schedulePlan.payEachDelivery ? "Yes" : "No"}</p>
          <p><strong>Pay Last Delivery:</strong> {summary.schedulePlan.payLastDelivery ? "Yes" : "No"}</p>
        </div>
      )}

      {/* Error */}
      {error && <p className="text-red-600 font-medium mb-4">{error}</p>}

      {/* Confirm Button */}
      <div className="pt-2">
        <PrimaryButton onClick={handleConfirm} disabled={loading}>
          {loading ? "Placing Order..." : "Confirm Order"}
        </PrimaryButton>
      </div>
    </div>
  );
}
