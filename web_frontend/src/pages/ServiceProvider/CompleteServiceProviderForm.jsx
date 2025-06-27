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
  const [services, setServices] = useState([]);
  const [subServices, setSubServices] = useState([]);
  const [schedulePlanOptions, setSchedulePlanOptions] = useState([]);

  const [selectedService, setSelectedService] = useState("");
  const [selectedSubService, setSelectedSubService] = useState("");
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

    axios.get("/items/all").then(res => {
      const allItems = res.data;
      setItems(allItems);

      const uniqueServices = [];
      const uniqueSubServices = [];

      allItems.forEach(item => {
        if (item.service && !uniqueServices.some(s => s.serviceId === item.service.serviceId)) {
          uniqueServices.push(item.service);
        }
        if (item.subService && !uniqueSubServices.some(s => s.subServiceId === item.subService.subServiceId)) {
          uniqueSubServices.push(item.subService);
        }
      });

      setServices(uniqueServices);
      setSubServices(uniqueSubServices);
    }).catch(console.error);

    axios.get("/schedule-plans").then(res => setSchedulePlanOptions(res.data)).catch(console.error);
  }, [propUserId]);

  const filteredItems = items.filter(item => {
    const matchesService = selectedService ? item.service?.serviceId === selectedService : true;
    const matchesSubService = selectedSubService ? item.subService?.subServiceId === selectedSubService : true;
    return matchesService && matchesSubService;
  });

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
      <h2 className="text-4xl font-bold text-center text-orange-600 mb-6">Service Provider Profile</h2>

      {/* Business Info */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <input required name="businessName" type="text" placeholder="Business Name" className="input" onChange={e => setFormData({ ...formData, businessName: e.target.value })} />
        <input required name="businessLicenseNumber" type="text" placeholder="License Number" className="input" onChange={e => setFormData({ ...formData, businessLicenseNumber: e.target.value })} />
        <input required name="gstNumber" type="text" placeholder="GST Number" className="input" onChange={e => setFormData({ ...formData, gstNumber: e.target.value })} />
        <label className="flex items-center gap-2 text-sm">
          <input name="needOfDeliveryAgent" type="checkbox" className="accent-orange-600" onChange={e => setFormData({ ...formData, needOfDeliveryAgent: e.target.checked })} /> Need Delivery Agent?
        </label>
      </div>

      {/* Schedule Plans */}
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

      {/* Item Price Section */}
      <div>
        <h3 className="text-xl font-semibold mb-3">Add Item Price</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <select value={selectedService} onChange={e => {
            setSelectedService(e.target.value);
            setSelectedSubService("");
            setSelectedItem("");
          }} className="input">
            <option value="">Select Service</option>
            {services.map(service => (
              <option key={service.serviceId} value={service.serviceId}>{service.serviceName}</option>
            ))}
          </select>

          <select value={selectedSubService} onChange={e => {
            setSelectedSubService(e.target.value);
            setSelectedItem("");
          }} className="input" disabled={subServices.length === 0}>
            <option value="">Select Sub-Service</option>
            {subServices.map(sub => (
              <option key={sub.subServiceId} value={sub.subServiceId}>{sub.subServiceName}</option>
            ))}
          </select>

          <select value={selectedItem} onChange={e => setSelectedItem(e.target.value)} className="input" disabled={filteredItems.length === 0}>
            <option value="">Select Item</option>
            {filteredItems.map(item => (
              <option key={item.itemId} value={item.itemId}>{item.itemName}</option>
            ))}
          </select>
        </div>

        <div className="flex gap-4 mt-4">
          <input type="number" placeholder="Price" value={itemPrice} onChange={e => setItemPrice(e.target.value)} className="input" />
          <button type="button" onClick={handlePriceAdd} className="btn-orange">Add</button>
        </div>

        <ul className="mt-4 space-y-2">
          {formData.priceDTO.map(p => {
            const fullItem = items.find(i => i.itemId === p.item.itemId);
            return (
              <li key={p.item.itemId} className="bg-white p-3 rounded-md flex justify-between items-center shadow">
                <span>{fullItem?.itemName || p.item.itemId} — ₹{p.price}</span>
                <button onClick={() => removePriceItem(p.item.itemId)} className="text-red-500 hover:underline">Remove</button>
              </li>
            );
          })}
        </ul>
      </div>

      {/* Bank Details */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <input name="accountHolderName" type="text" placeholder="Account Holder Name" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, accountHolderName: e.target.value } })} />
        <input name="bankName" type="text" placeholder="Bank Name" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, bankName: e.target.value } })} />
        <input name="ifscCode" type="text" placeholder="IFSC Code" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, ifscCode: e.target.value } })} />
        <input name="bankAccountNumber" type="text" placeholder="Bank Account Number" className="input" onChange={e => setFormData({ ...formData, bankAccount: { ...formData.bankAccount, bankAccountNumber: e.target.value } })} />
      </div>

      {/* File Uploads */}
      <div>
        <h4 className="text-xl font-semibold mb-3">Upload Documents</h4>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <label className="flex flex-col">Aadhar Card (required)<input type="file" name="aadharCard" onChange={handleFileChange} className="file-input" required /></label>
          <label className="flex flex-col">PAN Card (optional)<input type="file" name="panCard" onChange={handleFileChange} className="file-input" /></label>
          <label className="flex flex-col">Utility Bill (required)<input type="file" name="utilityBill" onChange={handleFileChange} className="file-input" required /></label>
          <label className="flex flex-col">Profile Photo (required)<input type="file" name="profilePhoto" onChange={handleFileChange} className="file-input" required /></label>
        </div>
      </div>

      {/* Submit */}
      <div className="text-center mt-6">
        <button type="submit" disabled={!userId} className="btn-orange w-full sm:w-auto px-10 py-3 text-lg">Submit Profile</button>
      </div>
    </form>
  );
}
