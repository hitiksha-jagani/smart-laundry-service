import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "../../utils/axiosInstance";
import PrimaryButton from "../../components/PrimaryButton"; 

export default function InitialOrder({
  onNext,
  setDummyOrderId,
  initialOrderData,
  setInitialOrderData,
}) {
  const location = useLocation();
  const providerId = location.state?.providerId;

  const [providerItems, setProviderItems] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { pickupDate, pickupTime, items, goWithSchedulePlan } = initialOrderData;

  useEffect(() => {
    if (providerId) {
      setInitialOrderData((prev) => ({
        ...prev,
        serviceProviderId: providerId,
      }));
    }
  }, [providerId, setInitialOrderData]);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const res = await axios.get(`/customer/serviceProviders/${providerId}`);
        const prices = res.data.prices || [];
        setProviderItems(
          prices.map((p) => ({
            itemId: p.item.itemId,
            itemName: p.item.itemName,
            serviceName: p.item.serviceName || "N/A",
            subServiceName: p.item.subServiceName || "N/A",
            price: p.price,
          }))
        );
      } catch (e) {
        setError("Failed to fetch items.");
      }
    };
    if (providerId) fetchItems();
  }, [providerId]);

  const setField = (field, value) => {
    setInitialOrderData((prev) => ({ ...prev, [field]: value }));
  };

  const updateItem = (idx, field, value) => {
    const updated = [...items];
    updated[idx][field] = value;
    setField("items", updated);
  };

  const addLine = () => setField("items", [...items, { itemId: "", quantity: 1 }]);
  const removeLine = (idx) => setField("items", items.filter((_, i) => i !== idx));

  const validate = () => {
    if (!providerId || !pickupDate || !pickupTime) return "Fill all required fields";
    for (let it of items) {
      if (!it.itemId || it.quantity < 1) return "Please select item & quantity ≥1";
    }
    return null;
  };

  const submit = async () => {
    const v = validate();
    if (v) return setError(v);

    setLoading(true);
    try {
      const res = await axios.post("/orders/initial", {
        serviceProviderId: providerId,
        pickupDate,
        pickupTime,
        items,
        goWithSchedulePlan,
      });

      setDummyOrderId(res.data);
      onNext(goWithSchedulePlan);
    } catch (e) {
      setError(e.response?.data?.message || "Could not submit order");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-3xl mx-auto bg-white shadow rounded">
      <h2 className="text-xl font-semibold mb-4">Step 1: Start Your Laundry Order</h2>

      <div className="grid sm:grid-cols-2 gap-4 mb-6">
        <div>
          <label className="block text-muted mb-1">Pickup Date</label>
          <input
            type="date"
            className="w-full p-2 border border-gray-300 rounded"
            value={pickupDate}
            onChange={(e) => setField("pickupDate", e.target.value)}
          />
        </div>
        <div>
          <label className="block text-muted mb-1">Pickup Time</label>
          <input
            type="time"
            className="w-full p-2 border border-gray-300 rounded"
            value={pickupTime}
            onChange={(e) => setField("pickupTime", e.target.value)}
          />
        </div>
      </div>

      <h3 className="text-lg font-semibold text-text mb-2">Select Items</h3>
      {items.map((it, idx) => (
        <div key={idx} className="flex gap-3 items-end mb-4">
          <div className="flex-1">
            <label className="block text-muted mb-1">Item</label>
            <select
              className="w-full p-2 border border-gray-300 rounded"
              value={it.itemId}
              onChange={(e) => updateItem(idx, "itemId", e.target.value)}
            >
              <option value="">-- select item --</option>
              {providerItems.map((opt) => (
                <option key={opt.itemId} value={opt.itemId}>
                  {opt.itemName} / {opt.serviceName} / {opt.subServiceName} - ₹{opt.price}
                </option>
              ))}
            </select>
          </div>
          <div className="w-24">
            <label className="block text-muted mb-1">Qty</label>
            <input
              type="number"
              min="1"
              className="w-full p-2 border border-gray-300 rounded"
              value={it.quantity}
              onChange={(e) => updateItem(idx, "quantity", e.target.value)}
            />
          </div>
          {items.length > 1 && (
            <button
              className="text-red-600 mt-6 text-xl font-bold"
              onClick={() => removeLine(idx)}
            >
              ✕
            </button>
          )}
        </div>
      ))}

      <button className="text-blue-600 hover:underline mb-6 font-medium" onClick={addLine}>
        + Add another item
      </button>

      <div className="mb-6">
        <label className="flex items-center space-x-2">
          <input
            type="checkbox"
            checked={goWithSchedulePlan}
            onChange={() => setField("goWithSchedulePlan", !goWithSchedulePlan)}
            className="accent-blue-600"
          />
          <span className="text-text font-medium">Enable Schedule Plan</span>
        </label>
      </div>

      {error && <p className="text-red-600 font-medium mb-4">{error}</p>}

        <PrimaryButton onClick={submit} disabled={loading}>
        {loading ? "Submitting..." : "Next"}
      </PrimaryButton>
    </div>
  );
}
