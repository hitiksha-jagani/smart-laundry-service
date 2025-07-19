import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  ActivityIndicator,
  Alert,
  Button,
  StyleSheet,
} from "react-native";
import axios from "../../utils/axiosInstance";
import { useRoute, useNavigation } from "@react-navigation/native";

export default function OrderSummary() {
  const route = useRoute();
  const navigation = useNavigation();
  const { orderId } = route.params;

  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [paymentStarted, setPaymentStarted] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const res = await axios.get(`/orders/${orderId}/summary`);
        setSummary(res.data);
        console.log("Order Summary DTO from backend:", res.data);
      } catch (err) {
        console.error("Failed to fetch summary:", err);
        setError("Failed to load order summary.");
      } finally {
        setLoading(false);
      }
    };

    fetchSummary();
  }, [orderId]);

  const handlePayNow = () => {
    setPaymentStarted(true);
    navigation.navigate("PayPalPayment", {
      billId: summary.invoiceNumber,
      orderId: orderId,
    });
  };

  if (loading) {
    return <ActivityIndicator size="large" style={{ marginTop: 50 }} />;
  }

  if (error) {
    return <Text style={styles.errorText}>{error}</Text>;
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.heading}>Order Summary</Text>

      <Text><Text style={styles.label}>Order ID:</Text> {summary.orderId}</Text>
      <Text><Text style={styles.label}>Status:</Text> {summary.status}</Text>
      <Text><Text style={styles.label}>Service:</Text> {summary.serviceName}</Text>
      <Text><Text style={styles.label}>Sub-Service:</Text> {summary.subServiceName}</Text>

      <View style={styles.table}>
        {summary.items.map((item, i) => (
          <View key={i} style={styles.row}>
            <Text style={styles.cell}>{item.itemName}</Text>
            <Text style={styles.cell}>Qty: {item.quantity}</Text>
            <Text style={styles.cell}>₹{item.price.toFixed(2)}</Text>
            <Text style={styles.cell}>Total: ₹{item.finalPrice.toFixed(2)}</Text>
          </View>
        ))}
      </View>

      <View style={styles.totals}>
        <Text>Items Total: ₹{summary.itemsTotal.toFixed(2)}</Text>
        <Text>GST: ₹{summary.gstAmount.toFixed(2)}</Text>
        <Text>Delivery Charge: ₹{summary.deliveryCharge.toFixed(2)}</Text>
        {summary.promotionApplied && summary.discountAmount > 0 && (
          <Text>
            Discount: ₹{summary.discountAmount.toFixed(2)} ({summary.appliedPromoCode})
          </Text>
        )}
        <Text style={styles.finalAmount}>
          Final Amount: ₹{summary.finalAmount.toFixed(2)}
        </Text>
      </View>

      {summary.billStatus === "PAID" ? (
        <Text style={styles.paidText}>✅ Payment already completed.</Text>
      ) : (
        summary.invoiceNumber && summary.finalAmount > 0 && (
          paymentStarted ? (
            <Text style={styles.processingText}>Processing payment...</Text>
          ) : (
            <Button title="Pay with PayPal" onPress={handlePayNow} />
          )
        )
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
  },
  heading: {
    fontSize: 24,
    fontWeight: "bold",
    color: "purple",
    marginBottom: 16,
    textAlign: "center",
  },
  label: {
    fontWeight: "bold",
  },
  table: {
    marginVertical: 20,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: "#ccc",
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingVertical: 8,
  },
  cell: {
    flex: 1,
  },
  totals: {
    marginTop: 10,
    gap: 4,
  },
  finalAmount: {
    fontSize: 18,
    fontWeight: "bold",
    marginTop: 10,
  },
  paidText: {
    color: "green",
    marginTop: 20,
    textAlign: "center",
  },
  processingText: {
    color: "blue",
    marginTop: 20,
    textAlign: "center",
  },
  errorText: {
    color: "red",
    marginTop: 30,
    textAlign: "center",
  },
});
