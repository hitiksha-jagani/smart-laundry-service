import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Alert } from 'react-native';
import { jwtDecode } from 'jwt-decode';
import { Feather } from '@expo/vector-icons';
import { BASE_URL } from '../../config'; // ✅ Your backend URL config

export default function LoginScreen({ login }) {
  const navigation = useNavigation();

  const [formData, setFormData] = useState({ username: '', password: '' });
  const [otp, setOtp] = useState('');
  const [step, setStep] = useState(1);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
    setError('');
  };

  const handleLogin = async () => {
    try {
      const res = await fetch(`${BASE_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          username: formData.username.toLowerCase(),
        }),
      });

      const data = await res.text();

      if (res.ok) {
        setSuccessMessage(data);
        setTimeout(() => setSuccessMessage(''), 5000);
        setStep(2);
      } else {
        setError(data);
      }
    } catch (err) {
      setError('Something went wrong. Try again.');
    }
  };

  const handleOtpVerify = async () => {
    try {
      const normalizedUsername = formData.username.includes('@')
        ? formData.username.toLowerCase()
        : "+91" + formData.username.replace(/\D/g, '').slice(-10);

      const url = `${BASE_URL}/verify-otp?username=${encodeURIComponent(normalizedUsername)}&otp=${otp}`;

      const res = await fetch(url, { method: 'POST' });
      const rawText = await res.text();

      if (!res.ok) {
        console.error("OTP verification failed:", rawText);
        setError(rawText || 'OTP verification failed.');
        return;
      }

      let data;
      try {
        data = JSON.parse(rawText);
      } catch (e) {
        console.error("JSON parse error:", rawText);
        setError('Unexpected server response.');
        return;
      }

      if (!data.jwtToken) {
        console.error("No token in response:", data);
        setError("Invalid response from server. No token.");
        return;
      }

      let decoded;
      try {
        decoded = jwtDecode(data.jwtToken);
      } catch (e) {
        console.error("JWT Decode failed:", e);
        setError("Failed to decode token.");
        return;
      }

      const userId = decoded.id;
      if (!userId) {
        setError('User ID missing from token.');
        return;
      }

      if (data.role === 'SERVICE_PROVIDER') {
        try {
          const providerRes = await fetch(
            `${BASE_URL}/provider/orders/from-user/${userId}`,
            {
              headers: {
                Authorization: `Bearer ${data.jwtToken}`,
              },
            }
          );
          const providerId = await providerRes.text();
          login(data.jwtToken, data.role, userId, providerId);
          navigation.navigate('ProviderDashboard');
        } catch (err) {
          setError('Unable to retrieve service provider info.');
        }
      } else if (data.role === 'DELIVERY_AGENT') {
          try {
            // 1. Save token and userId in AsyncStorage
            await AsyncStorage.setItem('token', data.jwtToken);
            await AsyncStorage.setItem('role', data.role);
            await AsyncStorage.setItem('userId', userId);

            // 2. Check profile existence
            const response = await fetch(`http://localhost:8080/profile/exist/${userId}`);

            if (!response.ok) {
              throw new Error('Failed to check agent existence');
            }

            const exists = await response.json(); // Boolean

            // 3. Navigate based on existence
            if (exists) {
              navigation.navigate('DeliveryAgentRoutes', {
                screen: 'DeliveryPage', 
              });
            } else {
              navigation.navigate('DeliveryAgentRoutes', {
                screen: 'DeliveryPage', 
              });
            }

          } catch (err) {
            console.error('Error checking delivery agent existence:', err);
            Alert.alert('Error', 'Unable to verify agent profile. Try again.');
          }
        } 
      else {
        login(data.jwtToken, data.role, userId);
        switch (data.role) {
          case 'CUSTOMER':
            navigation.navigate('CustomerDashboard');
            break;
          case 'DELIVERY_AGENT':
            navigation.navigate('DeliverySummary');
            break;
          case 'ADMIN':
            navigation.navigate('RevenueSummary');
            break;
          default:
            setError('Unknown user role');
        }
      }
    } catch (err) {
      setError('Error verifying OTP.');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Login to Your Account</Text>

      {step === 1 ? (
        <>
          <TextInput
            style={styles.input}
            placeholder="Enter phone or email"
            value={formData.username}
            onChangeText={(text) => handleChange('username', text)}
            autoCapitalize="none"
          />

          <View style={styles.passwordContainer}>
            <TextInput
              style={styles.inputPassword}
              placeholder="Enter password"
              secureTextEntry={!showPassword}
              value={formData.password}
              onChangeText={(text) => handleChange('password', text)}
            />
            <TouchableOpacity
              onPress={() => setShowPassword(!showPassword)}
              style={styles.eyeIcon}
            >
              <Feather
                name={showPassword ? 'eye-off' : 'eye'}
                size={20}
                color="#666"
              />
            </TouchableOpacity>
          </View>

          {error ? <Text style={styles.error}>{error}</Text> : null}
          {successMessage ? <Text style={styles.success}>{successMessage}</Text> : null}

          <TouchableOpacity style={styles.button} onPress={handleLogin}>
            <Text style={styles.buttonText}>Send OTP</Text>
          </TouchableOpacity>

          <Text style={styles.linkText}>
            Don’t have an account?{' '}
            <Text
              style={styles.link}
              onPress={() => navigation.navigate('Register')}
            >
              Register here
            </Text>
          </Text>
        </>
      ) : (
        <>
          <TextInput
            style={styles.input}
            placeholder="Enter 6-digit OTP"
            value={otp}
            onChangeText={(text) => setOtp(text)}
            keyboardType="number-pad"
          />

          {error ? <Text style={styles.error}>{error}</Text> : null}
          {successMessage ? <Text style={styles.success}>{successMessage}</Text> : null}

          <TouchableOpacity style={styles.button} onPress={handleOtpVerify}>
            <Text style={styles.buttonText}>Verify OTP & Login</Text>
          </TouchableOpacity>
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    paddingTop: 60,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 30,
    textAlign: 'center',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 8,
    marginBottom: 15,
  },
  passwordContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    marginBottom: 15,
  },
  inputPassword: {
    flex: 1,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  eyeIcon: {
    paddingHorizontal: 10,
  },
  button: {
    backgroundColor: '#A566FF',
    paddingVertical: 14,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 5,
  },
  buttonText: {
    color: 'white',
    fontWeight: '600',
  },
  linkText: {
    marginTop: 20,
    textAlign: 'center',
  },
  link: {
    color: '#A566FF',
    fontWeight: 'bold',
  },
  error: {
    color: 'red',
    textAlign: 'center',
  },
  success: {
    color: 'green',
    textAlign: 'center',
  },
});
