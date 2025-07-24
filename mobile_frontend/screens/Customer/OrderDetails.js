import React, { useEffect, useState } from 'react';
import { View, Text, Button, ScrollView, ActivityIndicator, Alert, StyleSheet } from 'react-native';
import { useRoute, useNavigation } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';
import PayButton from '../../components/PayButton';
import * as Print from 'expo-print';
import * as Sharing from 'expo-sharing';
import OrderBill from './OrderBill'; 

export default function OrderDetails() {
  const route = useRoute();
  const navigation = useNavigation();
  const { orderId } = route.params;

  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const res = await axios.get(`/orders/${orderId}/bill`)
        setSummary(res.data);
      } catch (err) {
        console.error(err);
        setError('Failed to load order summary.');
      } finally {
        setLoading(false);
      }
    };
    fetchSummary();
  }, [orderId]);

  const downloadPDF = async () => {
    if (!summary) return;

    const html = `
      <html>
        <head><meta name="viewport" content="width=device-width, initial-scale=1" /></head>
        <body style="font-family: sans-serif; padding: 20px;">
          <h2>Order Bill - ${summary.invoiceNumber}</h2>
          <p><strong>Status:</strong> ${summary.status}</p>
          <p><strong>Total:</strong> $${summary.finalAmount}</p>
          <hr/>
          ${summary.items?.map(
            item => `<p>${item.itemName} - ${item.quantity} x $${item.unitPrice}</p>`
          ).join('')}
        </body>
      </html>
    `;

    try {
      const { uri } = await Print.printToFileAsync({ html });
      await Sharing.shareAsync(uri);
    } catch (err) {
      Alert.alert('Error', 'Failed to generate or share PDF');
    }
  };

  if (loading) return <ActivityIndicator size="large" color="#4B00B5" style={{ marginTop: 40 }} />;
  if (error) return <Text style={styles.error}>{error}</Text>;

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.heading}>Order Details</Text>

      {summary && (
        <>
          <OrderBill summary={summary} />

          {summary.status !== 'PAID' && summary.invoiceNumber && summary.finalAmount > 0 ? (
            <View style={styles.center}>
              <PayButton
                billId={summary.invoiceNumber}
                orderId={orderId}
              />
            </View>
          ) : (
            <Text style={styles.success}>✅ Payment already completed.</Text>
          )}

          <View style={styles.center}>
            <Button title="Download Bill PDF" onPress={downloadPDF} />
          </View>
        </>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#fff',
  },
  heading: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#4B00B5',
    marginBottom: 16,
    textAlign: 'center',
  },
  center: {
    marginTop: 20,
    alignItems: 'center',
  },
  success: {
    color: 'green',
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 16,
  },
  error: {
    color: 'red',
    textAlign: 'center',
    marginTop: 40,
  },
});
