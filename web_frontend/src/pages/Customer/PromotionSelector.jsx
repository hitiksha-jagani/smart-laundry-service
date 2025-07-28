import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";

export default function PromotionSelector({ orderId, onPromoApplied, currentSummary }) {
  const [promotions, setPromotions] = useState([]);
  const [selectedPromoId, setSelectedPromoId] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const isPromoAlreadyApplied = currentSummary?.isPromotionApplied;

  useEffect(() => {
    if (isPromoAlreadyApplied) return; // Don't fetch promos if one is already applied
    axios
      .get(`/orders/available-promotions?orderId=${orderId}`)
      .then(res => setPromotions(res.data))
      .catch(err => {
        console.error("Failed to fetch promotions:", err);
        setPromotions([]);
      });
  }, [orderId, isPromoAlreadyApplied]);

  const applyPromo = async () => {
    if (!selectedPromoId || isPromoAlreadyApplied) return;

    setIsLoading(true);
    setMessage("");

    try {
      const res = await axios.post(`/orders/apply-promo`, null, {
        params: { orderId, promotionId: selectedPromoId },
      });

      if (res.data.isPromotionApplied) {
        setMessage("✅ Promo applied successfully!");
      } else {
        setMessage(`⚠️ ${res.data.promotionMessage}`);
      }

      onPromoApplied(res.data); 
    } catch (err) {
      console.error(err);
      setMessage("❌ Failed to apply promotion.");
    } finally {
      setIsLoading(false);
    }
  };

  if (isPromoAlreadyApplied) {
    return (
      <div className="mt-6 p-4 border rounded bg-gray-50 text-green-700">
        ✅ Promotion <strong>{currentSummary.appliedPromoCode}</strong> is already applied.
      </div>
    );
  }

  if (promotions.length === 0) {
    return (
      <p className="mt-6 text-gray-600 border p-4 rounded bg-gray-50">
        No available promotions.
      </p>
    );
  }

  return (
    <div className="mt-6 p-4 border rounded bg-gray-50">
      <h3 className="text-lg font-semibold mb-2">Available Promotions</h3>

      <select
        className="w-full p-2 border rounded mb-2"
        value={selectedPromoId}
        onChange={(e) => setSelectedPromoId(e.target.value)}
        disabled={isLoading}
      >
        <option value="">-- Select Promo Code --</option>
        {promotions.map(p => (
          <option key={p.promotionId} value={p.promotionId}>
            {p.promoCode} - {p.description}
          </option>
        ))}
      </select>

      <button
        onClick={applyPromo}
        disabled={isLoading || !selectedPromoId}
        className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 disabled:opacity-50"
      >
        {isLoading ? "Applying..." : "Apply Promotion"}
      </button>

      {message && <p className="mt-2 text-sm text-blue-600">{message}</p>}
    </div>
  );
}
