import React, { useEffect, useState, useCallback } from 'react';
import {
  View,
  Text,
  FlatList,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
} from 'react-native';
import axios from '../../utils/axiosInstance';
import { useAuth } from '../../context/AuthContext';
import { useNavigation } from '@react-navigation/native';

const DeliveredOrdersScreen = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();
  const navigation = useNavigation();

  const fetchDeliveredOrders = useCallback(async () => {
    try {
      const res = await axios.get('/provider/orders/delivered', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setOrders(res.data);
    } catch (err) {
      console.error('Error fetching delivered orders', err);
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    if (token) fetchDeliveredOrders();
  }, [token, fetchDeliveredOrders]);

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
    </View>
  );

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <TouchableOpacity onPress={() => navigation.navigate('ProviderDashboard')}>
        <Text style={styles.backLink}>← Back to Dashboard</Text>
      </TouchableOpacity>

      <Text style={styles.title}>Delivered Orders</Text>

      {loading ? (
        <ActivityIndicator size="large" color="#EA580C" />
      ) : orders.length === 0 ? (
        <Text style={styles.noOrdersText}>No delivered orders yet.</Text>
      ) : (
        <FlatList
          data={orders}
          keyExtractor={(item) => item.orderId}
          renderItem={renderOrderItem}
          contentContainerStyle={{ paddingBottom: 30 }}
        />
      )}
    </ScrollView>
  );
};

export default DeliveredOrdersScreen;

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
    fontSize: 16,
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
    backgroundColor: '#DCFCE7',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 999,
    alignSelf: 'flex-start',
  },
  statusText: {
    color: '#15803D',
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
});
