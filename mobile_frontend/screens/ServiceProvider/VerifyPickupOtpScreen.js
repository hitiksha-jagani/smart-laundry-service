import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
} from "react-native";
import { useRoute, useNavigation } from "@react-navigation/native";
import axios from "../../utils/axiosInstance"; // adjust the path based on your structure

export default function VerifyPickupOtpScreen() {
  const [otp, setOtp] = useState("");
  const [message, setMessage] = useState("");
  const route = useRoute();
  const navigation = useNavigation();
  const { orderId } = route.params;

  const handleVerify = async () => {
    try {
      await axios.post("/emailotp/verify-pickup", { orderId, otp });
      setMessage("Pickup OTP verified! Redirecting...");
      setTimeout(() => navigation.navigate("OtpVerificationOrders"), 1500);
    } catch (err) {
      const errorMsg =
        err.response?.data?.message || "Verification failed. Try again.";
      setMessage(errorMsg);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>Verify Pickup OTP</Text>
      <TextInput
        style={styles.input}
        placeholder="Enter OTP"
        value={otp}
        onChangeText={setOtp}
        keyboardType="numeric"
      />
      <TouchableOpacity onPress={handleVerify} style={styles.button}>
        <Text style={styles.buttonText}>Verify</Text>
      </TouchableOpacity>
      {message ? <Text style={styles.message}>{message}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: "#FFF7ED", // Tailwind: orange-50
    justifyContent: "center",
  },
  heading: {
    fontSize: 22,
    fontWeight: "bold",
    marginBottom: 16,
    color: "#1F2937", // Tailwind: gray-800
    textAlign: "center",
  },
  input: {
    borderWidth: 1,
    borderColor: "#D1D5DB", // Tailwind: gray-300
    padding: 12,
    borderRadius: 8,
    fontSize: 16,
    backgroundColor: "#fff",
    marginBottom: 16,
  },
  button: {
    backgroundColor: "#2563EB", // Tailwind: blue-600
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: "center",
  },
  buttonText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "bold",
  },
  message: {
    marginTop: 16,
    textAlign: "center",
    color: "#DC2626", // Tailwind: red-600
    fontSize: 14,
  },
});
