import React, { useEffect, useState, useCallback } from "react";
import axios from "../../utils/axiosInstance";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

const DeliveredOrders = () => {
  const [orders, setOrders] = useState([]);
  const { token } = useAuth();
  const navigate = useNavigate();

  const fetchDeliveredOrders = useCallback(async () => {
    try {
      const res = await axios.get("/provider/orders/delivered", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setOrders(res.data);
    } catch (err) {
      console.error("Error fetching delivered orders", err);
    }
  }, [token]);

  useEffect(() => {
    if (token) fetchDeliveredOrders();
  }, [token, fetchDeliveredOrders]);

  return (
    <div className="min-h-screen bg-orange-50 p-6">
      <button
        onClick={() => navigate("/provider/dashboard")}
        className="mb-4 text-orange-600 hover:text-orange-800 font-medium"
      >
        ← Back to Dashboard
      </button>

      <h1 className="text-3xl font-bold text-orange-700 mb-6 text-center">
        Delivered Orders
      </h1>

      {orders.length === 0 ? (
        <p className="text-center text-gray-500">No delivered orders yet.</p>
      ) : (
        <div className="grid gap-6">
          {orders.map((order) => (
            <div
              key={order.orderId}
              className="bg-white rounded-2xl shadow-md p-6 border-l-4 border-orange-500 hover:shadow-xl transition"
            >
              <div className="flex justify-between items-center mb-4">
                <div>
                  <h2 className="text-xl font-semibold text-orange-800">
                    Order ID:{" "}
                    <span className="text-gray-800">{order.orderId}</span>
                  </h2>
                  <p className="text-sm text-gray-600">
                    Pickup Date: <strong>{order.pickupDate}</strong> | Time:{" "}
                    <strong>{order.pickupTime}</strong>
                  </p>
                </div>
                <span className="bg-green-100 text-green-800 text-xs px-3 py-1 rounded-full font-medium">
                  {order.status}
                </span>
              </div>

              <div className="overflow-x-auto">
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
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DeliveredOrders;