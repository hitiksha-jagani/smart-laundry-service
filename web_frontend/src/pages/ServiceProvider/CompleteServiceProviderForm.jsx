// import React, { useEffect, useState } from "react";
// import axios from "../../utils/axiosInstance";
// import { jwtDecode } from "jwt-decode";

// export default function ServiceProviderProfileForm({ userId: propUserId }) {
//   const [userId, setUserId] = useState(propUserId || "");
//   const [loading, setLoading] = useState(true);

//   const [formData, setFormData] = useState({
//     businessName: "",
//     businessLicenseNumber: "",
//     gstNumber: "",
//     needOfDeliveryAgent: false,
//     schedulePlans: [],
//     bankAccount: {
//       bankName: "",
//       ifscCode: "",
//       bankAccountNumber: "",
//       accountHolderName: ""
//     },
//     priceDTO: []
//   });

//   const [items, setItems] = useState([]);
//   const [schedulePlanOptions, setSchedulePlanOptions] = useState([]);
//   const [selectedItem, setSelectedItem] = useState("");
//   const [itemPrice, setItemPrice] = useState("");
//   const [fileUploads, setFileUploads] = useState({
//     aadharCard: null,
//     panCard: null,
//     utilityBill: null,
//     profilePhoto: null
//   });

//   useEffect(() => {
//     const token = localStorage.getItem("token");

//     if (propUserId) {
//       setUserId(propUserId);
//       setLoading(false);
//     } else if (token) {
//       try {
//         const decoded = jwtDecode(token);
//         if (decoded.exp * 1000 < Date.now()) throw new Error("Token expired");
//         if (decoded.id) {
//           setUserId(decoded.id);
//           setLoading(false);
//         } else {
//           throw new Error("ID missing in token");
//         }
//       } catch (e) {
//         console.error("Invalid or expired token", e);
//         localStorage.removeItem("token");
//         window.location.href = "/login";
//       }
//     } else {
//       window.location.href = "/login";
//     }

//     axios.get("/item/all").then(res => setItems(res.data)).catch(console.error);
//     axios.get("/schedule-plans").then(res => setSchedulePlanOptions(res.data)).catch(console.error);
//   }, [propUserId]);

//   const handlePriceAdd = () => {
//     const item = items.find(i => i.itemId === selectedItem);
//     if (item && itemPrice) {
//       const alreadyExists = formData.priceDTO.some(p => p.item.itemId === selectedItem);
//       if (!alreadyExists) {
//         setFormData(prev => ({
//           ...prev,
//           priceDTO: [
//             ...prev.priceDTO,
//             {
//               item: { itemId: item.itemId },
//               price: parseInt(itemPrice)
//             }
//           ]
//         }));
//         setSelectedItem("");
//         setItemPrice("");
//       } else {
//         alert("Item already added.");
//       }
//     }
//   };

//   const removePriceItem = (itemIdToRemove) => {
//     setFormData(prev => ({
//       ...prev,
//       priceDTO: prev.priceDTO.filter(p => p.item.itemId !== itemIdToRemove)
//     }));
//   };

//   const handleFileChange = (e) => {
//     setFileUploads({ ...fileUploads, [e.target.name]: e.target.files[0] });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     if (!userId) {
//       alert("User ID not found. Please login again.");
//       return;
//     }
// console.log("FormData to send:", JSON.stringify(formData, null, 2));
//     const data = new FormData();
//     data.append("data", new Blob([JSON.stringify(formData)], { type: "application/json" }));
//     data.append("aadharCard", fileUploads.aadharCard);
//     data.append("utilityBill", fileUploads.utilityBill);
//     data.append("profilePhoto", fileUploads.profilePhoto);
//     if (fileUploads.panCard) data.append("panCard", fileUploads.panCard);

//     try {
//       await axios.post(`/sp/complete-sp-profile/${userId}`, data, {
//         headers: { "Content-Type": "multipart/form-data" }
//       });
//       alert("Profile submitted successfully!");
//     } catch (err) {
//       alert("Submission failed.");
//       console.error(err);
//     }
//   };

//   if (loading) return <div className="p-4 text-center">Loading...</div>;

//   return (
//     <form onSubmit={handleSubmit} className="p-4 space-y-4">
//       <input required type="text" placeholder="Business Name" onChange={e => setFormData({ ...formData, businessName: e.target.value })} />
//       <input required type="text" placeholder="License Number" onChange={e => setFormData({ ...formData, businessLicenseNumber: e.target.value })} />
//       <input required type="text" placeholder="GST Number" onChange={e => setFormData({ ...formData, gstNumber: e.target.value })} />
//       <label>
//         <input type="checkbox" onChange={e => setFormData({ ...formData, needOfDeliveryAgent: e.target.checked })} /> Need Delivery Agent
//       </label>

//       <label>Schedule Plans:</label>
//       {schedulePlanOptions.map(plan => (
//         <label key={plan} style={{ display: 'block' }}>
//           <input
//             type="checkbox"
//             value={plan}
//             checked={formData.schedulePlans.includes(plan)}
//             onChange={e => {
//               setFormData(prev => ({
//                 ...prev,
//                 schedulePlans: e.target.checked
//                   ? [...prev.schedulePlans, plan]
//                   : prev.schedulePlans.filter(p => p !== plan)
//               }));
//             }}
//           />{" "}
//           {plan}
//         </label>
//       ))}

//       <h3>Add Item Price</h3>
//       <select value={selectedItem} onChange={e => setSelectedItem(e.target.value)}>
//         <option value="">Select Item</option>
//         {items.map(item => {
//           const serviceName = item.service?.serviceName || "";
//           const subServiceName = item.subService?.subServiceName || "";
//           return (
//             <option key={item.itemId} value={item.itemId}>
//               {item.itemName}
//               {serviceName && ` - ${serviceName}`}
//               {subServiceName && ` / ${subServiceName}`}
//             </option>
//           );
//         })}
//       </select>
//       <input type="number" placeholder="Price" value={itemPrice} onChange={e => setItemPrice(e.target.value)} />
//       <button type="button" onClick={handlePriceAdd}>Add Price</button>

//       <ul>
//         {formData.priceDTO.map(p => {
//           const fullItem = items.find(i => i.itemId === p.item.itemId);
//           return (
//             <li key={p.item.itemId}>
//               {fullItem?.itemName || p.item.itemId}
//               {fullItem?.service?.serviceName && ` - ${fullItem.service.serviceName}`}
//               {fullItem?.subService?.subServiceName && ` / ${fullItem.subService.subServiceName}`} — ₹{p.price}
//               <button type="button" onClick={() => removePriceItem(p.item.itemId)}>Remove</button>
//             </li>
//           );
//         })}
//       </ul>

//       <h4>Bank Details</h4>
//       <input type="text" placeholder="Account Holder Name" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, accountHolderName: e.target.value } })} />
//       <input type="text" placeholder="Bank Name" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, bankName: e.target.value } })} />
//       <input type="text" placeholder="IFSC Code" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, ifscCode: e.target.value } })} />
//       <input type="text" placeholder="Bank Account Number" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, bankAccountNumber: e.target.value } })} />

//       <h4>Upload Documents</h4>
//       <fieldset className="space-y-2">
//         <label>
//           Upload Aadhaar Card:
//           <input type="file" name="aadharCard" onChange={handleFileChange} />
//         </label>
//         <label>
//           Upload PAN Card:
//           <input type="file" name="panCard" onChange={handleFileChange} />
//         </label>
//         <label>
//           Upload Utility Bill:
//           <input type="file" name="utilityBill" onChange={handleFileChange} />
//         </label>
//         <label>
//           Upload Profile Photo:
//           <input type="file" name="profilePhoto" onChange={handleFileChange} />
//         </label>
//       </fieldset>

//       <button type="submit" disabled={!userId}>Submit Profile</button>
//     </form>
//   );
// }

// Tailwind-enhanced attractive design
import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { jwtDecode } from "jwt-decode";

export default function ServiceProviderProfileForm({ userId: propUserId }) {
  const [userId, setUserId] = useState(propUserId || "");
  const [loading, setLoading] = useState(true);

  const [formData, setFormData] = useState({
    businessName: "",
    businessLicenseNumber: "",
    gstNumber: "",
    needOfDeliveryAgent: false,
    schedulePlans: [],
    bankAccount: {
      bankName: "",
      ifscCode: "",
      bankAccountNumber: "",
      accountHolderName: ""
    },
    priceDTO: []
  });

  const [items, setItems] = useState([]);
  const [schedulePlanOptions, setSchedulePlanOptions] = useState([]);
  const [selectedItem, setSelectedItem] = useState("");
  const [itemPrice, setItemPrice] = useState("");
  const [fileUploads, setFileUploads] = useState({
    aadharCard: null,
    panCard: null,
    utilityBill: null,
    profilePhoto: null
  });

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (propUserId) {
      setUserId(propUserId);
      setLoading(false);
    } else if (token) {
      try {
        const decoded = jwtDecode(token);
        if (decoded.exp * 1000 < Date.now()) throw new Error("Token expired");
        if (decoded.id) {
          setUserId(decoded.id);
          setLoading(false);
        } else {
          throw new Error("ID missing in token");
        }
      } catch (e) {
        console.error("Invalid or expired token", e);
        localStorage.removeItem("token");
        window.location.href = "/login";
      }
    } else {
      window.location.href = "/login";
    }

    axios.get("/item/all").then(res => setItems(res.data)).catch(console.error);
    axios.get("/schedule-plans").then(res => setSchedulePlanOptions(res.data)).catch(console.error);
  }, [propUserId]);

  const handlePriceAdd = () => {
    const item = items.find(i => i.itemId === selectedItem);
    if (item && itemPrice) {
      const alreadyExists = formData.priceDTO.some(p => p.item.itemId === selectedItem);
      if (!alreadyExists) {
        setFormData(prev => ({
          ...prev,
          priceDTO: [...prev.priceDTO, { item: { itemId: item.itemId }, price: parseInt(itemPrice) }]
        }));
        setSelectedItem("");
        setItemPrice("");
      } else {
        alert("Item already added.");
      }
    }
  };

  const removePriceItem = (itemIdToRemove) => {
    setFormData(prev => ({
      ...prev,
      priceDTO: prev.priceDTO.filter(p => p.item.itemId !== itemIdToRemove)
    }));
  };

  const handleFileChange = (e) => {
    setFileUploads({ ...fileUploads, [e.target.name]: e.target.files[0] });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!userId) return alert("User ID not found. Please login again.");
    const data = new FormData();
    data.append("data", new Blob([JSON.stringify(formData)], { type: "application/json" }));
    data.append("aadharCard", fileUploads.aadharCard);
    data.append("utilityBill", fileUploads.utilityBill);
    data.append("profilePhoto", fileUploads.profilePhoto);
    if (fileUploads.panCard) data.append("panCard", fileUploads.panCard);
    try {
      await axios.post(`/sp/complete-sp-profile/${userId}`, data, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      alert("Profile submitted successfully!");
    } catch (err) {
      alert("Submission failed.");
      console.error(err);
    }
  };

  if (loading) return <div className="p-6 text-center text-gray-600 text-lg">Loading...</div>;

  return (
    <form onSubmit={handleSubmit} className="max-w-5xl mx-auto bg-[#FFF3E0] p-8 rounded-3xl shadow-xl space-y-6">
      <h2 className="text-4xl font-bold text-center text-orangeAccent mb-6">Service Provider Profile</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <input required type="text" placeholder="Business Name" className="input" onChange={e => setFormData({ ...formData, businessName: e.target.value })} />
        <input required type="text" placeholder="License Number" className="input" onChange={e => setFormData({ ...formData, businessLicenseNumber: e.target.value })} />
        <input required type="text" placeholder="GST Number" className="input" onChange={e => setFormData({ ...formData, gstNumber: e.target.value })} />
        <label className="flex items-center gap-2 text-sm">
          <input type="checkbox" className="accent-orangeAccent" onChange={e => setFormData({ ...formData, needOfDeliveryAgent: e.target.checked })} /> Do You Want Delivery Agent?
        </label>
      </div>

      <div>
        <label className="block font-semibold mb-2">Schedule Plan</label>
        <div className="flex flex-wrap gap-3">
          {schedulePlanOptions.map(plan => (
            <label key={plan} className="flex items-center gap-2 bg-white px-4 py-2 rounded-full border shadow-sm">
              <input
                type="checkbox"
                value={plan}
                checked={formData.schedulePlans.includes(plan)}
                onChange={e => {
                  setFormData(prev => ({
                    ...prev,
                    schedulePlans: e.target.checked
                      ? [...prev.schedulePlans, plan]
                      : prev.schedulePlans.filter(p => p !== plan)
                  }));
                }}
              /> {plan}
            </label>
          ))}
        </div>
      </div>

      <div>
        <h3 className="text-xl font-semibold mb-3">Add Service Item Pricing</h3>
        <div className="flex flex-col sm:flex-row gap-4">
          <select value={selectedItem} onChange={e => setSelectedItem(e.target.value)} className="input">
            <option value="">Select Item</option>
            {items.map(item => (
              <option key={item.itemId} value={item.itemId}>
                {item.itemName} {item.service?.serviceName && `- ${item.service.serviceName}`} {item.subService?.subServiceName && `/ ${item.subService.subServiceName}`}
              </option>
            ))}
          </select>
          <input type="number" placeholder="Price" value={itemPrice} onChange={e => setItemPrice(e.target.value)} className="input" />
          <button type="button" onClick={handlePriceAdd} className="btn-orange">Add</button>
        </div>

        <ul className="mt-4 space-y-2">
          {formData.priceDTO.map(p => {
            const fullItem = items.find(i => i.itemId === p.item.itemId);
            return (
              <li key={p.item.itemId} className="bg-white p-3 rounded-md flex justify-between items-center shadow">
                <span>
                  {fullItem?.itemName || p.item.itemId}
                  {fullItem?.service?.serviceName && ` - ${fullItem.service.serviceName}`}
                  {fullItem?.subService?.subServiceName && ` / ${fullItem.subService.subServiceName}`} — ₹{p.price}
                </span>
                <button onClick={() => removePriceItem(p.item.itemId)} className="text-red-500 hover:underline">Remove</button>
              </li>
            );
          })}
        </ul>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <input type="text" placeholder="Account Holder Name" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, accountHolderName: e.target.value } })} />
        <input type="text" placeholder="Bank Name" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, bankName: e.target.value } })} />
        <input type="text" placeholder="IFSC Code" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, ifscCode: e.target.value } })} />
        <input type="text" placeholder="Bank Account Number" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, bankAccountNumber: e.target.value } })} />
      </div>

      <div>
        <h4 className="text-xl font-semibold mb-3">Upload Documents</h4>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input type="file" name="aadharCard" onChange={handleFileChange} className="file-input" />
          <input type="file" name="panCard" onChange={handleFileChange} className="file-input" />
          <input type="file" name="utilityBill" onChange={handleFileChange} className="file-input" />
          <input type="file" name="profilePhoto" onChange={handleFileChange} className="file-input" />
        </div>
      </div>

      <div className="text-center mt-6">
        <button type="submit" disabled={!userId} className="btn-orange w-full sm:w-auto px-10 py-3 text-lg">Submit Profile</button>
      </div>
    </form>
  );
}
