import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { useAuth } from "../../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";

export default function CustomerOrderHistory() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrderHistory = async () => {
      try {
        const response = await axios.get("/orders/history", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setOrders(response.data);
      } catch (error) {
        console.error("Failed to fetch order history", error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrderHistory();
  }, [token]);

  if (loading) {
    return (
      <div className="text-center text-blue-600 mt-10 text-lg font-medium">
        Loading...
      </div>
    );
  }

  if (orders.length === 0) {
    return <p className="text-center mt-8">No past orders found.</p>;
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h2 className="text-2xl font-semibold mb-6 text-center">My Order History</h2>
      <div className="space-y-4">
        {orders.map((order) => (
          <div
            key={order.orderId}
            className="bg-white shadow-md border rounded-lg p-4"
          >
            {/* Order Info */}
            <div className="flex justify-between mb-2">
              <span className="text-gray-600 font-medium">Order ID:</span>
              <span>{order.orderId}</span>
            </div>
            <div className="flex justify-between mb-2">
              <span className="text-gray-600 font-medium">Pickup:</span>
              <span>
                {order.pickupDate} at {order.pickupTime}
              </span>
            </div>
            <div className="flex justify-between mb-2">
              <span className="text-gray-600 font-medium">Delivery:</span>
              <span>
                {order.deliveryDate
                  ? `${order.deliveryDate} at ${order.deliveryTime}`
                  : "Pending"}
              </span>
            </div>
            <div className="flex justify-between mb-2">
              <span className="text-gray-600 font-medium">Status:</span>
              <span className="font-semibold text-blue-600">{order.status}</span>
            </div>
            <div className="text-sm text-gray-700 mt-2 space-y-1">
              <p>
                <strong>Contact:</strong> {order.contactName} ({order.contactPhone})
              </p>
              <p>
                <strong>Address:</strong> {order.contactAddress}
              </p>
            </div>
            <div className="text-xs text-gray-500 mt-2">
              Created at: {new Date(order.createdAt).toLocaleString()}
            </div>

            {/* Promotion Button */}
            <div className="mt-4 text-center">
              <button
                onClick={() => {
                  if (order.isPromotionApplied) {
                    alert("A promotion is already applied to this order.");
                  } else {
                    navigate(`/orders/${order.orderId}/promotions`);
                  }
                }}
                disabled={order.isPromotionApplied}
                className={`px-6 py-2 rounded font-medium ${
                  order.isPromotionApplied
                    ? "bg-gray-400 cursor-not-allowed text-white"
                    : "bg-green-600 hover:bg-green-700 text-white"
                }`}
              >
                {order.isPromotionApplied
                  ? `Promotion Applied (${order.appliedPromoCode})`
                  : "Apply Promotion"}
              </button>
            </div>

            {/* View Summary Button */}
            <div className="mt-4 text-right">
              <button
                onClick={() => navigate(`/orders/${order.orderId}/summary`)}
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
              >
                View Summary
              </button>
            </div>

            {/* View Bill Button */}
            <div className="mt-2 text-right">
              <button
                onClick={() => navigate(`/orders/${order.orderId}/bill`)}
                className="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700"
              >
                View Bill
              </button>
            </div>
            {order.status !== "DELIVERED" && (
            <div className="mt-2 text-right">
              <button
                onClick={() => navigate(`/orders/${order.orderId}/track`)}
                className="bg-orange-500 text-white px-4 py-2 rounded hover:bg-orange-600"
              >
                Track Order
              </button>
            </div>
          )}

          <button
            onClick={() => navigate(`/orders/${order.orderId}/cancel`)}
            className="mt-4 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
          >
            Cancel Order
          </button>
        <button
          className="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600 mt-4"
          onClick={() => navigate(`/orders/${order.orderId}/reschedule`)}
        >
          Reschedule Order
        </button>


            {/* ✅ Feedback Button */}
            {order.status === "DELIVERED" && (
              <div className="mt-4 text-center">
                <Link to={`/feedback/${order.orderId}`}>
                  <button className="text-sm text-blue-600 underline hover:text-blue-800">
                    Give Feedback
                  </button>
                </Link>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
