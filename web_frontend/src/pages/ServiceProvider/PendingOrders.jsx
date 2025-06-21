import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "../../utils/axiosInstance";
import { useAuth } from "../../context/AuthContext";

export default function PendingOrders() {
  const [pendingOrders, setPendingOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await axios.get(`/provider/orders/pending`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPendingOrders(res.data);
      } catch (err) {
        console.error("Failed to fetch orders:", err);
      } finally {
        setLoading(false);
      }
    };

    if (token) fetchData();
  }, [token]);

  const handleAccept = async (orderId) => {
    try {
      await axios.post(`/provider/orders/accept/${orderId}`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("Order accepted!");
      const res = await axios.get(`/provider/orders/pending`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPendingOrders(res.data);
    } catch (err) {
      alert("Failed to accept order.");
      console.error(err);
    }
  };

  const handleReject = async (orderId) => {
    try {
      await axios.post(`/provider/orders/${orderId}/reject`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("Order rejected.");
      const res = await axios.get(`/provider/orders/pending`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPendingOrders(res.data);
    } catch (err) {
      alert("Failed to reject order.");
      console.error(err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-orange-50 flex items-center justify-center">
        <p className="text-orange-600 font-medium">Loading orders...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-orange-50 p-6">
      <button
        onClick={() => navigate("/provider/dashboard")}
        className="mb-4 text-orange-600 hover:text-orange-800 font-medium"
      >
        ← Back to Dashboard
      </button>

      <h2 className="text-3xl font-bold text-orange-700 mb-6 text-center">
        Pending Orders
      </h2>

      {pendingOrders.length === 0 ? (
        <p className="text-center text-gray-500">No pending orders.</p>
      ) : (
        <div className="grid gap-6">
          {pendingOrders.map((order) => (
            <div
              key={order.orderId}
              className="bg-white rounded-2xl shadow-md p-6 border-l-4 border-orange-500 hover:shadow-lg transition"
            >
              <div className="flex justify-between items-center mb-4">
                <div>
                  <h3 className="text-xl font-semibold text-orange-800">
                    Order ID: <span className="text-gray-800">{order.orderId}</span>
                  </h3>
                  <p className="text-sm text-gray-600">
                    Pickup Date: <strong>{order.pickupDate}</strong> | Time:{" "}
                    <strong>{order.pickupTime}</strong>
                  </p>
                </div>
                <span className="bg-orange-100 text-orange-800 text-xs px-3 py-1 rounded-full font-medium">
                  PENDING
                </span>
              </div>

              <div className="overflow-x-auto mt-2">
                <table className="min-w-full text-sm border-t border-orange-100">
                  <thead className="text-orange-700 bg-orange-100">
                    <tr>
                      <th className="px-4 py-2">Item</th>
                      <th className="px-4 py-2">Service</th>
                      <th className="px-4 py-2">Sub-service</th>
                      <th className="px-4 py-2">Quantity</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white">
                    {order.items.map((item, idx) => (
                      <tr key={idx} className="border-b border-orange-100">
                        <td className="px-4 py-2">{item.itemName}</td>
                        <td className="px-4 py-2">{item.service}</td>
                        <td className="px-4 py-2">{item.subService}</td>
                        <td className="px-4 py-2">{item.quantity}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="mt-6 flex justify-end gap-3">
                <button
                  onClick={() => handleAccept(order.orderId)}
                  className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded font-semibold shadow"
                >
                  Accept
                </button>
                <button
                  onClick={() => handleReject(order.orderId)}
                  className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded font-semibold shadow"
                >
                  Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
