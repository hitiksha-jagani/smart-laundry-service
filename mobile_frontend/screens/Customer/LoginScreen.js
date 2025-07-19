import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { Eye, EyeOff } from "lucide-react-native";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../../context/AuthContext";
import { jwtDecode } from "jwt-decode";
import { BASE_URL } from "../../config";
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Alert } from 'react-native';

export default function LoginScreen() {
  const navigation = useNavigation();
  const { login } = useAuth();

  const [formData, setFormData] = useState({ username: "", password: "" });
  const [otp, setOtp] = useState("");
  const [step, setStep] = useState(1);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (key, value) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
    setError("");
  };

  const handleLogin = async () => {
    setError("");
    setSuccessMessage("");

    try {
      const res = await fetch(`${BASE_URL}/login`, {
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
      const url = `${BASE_URL}/verify-otp?username=${formData.username}&otp=${otp}`;
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

      login(data.jwtToken, data.role, userId);

      switch (data.role) {
        case "CUSTOMER":
          navigation.navigate("CustomerDashboard");
          break;

        case "ADMIN":
          navigation.navigate("RevenueSummary");
          break;

        case "SERVICE_PROVIDER":
          try {
            const providerRes = await fetch(
              `${BASE_URL}/provider/orders/from-user/${userId}`,
              {
                headers: {
                  Authorization: `Bearer ${data.jwtToken}`,
                },
              }
            );

            if (providerRes.ok) {
              const providerId = await providerRes.text();
              login(data.jwtToken, data.role, userId, providerId);
              navigation.navigate("ProviderDrawer", {
              screen: "ProviderDashboard",
            });

            } else if (providerRes.status === 404) {
              navigation.navigate("ProviderCompleteProfile");
            } else {
              throw new Error("Provider check failed");
            }
          } catch (err) {
            console.error("Service provider error:", err);
            setError("Unable to retrieve service provider info.");
          }
          break;

        //   else if (data.role === 'DELIVERY_AGENT') {
        //   try {
        //     // 1. Save token and userId in AsyncStorage
        //     await AsyncStorage.setItem('token', data.jwtToken);
        //     await AsyncStorage.setItem('role', data.role);
        //     await AsyncStorage.setItem('userId', userId);

        //     // 2. Check profile existence
        //     const response = await fetch(`http://localhost:8080/profile/exist/${userId}`);

        //     if (!response.ok) {
        //       throw new Error('Failed to check agent existence');
        //     }

        //     const exists = await response.json(); // Boolean

        //     // 3. Navigate based on existence
        //     if (exists) {
        //       navigation.navigate('DeliveryAgentRoutes', {
        //         screen: 'DeliveryPage', 
        //       });
        //     } else {
        //       navigation.navigate('DeliveryAgentRoutes', {
        //         screen: 'DeliveryPage', 
        //       });
        //     }

        //   } catch (err) {
        //     console.error('Error checking delivery agent existence:', err);
        //     Alert.alert('Error', 'Unable to verify agent profile. Try again.');
        //   }
        // } 

        case "DELIVERY_AGENT":
          try {
            const agentRes = await fetch(`${BASE_URL}/profile/exist/${userId}`);
            if (!agentRes.ok) throw new Error("Agent check failed");

            const exists = await agentRes.json();
            if (exists) {
              navigation.navigate("DeliveryPage");
            } else {
              navigation.navigate("AgentCompleteProfile");
            }
          } catch (err) {
            console.error("Delivery agent error:", err);
            setError("Unable to verify agent profile.");
          }
          break;

        default:
          setError("Unknown role.");
      }
    } catch (err) {
      console.error(err);
      setError("OTP verification error.");
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      style={styles.container}
    >
      <Text style={styles.title}>Login to Your Account</Text>

      {step === 1 ? (
        <>
          <TextInput
            style={styles.input}
            placeholder="Phone or Email"
            value={formData.username}
            onChangeText={(text) => handleChange("username", text)}
          />
          <View style={styles.passwordContainer}>
            <TextInput
              style={styles.passwordInput}
              placeholder="Password"
              secureTextEntry={!showPassword}
              value={formData.password}
              onChangeText={(text) => handleChange("password", text)}
            />
            <TouchableOpacity onPress={() => setShowPassword((prev) => !prev)}>
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </TouchableOpacity>
          </View>

          {error ? <Text style={styles.error}>{error}</Text> : null}
          {successMessage ? <Text style={styles.success}>{successMessage}</Text> : null}

          <TouchableOpacity onPress={handleLogin} style={styles.button}>
            <Text style={styles.buttonText}>Send OTP</Text>
          </TouchableOpacity>
        </>
      ) : (
        <>
          <TextInput
            style={styles.input}
            placeholder="Enter 6-digit OTP"
            value={otp}
            onChangeText={setOtp}
            keyboardType="numeric"
          />
          {error ? <Text style={styles.error}>{error}</Text> : null}
          {successMessage ? <Text style={styles.success}>{successMessage}</Text> : null}

          <TouchableOpacity onPress={handleOtpVerify} style={styles.button}>
            <Text style={styles.buttonText}>Verify OTP & Login</Text>
          </TouchableOpacity>
        </>
      )}
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: "center",
    backgroundColor: "#fff",
  },
  title: {
    fontSize: 24,
    marginBottom: 24,
    textAlign: "center",
    fontWeight: "bold",
    color: "#333",
  },
  input: {
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 12,
    marginBottom: 12,
  },
  passwordContainer: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginBottom: 12,
    justifyContent: "space-between",
  },
  passwordInput: {
    flex: 1,
    paddingRight: 10,
  },
  button: {
    backgroundColor: "#A566FF",
    paddingVertical: 14,
    borderRadius: 8,
    marginTop: 12,
  },
  buttonText: {
    color: "white",
    textAlign: "center",
    fontWeight: "600",
    fontSize: 16,
  },
  error: {
    color: "red",
    marginBottom: 8,
    textAlign: "center",
  },
  success: {
    color: "green",
    marginBottom: 8,
    textAlign: "center",
  },
});





