import React from "react";
import Navbar from "./Navbar";
import Footer from "./Footer";

const AuthLayout = ({ title, children, widthClass = "max-w-sm" }) => {
  return (
    <>
      <Navbar />

      <main className="min-h-screen bg-gray-50 flex flex-col">
        <div className="flex-grow flex items-center justify-center px-4 py-12">
          <div className={`w-full ${widthClass} bg-white px-6 py-8 md:px-8 rounded-2xl shadow-lg border border-gray-200`}>
            <h2 className="text-2xl md:text-3xl font-bold text-center text-purple-700 mb-6">
              {title}
            </h2>
            {children}
          </div>
        </div>
      </main>

      <Footer />
    </>
  );
};

export default AuthLayout;
