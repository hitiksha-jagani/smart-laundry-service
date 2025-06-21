import React from "react";
import InputField from "../../components/InputField";
import axios from "../../utils/axiosInstance";
import PrimaryButton from "../../components/PrimaryButton";

export default function ContactInfo({
  dummyOrderId,
  userId,
  onNext,
  onPrev,
  initialOrderData,
  setInitialOrderData,
}) {
  const { contactName = "", contactPhone = "", contactAddress = "" } = initialOrderData || {};

  const [error, setError] = React.useState("");
  const [loading, setLoading] = React.useState(false);

  const setField = (field, value) => {
    setInitialOrderData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async () => {
    setError("");

    if (!contactName || !contactPhone || !contactAddress) {
      return setError("All fields are required.");
    }
    if (!/^\d{10}$/.test(contactPhone)) {
      return setError("Phone number must be exactly 10 digits.");
    }

    try {
      setLoading(true);
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
    <div className="p-6 max-w-xl mx-auto bg-white shadow rounded">
      <h2 className="text-xl font-semibold mb-4">Step 3: Contact Information</h2>

      <div className="mb-4">
        <InputField
          label="Contact Name"
          name="contactName"
          value={contactName}
          onChange={(e) => setField("contactName", e.target.value)}
        />
      </div>

      <div className="mb-4">
        <InputField
          label="Phone Number"
          name="contactPhone"
          type="tel"
          value={contactPhone}
          onChange={(e) => setField("contactPhone", e.target.value)}
        />
      </div>

      <div className="mb-4">
        <InputField
          label="Address"
          name="contactAddress"
          value={contactAddress}
          onChange={(e) => setField("contactAddress", e.target.value)}
        />
      </div>

      {error && <p className="text-red-600 font-medium mb-4">{error}</p>}

      <div className="flex justify-between pt-4">
        <button
          onClick={onPrev}
          className="px-6 py-3 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition"
        >
          Previous
        </button>

        <PrimaryButton onClick={handleSubmit} disabled={loading}>
          {loading ? "Saving..." : "Next"}
        </PrimaryButton>
      </div>
    </div>
  );
}
