import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../../utils/axiosInstance";
import { useAuth } from "../../context/AuthContext";
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";
import { FaStar, FaStarHalfAlt, FaRegStar } from "react-icons/fa"; 

export default function ProviderDetail() {
  const { providerId } = useParams();
  const navigate = useNavigate();
  const { isLoggedIn } = useAuth();
  const [provider, setProvider] = useState(null);

  useEffect(() => {
    async function fetchProviderDetails() {
      try {
        const res = await axios.get(`/customer/serviceProviders/${providerId}`);
        setProvider(res.data);
      } catch (err) {
        console.error("Failed to fetch provider details", err);
      }
    }

    fetchProviderDetails();
  }, [providerId]);

  if (!provider)
    return <p className="text-center text-gray-700 mt-10">Loading...</p>;

  return (
    <div className="flex flex-col min-h-screen bg-white text-gray-900">
      <Navbar />

      <main className="flex-grow p-6 max-w-5xl mx-auto">
        <button
          className="mb-4 bg-[#4B00B5] text-white px-4 py-2 rounded hover:bg-[#360088]"
          onClick={() => navigate(-1)}
        >
          Go Back
        </button>

        <h1 className="text-3xl font-bold mb-2 text-[#4B00B5]">
          {provider.businessName}
        </h1>

        <img
          src={provider.photoImage || "/default-provider.jpg"}
          alt="Provider"
          onError={(e) => (e.target.src = "/default-provider.jpg")}
          className="w-full h-60 object-contain bg-gray-100 rounded-lg mb-4"
        />

        <p className="mb-2 text-lg">
          {provider.address?.areaName || "N/A"},{" "}
          {provider.address?.city?.cityName || "N/A"}
        </p>

        {/* Star Rating Display */}
        <div className="flex items-center mb-4">
          {Array.from({ length: 5 }).map((_, index) => {
            const rating = provider.averageRating || 0;
            const full = index + 1 <= Math.floor(rating);
            const half = index + 1 - rating <= 0.5 && index + 1 > rating;

            return full ? (
              <FaStar key={index} className="text-yellow-400" />
            ) : half ? (
              <FaStarHalfAlt key={index} className="text-yellow-300" />
            ) : (
              <FaRegStar key={index} className="text-gray-300" />
            );
          })}
          <span className="ml-2 text-sm text-gray-700">
            {provider.averageRating?.toFixed(1) || "0.0"}/5
          </span>
        </div>

        {/* Items and Prices */}
        <h2 className="text-2xl font-semibold mt-6 mb-2 text-[#4B00B5]">
          Items & Prices
        </h2>
        <ul className="mb-6">
          {provider.prices?.length > 0 ? (
            provider.prices.map((price, index) => (
              <li key={index} className="mb-4 border-b border-gray-300 pb-2">
                <p className="text-lg font-semibold text-gray-800">
                  • {price.item.itemName}
                </p>
                <p className="text-sm ml-4 text-gray-500">
                  Service: {price.item.serviceName || "N/A"}, Sub-Service:{" "}
                  {price.item.subServiceName || "N/A"}
                </p>
                <p className="text-sm ml-4 text-[#FF4774]">
                  Price: ₹{price.price || "N/A"}
                </p>
              </li>
            ))
          ) : (
            <li className="text-gray-500">No pricing information available.</li>
          )}
        </ul>

        {/* Reviews */}
        <h2 className="text-2xl font-semibold mt-6 mb-2 text-[#4B00B5]">
          Reviews
        </h2>
        <ul>
          {provider.reviews?.length > 0 ? (
            provider.reviews.map((review, idx) => (
              <li key={idx} className="mb-2 text-gray-700">
                <strong>{review.name}</strong>: {review.review}
              </li>
            ))
          ) : (
            <li className="text-gray-500">No reviews yet.</li>
          )}
        </ul>

        {/* Book Now Button */}
        <button
          className={`mt-8 px-6 py-2 rounded font-semibold ${
            isLoggedIn
              ? "bg-[#FF4774] text-white hover:bg-pink-600"
              : "bg-gray-300 text-gray-600 cursor-not-allowed"
          }`}
          disabled={!isLoggedIn}
          onClick={() => {
            if (!isLoggedIn) {
              alert("Please login to book a service.");
            } else {
              navigate("/order/book", { state: { providerId } });
            }
          }}
        >
          Book Now
        </button>
      </main>

      <Footer />
    </div>
  );
}
