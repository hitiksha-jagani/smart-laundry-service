import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { useNavigate } from "react-router-dom";

const UpdateProfile = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    userId: "", // Will be filled from token/localStorage/etc.
    firstName: "",
    lastName: "",
    email: "",
    phoneNo: "",
    preferredLanguage: "",
  });

  const [message, setMessage] = useState("");

  useEffect(() => {
    // Example: fetch current user data to prefill form
    const storedUserId = localStorage.getItem("userId"); // Adjust as per your storage
    if (storedUserId) {
      setFormData(prev => ({ ...prev, userId: storedUserId }));
      axios.get(`/user/${storedUserId}`) // You can create this endpoint if not present
        .then(res => {
          const user = res.data;
          setFormData({
            userId: user.userId,
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phoneNo: user.phoneNo,
            preferredLanguage: user.preferredLanguage || "",
          });
        });
    }
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.put("/customer/profile/update", formData)
      setMessage("Profile updated successfully.");

      setTimeout(() => {
        navigate("/customer/dashboard"); 
      }, 3000);
    } catch (err) {
      setMessage(err.response?.data || "Something went wrong.");
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 bg-white shadow-md rounded-lg">
      <h2 className="text-2xl font-bold mb-4 text-orange-600">Update Profile</h2>

      {message && (
        <div className="mb-4 text-green-600 font-semibold">{message}</div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          name="firstName"
          placeholder="First Name"
          className="w-full p-2 border rounded"
          value={formData.firstName}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="lastName"
          placeholder="Last Name"
          className="w-full p-2 border rounded"
          value={formData.lastName}
          onChange={handleChange}
        />

        <input
          type="email"
          name="email"
          placeholder="Email"
          className="w-full p-2 border rounded"
          value={formData.email}
          onChange={handleChange}
          required
        />

        <input
          type="tel"
          name="phoneNo"
          placeholder="Phone Number"
          className="w-full p-2 border rounded"
          value={formData.phoneNo}
          onChange={handleChange}
          required
        />

        <select
          name="preferredLanguage"
          className="w-full p-2 border rounded"
          value={formData.preferredLanguage}
          onChange={handleChange}
        >
          <option value="">Select Language</option>
          <option value="ENGLISH">English</option>
          <option value="HINDI">Hindi</option>
          <option value="MARATHI">Marathi</option>
          {/* Add more as needed */}
        </select>

        <button
          type="submit"
          className="w-full bg-orange-500 text-white py-2 rounded hover:bg-orange-600 transition"
        >
          Update Profile
        </button>
      </form>
    </div>
  );
};

export default UpdateProfile;
