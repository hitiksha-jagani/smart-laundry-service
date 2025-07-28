import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "../../utils/axiosInstance";
import RazorpayButton from "../../components/RazorpayButton";

export default function OrderSummary() {
  const { orderId } = useParams();
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [paymentStarted, setPaymentStarted] = useState(false);

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const res = await axios.get(`/orders/${orderId}/summary`);
        console.log("Order Summary DTO from backend:", res.data);
        setSummary(res.data);
             
      } catch (err) {
        console.error("Failed to fetch summary:", err);
        setError("Failed to load order summary.");
      } finally {
        setLoading(false);
      }
    };
    fetchSummary();
  }, [orderId]);

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-blue-700 mb-4">Order Summary</h2>

      {loading && <p>Loading...</p>}
      {error && <p className="text-red-600">{error}</p>}

      {!loading && summary && (
        <div className="border rounded p-4 bg-white shadow">
          <p><strong>Order ID:</strong> {summary.orderId}</p>
          <p><strong>Payment Status:</strong> {summary.status}</p>
          <p><strong>Service:</strong> {summary.serviceName}</p>
          <p><strong>Sub-Service:</strong> {summary.subServiceName}</p>
          <p><strong>Order Status:</strong> {summary.orderStatus}</p>

          <table className="w-full mt-4 border">
            <thead>
              <tr className="bg-gray-100">
                <th className="p-2 border text-left">Item</th>
                <th className="p-2 border text-left">Qty</th>
                <th className="p-2 border text-left">Rate</th>
                <th className="p-2 border text-left">Total</th>
              </tr>
            </thead>
            <tbody>
              {summary.items.map((item, i) => (
                <tr key={i}>
                  <td className="p-2 border">{item.itemName}</td>
                  <td className="p-2 border">{item.quantity}</td>
                  <td className="p-2 border">₹{item.price.toFixed(2)}</td>
                  <td className="p-2 border">₹{item.finalPrice.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="text-right mt-4 space-y-1">
            <p><strong>Items Total:</strong> ₹{summary.itemsTotal.toFixed(2)}</p>
            <p><strong>GST:</strong> ₹{summary.gstAmount.toFixed(2)}</p>
            <p><strong>Delivery Charge:</strong> ₹{summary.deliveryCharge.toFixed(2)}</p>
            {summary.promotionApplied && summary.discountAmount > 0 && (
              <p className="text-black-600">
                <strong>Discount:</strong> ₹{summary.discountAmount.toFixed(2)} ({summary.appliedPromoCode})
              </p>
            )}
            <p className="text-xl font-bold mt-2">
              Final Amount: ₹{summary.finalAmount.toFixed(2)}
            </p>
          </div>


          {/* ✅ Payment Section */}
          {summary.status === "PAID" ? (
          <p className="text-green-700 font-semibold mt-4">✅ Payment already completed.</p>
        ) : summary.invoiceNumber && summary.finalAmount > 0 && summary.orderStatus === "DELIVERED" ? (
          <div className="mt-6">
            {paymentStarted ? (
              <p className="text-blue-600 font-medium">Processing payment...</p>
            ) : (
              <RazorpayButton
                 invoiceNumber={summary.invoiceNumber}
                finalPrice={summary.finalAmount}
                orderId={orderId}
                onStart={() => setPaymentStarted(true)}
              />
            )}
          </div>
        ) : (
          <p className="text-yellow-600 mt-4">🕒 Payment will be available after delivery.</p>
        )}

        </div>
      )}
    </div>
  );
}
