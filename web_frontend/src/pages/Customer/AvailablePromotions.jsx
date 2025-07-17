import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../../utils/axiosInstance";

export default function AvailablePromotions() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [promotions, setPromotions] = useState([]);
  const [appliedPromoCode, setAppliedPromoCode] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [applyingPromoId, setApplyingPromoId] = useState(null);
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetchPromotions();
  }, [orderId]);

  const fetchPromotions = async () => {
    try {
      setLoading(true);
      const res = await axios.get(`/orders/${orderId}/summary`);
      const summary = res.data;
      setAppliedPromoCode(summary.appliedPromoCode || null);

      const promoRes = await axios.get(`/orders/available-promotions`, {
        params: { orderId },
      });
      setPromotions(promoRes.data);
    } catch (err) {
      console.error("Error fetching promotions", err);
      setError("Failed to load promotions.");
    } finally {
      setLoading(false);
    }
  };

  const handleApplyPromotion = async (promotionId) => {
    if (appliedPromoCode) {
      setMessage("⚠️ A promotion is already applied. Only one promo can be used per order.");
      return;
    }

    setApplyingPromoId(promotionId);
    setMessage("");

    try {
      const res = await axios.post(`/orders/apply-promo`, null, {
        params: { orderId, promotionId },
      });

      const summary = res.data;

      if (summary.isPromotionApplied) {
        navigate(`/customer/orders/${orderId}/summary`);
      } else {
        setMessage(summary.promotionMessage || "⚠️ Promotion could not be applied.");
      }
    } catch (err) {
      console.error("Error applying promotion", err);
      setMessage("❌ Failed to apply promotion.");
    } finally {
      setApplyingPromoId(null);
    }
  };

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <h2 className="text-2xl font-bold mb-4">Available Promotions</h2>

      {loading && <p>Loading promotions...</p>}
      {error && <p className="text-red-600">{error}</p>}
      {message && <p className="text-blue-600 font-medium mt-2">{message}</p>}

      {appliedPromoCode && (
        <p className="text-green-700 mb-4 font-medium">
          ✅ Promotion <strong>{appliedPromoCode}</strong> is already applied to this order.
        </p>
      )}

      {!loading && promotions.length === 0 && (
        <p>No active promotions available for this order.</p>
      )}

      {!loading && promotions.length > 0 && (
        <div className="grid gap-4">
          {promotions.map((promo) => (
            <div
              key={promo.promotionId}
              className="border rounded p-4 shadow bg-white hover:shadow-md transition"
            >
              <h3 className="text-lg font-semibold text-blue-600">{promo.promoCode}</h3>
              <p className="text-gray-700">{promo.description}</p>
              <p className="text-sm text-gray-500 mt-1">
                Valid from <strong>{new Date(promo.startDate).toLocaleDateString()}</strong> to{" "}
                <strong>{new Date(promo.endDate).toLocaleDateString()}</strong>
              </p>
              <button
                onClick={() => handleApplyPromotion(promo.promotionId)}
                disabled={!!appliedPromoCode || applyingPromoId === promo.promotionId}
                className="mt-3 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-60"
              >
                {applyingPromoId === promo.promotionId
                  ? "Applying..."
                  : appliedPromoCode
                  ? "Promo already applied"
                  : "Apply"}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
