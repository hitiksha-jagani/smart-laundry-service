import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function OrderSuccess() {
  const navigate = useNavigate();

  useEffect(() => {
    const timeout = setTimeout(() => {
      navigate("/"); 
    }, 5000);

    return () => clearTimeout(timeout); 
  }, [navigate]);

  return (
    <div className="flex flex-col items-center justify-center h-screen text-center bg-green-100">
      <h1 className="text-3xl font-bold text-green-700 mb-4">🎉 Order Placed Successfully!</h1>
      <p className="text-lg text-green-800">You will be redirected to the homepage shortly...</p>
      <p className="text-sm text-gray-500 mt-2">If not redirected, <button onClick={() => navigate("/")} className="underline text-blue-600">click here</button>.</p>
    </div>
  );
}
