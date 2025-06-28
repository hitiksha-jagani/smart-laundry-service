import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { useParams } from "react-router-dom";

const statusLabels = {
  ACCEPTED: "Accepted",
  PICKED_UP: "Picked Up",
  IN_CLEANING: "Cleaning",
  READY_FOR_DELIVERY: "Ready for Delivery",
  DELIVERED: "Delivered"
};

export default function TrackOrder() {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    axios.get(`/orders/track/${orderId}`)
      .then(res => setOrder(res.data))
      .catch(() => setError("Failed to fetch order tracking details."));
  }, [orderId]);

  if (error) return <div className="text-center text-red-500 p-6">{error}</div>;
  if (!order) return <div className="text-center p-6">Loading...</div>;

  return (
    <div className="max-w-3xl mx-auto p-6">
      <h2 className="text-2xl font-semibold text-orange-600 mb-4">Track Your Order</h2>

      <div className="mb-6 bg-white shadow p-4 rounded-lg border border-orange-200">
        <p><span className="font-semibold">Order ID:</span> {order.orderId}</p>
        <p><span className="font-semibold">Pickup Date:</span> {order.pickupDate}</p>
        <p><span className="font-semibold">Pickup Time:</span> {order.pickupTime}</p>
        <p><span className="font-semibold">Current Status:</span> {statusLabels[order.status]}</p>
      </div>

      <div className="mt-8">
        <h3 className="text-lg font-medium mb-3 text-gray-700">Order Status Timeline</h3>
        <ol className="relative border-l border-orange-300">
          {order.statusHistory.map((entry, index) => (
            <li key={index} className="mb-6 ml-4">
              <div className="absolute w-3 h-3 bg-orange-500 rounded-full mt-1.5 -left-1.5 border border-white"></div>
              <time className="text-xs text-gray-500">{new Date(entry.changedAt).toLocaleString()}</time>
              <p className="text-sm font-semibold text-gray-800">{statusLabels[entry.status] || entry.status}</p>
            </li>
          ))}
        </ol>
      </div>
    </div>
  );
}
