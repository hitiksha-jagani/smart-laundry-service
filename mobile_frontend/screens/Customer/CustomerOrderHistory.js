import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  StyleSheet,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';
import { useAuth } from '../../context/AuthContext';

export default function CustomerOrderHistory() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await axios.get('/orders/history', {
          headers: { Authorization: `Bearer ${token}` },
        });
        const filteredOrders = res.data.filter(order => order.status !== 'PENDING');
        setOrders(filteredOrders);
      } catch (err) {
        console.error('Error fetching orders:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [token]);

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#2563EB" />
        <Text style={styles.loadingText}>Loading...</Text>
      </View>
    );
  }

  if (orders.length === 0) {
    return (
      <View style={styles.centered}>
        <Text>No past orders found.</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>My Order History</Text>

      {orders.map(order => (
        <View key={order.orderId} style={styles.card}>
          <Text style={styles.label}>Order ID: <Text style={styles.value}>{order.orderId}</Text></Text>
          <Text style={styles.label}>Pickup: <Text style={styles.value}>{order.pickupDate} at {order.pickupTime}</Text></Text>
          <Text style={styles.label}>Delivery: <Text style={styles.value}>{order.deliveryDate ? `${order.deliveryDate} at ${order.deliveryTime}` : 'Pending'}</Text></Text>
          <Text style={styles.label}>Status: <Text style={[styles.value, styles.status]}>{order.status}</Text></Text>
          <Text style={styles.label}>Contact: <Text style={styles.value}>{order.contactName} ({order.contactPhone})</Text></Text>
          <Text style={styles.label}>Address: <Text style={styles.value}>{order.contactAddress}</Text></Text>
          <Text style={styles.timestamp}>Created at: {new Date(order.createdAt).toLocaleString()}</Text>

          <View style={styles.buttonGroup}>
            <TouchableOpacity
              style={order.isPromotionApplied ? styles.disabledBtn : styles.greenBtn}
              onPress={() =>
                order.isPromotionApplied
                  ? Alert.alert('Info', 'A promotion is already applied to this order.')
                  : navigation.navigate('AvailablePromotions', { orderId: order.orderId })
              }
              disabled={order.isPromotionApplied}
            >
              <Text style={styles.btnText}>
                {order.isPromotionApplied
                  ? `Promotion Applied (${order.appliedPromoCode})`
                  : 'Apply Promotion'}
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.blueBtn}
              onPress={() => navigation.navigate('OrderSummary', { orderId: order.orderId })}
            >
              <Text style={styles.btnText}>View Summary</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.purpleBtn}
              onPress={() => navigation.navigate('OrderBill', { orderId: order.orderId })}
            >
              <Text style={styles.btnText}>View Bill</Text>
            </TouchableOpacity>

            {order.status !== 'DELIVERED' && (
              <TouchableOpacity
                style={styles.orangeBtn}
                onPress={() => navigation.navigate('TrackOrder', { orderId: order.orderId })}
              >
                <Text style={styles.btnText}>Track Order</Text>
              </TouchableOpacity>
            )}

            <TouchableOpacity
              style={styles.redBtn}
              onPress={() => navigation.navigate('CancelOrder', { orderId: order.orderId })}
            >
              <Text style={styles.btnText}>Cancel Order</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.yellowBtn}
              onPress={() => navigation.navigate('RescheduleOrder', { orderId: order.orderId })}
            >
              <Text style={styles.btnText}>Reschedule Order</Text>
            </TouchableOpacity>

            {order.status === 'DELIVERED' && (
              <TouchableOpacity
                onPress={() => navigation.navigate('Feedback', { orderId: order.orderId })}
              >
                <Text style={styles.link}>Give Feedback</Text>
              </TouchableOpacity>
            )}
          </View>
        </View>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { padding: 16 },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#2563EB',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 16,
  },
  card: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
    elevation: 3,
  },
  label: {
    fontWeight: 'bold',
    color: '#333',
  },
  value: {
    fontWeight: 'normal',
  },
  status: {
    color: '#2563EB',
  },
  timestamp: {
    fontSize: 12,
    color: '#888',
    marginTop: 8,
  },
  buttonGroup: {
    marginTop: 16,
    gap: 8,
  },
  greenBtn: {
    backgroundColor: '#16A34A',
    padding: 10,
    borderRadius: 8,
  },
  disabledBtn: {
    backgroundColor: '#A1A1AA',
    padding: 10,
    borderRadius: 8,
  },
  blueBtn: {
    backgroundColor: '#2563EB',
    padding: 10,
    borderRadius: 8,
  },
  purpleBtn: {
    backgroundColor: '#7C3AED',
    padding: 10,
    borderRadius: 8,
  },
  orangeBtn: {
    backgroundColor: '#F97316',
    padding: 10,
    borderRadius: 8,
  },
  redBtn: {
    backgroundColor: '#DC2626',
    padding: 10,
    borderRadius: 8,
  },
  yellowBtn: {
    backgroundColor: '#FACC15',
    padding: 10,
    borderRadius: 8,
  },
  btnText: {
    color: '#fff',
    textAlign: 'center',
    fontWeight: '600',
  },
  link: {
    color: '#2563EB',
    textAlign: 'center',
    marginTop: 8,
    textDecorationLine: 'underline',
  },
});
