import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, ActivityIndicator, StyleSheet } from 'react-native';
import { useRoute, useNavigation } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import axios from '../../utils/axiosInstance';

const VerifyDeliveryOtpScreen = () => {
  const route = useRoute();
  const navigation = useNavigation();
  const { orderId } = route.params || {};
  const { userId } = useAuth();

  const [otp, setOtp] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleVerify = async () => {
    setMessage('');
    setError('');

    if (!otp.trim()) {
      setError('Please enter the OTP.');
      return;
    }

    if (!userId) {
      setError('User not logged in. Please re-login.');
      return;
    }

    try {
      setLoading(true);
      await axios.post(
        `/emailotp/verify-delivery`,
        {}, // Empty body
        {
          params: {
            orderId,
            otp,
            verifierId: userId,
          },
        }
      );

      setMessage('OTP verified successfully. Redirecting...');
      setTimeout(() => {
        navigation.replace('OtpVerificationOrders');
      }, 1500);
    } catch (err) {
      console.error('OTP verification error:', err);
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        'Verification failed. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Verify Delivery OTP</Text>

      <TextInput
        style={styles.input}
        placeholder="Enter OTP"
        value={otp}
        onChangeText={setOtp}
        keyboardType="numeric"
      />

      <TouchableOpacity onPress={handleVerify} style={styles.button} disabled={loading}>
        {loading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.buttonText}>Verify OTP</Text>
        )}
      </TouchableOpacity>

      {message ? <Text style={styles.success}>{message}</Text> : null}
      {error ? <Text style={styles.error}>{error}</Text> : null}
    </View>
  );
};

export default VerifyDeliveryOtpScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 16,
    textAlign: 'center',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 6,
    padding: 12,
    marginBottom: 16,
    fontSize: 16,
  },
  button: {
    backgroundColor: '#059669',
    paddingVertical: 12,
    borderRadius: 6,
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  success: {
    color: 'green',
    marginTop: 16,
    textAlign: 'center',
  },
  error: {
    color: 'red',
    marginTop: 16,
    textAlign: 'center',
  },
});
