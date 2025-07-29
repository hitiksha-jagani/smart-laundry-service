import React, { useState } from "react";
import html2pdf from "html2pdf.js";

export default function OrderBill({ summary, refProp }) {
  const [loading, setLoading] = useState(false);

  const downloadPDF = async () => {
    if (!refProp?.current) {
      alert("Content not available to download.");
      return;
    }

    setLoading(true);
    try {
      const opt = {
        margin:       0.5,
        filename:     `Laundry_Bill_${summary.orderId}.pdf`,
        image:        { type: "jpeg", quality: 0.98 },
        html2canvas:  { scale: 2 },
        jsPDF:        { unit: "in", format: "letter", orientation: "portrait" },
      };

      await html2pdf().set(opt).from(refProp.current).save();
    } catch (error) {
      console.error("PDF download failed:", error);
    } finally {
      setLoading(false);
    }
  };

  if (!summary) return <p>No summary available</p>;

  return (
    <div ref={refProp} className="p-6 max-w-2xl mx-auto bg-white border rounded shadow">
      <h1 className="text-2xl font-bold text-center mb-4">Laundry Bill</h1>

      <div className="mb-4">
        <p><strong>Order ID:</strong> {summary.orderId}</p>
        <p><strong>Bill Status:</strong> {summary.status}</p>
        <p><strong>Service:</strong> {summary.serviceName}</p>
        <p><strong>Sub-Service:</strong> {summary.subServiceName}</p>
      </div>

      <table className="w-full mb-4 border">
        <thead>
          <tr className="bg-gray-100">
            <th className="text-left p-2 border">Item</th>
            <th className="text-left p-2 border">Qty</th>
            <th className="text-left p-2 border">Rate</th>
            <th className="text-left p-2 border">Total</th>
          </tr>
        </thead>
        <tbody>
          {summary.items.map((item, i) => (
            <tr key={i}>
              <td className="p-2 border">{item.itemName}</td>
              <td className="p-2 border">{item.quantity}</td>
              <td className="p-2 border">
                ₹{item.price.toLocaleString("en-IN", { minimumFractionDigits: 2 })}
              </td>
              <td className="p-2 border">
                ₹{item.finalPrice.toLocaleString("en-IN", { minimumFractionDigits: 2 })}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="text-right">
        <p><strong>Items Total:</strong> ₹{summary.itemsTotal.toFixed(2)}</p>
        <p><strong>GST:</strong> ₹{summary.gstAmount.toFixed(2)}</p>
        <p><strong>Delivery Charge:</strong> ₹{summary.deliveryCharge.toFixed(2)}</p>

        <p className={summary.isPromotionApplied ? "text-green-600" : "text-gray-600"}>
          <strong>Discount:</strong> ₹{summary.discountAmount.toFixed(2)}
          {summary.appliedPromoCode && ` (${summary.appliedPromoCode})`}
        </p>

        {summary.promotionMessage && (
          <p className="text-sm italic text-gray-500 mt-1">{summary.promotionMessage}</p>
        )}

        <p className="text-xl font-bold mt-2">
          Final Amount: ₹{summary.finalAmount.toFixed(2)}
        </p>
      </div>

      <div className="text-center mt-6">
        <button
          onClick={downloadPDF}
          disabled={loading}
          className={`px-6 py-2 rounded text-white ${
            loading ? "bg-gray-400 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"
          }`}
        >
          {loading ? "Generating..." : "Download Bill"}
        </button>
      </div>
    </div>
  );
}
