import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  StyleSheet,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import axios from "../../utils/axiosInstance";
import { useAuth } from "../../context/AuthContext";

export default function PendingOrdersScreen() {
  const [pendingOrders, setPendingOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await axios.get("/provider/orders/pending", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPendingOrders(res.data);
      } catch (err) {
        console.error("Failed to fetch orders:", err);
        Alert.alert("Error", "Could not load orders.");
      } finally {
        setLoading(false);
      }
    };

    if (token) fetchOrders();
  }, [token]);

  const handleAccept = async (orderId) => {
    try {
      await axios.post(`/provider/orders/accept/${orderId}`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      Alert.alert("Success", "Order accepted!");
      refreshOrders();
    } catch (err) {
      console.error(err);
      Alert.alert("Error", "Failed to accept order.");
    }
  };

  const handleReject = async (orderId) => {
    try {
      await axios.post(`/provider/orders/${orderId}/reject`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      Alert.alert("Rejected", "Order rejected.");
      refreshOrders();
    } catch (err) {
      console.error(err);
      Alert.alert("Error", "Failed to reject order.");
    }
  };

  const refreshOrders = async () => {
    try {
      const res = await axios.get("/provider/orders/pending", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPendingOrders(res.data);
    } catch (err) {
      console.error("Refresh failed:", err);
    }
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#fb923c" />
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text style={styles.backBtn}>← Back to Dashboard</Text>
      </TouchableOpacity>

      <Text style={styles.heading}>Pending Orders</Text>

      {pendingOrders.length === 0 ? (
        <Text style={styles.noData}>No pending orders.</Text>
      ) : (
        pendingOrders.map((order) => (
          <View key={order.orderId} style={styles.card}>
            <View style={styles.cardHeader}>
              <View>
                <Text style={styles.orderId}>Order ID: {order.orderId}</Text>
                <Text style={styles.pickup}>
                  Pickup Date: <Text style={styles.bold}>{order.pickupDate}</Text> | Time:{" "}
                  <Text style={styles.bold}>{order.pickupTime}</Text>
                </Text>
              </View>
              <Text style={styles.status}>PENDING</Text>
            </View>

            <View style={styles.table}>
              {order.items.map((item, idx) => (
                <View key={idx} style={styles.row}>
                  <Text style={styles.cell}>{item.itemName}</Text>
                  <Text style={styles.cell}>{item.service}</Text>
                  <Text style={styles.cell}>{item.subService}</Text>
                  <Text style={styles.cell}>{item.quantity}</Text>
                </View>
              ))}
            </View>

            <View style={styles.actionRow}>
              <TouchableOpacity
                onPress={() => handleAccept(order.orderId)}
                style={[styles.actionBtn, styles.accept]}
              >
                <Text style={styles.actionText}>Accept</Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => handleReject(order.orderId)}
                style={[styles.actionBtn, styles.reject]}
              >
                <Text style={styles.actionText}>Reject</Text>
              </TouchableOpacity>
            </View>
          </View>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFF7ED", // orange-50
    padding: 16,
  },
  centered: {
    flex: 1,
    justifyContent: "center",
  },
  backBtn: {
    color: "#EA580C",
    marginBottom: 12,
    fontSize: 16,
  },
  heading: {
    fontSize: 24,
    fontWeight: "bold",
    color: "#C2410C", // orange-700
    textAlign: "center",
    marginBottom: 20,
  },
  noData: {
    textAlign: "center",
    color: "#6B7280", // gray-500
    marginTop: 30,
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
    borderLeftWidth: 4,
    borderLeftColor: "#F97316", // orange-500
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 12,
  },
  orderId: {
    fontSize: 16,
    fontWeight: "bold",
    color: "#9A3412", // orange-800
  },
  pickup: {
    fontSize: 13,
    color: "#4B5563",
  },
  bold: {
    fontWeight: "bold",
  },
  status: {
    backgroundColor: "#FFEDD5",
    color: "#C2410C",
    fontSize: 12,
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 20,
    overflow: "hidden",
    fontWeight: "600",
    alignSelf: "center",
  },
  table: {
    marginTop: 10,
    borderTopWidth: 1,
    borderTopColor: "#FCD34D", // orange-100
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingVertical: 6,
    borderBottomWidth: 1,
    borderBottomColor: "#FFE9CA",
  },
  cell: {
    flex: 1,
    fontSize: 13,
    color: "#374151",
    paddingHorizontal: 4,
  },
  actionRow: {
    flexDirection: "row",
    justifyContent: "flex-end",
    marginTop: 16,
    gap: 10,
  },
  actionBtn: {
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 6,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 1,
  },
  accept: {
    backgroundColor: "#22C55E",
  },
  reject: {
    backgroundColor: "#EF4444",
  },
  actionText: {
    color: "white",
    fontWeight: "600",
  },
});
