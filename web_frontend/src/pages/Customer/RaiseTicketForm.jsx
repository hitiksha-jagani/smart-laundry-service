import React, { useState } from "react";
import axios from "../../utils/axiosInstance";
import { useNavigate } from "react-router-dom";

export default function RaiseTicketForm() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [category, setCategory] = useState("GENERAL");
  const [photo, setPhoto] = useState(null);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccessMsg("");

    const ticketData = {
      title,
      description,
      category,
      status: "NOT_RESPONDED",
      submittedAt: new Date().toISOString(), // Optional; backend also defaults
    };

    const formData = new FormData();
    formData.append("ticket", JSON.stringify(ticketData));
    if (photo) {
      formData.append("photo", photo);
    }

    try {
      const res = await axios.post("/ticket/raise", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      setSuccessMsg("Ticket raised successfully.");
      setTimeout(() => navigate("/customer/dashboard"), 1500);
    } catch (err) {
      setError(err?.response?.data || "Failed to raise ticket. Try again.");
    }
  };

  return (
    <div className="max-w-xl mx-auto mt-10 p-6 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-6 text-purple-700 text-center">Raise a Ticket</h2>

      {error && <p className="text-red-600 text-center mb-4">{error}</p>}
      {successMsg && <p className="text-green-600 text-center mb-4">{successMsg}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block mb-1 font-medium">Title</label>
          <input
            type="text"
            required
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

        <div>
          <label className="block mb-1 font-medium">Description</label>
          <textarea
            required
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2"
            rows={4}
          />
        </div>

        <div>
          <label className="block mb-1 font-medium">Category</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2"
          >
            <option value="GENERAL">General</option>
            <option value="PAYMENT">Payment</option>
            <option value="ORDER">Order</option>
            <option value="DELIVERY">Delivery</option>
            <option value="OTHER">Other</option>
          </select>
        </div>

        <div>
          <label className="block mb-1 font-medium">Optional Photo</label>
          <input
            type="file"
            accept="image/*"
            onChange={(e) => setPhoto(e.target.files[0])}
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

        <button
          type="submit"
          className="w-full bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 rounded"
        >
          Submit Ticket
        </button>
      </form>
    </div>
  );
}
