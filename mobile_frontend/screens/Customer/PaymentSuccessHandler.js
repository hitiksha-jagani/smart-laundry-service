import React, { useEffect } from 'react';
import { Text } from 'react-native';
import * as Linking from 'expo-linking';
import { useNavigation } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';

export default function PaymentSuccessHandler() {
  const navigation = useNavigation();

  useEffect(() => {
    const handleUrl = async ({ url }) => {
      const parsed = Linking.parse(url);
      const billId = parsed.queryParams?.billId;
      const orderId = parsed.queryParams?.orderId;

      if (!billId || !orderId) return;

      try {
        await axios.get(`/payments/success?orderId=${orderId}&billId=${billId}`);
        navigation.navigate('OrderBill', { orderId });
      } catch (err) {
        console.error('Payment success handling failed:', err);
      }
    };

    const sub = Linking.addEventListener('url', handleUrl);

    Linking.getInitialURL().then((url) => {
      if (url) handleUrl({ url });
    });

    return () => sub.remove();
  }, []);

  return <Text>Processing payment...</Text>;
}
