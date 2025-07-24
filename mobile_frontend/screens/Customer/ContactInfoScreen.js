import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
} from "react-native";
import axios from "../../utils/axiosInstance";
import InputField from "../../components/InputField";
import PrimaryButton from "../../components/PrimaryButton";

export default function ContactInfo({
  dummyOrderId,
  initialOrderData,
  setInitialOrderData,
  onNext,
  onPrev,
}) {
  const [contactName, setContactName] = useState(initialOrderData?.contactName || "");
  const [contactPhone, setContactPhone] = useState(initialOrderData?.contactPhone || "");
  const [contactAddress, setContactAddress] = useState(initialOrderData?.contactAddress || "");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setError("");

    if (!contactName || !contactPhone || !contactAddress) {
      return setError("All fields are required.");
    }

    if (!/^\d{10}$/.test(contactPhone)) {
      return setError("Phone number must be exactly 10 digits.");
    }

    try {
      setLoading(true);
      await axios.post(`/orders/contact/${dummyOrderId}`, {
        contactName,
        contactPhone,
        contactAddress,
      });

      setInitialOrderData((prev) => ({
        ...prev,
        contactName,
        contactPhone,
        contactAddress,
      }));

      onNext();
    } catch (err) {
      console.error("Contact info error:", err);
      setError(err?.response?.data?.message || "Failed to save contact info.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <View>
      <Text style={styles.heading}>Step 3: Contact Information</Text>

      <InputField
        label="Contact Name"
        value={contactName}
        onChangeText={setContactName}
        placeholder="Enter your name"
      />

      <InputField
        label="Phone Number"
        value={contactPhone}
        onChangeText={setContactPhone}
        placeholder="10-digit number"
        keyboardType="phone-pad"
      />

      <InputField
        label="Address"
        value={contactAddress}
        onChangeText={setContactAddress}
        placeholder="Enter full address"
        multiline
      />

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <View style={styles.buttonRow}>
        <TouchableOpacity onPress={onPrev} style={styles.secondaryButton}>
          <Text style={styles.secondaryText}>Previous</Text>
        </TouchableOpacity>

        <PrimaryButton onPress={handleSubmit} disabled={loading}>
          {loading ? <ActivityIndicator color="#fff" /> : "Next"}
        </PrimaryButton>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  heading: {
    fontSize: 20,
    fontWeight: "600",
    marginBottom: 20,
  },
  error: {
    color: "red",
    fontWeight: "500",
    marginVertical: 10,
  },
  buttonRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 20,
  },
  secondaryButton: {
    backgroundColor: "#e0e0e0",
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 8,
    alignItems: "center",
    minWidth: 100,
  },
  secondaryText: {
    color: "#333",
    fontWeight: "500",
  },
});
