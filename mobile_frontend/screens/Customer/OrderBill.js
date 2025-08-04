import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import * as Print from 'expo-print';
import * as Sharing from 'expo-sharing';
import { useRoute } from '@react-navigation/native';
import axiosInstance from '../../utils/axiosInstance';


export default function OrderBill() {
  const { orderId } = useRoute().params;
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [pdfLoading, setPdfLoading] = useState(false);

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const res = await axiosInstance.get(`/bills/${orderId}`);
        setSummary(res.data);
      } catch (error) {
        console.error('Error loading summary:', error);
        Alert.alert('Error', 'Could not load bill.');
      } finally {
        setLoading(false);
      }
    };

    fetchSummary();
  }, [orderId]);

  const downloadPDF = async () => {
    if (!summary) {
      Alert.alert("Error", "No summary available.");
      return;
    }

    setPdfLoading(true);
    try {
      const htmlContent = generateHTML(summary);
      const { uri } = await Print.printToFileAsync({ html: htmlContent });
      await Sharing.shareAsync(uri);
    } catch (error) {
      console.error('PDF generation failed:', error);
      Alert.alert('Error', 'Failed to generate PDF.');
    } finally {
      setPdfLoading(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#0d6efd" />
      </View>
    );
  }

  if (!summary) {
    return (
      <View style={styles.center}>
        <Text>No summary available.</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Laundry Bill</Text>

      <View style={styles.section}>
        <Text><Text style={styles.label}>Order ID:</Text> {summary.orderId}</Text>
        <Text><Text style={styles.label}>Bill Status:</Text> {summary.status}</Text>
        <Text><Text style={styles.label}>Service:</Text> {summary.serviceName}</Text>
        <Text><Text style={styles.label}>Sub-Service:</Text> {summary.subServiceName}</Text>
      </View>

      <View style={styles.table}>
        <View style={styles.tableHeader}>
          <Text style={styles.cellHeader}>Item</Text>
          <Text style={styles.cellHeader}>Qty</Text>
          <Text style={styles.cellHeader}>Rate</Text>
          <Text style={styles.cellHeader}>Total</Text>
        </View>

        {summary.items.map((item, i) => (
          <View key={i} style={styles.tableRow}>
            <Text style={styles.cell}>{item.itemName}</Text>
            <Text style={styles.cell}>{item.quantity}</Text>
            <Text style={styles.cell}>₹{item.price.toFixed(2)}</Text>
            <Text style={styles.cell}>₹{item.finalPrice.toFixed(2)}</Text>
          </View>
        ))}
      </View>

      <View style={styles.totals}>
        <Text><Text style={styles.label}>Items Total:</Text> ₹{summary.itemsTotal.toFixed(2)}</Text>
        <Text><Text style={styles.label}>GST:</Text> ₹{summary.gstAmount.toFixed(2)}</Text>
        <Text><Text style={styles.label}>Delivery Charge:</Text> ₹{summary.deliveryCharge.toFixed(2)}</Text>
        <Text style={summary.isPromotionApplied ? styles.discount : styles.normal}>
          <Text style={styles.label}>Discount:</Text> ₹{summary.discountAmount.toFixed(2)}
          {summary.appliedPromoCode ? ` (${summary.appliedPromoCode})` : ''}
        </Text>
        {summary.promotionMessage && (
          <Text style={styles.promoMsg}>{summary.promotionMessage}</Text>
        )}
        <Text style={styles.finalAmount}>
          Final Amount: ₹{summary.finalAmount.toFixed(2)}
        </Text>
      </View>

      <TouchableOpacity
        onPress={downloadPDF}
        disabled={pdfLoading}
        style={[styles.button, pdfLoading && styles.buttonDisabled]}
      >
        {pdfLoading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.buttonText}>Download Bill</Text>
        )}
      </TouchableOpacity>
    </ScrollView>
  );
}

// HTML generator for PDF
const generateHTML = (summary) => {
  const rows = summary.items.map(
    (item) => `
      <tr>
        <td>${item.itemName}</td>
        <td>${item.quantity}</td>
        <td>₹${item.price.toFixed(2)}</td>
        <td>₹${item.finalPrice.toFixed(2)}</td>
      </tr>`
  ).join('');

  return `
    <html>
      <head>
        <style>
          body { font-family: Arial; padding: 20px; }
          h1 { text-align: center; color: #1e40af; }
          table { width: 100%; border-collapse: collapse; margin-top: 20px; }
          th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
          th { background-color: #f3f4f6; }
          .total { margin-top: 20px; text-align: right; }
        </style>
      </head>
      <body>
        <h1>Laundry Bill</h1>
        <p><strong>Order ID:</strong> ${summary.orderId}</p>
        <p><strong>Bill Status:</strong> ${summary.status}</p>
        <p><strong>Service:</strong> ${summary.serviceName}</p>
        <p><strong>Sub-Service:</strong> ${summary.subServiceName}</p>

        <table>
          <tr><th>Item</th><th>Qty</th><th>Rate</th><th>Total</th></tr>
          ${rows}
        </table>

        <div class="total">
          <p><strong>Items Total:</strong> ₹${summary.itemsTotal.toFixed(2)}</p>
          <p><strong>GST:</strong> ₹${summary.gstAmount.toFixed(2)}</p>
          <p><strong>Delivery Charge:</strong> ₹${summary.deliveryCharge.toFixed(2)}</p>
          <p><strong>Discount:</strong> ₹${summary.discountAmount.toFixed(2)} ${summary.appliedPromoCode ? `(${summary.appliedPromoCode})` : ''}</p>
          <p><strong>Final Amount:</strong> ₹${summary.finalAmount.toFixed(2)}</p>
        </div>
      </body>
    </html>
  `;
};

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 12,
    color: '#1e40af',
  },
  section: {
    marginBottom: 16,
  },
  label: {
    fontWeight: 'bold',
  },
  table: {
    marginTop: 12,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: '#ddd',
  },
  tableHeader: {
    flexDirection: 'row',
    backgroundColor: '#f3f4f6',
    paddingVertical: 6,
  },
  tableRow: {
    flexDirection: 'row',
    paddingVertical: 6,
    borderTopWidth: 1,
    borderColor: '#eee',
  },
  cellHeader: {
    flex: 1,
    fontWeight: '600',
    paddingHorizontal: 4,
  },
  cell: {
    flex: 1,
    paddingHorizontal: 4,
  },
  totals: {
    marginTop: 16,
  },
  discount: {
    color: 'green',
  },
  normal: {
    color: '#555',
  },
  promoMsg: {
    fontStyle: 'italic',
    color: '#666',
    marginTop: 4,
  },
  finalAmount: {
    marginTop: 10,
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
  },
  button: {
    backgroundColor: '#2563eb',
    marginTop: 24,
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonDisabled: {
    backgroundColor: '#9ca3af',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
