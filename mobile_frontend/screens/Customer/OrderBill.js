import React from 'react';
import { View, Text, FlatList, StyleSheet } from 'react-native';

const formatRupee = (amount) =>
  `₹${parseFloat(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;

export default function OrderBill({ summary }) {
  if (!summary) {
    return <Text style={styles.message}>No summary available</Text>;
  }

  const renderItem = ({ item }) => (
    <View style={styles.row}>
      <Text style={styles.cell}>{item.itemName}</Text>
      <Text style={styles.cell}>{item.quantity}</Text>
      <Text style={styles.cell}>{formatRupee(item.price)}</Text>
      <Text style={styles.cell}>{formatRupee(item.finalPrice)}</Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Laundry Bill</Text>

      <View style={styles.section}>
        <Text><Text style={styles.label}>Order ID:</Text> {summary.orderId}</Text>
        <Text><Text style={styles.label}>Bill Status:</Text> {summary.status}</Text>
        <Text><Text style={styles.label}>Service:</Text> {summary.serviceName}</Text>
        <Text><Text style={styles.label}>Sub-Service:</Text> {summary.subServiceName}</Text>
      </View>

      <View style={styles.tableHeader}>
        <Text style={[styles.headerCell, { flex: 2 }]}>Item</Text>
        <Text style={styles.headerCell}>Qty</Text>
        <Text style={styles.headerCell}>Rate</Text>
        <Text style={styles.headerCell}>Total</Text>
      </View>

      <FlatList
        data={summary.items}
        renderItem={renderItem}
        keyExtractor={(_, index) => index.toString()}
      />

      <View style={styles.summary}>
        <Text style={styles.summaryText}>Items Total: {formatRupee(summary.itemsTotal)}</Text>
        <Text style={styles.summaryText}>GST: {formatRupee(summary.gstAmount)}</Text>
        <Text style={styles.summaryText}>Delivery Charge: {formatRupee(summary.deliveryCharge)}</Text>

        <Text style={[
          styles.summaryText,
          summary.isPromotionApplied ? styles.discountGreen : styles.discountGray,
        ]}>
          Discount: {formatRupee(summary.discountAmount)}
          {summary.appliedPromoCode && ` (${summary.appliedPromoCode})`}
        </Text>

        {summary.promotionMessage && (
          <Text style={styles.promotionMessage}>{summary.promotionMessage}</Text>
        )}

        <Text style={styles.finalAmount}>
          Final Amount: {formatRupee(summary.finalAmount)}
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    margin: 12,
    backgroundColor: '#fff',
    borderRadius: 10,
    elevation: 2,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 12,
  },
  section: {
    marginBottom: 16,
  },
  label: {
    fontWeight: '600',
  },
  tableHeader: {
    flexDirection: 'row',
    backgroundColor: '#F3F4F6',
    paddingVertical: 8,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: '#D1D5DB',
  },
  headerCell: {
    flex: 1,
    fontWeight: 'bold',
    paddingHorizontal: 4,
    textAlign: 'left',
  },
  row: {
    flexDirection: 'row',
    paddingVertical: 6,
    borderBottomWidth: 1,
    borderColor: '#E5E7EB',
  },
  cell: {
    flex: 1,
    paddingHorizontal: 4,
  },
  summary: {
    marginTop: 16,
    alignItems: 'flex-end',
  },
  summaryText: {
    fontSize: 14,
    marginBottom: 4,
  },
  discountGreen: {
    color: '#16A34A',
  },
  discountGray: {
    color: '#6B7280',
  },
  promotionMessage: {
    fontStyle: 'italic',
    fontSize: 12,
    color: '#6B7280',
    marginTop: 4,
  },
  finalAmount: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 8,
  },
  message: {
    padding: 20,
    textAlign: 'center',
    fontSize: 16,
    color: '#4B5563',
  },
});
