import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { i18n } = useTranslation();
  const { isLoggedIn, logout } = useAuth(); // ✅ use auth state
  const navigate = useNavigate();

  const changeLanguage = (e) => {
    i18n.changeLanguage(e.target.value);
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="bg-gradient-to-r from-[#4B00B5] to-[#FF4774] sticky top-0 z-10 shadow-md">
      <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
        <h2 className="text-xl font-bold text-white tracking-wide">Smart Laundry</h2>

        <nav className="flex items-center space-x-6 text-sm text-white">
          <Link
            to="/customer/dashboard"
            className="relative transition-all duration-200 hover:text-[#FFD200] hover:scale-105 after:absolute after:left-0 after:-bottom-1 after:w-0 after:h-[2px] after:bg-[#FFD200] hover:after:w-full after:transition-all after:duration-300"
          >
            Home
          </Link>

          <Link
            to="/service-providers"
            className="relative transition-all duration-200 hover:text-[#FFD200] hover:scale-105 after:absolute after:left-0 after:-bottom-1 after:w-0 after:h-[2px] after:bg-[#FFD200] hover:after:w-full after:transition-all after:duration-300"
          >
            Book Services
          </Link>

            {isLoggedIn && (
            <Link to="/my-profile" className="...">My Profile</Link>
          )}

          {/* ✅ Toggle Login/Register or Logout based on isLoggedIn */}
          {!isLoggedIn ? (
            <>
              <Link
                to="/login"
                className="relative transition-all duration-200 hover:text-[#FFD200] hover:scale-105 after:absolute after:left-0 after:-bottom-1 after:w-0 after:h-[2px] after:bg-[#FFD200] hover:after:w-full after:transition-all after:duration-300"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="relative transition-all duration-200 hover:text-[#FFD200] hover:scale-105 after:absolute after:left-0 after:-bottom-1 after:w-0 after:h-[2px] after:bg-[#FFD200] hover:after:w-full after:transition-all after:duration-300"
              >
                Sign Up
              </Link>
            </>
          ) : (
            <button
              onClick={handleLogout}
              className="bg-white/10 hover:bg-white/20 px-3 py-1.5 rounded text-white font-medium transition"
            >
              Logout
            </button>

          )}

        </nav>
      </div>
    </header>
  );
}

