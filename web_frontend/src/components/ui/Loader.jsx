// src/components/ui/Loader.jsx
import React from "react";

const Loader = () => {
  return (
    <div className="flex justify-center items-center py-6">
      <div className="w-10 h-10 border-4 border-purple-500 border-t-transparent rounded-full animate-spin"></div>
    </div>
  );
};

export default Loader;
