import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  Image,
  TouchableOpacity,
  ActivityIndicator,
  StyleSheet,
  Alert,
} from "react-native";
import { useNavigation, useRoute } from "@react-navigation/native";
import axios from "../../utils/axiosInstance";
import { useAuth } from "../../context/AuthContext";
import { fixImageUrl } from "../../utils/replaceLocalhost";
export default function ProviderDetailScreen() {
  const route = useRoute();
  const navigation = useNavigation();
  const { providerId } = route.params || {};
  const { isLoggedIn } = useAuth();

  const [provider, setProvider] = useState(null);
  const [completedOrders, setCompletedOrders] = useState([]);

  useEffect(() => {
    fetchProviderDetails();
    fetchCompletedOrders();
  }, [providerId]);

  const fetchProviderDetails = async () => {
    try {
      const res = await axios.get(`/customer/serviceProviders/${providerId}`);
      setProvider(res.data);
    } catch (err) {
      console.error("Failed to fetch provider details", err);
    }
  };

  const fetchCompletedOrders = async () => {
    try {
      const res = await axios.get(
        `/provider/orders/${providerId}/order-history?status=COMPLETED`
      );
      setCompletedOrders(res.data || []);
    } catch (err) {
      console.error("Failed to fetch completed orders", err);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "N/A";
    const date = new Date(dateStr);
    return date.toLocaleDateString("en-IN", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  if (!provider) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#4B00B5" />
        <Text style={styles.loadingText}>Loading...</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <TouchableOpacity
        onPress={() => navigation.goBack()}
        style={styles.backButton}
      >
        <Text style={styles.backButtonText}>← Go Back</Text>
      </TouchableOpacity>

      <Text style={styles.heading}>{provider.businessName}</Text>

      <Image
          source={{
            uri: provider.photoImage
              ? fixImageUrl(provider.photoImage)
              : "https://via.placeholder.com/150",
          }}
          style={styles.image}
        />

      <Text style={styles.subText}>
        {provider.address?.areaName || "N/A"},{" "}
        {provider.address?.city?.cityName || "N/A"}
      </Text>

      {/* Rating */}
      <Text style={styles.rating}>
        Rating: {provider.averageRating?.toFixed(1) || "0.0"} / 5
      </Text>

      {/* Items and Prices */}
      <Text style={styles.sectionTitle}>Items & Prices</Text>
      {provider.prices?.length > 0 ? (
        provider.prices.map((price, idx) => (
          <View key={idx} style={styles.itemContainer}>
            <Text style={styles.itemName}>• {price.item.itemName}</Text>
            <Text style={styles.itemDetail}>
              Service: {price.item.serviceName || "N/A"}, Sub-Service:{" "}
              {price.item.subServiceName || "N/A"}
            </Text>
            <Text style={styles.itemPrice}>₹{price.price || "N/A"}</Text>
          </View>
        ))
      ) : (
        <Text style={styles.placeholder}>No pricing info available.</Text>
      )}

      {/* Reviews */}
      <Text style={styles.sectionTitle}>Reviews</Text>
      {provider.reviews?.length > 0 ? (
        provider.reviews.map((review, idx) => (
          <View key={idx} style={styles.reviewContainer}>
            <Text style={styles.reviewName}>{review.name}</Text>
            <Text>{review.review}</Text>
          </View>
        ))
      ) : (
        <Text style={styles.placeholder}>No reviews yet.</Text>
      )}

      {/* Book Now Button */}
      <TouchableOpacity
        style={isLoggedIn ? styles.bookButton : styles.disabledButton}
        disabled={!isLoggedIn}
        onPress={() => {
          if (!isLoggedIn) {
            Alert.alert("Please login to book a service.");
            navigation.navigate("Login", {
              redirectTo: "OrderBooking",
            });
          } else {
            navigation.navigate("OrderBooking", {
              providerId,
            });
          }
        }}
      >
        <Text style={styles.bookText}>Book Now</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    paddingBottom: 40,
    backgroundColor: "#fff",
  },
  centered: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: "#555",
  },
  heading: {
    fontSize: 26,
    fontWeight: "bold",
    color: "#4B00B5",
    marginBottom: 10,
  },
  subText: {
    fontSize: 16,
    color: "#555",
    marginBottom: 10,
  },
  image: {
    width: "100%",
    height: 180,
    borderRadius: 8,
    backgroundColor: "#f0f0f0",
    resizeMode: "contain",
    marginBottom: 10,
  },
  rating: {
    fontSize: 14,
    color: "#333",
    marginBottom: 12,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: "bold",
    color: "#4B00B5",
    marginTop: 20,
    marginBottom: 6,
  },
  itemContainer: {
    marginBottom: 12,
    paddingBottom: 8,
    borderBottomColor: "#e0e0e0",
    borderBottomWidth: 1,
  },
  itemName: {
    fontSize: 16,
    fontWeight: "600",
    color: "#333",
  },
  itemDetail: {
    fontSize: 13,
    color: "#777",
    marginTop: 2,
  },
  itemPrice: {
    fontSize: 14,
    color: "#FF4774",
    marginTop: 2,
  },
  reviewContainer: {
    marginBottom: 10,
    backgroundColor: "#f8f8f8",
    padding: 8,
    borderRadius: 6,
  },
  reviewName: {
    fontWeight: "bold",
    marginBottom: 2,
  },
  placeholder: {
    color: "#999",
    marginBottom: 10,
  },
  bookButton: {
    marginTop: 20,
    backgroundColor: "#FF4774",
    padding: 14,
    borderRadius: 8,
    alignItems: "center",
  },
  disabledButton: {
    marginTop: 20,
    backgroundColor: "#ccc",
    padding: 14,
    borderRadius: 8,
    alignItems: "center",
  },
  bookText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "600",
  },
  backButton: {
    marginBottom: 10,
  },
  backButtonText: {
    color: "#4B00B5",
    fontWeight: "bold",
    fontSize: 16,
  },
});
