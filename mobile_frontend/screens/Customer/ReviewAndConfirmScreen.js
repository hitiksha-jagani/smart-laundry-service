import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  ActivityIndicator,
} from "react-native";
import axios from "../../utils/axiosInstance";
import AsyncStorage from "@react-native-async-storage/async-storage";
import PrimaryButton from "../../components/PrimaryButton";

export default function ReviewAndConfirm({
  dummyOrderId,
  onPrev,
  onOrderCreated,
}) {
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const token = await AsyncStorage.getItem("token");
        const res = await axios.get("/orders/summary-from-redis", {
          params: { dummyOrderId },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setSummary(res.data);
      } catch (err) {
        console.error("Fetch summary error:", err);
        setError("Failed to load order summary. Please try again.");
      }
    };

    if (dummyOrderId) fetchSummary();
    else setError("Invalid or missing order reference.");
  }, [dummyOrderId]);

  const handleConfirm = async () => {
    setError("");
    setLoading(true);

    try {
      const token = await AsyncStorage.getItem("token");
      const res = await axios.post(`/orders/place/${dummyOrderId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });

      onOrderCreated(res.data);
    } catch (err) {
      console.error("Place order error:", err);
      setError(err.response?.data?.message || "Failed to place order.");
    } finally {
      setLoading(false);
    }
  };

  if (!summary && !error) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#4B00B5" />
        <Text style={styles.loadingText}>Loading summary...</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.heading}>Step 4: Review & Confirm</Text>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Pickup Info</Text>
        <Text>Date: {summary?.pickupDate || "N/A"}</Text>
        <Text>Time: {summary?.pickupTime || "N/A"}</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Items</Text>
        {summary?.bookingItems?.length > 0 ? (
          summary.bookingItems.map((item, i) => (
            <Text key={i}>{item.itemName} × {item.quantity}</Text>
          ))
        ) : (
          <Text>No items found.</Text>
        )}
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Contact Info</Text>
        <Text>Name: {summary?.contactName || "N/A"}</Text>
        <Text>Phone: {summary?.contactPhone || "N/A"}</Text>
        <Text>Address: {summary?.contactAddress || "N/A"}</Text>
      </View>

      {summary?.schedulePlan && (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Schedule Plan</Text>
          <Text>Plan: {summary.schedulePlan.plan}</Text>
          <Text>Pay Each Delivery: {summary.schedulePlan.payEachDelivery ? "Yes" : "No"}</Text>
          <Text>Pay Last Delivery: {summary.schedulePlan.payLastDelivery ? "Yes" : "No"}</Text>
        </View>
      )}

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <View style={styles.buttonRow}>
        <PrimaryButton onPress={onPrev}>Previous</PrimaryButton>
        <PrimaryButton onPress={handleConfirm} disabled={loading}>
          {loading ? <ActivityIndicator color="#fff" /> : "Confirm Order"}
        </PrimaryButton>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { padding: 20, backgroundColor: "#fff" },
  centered: { flex: 1, justifyContent: "center", alignItems: "center", padding: 16 },
  loadingText: { marginTop: 10, fontSize: 16, color: "#666" },
  heading: { fontSize: 22, fontWeight: "bold", marginBottom: 16 },
  card: { backgroundColor: "#f9f9f9", padding: 14, borderRadius: 8, marginBottom: 12 },
  cardTitle: { fontWeight: "600", fontSize: 16, marginBottom: 6 },
  error: { color: "red", fontWeight: "500", marginVertical: 10 },
  buttonRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 20,
  },
});
