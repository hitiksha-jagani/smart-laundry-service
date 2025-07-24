import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "../../utils/axiosInstance";

const MyProfile = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState({});
  const [orderStats, setOrderStats] = useState({ delivered: 0, cancelled: 0 });
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    if (!userId) return;

    // ✅ Fetch user profile
    axios.get(`/user/${userId}`)
      .then((res) => {
        setUser(res.data);
      })
      .catch((err) => {
        console.error("Failed to load user profile", err);
        alert("Something went wrong while loading your profile.");
      });

    // ✅ Fetch order statistics
    axios
      .get(`/orders/user/${userId}/stats`)
      .then((res) => {
        setOrderStats({
          delivered: res.data.completed,
          cancelled: res.data.cancelled,
        });
      })
      .catch((err) => {
        console.error("Failed to load order statistics", err);
        alert("Something went wrong while loading your order stats.");
      });
  }, [userId]);

  const handleEdit = () => {
    navigate("/update-profile");
  };

  const handleHistory = () => {
    navigate("/customer/Orderhistory");
  };

  return (
    <div className="max-w-4xl mx-auto mt-10 p-4">
      <h2 className="text-3xl font-bold text-orange-600 mb-6">My Profile</h2>

      <div className="bg-white shadow rounded-lg p-6 mb-6">
        <h3 className="text-xl font-semibold mb-2">User Information</h3>
        <p>
          <strong>Name:</strong> {user.firstName} {user.lastName}
        </p>
        <p>
          <strong>Email:</strong> {user.email}
        </p>
        <p>
          <strong>Phone:</strong> {user.phoneNo}
        </p>
        <p>
          <strong>Preferred Language:</strong> {user.preferredLanguage}
        </p>
        <button
          onClick={handleEdit}
          className="mt-4 px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
        >
          Edit Profile
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-xl font-semibold text-orange-500 mb-4">
            Order Statistics
          </h3>
          <p className="mb-2">
            Delivered Orders: <strong>{orderStats.delivered}</strong>
          </p>
          <p>
            Cancelled Orders: <strong>{orderStats.cancelled}</strong>
          </p>
        </div>

        <div className="bg-white shadow rounded-lg p-6 flex flex-col justify-between">
          <div>
            <h3 className="text-xl font-semibold text-orange-500 mb-4">
              Order History
            </h3>
            <p>View all your past orders with full details.</p>
          </div>
          <button
            onClick={handleHistory}
            className="mt-6 px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
          >
            View Order History
          </button>
        </div>
      </div>
    </div>
  );
};

export default MyProfile;
