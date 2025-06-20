import React, { useState } from "react";
import InputField from "../../components/InputField";
import axios from "../../utils/axiosInstance";

export default function ContactInfo({ dummyOrderId, userId, onNext, onPrev }) {
  const [formData, setFormData] = useState({
    contactName: "",
    contactPhone: "",
    contactAddress: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async () => {
    const { contactName, contactPhone, contactAddress } = formData;
    setError("");

    if (!contactName || !contactPhone || !contactAddress) {
      return setError("All fields are required.");
    }
    if (!/^\d{10}$/.test(contactPhone)) {
      return setError("Phone number must be exactly 10 digits.");
    }

    try {
      setLoading(true);

      // ✅ Fix the URL to match backend route
      await axios.post(`/orders/contact/${dummyOrderId}`, {
        contactName,
        contactPhone,
        contactAddress,
      });

      onNext();
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Failed to save contact info.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6 bg-light rounded-xl shadow-lg mt-6">
      <h2 className="text-2xl font-bold text-text mb-4">Step 3: Contact Information</h2>

      <div className="mb-4">
        <InputField
          label="Contact Name"
          name="contactName"
          value={formData.contactName}
          onChange={handleChange}
          placeholder="" // avoid overlap
        />
      </div>

      <div className="mb-4">
        <InputField
          label="Phone Number"
          name="contactPhone"
          type="tel"
          value={formData.contactPhone}
          onChange={handleChange}
          placeholder=""
        />
      </div>

      <div className="mb-4">
        <InputField
          label="Address"
          name="contactAddress"
          value={formData.contactAddress}
          onChange={handleChange}
          placeholder=""
        />
      </div>

      {error && <p className="text-error mb-4 font-medium">{error}</p>}

      <div className="flex justify-between">
        <button
          onClick={onPrev}
          className="px-6 py-2 bg-border text-text rounded hover:bg-muted transition"
        >
          Previous
        </button>

        <button
          onClick={handleSubmit}
          disabled={loading}
          className="px-6 py-2 bg-accent4 text-white font-semibold rounded hover:bg-accent2 transition disabled:opacity-50"
        >
          {loading ? "Saving..." : "Next"}
        </button>
      </div>
    </div>
  );
}
