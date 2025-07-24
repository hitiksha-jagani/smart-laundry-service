// OrderBookingWizardScreen.jsx
import React, { useEffect, useState } from "react";
import { View, Text, ScrollView, StyleSheet, Alert } from "react-native";
import { useNavigation, useRoute } from "@react-navigation/native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { jwtDecode } from "jwt-decode";

import InitialOrder from "./InitialOrderScreen";
import SchedulePlan from "./SchedulePlanScreen";
import ContactInfo from "./ContactInfoScreen";
import ReviewAndConfirm from "./ReviewAndConfirmScreen";

export default function OrderBookingWizardScreen() {
  const [step, setStep] = useState(1);
  const [dummyOrderId, setDummyOrderId] = useState("");
  const [initialOrderData, setInitialOrderData] = useState({
    items: [{ itemId: "", quantity: 1 }],
    pickupDate: "",
    pickupTime: "",
    goWithSchedulePlan: false,
    serviceProviderId: "",
    schedulePlan: "",
    paymentOption: "",
    contactName: "",
    contactPhone: "",
    contactAddress: "",
  });

  const navigation = useNavigation();
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const getUserId = async () => {
      try {
        const token = await AsyncStorage.getItem("token");
        if (token) {
          const decoded = jwtDecode(token);
          setUserId(decoded.userId);
        }
      } catch (e) {
        console.error("Failed to decode token", e);
        Alert.alert("Error", "Failed to load user info.");
      }
    };
    getUserId();
  }, []);

  const nextStep = (useSchedulePlan = null) => {
    if (step === 1 && useSchedulePlan !== null) {
      setInitialOrderData((prev) => ({ ...prev, goWithSchedulePlan: useSchedulePlan }));
      setStep(useSchedulePlan ? 2 : 3);
    } else {
      setStep((prev) => prev + 1);
    }
  };

  const prevStep = () => setStep((prev) => prev - 1);

  const steps = ["Initial Order", "Schedule Plan", "Contact Info", "Review & Confirm"];

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.stepTracker}>
        {steps.map((label, index) => (
          <View key={index} style={styles.stepItem}>
            <View
              style={[styles.stepCircle,
                step === index + 1
                  ? styles.stepCurrent
                  : step > index + 1
                  ? styles.stepDone
                  : styles.stepPending]}
            >
              <Text style={styles.stepNumber}>{index + 1}</Text>
            </View>
            <Text style={styles.stepLabel}>{label}</Text>
          </View>
        ))}
      </View>

      {step === 1 && (
        <InitialOrder
          onNext={nextStep}
          setDummyOrderId={setDummyOrderId}
          userId={userId}
          initialOrderData={initialOrderData}
          setInitialOrderData={setInitialOrderData}
        />
      )}
      {step === 2 && (
        <SchedulePlan
          dummyOrderId={dummyOrderId}
          userId={userId}
          providerId={initialOrderData.serviceProviderId}
          onNext={nextStep}
          onPrev={prevStep}
          initialOrderData={initialOrderData}
          setInitialOrderData={setInitialOrderData}
        />
      )}
      {step === 3 && (
        <ContactInfo
          dummyOrderId={dummyOrderId}
          userId={userId}
          initialOrderData={initialOrderData}
          setInitialOrderData={setInitialOrderData}
          onNext={nextStep}
          onPrev={() => setStep(initialOrderData.goWithSchedulePlan ? 2 : 1)}
        />
      )}
      {step === 4 && (
        <ReviewAndConfirm
          dummyOrderId={dummyOrderId}
          userId={userId}
          onPrev={() => setStep(3)}
          onOrderCreated={(order) => {
            console.log("Order placed:", order);
            navigation.navigate("OrderSuccess", { order });
          }}
        />
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { padding: 16, paddingBottom: 40, backgroundColor: "#fff" },
  stepTracker: { flexDirection: "row", justifyContent: "space-around", marginBottom: 24 },
  stepItem: { alignItems: "center", flex: 1 },
  stepCircle: { width: 32, height: 32, borderRadius: 16, justifyContent: "center", alignItems: "center", marginBottom: 4 },
  stepNumber: { color: "#fff", fontWeight: "bold" },
  stepCurrent: { backgroundColor: "#4B00B5" },
  stepDone: { backgroundColor: "#22c55e" },
  stepPending: { backgroundColor: "#d4d4d4" },
  stepLabel: { fontSize: 10, textAlign: "center", color: "#555" },
});
