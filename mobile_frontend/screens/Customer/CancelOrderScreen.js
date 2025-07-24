import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator, StyleSheet } from 'react-native';
import axios from '../../utils/axiosInstance';
import { useNavigation, useRoute } from '@react-navigation/native';

export default function CancelOrderScreen() {
  const navigation = useNavigation();
  const route = useRoute();
  const { orderId } = route.params;

  const [message, setMessage] = useState('Cancelling your order...');
  const [error, setError] = useState(null);

  useEffect(() => {
    const cancelOrder = async () => {
      try {
        await axios.post(`/orders/cancel/${orderId}`);
        setMessage('✅ Order cancelled successfully. Redirecting to dashboard...');
        setTimeout(() => navigation.navigate('CustomerDashboard'), 5000);
      } catch (err) {
        const errorMsg =
          err.response?.data?.message || '❌ Failed to cancel order. Please try again later.';
        setError(errorMsg);
        setTimeout(() => navigation.navigate('CustomerDashboard'), 5000);
      }
    };

    cancelOrder();
  }, [orderId]);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Cancel Order</Text>
      <View style={styles.messageContainer}>
        {error ? (
          <Text style={styles.errorText}>{error}</Text>
        ) : (
          <>
            <Text style={styles.successText}>{message}</Text>
            <ActivityIndicator size="large" color="#FF4774" style={{ marginTop: 16 }} />
          </>
        )}
        <Text style={styles.noteText}>You’ll be redirected in 5 seconds...</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FF4774',
    marginBottom: 20,
  },
  messageContainer: {
    alignItems: 'center',
  },
  successText: {
    fontSize: 18,
    color: 'green',
    textAlign: 'center',
  },
  errorText: {
    fontSize: 18,
    color: 'red',
    textAlign: 'center',
  },
  noteText: {
    marginTop: 12,
    fontSize: 14,
    color: 'gray',
    textAlign: 'center',
  },
});
