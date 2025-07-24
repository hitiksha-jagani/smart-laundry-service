// screens/Customer/PaymentSuccessScreen.js

import React, { useEffect } from 'react';
import { View, Text, ActivityIndicator, Alert, StyleSheet } from 'react-native';
import axios from '../utils/axiosInstance';
import { useNavigation, useRoute } from '@react-navigation/native';

const PaymentSuccessScreen = () => {
  const navigation = useNavigation();
  const route = useRoute();
  const { orderId, billId } = route.params || {};

  useEffect(() => {
    if (orderId && billId) {
      axios
        .get(`/payments/success?orderId=${orderId}&billId=${billId}`)
        .then(() => {
          Alert.alert('Payment Success 🎉');
          setTimeout(() => navigation.navigate('CustomerOrderHistory'), 1500);
        })
        .catch(() => {
          Alert.alert('Error saving payment');
          setTimeout(() => navigation.goBack(), 1500);
        });
    }
  }, [orderId, billId]);

  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color="#10B981" />
      <Text style={styles.text}>Processing your payment...</Text>
    </View>
  );
};

export default PaymentSuccessScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    backgroundColor: '#fff',
  },
  text: {
    marginTop: 20,
    fontSize: 18,
    color: '#374151', // Tailwind gray-700
    textAlign: 'center',
  },
});
