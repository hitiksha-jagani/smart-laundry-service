import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { useNavigate } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";

export default function EditServiceProviderProfile() {
  const [profile, setProfile] = useState(null);
  const [items, setItems] = useState([]);
  const [selectedPrices, setSelectedPrices] = useState([]);
  const [blockedDays, setBlockedDays] = useState([]);
  const [selectedDates, setSelectedDates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    axios.get("/sp/sp-profile")
      .then(res => {
        setProfile(res.data);
        setSelectedPrices(res.data.priceDTO || []);
        if (res.data.serviceProviderId) {
          axios.get(`/sp/block-days?providerId=${res.data.serviceProviderId}`)
            .then(res => {
              const localDates = res.data.map(date => new Date(date));
              setBlockedDays(localDates);
              setSelectedDates(localDates);
            })
            .catch(() => console.log("Failed to fetch blocked days"));
        }
        setLoading(false);
      })
      .catch(() => {
        setError("Failed to load profile");
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (profile?.serviceId) {
      axios.get(`/items?serviceId=${profile.serviceId}&subServiceId=${profile.subServiceId || ""}`)
        .then(res => setItems(res.data))
        .catch(() => alert("Failed to fetch items"));
    }
  }, [profile?.serviceId, profile?.subServiceId]);

  const handleChange = (field, value) => {
    setProfile(prev => ({ ...prev, [field]: value }));
  };

  const handleAddressChange = (field, value) => {
    setProfile(prev => ({
      ...prev,
      address: {
        ...prev.address,
        [field]: value
      }
    }));
  };

  const handleBankChange = (field, value) => {
    setProfile(prev => ({
      ...prev,
      bankAccount: {
        ...prev.bankAccount,
        [field]: value
      }
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const completeProfile = {
        ...profile,
        priceDTO: selectedPrices
      };

      const formData = new FormData();
      formData.append("profile", new Blob([JSON.stringify(completeProfile)], { type: "application/json" }));

      // File inputs
      if (profile.aadharCard) formData.append("aadharCard", profile.aadharCard);
      if (profile.panCard) formData.append("panCard", profile.panCard);
      if (profile.utilityBill) formData.append("utilityBill", profile.utilityBill);
      if (profile.profilePhoto) formData.append("profilePhoto", profile.profilePhoto);

      await axios.put("/sp/sp-profile/edit", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });

      await axios.post("/sp/block-days", {
        providerId: profile.serviceProviderId,
        dates: selectedDates.map(d => d.toISOString().split("T")[0])
      });

      alert("Profile updated successfully");
      navigate("/sp/dashboard");
    } catch {
      alert("Failed to update profile");
    }
  };

  if (loading) return <div className="text-center p-6">Loading...</div>;
  if (error) return <div className="text-red-500 text-center p-6">{error}</div>;

  return (
    <form onSubmit={handleSubmit} className="bg-white p-8 shadow-md rounded-2xl max-w-4xl mx-auto space-y-6 border border-orange-200">
      <h2 className="text-2xl font-semibold text-orange-600">Edit Profile</h2>

      {/* Personal Info */}
      <section className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div><label className="text-sm text-gray-600">First Name</label><input value={profile.firstName || ""} onChange={e => handleChange("firstName", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
        <div><label className="text-sm text-gray-600">Last Name</label><input value={profile.lastName || ""} onChange={e => handleChange("lastName", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
        <div><label className="text-sm text-gray-600">Business Name</label><input value={profile.businessName || ""} onChange={e => handleChange("businessName", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
        <div><label className="text-sm text-gray-600">Business License Number</label><input value={profile.businessLicenseNumber || ""} onChange={e => handleChange("businessLicenseNumber", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
        <div><label className="text-sm text-gray-600">GST Number</label><input value={profile.gstNumber || ""} onChange={e => handleChange("gstNumber", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
        <div className="flex items-center space-x-2">
          <input type="checkbox" checked={profile.needOfDeliveryAgent} onChange={e => handleChange("needOfDeliveryAgent", e.target.checked)} />
          <label className="text-sm text-gray-600">Need Delivery Agent?</label>
        </div>
        <div className="md:col-span-2">
          <label className="text-sm text-gray-600">Schedule Plans</label>
          <input value={profile.schedulePlans?.join(",") || ""} onChange={e => handleChange("schedulePlans", e.target.value.split(","))} className="w-full mt-1 p-2 border rounded-lg" />
        </div>
      </section>

      {/* File Upload Section */}
      <section>
        <h3 className="text-lg font-medium text-orange-500 mb-2">Upload Documents</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label className="text-sm text-gray-600">Aadhar Card</label><input type="file" accept="image/*" onChange={e => handleChange("aadharCard", e.target.files[0])} /></div>
          <div><label className="text-sm text-gray-600">PAN Card</label><input type="file" accept="image/*" onChange={e => handleChange("panCard", e.target.files[0])} /></div>
          <div><label className="text-sm text-gray-600">Utility Bill</label><input type="file" accept="image/*" onChange={e => handleChange("utilityBill", e.target.files[0])} /></div>
          <div><label className="text-sm text-gray-600">Profile Photo</label><input type="file" accept="image/*" onChange={e => handleChange("profilePhoto", e.target.files[0])} /></div>
        </div>
      </section>

      {/* Address */}
      <section>
        <h3 className="text-lg font-medium text-orange-500 mb-2">Address</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label className="text-sm text-gray-600">Name</label><input value={profile.address?.name || ""} onChange={e => handleAddressChange("name", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
          <div><label className="text-sm text-gray-600">Area</label><input value={profile.address?.areaName || ""} onChange={e => handleAddressChange("areaName", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
          <div><label className="text-sm text-gray-600">Pincode</label><input value={profile.address?.pincode || ""} onChange={e => handleAddressChange("pincode", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
          <div><label className="text-sm text-gray-600">City</label><input value={profile.address?.cityName || ""} onChange={e => handleAddressChange("cityName", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
        </div>
      </section>

      {/* Bank */}
      <section>
        <h3 className="text-lg font-medium text-orange-500 mb-2">Bank Details</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label className="text-sm text-gray-600">Bank Name</label><input value={profile.bankAccount?.bankName || ""} onChange={e => handleBankChange("bankName", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
          <div><label className="text-sm text-gray-600">IFSC Code</label><input value={profile.bankAccount?.ifscCode || ""} onChange={e => handleBankChange("ifscCode", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
          <div><label className="text-sm text-gray-600">Account Number</label><input value={profile.bankAccount?.bankAccountNumber || ""} onChange={e => handleBankChange("bankAccountNumber", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
          <div><label className="text-sm text-gray-600">Account Holder Name</label><input value={profile.bankAccount?.accountHolderName || ""} onChange={e => handleBankChange("accountHolderName", e.target.value)} className="w-full mt-1 p-2 border rounded-lg" /></div>
        </div>
      </section>

      {/* Pricing Section */}
      <section>
        <h3 className="text-lg font-medium text-orange-500 mb-2">Set Your Prices</h3>
        <table className="w-full border text-sm">
          <thead>
            <tr className="bg-orange-100 text-left">
              <th className="p-2 border">Item</th>
              <th className="p-2 border">Price (₹)</th>
            </tr>
          </thead>
          <tbody>
            {items.map(item => {
              const matched = selectedPrices.find(p => p.itemId === item.itemId) || {};
              return (
                <tr key={item.itemId}>
                  <td className="p-2 border">{item.itemName}</td>
                  <td className="p-2 border">
                    <input
                      type="number"
                      min="0"
                      className="w-full border p-1 rounded"
                      value={matched.price || ""}
                      onChange={e => {
                        const price = parseFloat(e.target.value);
                        setSelectedPrices(prev => {
                          const others = prev.filter(p => p.itemId !== item.itemId);
                          return [...others, { itemId: item.itemId, itemName: item.itemName, price }];
                        });
                      }}
                    />
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </section>

      {/* Availability Calendar */}
      <section>
        <h3 className="text-lg font-medium text-orange-500 mb-2">Manage Off-Days</h3>
        <p className="text-sm text-gray-600 mb-2">Click dates to block/unblock your availability.</p>
        <Calendar
          onClickDay={(date) => {
            const exists = selectedDates.some(d => d.toDateString() === date.toDateString());
            if (exists) {
              setSelectedDates(prev => prev.filter(d => d.toDateString() !== date.toDateString()));
            } else {
              setSelectedDates(prev => [...prev, date]);
            }
          }}
          tileClassName={({ date }) =>
            selectedDates.some(d => d.toDateString() === date.toDateString()) ? "bg-orange-200 rounded-full" : ""
          }
        />
      </section>

      {/* Submit */}
      <div className="text-right">
        <button type="submit" className="bg-orange-500 text-white px-6 py-2 rounded-lg shadow hover:bg-orange-600 transition">
          Update Profile
        </button>
      </div>
    </form>
  );
}
