import React, { useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import axios from "../../utils/axiosInstance";
import { useNavigate } from "react-router-dom";
import { FaBars, FaTimes } from "react-icons/fa";

export default function ProviderSidebar({ collapsed, toggleSidebar }) {
  const { logout } = useAuth();
  const [provider, setProvider] = useState(null);
  const [providerId] = useState(() => localStorage.getItem("providerId"));
  const navigate = useNavigate();

  useEffect(() => {
    if (providerId) {
      axios
        .get(`/provider/${providerId}`)
        .then((res) => setProvider(res.data))
        .catch((err) => console.error("Failed to load provider:", err));
    }
  }, [providerId]);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="fixed top-0 left-0 h-full z-40">
      <aside
        className={`${
          collapsed ? "w-20" : "w-64"
        } h-full bg-orange-300 text-black flex flex-col justify-between shadow-lg transition-all duration-300 rounded-r-3xl`}
      >
        {/* Toggle Button */}
        <div className="flex justify-end p-3">
          <button onClick={toggleSidebar}>
            {collapsed ? <FaBars size={20} /> : <FaTimes size={20} />}
          </button>
        </div>

        {/* Provider Info */}
        {!collapsed && (
          <div className="text-center py-2 px-4 border-b border-orange-400">
            <h1 className="text-xl font-bold">SmartLaundry</h1>
            {provider ? (
              <>
                <p className="text-sm mt-2">Provider ID: {provider.serviceProviderId}</p>
                <p className="text-sm">{provider.phoneNumber}</p>
                <p className="text-sm text-gray-800">{provider.email}</p>
                <div className="mt-2 flex items-center justify-center">
                  <span className="w-3 h-3 rounded-full bg-green-600 mr-2"></span>
                  <span className="text-sm font-semibold">Active</span>
                </div>
              </>
            ) : (
              <p className="text-sm">Loading...</p>
            )}
          </div>
        )}

        {/* Navigation */}
        <nav className="mt-4 px-4 space-y-2">
          <ul className="space-y-1 text-sm">
            <li>
              <button
                className="block w-full text-left px-2 py-1 hover:bg-orange-400 rounded"
                onClick={() => navigate("/provider/dashboard")}
              >
                Home
              </button>
            </li>
            <li>
              <button
                className="block w-full text-left px-2 py-1 hover:bg-orange-400 rounded"
                onClick={() => navigate("/provider/active-orders")}
              >
                Manage Workload
              </button>
            </li>
            {/* <li>
              <button
                className="block w-full text-left px-2 py-1 hover:bg-orange-400 rounded"
                onClick={() => navigate("/provider/delivered-orders")}
              >
                Payment
              </button>
            </li> */}
            <li>
              <button
                className="block w-full text-left px-2 py-1 hover:bg-orange-400 rounded"
                onClick={() => navigate("/provider/completed-orders")}
              >
                Order History
              </button>
            </li>
            <li>
              <button
                className="block w-full text-left px-2 py-1 hover:bg-orange-400 rounded"
                onClick={() => navigate("/sp/edit-profile")}
              >
                My Profile
              </button>
            </li>
            <li>
              <button
                className="block w-full text-left px-2 py-1 hover:bg-red-400 rounded font-semibold"
                onClick={handleLogout}
              >
                Log Out
              </button>
            </li>
          </ul>
        </nav>

        {/* Footer */}
        {!collapsed && (
          <div className="p-4 border-t border-orange-400 text-center text-sm">
            <p>&copy; {new Date().getFullYear()} SmartLaundry</p>
          </div>
        )}
      </aside>
    </div>
  );
}
