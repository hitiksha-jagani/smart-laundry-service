import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

export default function Navbar() {
  const { i18n } = useTranslation();

  const changeLanguage = (e) => {
    i18n.changeLanguage(e.target.value);
  };

  return (
    <header className="bg-gradient-to-r from-[#4B00B5] to-[#FF4774] sticky top-0 z-10 shadow-md">
      <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
        <h2 className="text-xl font-bold text-white tracking-wide">Smart Laundry</h2>
        
        <nav className="flex items-center space-x-6 text-sm text-white">
          {["/", "/book", "/login", "/register"].map((path, i) => {
            const labels = ["Home", "Book Services", "Login", "Sign Up"];
            return (
              <Link
                key={path}
                to={path}
                className="relative transition-all duration-200 hover:text-[#FFD200] hover:scale-105 after:absolute after:left-0 after:-bottom-1 after:w-0 after:h-[2px] after:bg-[#FFD200] hover:after:w-full after:transition-all after:duration-300"
              >
                {labels[i]}
              </Link>
            );
          })}

          <select
            onChange={changeLanguage}
            value={i18n.language}
            className="border border-white bg-transparent text-white px-2 py-1 rounded hover:bg-white/10 transition-all duration-200"
          >
            <option value="en" className="text-black">English</option>
            <option value="hi" className="text-black">हिन्दी</option>
          </select>
        </nav>
      </div>
    </header>
  );
}
