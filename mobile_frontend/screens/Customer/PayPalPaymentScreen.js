import React, { useEffect } from 'react';
import { ActivityIndicator, View, Alert } from 'react-native';
import * as Linking from 'expo-linking';
import axios from '../../utils/axiosInstance';

export default function PayPalPaymentScreen({ route }) {
  const { billId, orderId } = route.params;

  useEffect(() => {
    const initiatePayment = async () => {
      try {
        const res = await axios.post('/payments/create', {
          billId,
          mobile: true,
        });

        const approvalUrl = res.data.approvalUrl;
        if (approvalUrl) {
          Linking.openURL(approvalUrl);
        } else {
          Alert.alert('Error', 'Approval URL not returned from backend.');
        }
      } catch (err) {
        console.error('Error creating payment:', err);
        Alert.alert('Error', 'Payment creation failed.');
      }
    };

    initiatePayment();
  }, []);

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <ActivityIndicator size="large" />
    </View>
  );
}
