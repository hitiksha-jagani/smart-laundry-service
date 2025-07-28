

// import React, { useEffect, useState } from "react";
// import { useLocation } from "react-router-dom";
// import axios from "../../utils/axiosInstance";
// import PrimaryButton from "../../components/PrimaryButton";

// export default function InitialOrder({
//   onNext,
//   setDummyOrderId,
//   initialOrderData,
//   setInitialOrderData,
// }) {
//   const location = useLocation();
//   const providerId = location.state?.providerId;

//   const [providerItems, setProviderItems] = useState([]);
//   const [error, setError] = useState("");
//   const [loading, setLoading] = useState(false);
//   const [availableServices, setAvailableServices] = useState([]);
//   const [availableSubServices, setAvailableSubServices] = useState([]);

//   const { pickupDate, pickupTime, items, goWithSchedulePlan } = initialOrderData;

//   useEffect(() => {
//     if (providerId) {
//       setInitialOrderData((prev) => ({
//         ...prev,
//         serviceProviderId: providerId,
//       }));
//     }
//   }, [providerId, setInitialOrderData]);

//   useEffect(() => {
//     const fetchItems = async () => {
//       try {
//         const res = await axios.get(`/customer/serviceProviders/${providerId}`);
//         const prices = res.data.prices || [];
//         const items = prices.map((p) => ({
//           itemId: p.item.itemId,
//           itemName: p.item.itemName,
//           serviceId: p.item.serviceId,
//           serviceName: p.item.serviceName,
//           subServiceId: p.item.subServiceId,
//           subServiceName: p.item.subServiceName,
//           price: p.price,
//         }));
//         setProviderItems(items);
//       } catch (e) {
//         setError("Failed to fetch items.");
//       }
//     };
//     if (providerId) fetchItems();
//   }, [providerId]);

//   const setField = (field, value) => {
//     setInitialOrderData((prev) => ({ ...prev, [field]: value }));
//   };

//   const updateItem = (idx, field, value) => {
//     const updated = [...items];
//     updated[idx][field] = value;

//     if (field === "itemId") {
//       const selectedItem = providerItems.find((item) => item.itemId === value);
//       if (selectedItem) {
//         updated[idx].serviceId = selectedItem.serviceId || "";
//         updated[idx].subServiceId = selectedItem.subServiceId || "";
//       } else {
//         updated[idx].serviceId = "";
//         updated[idx].subServiceId = "";
//       }
//     }

//     setField("items", updated);
//   };

//   const addLine = () =>
//     setField("items", [...items, { serviceId: "", subServiceId: "", itemId: "", quantity: 1 }]);
//   const removeLine = (idx) => setField("items", items.filter((_, i) => i !== idx));

//   const validate = () => {
//     if (!providerId || !pickupDate || !pickupTime) return "Fill all required fields";
//     for (let it of items) {
//       if (!it.itemId || it.quantity < 1) return "Please select item & quantity ≥1";
//     }
//     return null;
//   };

//  const submit = async () => {
//   const v = validate();
//   if (v) return setError(v);

//   setLoading(true);
//   try {
//     // Only keep itemId and quantity for each item
//     const cleanedItems = items.map(({ itemId, quantity }) => ({
//       itemId,
//       quantity,
//     }));

//     const res = await axios.post("/orders/initial", {
//       serviceProviderId: providerId,
//       pickupDate,
//       pickupTime,
//       items: cleanedItems,
//       goWithSchedulePlan,
//     });

//     setDummyOrderId(res.data);
//     onNext(goWithSchedulePlan);
//   } catch (e) {
//     setError(e.response?.data?.message || "Could not submit order");
//   } finally {
//     setLoading(false);
//   }
// };


//   return (
//     <div className="p-6 max-w-3xl mx-auto bg-white shadow rounded">
//       <h2 className="text-xl font-semibold mb-4">Step 1: Start Your Laundry Order</h2>

//       <div className="grid sm:grid-cols-2 gap-4 mb-6">
//         <div>
//           <label className="block text-muted mb-1">Pickup Date</label>
//           <input
//             type="date"
//             className="w-full p-2 border border-gray-300 rounded"
//             value={pickupDate}
//             onChange={(e) => setField("pickupDate", e.target.value)}
//             min={new Date().toISOString().split("T")[0]} // 🔒 Restrict past dates
//           />

//         </div>
//         <div>
//           <label className="block text-muted mb-1">Pickup Time</label>
//           <input
//             type="time"
//             className="w-full p-2 border border-gray-300 rounded"
//             value={pickupTime}
//             onChange={(e) => setField("pickupTime", e.target.value)}
//           />
//         </div>
//       </div>

//       <h3 className="text-lg font-semibold text-text mb-2">Select Items</h3>
//       {items.map((it, idx) => {
//         const filteredSubServices = providerItems
//           .filter((i) => i.itemId === it.itemId && i.subServiceId)
//           .map((i) => ({ subServiceId: i.subServiceId, subServiceName: i.subServiceName }));

//         const filteredServices = providerItems
//           .filter((i) => i.itemId === it.itemId && i.serviceId)
//           .map((i) => ({ serviceId: i.serviceId, serviceName: i.serviceName }));

//         return (
//           <div key={idx} className="grid grid-cols-1 sm:grid-cols-5 gap-3 items-end mb-4">
//             <div>
//               <label className="block text-muted mb-1">Item</label>
//               <select
//                 className="w-full p-2 border border-gray-300 rounded"
//                 value={it.itemId || ""}
//                 onChange={(e) => updateItem(idx, "itemId", e.target.value)}
//               >
//                 <option value="">-- select item --</option>
//                 {providerItems.map((opt) => (
//                   <option key={opt.itemId} value={opt.itemId}>
//                     {opt.itemName} - ₹{opt.price}
//                   </option>
//                 ))}
//               </select>
//             </div>
//             <div>
//               <label className="block text-muted mb-1">Sub-Service</label>
//               <select
//                 className="w-full p-2 border border-gray-300 rounded"
//                 value={it.subServiceId || ""}
//                 onChange={(e) => updateItem(idx, "subServiceId", e.target.value)}
//                 disabled={!filteredSubServices.length}
//               >
//                 <option value="">-- select subservice --</option>
//                 {filteredSubServices.map((s, i) => (
//                   <option key={i} value={s.subServiceId}>{s.subServiceName}</option>
//                 ))}
//               </select>
//             </div>
//             <div>
//               <label className="block text-muted mb-1">Service</label>
//               <select
//                 className="w-full p-2 border border-gray-300 rounded"
//                 value={it.serviceId || ""}
//                 onChange={(e) => updateItem(idx, "serviceId", e.target.value)}
//                 disabled={!filteredServices.length}
//               >
//                 <option value="">-- select service --</option>
//                 {filteredServices.map((s, i) => (
//                   <option key={i} value={s.serviceId}>{s.serviceName}</option>
//                 ))}
//               </select>
//             </div>
//             <div>
//               <label className="block text-muted mb-1">Qty</label>
//               <input
//                 type="number"
//                 min="1"
//                 className="w-full p-2 border border-gray-300 rounded"
//                 value={it.quantity}
//                 onChange={(e) => updateItem(idx, "quantity", e.target.value)}
//               />
//             </div>
//             {items.length > 1 && (
//               <button
//                 className="text-red-600 text-xl font-bold"
//                 onClick={() => removeLine(idx)}
//               >
//                 ✕
//               </button>
//             )}
//           </div>
//         );
//       })}

//       <button className="text-blue-600 hover:underline mb-6 font-medium" onClick={addLine}>
//         + Add another item
//       </button>

//       <div className="mb-6">
//         <label className="flex items-center space-x-2">
//           <input
//             type="checkbox"
//             checked={goWithSchedulePlan}
//             onChange={() => setField("goWithSchedulePlan", !goWithSchedulePlan)}
//             className="accent-blue-600"
//           />
//           <span className="text-text font-medium">Enable Schedule Plan</span>
//         </label>
//       </div>

//       {error && <p className="text-red-600 font-medium mb-4">{error}</p>}

//       <PrimaryButton onClick={submit} disabled={loading}>
//         {loading ? "Submitting..." : "Next"}
//       </PrimaryButton>
//     </div>
//   );
// }
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
  const [availableServices, setAvailableServices] = useState([]);
  const [availableSubServices, setAvailableSubServices] = useState([]);

  const { pickupDate, pickupTime, items, goWithSchedulePlan } = initialOrderData;

  const getTodayDateString = () => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today.toISOString().split("T")[0];
  };

  const getRoundedCurrentTime = () => {
    const now = new Date();
    now.setMinutes(now.getMinutes() + 10 - (now.getMinutes() % 10));
    return now.toTimeString().slice(0, 5);
  };

  const isToday = (selectedDate) => {
    const today = new Date();
    const date = new Date(selectedDate);
    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    );
  };

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
        const items = prices.map((p) => ({
          itemId: p.item.itemId,
          itemName: p.item.itemName,
          serviceId: p.item.serviceId,
          serviceName: p.item.serviceName,
          subServiceId: p.item.subServiceId,
          subServiceName: p.item.subServiceName,
          price: p.price,
        }));
        setProviderItems(items);
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

    if (field === "itemId") {
      const selectedItem = providerItems.find((item) => item.itemId === value);
      if (selectedItem) {
        updated[idx].serviceId = selectedItem.serviceId || "";
        updated[idx].subServiceId = selectedItem.subServiceId || "";
      } else {
        updated[idx].serviceId = "";
        updated[idx].subServiceId = "";
      }
    }

    setField("items", updated);
  };

  const addLine = () =>
    setField("items", [...items, { serviceId: "", subServiceId: "", itemId: "", quantity: 1 }]);

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
      const cleanedItems = items.map(({ itemId, quantity }) => ({
        itemId,
        quantity,
      }));

      const res = await axios.post("/orders/initial", {
        serviceProviderId: providerId,
        pickupDate,
        pickupTime,
        items: cleanedItems,
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
            min={getTodayDateString()}
          />
        </div>
        <div>
          <label className="block text-muted mb-1">Pickup Time</label>
          <input
            type="time"
            className="w-full p-2 border border-gray-300 rounded"
            value={pickupTime}
            onChange={(e) => setField("pickupTime", e.target.value)}
            min={isToday(pickupDate) ? getRoundedCurrentTime() : "00:00"}
          />
        </div>
      </div>

      <h3 className="text-lg font-semibold text-text mb-2">Select Items</h3>
      {items.map((it, idx) => {
        const filteredSubServices = providerItems
          .filter((i) => i.itemId === it.itemId && i.subServiceId)
          .map((i) => ({ subServiceId: i.subServiceId, subServiceName: i.subServiceName }));

        const filteredServices = providerItems
          .filter((i) => i.itemId === it.itemId && i.serviceId)
          .map((i) => ({ serviceId: i.serviceId, serviceName: i.serviceName }));

        return (
          <div key={idx} className="grid grid-cols-1 sm:grid-cols-5 gap-3 items-end mb-4">
            <div>
              <label className="block text-muted mb-1">Item</label>
              <select
                className="w-full p-2 border border-gray-300 rounded"
                value={it.itemId || ""}
                onChange={(e) => updateItem(idx, "itemId", e.target.value)}
              >
                <option value="">-- select item --</option>
                {providerItems.map((opt) => (
                  <option key={opt.itemId} value={opt.itemId}>
                    {opt.itemName} - ₹{opt.price}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-muted mb-1">Sub-Service</label>
              <select
                className="w-full p-2 border border-gray-300 rounded"
                value={it.subServiceId || ""}
                onChange={(e) => updateItem(idx, "subServiceId", e.target.value)}
                disabled={!filteredSubServices.length}
              >
                <option value="">-- select subservice --</option>
                {filteredSubServices.map((s, i) => (
                  <option key={i} value={s.subServiceId}>{s.subServiceName}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-muted mb-1">Service</label>
              <select
                className="w-full p-2 border border-gray-300 rounded"
                value={it.serviceId || ""}
                onChange={(e) => updateItem(idx, "serviceId", e.target.value)}
                disabled={!filteredServices.length}
              >
                <option value="">-- select service --</option>
                {filteredServices.map((s, i) => (
                  <option key={i} value={s.serviceId}>{s.serviceName}</option>
                ))}
              </select>
            </div>
            <div>
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
                className="text-red-600 text-xl font-bold"
                onClick={() => removeLine(idx)}
              >
                ✕
              </button>
            )}
          </div>
        );
      })}

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
