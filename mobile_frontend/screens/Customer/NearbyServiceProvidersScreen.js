import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  Image,
  ActivityIndicator,
  StyleSheet,
} from "react-native";
import * as Location from "expo-location";
import { useNavigation } from "@react-navigation/native";
import axios from "../../utils/axiosInstance";
import { useAuth } from "../../context/AuthContext";
import { fixImageUrl } from "../../utils/replaceLocalhost";
export default function NearbyServiceProvidersScreen() {
  const [allProviders, setAllProviders] = useState([]);
  const [nearbyProviders, setNearbyProviders] = useState([]);
  const [showNearby, setShowNearby] = useState(false);
  const [loadingNearby, setLoadingNearby] = useState(false);
  const [error, setError] = useState("");
  const [pinCode, setPinCode] = useState("");

  const { isLoggedIn } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    fetchAllProviders();
  }, []);

  const fetchAllProviders = async () => {
    try {
      const res = await axios.get("/customer/serviceProviders");
      setAllProviders(res.data);
    } catch (err) {
      setError("Failed to load service providers.");
    }
  };

  const fetchNearbyProviders = async () => {
    setLoadingNearby(true);
    setError("");
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") {
        setError("Permission to access location was denied.");
        return;
      }

      const location = await Location.getCurrentPositionAsync({});
      const { latitude, longitude } = location.coords;

      const res = await axios.get("/customer/serviceProviders/nearby", {
        params: { lat: latitude, lng: longitude, radiusKm: 5 },
      });
      setNearbyProviders(res.data);
      setShowNearby(true);
    } catch (err) {
      setError("Failed to fetch nearby providers.");
    } finally {
      setLoadingNearby(false);
    }
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
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.heading}>Service Providers</Text>

      <View style={styles.buttonRow}>
        <TouchableOpacity
          style={[styles.button, loadingNearby && styles.disabledButton]}
          onPress={fetchNearbyProviders}
          disabled={loadingNearby}
        >
          <Text style={styles.buttonText}>
            {loadingNearby ? "Finding Nearby..." : "Show Nearby Providers"}
          </Text>
        </TouchableOpacity>

        <TextInput
          style={styles.input}
          placeholder="Enter PIN Code"
          value={pinCode}
          onChangeText={setPinCode}
        />

        <TouchableOpacity style={styles.buttonAlt} onPress={fetchNearbyByPin}>
          <Text style={styles.buttonText}>Find by PIN</Text>
        </TouchableOpacity>

        {showNearby && (
          <TouchableOpacity
            style={styles.clearButton}
            onPress={() => setShowNearby(false)}
          >
            <Text style={styles.clearText}>Show All Providers</Text>
          </TouchableOpacity>
        )}
      </View>

      {error ? (
        <Text style={styles.errorText}>{error}</Text>
      ) : providersToShow.length === 0 ? (
        <ActivityIndicator size="large" color="#4B00B5" style={{ marginTop: 20 }} />
      ) : (
        providersToShow.map((provider) => (
          <View key={provider.serviceProviderId} style={styles.card}>
         <Image
              source={{
                uri: provider.photoImage
                  ? fixImageUrl(provider.photoImage)
                  : "https://via.placeholder.com/150",
              }}
              style={styles.image}
            />

            <View style={styles.cardContent}>
              <Text style={styles.title}>{provider.businessName}</Text>
              <Text style={styles.subText}>
                {provider.address?.areaName}, {provider.address?.city?.cityName}
              </Text>

              <Text style={styles.rating}>
                Rating: {provider.averageRating?.toFixed(1) || "0.0"}/5
              </Text>

              {(provider.items?.length > 0
                ? provider.items.slice(0, 3)
                : []
              ).map((item, i) => (
                <Text key={i} style={styles.itemText}>• {item.itemName}</Text>
              ))}

              <View style={styles.actions}>
                <TouchableOpacity
                  onPress={() =>
                    navigation.navigate("ProviderDetail", {
                      providerId: provider.serviceProviderId,
                      lat: provider.address?.latitude,
                      lng: provider.address?.longitude,
                    })
                  }
                >
                  <Text style={styles.link}>View Details</Text>
                </TouchableOpacity>

                <TouchableOpacity
                  style={isLoggedIn ? styles.bookButton : styles.disabledButton}
                  disabled={!isLoggedIn}
                  onPress={() => {
                    if (!isLoggedIn) {
                      navigation.navigate("Login", {
                        redirectTo: "OrderBooking",
                      });
                    } else {
                      navigation.navigate("OrderBooking", {
                        providerId: provider.serviceProviderId,
                      });
                    }
                  }}
                >
                  <Text style={styles.bookButtonText}>Book Now</Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: "#ffffff",
  },
  heading: {
    fontSize: 24,
    fontWeight: "bold",
    textAlign: "center",
    color: "#4B00B5",
    marginBottom: 16,
  },
  buttonRow: {
    marginBottom: 20,
  },
  button: {
    backgroundColor: "#4B00B5",
    padding: 12,
    borderRadius: 8,
    marginBottom: 10,
  },
  buttonAlt: {
    backgroundColor: "#FF4774",
    padding: 12,
    borderRadius: 8,
    marginBottom: 10,
  },
  clearButton: {
    marginTop: 8,
  },
  clearText: {
    textAlign: "center",
    color: "gray",
  },
  disabledButton: {
    backgroundColor: "#cccccc",
  },
  buttonText: {
    color: "white",
    textAlign: "center",
    fontWeight: "600",
  },
  input: {
    borderColor: "#ccc",
    borderWidth: 1,
    borderRadius: 6,
    padding: 10,
    marginBottom: 10,
    backgroundColor: "#f8f8f8",
  },
  errorText: {
    color: "red",
    textAlign: "center",
    marginBottom: 16,
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 10,
    marginBottom: 20,
    overflow: "hidden",
    borderWidth: 1,
    borderColor: "#e0e0e0",
  },
  image: {
    width: "100%",
    height: 180,
    resizeMode: "contain",
    backgroundColor: "#f2f2f2",
  },
  cardContent: {
    padding: 12,
  },
  title: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 4,
  },
  subText: {
    fontSize: 14,
    color: "#666",
  },
  rating: {
    fontSize: 14,
    marginVertical: 6,
  },
  itemText: {
    fontSize: 13,
    color: "#444",
  },
  actions: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 12,
  },
  link: {
    color: "#FF4774",
    fontWeight: "600",
  },
  bookButton: {
    backgroundColor: "#4B00B5",
    borderRadius: 6,
    paddingVertical: 6,
    paddingHorizontal: 12,
  },
  bookButtonText: {
    color: "white",
    fontWeight: "600",
    fontSize: 12,
  },
});
