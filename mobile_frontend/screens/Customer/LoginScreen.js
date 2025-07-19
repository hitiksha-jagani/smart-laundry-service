import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, Alert } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useNavigation } from "@react-navigation/native";
import { jwtDecode } from "jwt-decode";
import { useAuth } from "../../context/AuthContext";

const LoginScreen = () => {
  const navigation = useNavigation();
  const { login } = useAuth();

  const [formData, setFormData] = useState({ username: "", password: "" });
  const [otp, setOtp] = useState("");
  const [step, setStep] = useState(1);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const handleChange = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
    setError("");
  };

  const handleLogin = async () => {
    setError("");
    setSuccessMessage("");

    try {
      const res = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      const data = await res.text();

      if (res.ok) {
        setSuccessMessage(data);
        setTimeout(() => setSuccessMessage(""), 5000);
        setStep(2);
      } else {
        setError(data);
      }
    } catch (err) {
      console.error(err);
      setError("Something went wrong. Try again.");
    }
  };

  const handleOtpVerify = async () => {
    setError("");
    setSuccessMessage("");

    try {
      const url = `http://localhost:8080/verify-otp?username=${formData.username}&otp=${otp}`;
      const res = await fetch(url, { method: "POST" });

      const rawText = await res.text();
      if (!res.ok) {
        setError(rawText || "OTP verification failed.");
        return;
      }

      let data;
      try {
        data = JSON.parse(rawText);
      } catch (e) {
        console.error("Invalid JSON:", rawText);
        setError("Unexpected response from server.");
        return;
      }

      const decoded = jwtDecode(data.jwtToken);
      const userId = decoded.id;

      if (!userId) {
        setError("User ID missing from token.");
        return;
      }

      await AsyncStorage.setItem("token", data.jwtToken);
      await AsyncStorage.setItem("userId", userId);

      if (data.role === "SERVICE_PROVIDER") {
        try {
          const providerRes = await fetch(
            `http://localhost:8080/provider/orders/from-user/${userId}`,
            {
              headers: { Authorization: `Bearer ${data.jwtToken}` },
            }
          );

          if (providerRes.ok) {
            const providerId = await providerRes.text();
            await AsyncStorage.setItem("providerId", providerId);
            login(data.jwtToken, data.role, userId, providerId);
            navigation.navigate("ProviderDashboard");
          } else if (providerRes.status === 404) {
            login(data.jwtToken, data.role, userId);
            navigation.navigate("CompleteProfile");
          } else {
            throw new Error("Failed to check service provider status.");
          }
        } catch (err) {
          console.error("Error checking service provider status:", err);
          setError("Unable to retrieve service provider info.");
        }
      } else if (data.role === "DELIVERY_AGENT") {
        login(data.jwtToken, data.role, userId);

        try {
          const agentRes = await fetch(`http://localhost:8080/profile/exist/${userId}`);

          if (!agentRes.ok) throw new Error("Failed to check agent existence.");

          const exists = await agentRes.json();

          if (exists) {
            navigation.navigate("DeliveriesSummary");
          } else {
            navigation.navigate("CompleteProfile");
          }
        } catch (err) {
          console.error("Error checking delivery agent existence:", err);
          setError("Unable to verify agent profile. Try again.");
        }
      } else {
        login(data.jwtToken, data.role, userId);
        switch (data.role) {
          case "CUSTOMER":
            navigation.navigate("CustomerDashboard");
            break;
          case "ADMIN":
            navigation.navigate("AdminRevenue");
            break;
          default:
            setError("Unknown user role");
        }
      }
    } catch (err) {
      console.error(err);
      setError("Error verifying OTP.");
    }
  };

  return (
    <View style={{ padding: 20 }}>
      {step === 1 ? (
        <>
          <Text>Phone or Email</Text>
          <TextInput
            placeholder="Enter phone or email"
            value={formData.username}
            onChangeText={(value) => handleChange("username", value)}
            style={{ borderWidth: 1, borderRadius: 5, padding: 10, marginBottom: 10 }}
          />

          <Text>Password</Text>
          <TextInput
            placeholder="Enter password"
            value={formData.password}
            secureTextEntry
            onChangeText={(value) => handleChange("password", value)}
            style={{ borderWidth: 1, borderRadius: 5, padding: 10, marginBottom: 10 }}
          />

          {error ? <Text style={{ color: "red" }}>{error}</Text> : null}
          {successMessage ? <Text style={{ color: "green" }}>{successMessage}</Text> : null}

          <TouchableOpacity onPress={handleLogin} style={{ backgroundColor: "#A566FF", padding: 15, borderRadius: 10 }}>
            <Text style={{ color: "white", textAlign: "center" }}>Send OTP</Text>
          </TouchableOpacity>
        </>
      ) : (
        <>
          <Text>Enter OTP</Text>
          <TextInput
            placeholder="Enter 6-digit OTP"
            value={otp}
            keyboardType="number-pad"
            onChangeText={setOtp}
            style={{ borderWidth: 1, borderRadius: 5, padding: 10, marginBottom: 10 }}
          />

          {error ? <Text style={{ color: "red" }}>{error}</Text> : null}
          {successMessage ? <Text style={{ color: "green" }}>{successMessage}</Text> : null}

          <TouchableOpacity onPress={handleOtpVerify} style={{ backgroundColor: "#A566FF", padding: 15, borderRadius: 10 }}>
            <Text style={{ color: "white", textAlign: "center" }}>Verify OTP & Login</Text>
          </TouchableOpacity>
        </>
      )}
    </View>
  );
};

export default LoginScreen;