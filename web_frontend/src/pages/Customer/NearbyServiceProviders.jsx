import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { FaStar, FaStarHalfAlt, FaRegStar } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";

export default function NearbyServiceProviders() {
  const [allProviders, setAllProviders] = useState([]);
  const [nearbyProviders, setNearbyProviders] = useState([]);
  const [showNearby, setShowNearby] = useState(false);
  const [loadingNearby, setLoadingNearby] = useState(false);
  const [error, setError] = useState("");
  const [pinCode, setPinCode] = useState("");

  const navigate = useNavigate();
  const { isLoggedIn } = useAuth();

  useEffect(() => {
    fetchAllProviders();
  }, []);

  const fetchAllProviders = async () => {
    try {
      const res = await axios.get("/customer/serviceProviders");
      setAllProviders(res.data);
    } catch (err) {
      console.error("Error fetching providers:", err?.response?.data || err.message);
      setError("Failed to load service providers.");
    }
  };

  const fetchNearbyProviders = async () => {
    if (!navigator.geolocation) {
      return setError("Geolocation not supported.");
    }

    setLoadingNearby(true);
    setError("");
    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;

        try {
          const res = await axios.get("/customer/serviceProviders/nearby", {
            params: { lat, lng, radiusKm: 5 },
          });
          setNearbyProviders(res.data);
          setShowNearby(true);
        } catch (err) {
          setError("Failed to fetch nearby service providers.");
        } finally {
          setLoadingNearby(false);
        }
      },
      () => {
        setError("Permission denied or error fetching location.");
        setLoadingNearby(false);
      }
    );
  };

  const fetchNearbyByPin = async () => {
    setLoadingNearby(true);
    setError("");
    try {
      const res = await axios.get("/customer/location/resolve-pin", {
        params: { pinCode },
      });

      const { latitude, longitude } = res.data;

      const nearbyRes = await axios.get("/customer/serviceProviders/nearby", {
        params: { lat: latitude, lng: longitude, radiusKm: 5 },
      });

      setNearbyProviders(nearbyRes.data);
      setShowNearby(true);
    } catch (err) {
      setError("Could not resolve PIN or fetch providers.");
    } finally {
      setLoadingNearby(false);
    }
  };

  const providersToShow = showNearby ? nearbyProviders : allProviders;

  return (
    <div className="flex flex-col min-h-screen bg-white text-gray-900">
      <Navbar />

      <main className="flex-grow px-6 py-8">
        <h1 className="text-3xl font-bold mb-6 text-center text-[#4B00B5]">Service Providers</h1>

        <div className="flex flex-wrap justify-center gap-4 mb-6">
          <button
            onClick={fetchNearbyProviders}
            disabled={loadingNearby}
            className="bg-[#4B00B5] text-white font-semibold px-6 py-2 rounded-lg hover:bg-[#360088] disabled:opacity-60"
          >
            {loadingNearby ? "Finding nearby..." : "Show Nearby Providers"}
          </button>

          <div className="flex flex-col sm:flex-row items-center gap-2">
            <input
              type="text"
              placeholder="Enter PIN Code"
              value={pinCode}
              onChange={(e) => setPinCode(e.target.value)}
              className="px-4 py-2 rounded-md border border-gray-300 text-gray-800 w-60"
            />
            <button
              onClick={fetchNearbyByPin}
              className="bg-[#FF4774] text-white font-semibold px-4 py-2 rounded-md hover:bg-pink-600"
            >
              Find by PIN
            </button>
          </div>

          {showNearby && (
            <button
              onClick={() => setShowNearby(false)}
              className="bg-gray-200 text-gray-800 font-semibold px-4 py-2 rounded-md hover:bg-gray-300"
            >
              Show All Providers
            </button>
          )}
        </div>

        {error && <p className="text-center text-red-600 font-medium mb-4">{error}</p>}

        {providersToShow.length === 0 && !error && (
          <p className="text-center text-gray-700">Loading providers...</p>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
          {providersToShow.map((provider) => (
            <div
              key={provider.serviceProviderId}
              className="bg-white border border-gray-200 text-gray-900 rounded-xl shadow hover:shadow-lg transition duration-300 w-full max-w-sm mx-auto"
            >
              <img
                src={provider.photoImage || "/default-provider.jpg"}
                alt={provider.businessName}
                onError={(e) => (e.target.src = "/default-provider.jpg")}
                className="w-full h-48 object-contain bg-gray-100 rounded-t"
              />
              <div className="p-4">
                <h2 className="text-xl font-semibold mb-1">{provider.businessName}</h2>
                <p className="text-sm text-gray-600 mb-2">
                  {provider.address?.areaName}, {provider.address?.city?.cityName}
                </p>

                <div className="flex items-center mb-2">
                  {Array.from({ length: 5 }).map((_, index) => {
                    const rating = provider.averageRating || 0;
                    const full = index + 1 <= Math.floor(rating);
                    const half = index + 1 - rating <= 0.5 && index + 1 > rating;

                    return full ? (
                      <FaStar key={index} className="text-yellow-400 text-sm" />
                    ) : half ? (
                      <FaStarHalfAlt key={index} className="text-yellow-300 text-sm" />
                    ) : (
                      <FaRegStar key={index} className="text-gray-300 text-sm" />
                    );
                  })}
                  <span className="ml-2 text-sm">
                    {provider.averageRating?.toFixed(1) || "0.0"}/5
                  </span>
                </div>

                <ul className="text-sm text-gray-700 mb-2">
                  {provider.items?.slice(0, 3).map((item, i) => (
                    <li key={i}>• {item.itemName}</li>
                  ))}
                </ul>

                <div className="flex justify-between items-center mt-2">
                  <button
                    className="text-sm text-[#FF4774] font-semibold hover:underline"
                    onClick={() =>
                      navigate(`/provider/${provider.serviceProviderId}`, {
                        state: {
                          lat: provider.address?.latitude,
                          lng: provider.address?.longitude,
                        },
                      })
                    }
                  >
                    View Details
                  </button>

                  <button
                    className={`text-sm px-3 py-1 rounded ${
                      isLoggedIn
                        ? "bg-[#4B00B5] text-white hover:bg-[#360088]"
                        : "bg-gray-300 text-gray-600 cursor-not-allowed"
                    }`}
                    disabled={!isLoggedIn}
                    onClick={() => {
                      if (!isLoggedIn) {
                        navigate("/login", { state: { redirectTo: "/order/book" } });
                      } else {
                        navigate("/order/book", {
                          state: { providerId: provider.serviceProviderId },
                        });
                      }
                    }}
                  >
                    Book Now
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </main>

      <Footer />
    </div>
  );
}
