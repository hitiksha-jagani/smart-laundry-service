import React, { useEffect, useState } from "react";
import axios from "../../utils/axiosInstance";
import { FaStar } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function NearbyServiceProviders() {
  const [allProviders, setAllProviders] = useState([]);
  const [nearbyProviders, setNearbyProviders] = useState([]);
  const [showNearby, setShowNearby] = useState(false);
  const [loadingNearby, setLoadingNearby] = useState(false);
  const [error, setError] = useState("");
  const [pinCode, setPinCode] = useState("");

  const navigate = useNavigate();

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
    <div className="min-h-screen bg-gradient-to-br from-[#4B00B5] to-[#FF4774] p-6 text-white">
      <h1 className="text-3xl font-bold mb-6 text-center">Service Providers</h1>

      <div className="flex flex-wrap justify-center gap-4 mb-6">
        <button
          onClick={fetchNearbyProviders}
          disabled={loadingNearby}
          className="bg-white text-[#4B00B5] font-semibold px-6 py-2 rounded-lg shadow hover:bg-gray-100 disabled:opacity-60"
        >
          {loadingNearby ? "Finding nearby..." : "Show Nearby Providers"}
        </button>

        <div className="flex flex-col sm:flex-row items-center gap-2">
          <input
            type="text"
            placeholder="Enter PIN Code"
            value={pinCode}
            onChange={(e) => setPinCode(e.target.value)}
            className="px-4 py-2 rounded-md text-gray-800 w-60"
          />
          <button
            onClick={fetchNearbyByPin}
            className="bg-white text-[#FF4774] font-semibold px-4 py-2 rounded-md hover:bg-gray-100"
          >
            Find by PIN
          </button>
        </div>

        {showNearby && (
          <button
            onClick={() => setShowNearby(false)}
            className="bg-white text-gray-700 font-semibold px-4 py-2 rounded-md hover:bg-gray-100"
          >
            Show All Providers
          </button>
        )}
      </div>

      {error && <p className="text-center text-red-200 font-medium mb-4">{error}</p>}

      {providersToShow.length === 0 && !error && (
        <p className="text-center text-white">Loading providers...</p>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {providersToShow.map((provider) => (
          <div
            key={provider.serviceProviderId}
            className="bg-white text-gray-900 rounded-xl shadow-lg overflow-hidden w-full max-w-sm mx-auto hover:shadow-xl transition duration-300"
          >
            <img
              src={provider.photoImage || "/default-provider.jpg"}
              alt={provider.businessName}
              onError={(e) => (e.target.src = "/default-provider.jpg")}
              className="w-full h-48 object-contain bg-white rounded-t"
            />
            <div className="p-4">
              <h2 className="text-xl font-semibold mb-1">{provider.businessName}</h2>
              <p className="text-sm text-gray-600 mb-2">
                {provider.address?.areaName}, {provider.address?.city?.cityName}
              </p>

              <div className="flex items-center mb-2">
                {[...Array(5)].map((_, i) => (
                  <FaStar
                    key={i}
                    className={`text-sm ${i < provider.averageRating ? "text-yellow-400" : "text-gray-300"}`}
                  />
                ))}
                <span className="ml-2 text-sm">{provider.averageRating}/5</span>
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
                  className="text-sm text-white bg-[#4B00B5] px-3 py-1 rounded hover:bg-[#360088]"
                  onClick={() =>
                    navigate("/order/book", {
                      state: { providerId: provider.serviceProviderId },
                    })
                  }
                >
                  Book Now
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
