import React, { useEffect, useState, useCallback } from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ScrollView,
} from 'react-native';
import axios from '../../utils/axiosInstance';
import { useAuth } from '../../context/AuthContext';
import { useNavigation } from '@react-navigation/native';

const ActiveOrdersScreen = () => {
  const [orders, setOrders] = useState([]);
  const { token } = useAuth();
  const navigation = useNavigation();

  const fetchOrders = useCallback(async () => {
    try {
      const res = await axios.get(`/provider/orders/active`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setOrders(res.data);
    } catch (err) {
      console.error('Error fetching active orders', err);
    }
  }, [token]);

  useEffect(() => {
    if (token) fetchOrders();
  }, [token, fetchOrders]);

  const markAsReady = async (orderId) => {
    try {
      await axios.put(`/provider/orders/${orderId}/ready-for-delivery`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      Alert.alert('Success', 'Order marked as READY_FOR_DELIVERY and OTP sent.');
      fetchOrders();
    } catch (err) {
      console.error('Failed to update order:', err);
      Alert.alert('Error', 'Failed to mark order as ready.');
    }
  };

  const renderOrderItem = ({ item: order }) => (
    <View style={styles.card}>
      <View style={styles.headerRow}>
        <View>
          <Text style={styles.orderId}>
            Order ID: <Text style={styles.grayText}>{order.orderId}</Text>
          </Text>
          <Text style={styles.dateTime}>
            Pickup Date: <Text style={styles.boldText}>{order.pickupDate}</Text> | Time:{' '}
            <Text style={styles.boldText}>{order.pickupTime}</Text>
          </Text>
        </View>
        <View style={styles.statusBadge}>
          <Text style={styles.statusText}>{order.status}</Text>
        </View>
      </View>

      <View style={styles.table}>
        <View style={styles.tableHeader}>
          <Text style={styles.tableHeaderCell}>Item</Text>
          <Text style={styles.tableHeaderCell}>Service</Text>
          <Text style={styles.tableHeaderCell}>Sub-service</Text>
          <Text style={styles.tableHeaderCell}>Qty</Text>
        </View>
        {order.items.map((item, idx) => (
          <View key={idx} style={styles.tableRow}>
            <Text style={styles.cell}>{item.itemName}</Text>
            <Text style={styles.cell}>{item.service}</Text>
            <Text style={styles.cell}>{item.subService}</Text>
            <Text style={styles.cell}>{item.quantity}</Text>
          </View>
        ))}
      </View>

      {order.status === 'IN_CLEANING' && (
        <TouchableOpacity
          onPress={() => markAsReady(order.orderId)}
          style={styles.readyButton}
        >
          <Text style={styles.readyButtonText}>Mark as Ready for Delivery</Text>
        </TouchableOpacity>
      )}
    </View>
  );

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <TouchableOpacity onPress={() => navigation.navigate('ProviderDashboard')}>
        <Text style={styles.backLink}>← Back to Dashboard</Text>
      </TouchableOpacity>

      <Text style={styles.title}>Active Orders</Text>

      {orders.length === 0 ? (
        <Text style={styles.noOrdersText}>No active orders available.</Text>
      ) : (
        <FlatList
          data={orders}
          keyExtractor={(item) => item.orderId}
          renderItem={renderOrderItem}
          contentContainerStyle={{ paddingBottom: 20 }}
        />
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#FFF7ED',
    flexGrow: 1,
  },
  backLink: {
    color: '#EA580C',
    marginBottom: 12,
    fontWeight: '500',
  },
  title: {
    fontSize: 26,
    fontWeight: '700',
    color: '#C2410C',
    textAlign: 'center',
    marginBottom: 20,
  },
  noOrdersText: {
    textAlign: 'center',
    color: '#6B7280',
  },
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
    borderLeftWidth: 4,
    borderLeftColor: '#F97316',
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 5,
    elevation: 3,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  orderId: {
    fontSize: 16,
    fontWeight: '600',
    color: '#C2410C',
  },
  grayText: {
    color: '#1F2937',
  },
  dateTime: {
    fontSize: 13,
    color: '#6B7280',
  },
  boldText: {
    fontWeight: '700',
  },
  statusBadge: {
    backgroundColor: '#FFEDD5',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 999,
    alignSelf: 'flex-start',
  },
  statusText: {
    color: '#C2410C',
    fontSize: 12,
    fontWeight: '600',
  },
  table: {
    marginTop: 10,
  },
  tableHeader: {
    flexDirection: 'row',
    borderBottomWidth: 1,
    borderColor: '#FCD34D',
    paddingBottom: 6,
  },
  tableHeaderCell: {
    flex: 1,
    fontWeight: '700',
    color: '#C2410C',
    fontSize: 13,
  },
  tableRow: {
    flexDirection: 'row',
    paddingVertical: 4,
  },
  cell: {
    flex: 1,
    fontSize: 13,
    color: '#374151',
  },
  readyButton: {
    marginTop: 12,
    backgroundColor: '#F97316',
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: 'center',
  },
  readyButtonText: {
    color: 'white',
    fontWeight: '600',
  },
});

export default ActiveOrdersScreen;
