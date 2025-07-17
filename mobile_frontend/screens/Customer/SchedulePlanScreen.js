import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  Alert,
  ActivityIndicator,
  TouchableOpacity,
} from "react-native";
import axios from "../../utils/axiosInstance";
import { Picker } from "@react-native-picker/picker";
import PrimaryButton from "../../components/PrimaryButton";

export default function SchedulePlanScreen({
  dummyOrderId,
  onNext,
  onPrev,
  providerId,
  initialOrderData,
  setInitialOrderData,
}) {
  const [availablePlans, setAvailablePlans] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { schedulePlan, paymentOption } = initialOrderData;

  useEffect(() => {
    const fetchAvailablePlans = async () => {
      try {
        const res = await axios.get(`/schedule-plans/${providerId}`);
        setAvailablePlans(res.data || []);
      } catch (err) {
        console.error("Error fetching schedule plans:", err);
        setError("Could not load schedule plan options.");
      }
    };

    if (providerId) fetchAvailablePlans();
  }, [providerId]);

  const setField = (field, value) => {
    setInitialOrderData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async () => {
    if (!schedulePlan) {
      setError("Please select a schedule plan.");
      return;
    }

    const payEachDelivery = paymentOption === "EACH";
    const payLastDelivery = paymentOption === "LAST";

    if (payEachDelivery === payLastDelivery) {
      setError("Please select exactly one payment option.");
      return;
    }

    try {
      setLoading(true);
      const res = await axios.post(`/orders/schedule-plan/${dummyOrderId}`, {
        schedulePlan,
        payEachDelivery,
        payLastDelivery,
      });

      if (res.status === 200){
        setTimeout(() => {
          onNext();
        }, 100);
      }
    } catch (err) {
      console.error("Error saving schedule plan:", err);
      setError("Failed to save schedule plan. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>Step 2: Select Schedule Plan</Text>

      <Text style={styles.label}>Schedule Plan:</Text>
      <View style={styles.pickerWrapper}>
        <Picker
          selectedValue={schedulePlan}
          onValueChange={(value) => setField("schedulePlan", value)}
        >
          <Picker.Item label="-- Select Plan --" value="" />
          {availablePlans.map((plan) => (
            <Picker.Item
              key={plan}
              label={plan.charAt(0) + plan.slice(1).toLowerCase().replace("_", " ")}
              value={plan}
            />
          ))}
        </Picker>
      </View>

      <Text style={styles.label}>Payment Option:</Text>
      <TouchableOpacity
        style={styles.radioOption}
        onPress={() => setField("paymentOption", "EACH")}
      >
        <Text style={styles.radio}>
          {paymentOption === "EACH" ? "◉" : "○"} Pay After Each Delivery
        </Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.radioOption}
        onPress={() => setField("paymentOption", "LAST")}
      >
        <Text style={styles.radio}>
          {paymentOption === "LAST" ? "◉" : "○"} Pay After Last Delivery
        </Text>
      </TouchableOpacity>

      {error !== "" && <Text style={styles.error}>{error}</Text>}

      <View style={styles.buttonRow}>
        <TouchableOpacity onPress={onPrev} style={styles.prevButton}>
          <Text style={styles.prevText}>Previous</Text>
        </TouchableOpacity>

        <PrimaryButton onPress={handleSubmit} disabled={loading}>
          {loading ? "Saving..." : "Next"}
        </PrimaryButton>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: "#fff",
    flex: 1,
  },
  heading: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 16,
  },
  label: {
    fontWeight: "600",
    marginBottom: 6,
    marginTop: 16,
  },
  pickerWrapper: {
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 6,
    overflow: "hidden",
  },
  radioOption: {
    marginVertical: 6,
  },
  radio: {
    fontSize: 16,
    color: "#333",
  },
  error: {
    color: "red",
    fontWeight: "500",
    marginVertical: 10,
  },
  buttonRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 30,
    alignItems: "center",
  },
  prevButton: {
    backgroundColor: "#eee",
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 6,
  },
  prevText: {
    color: "#444",
    fontWeight: "600",
  },
});
