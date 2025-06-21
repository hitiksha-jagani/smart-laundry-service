import React from "react";

export default function PrimaryButton({ children, onClick, type = "button", disabled = false }) {
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className="px-6 py-3 bg-accent4 text-white font-semibold rounded-lg hover:bg-accent4Hover transition disabled:opacity-50"
    >
      {children}
    </button>
  );
}
