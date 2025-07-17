import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  ActivityIndicator,
} from 'react-native';
import { useRoute } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';
const statusLabels = {
  ACCEPTED: 'Accepted',
  PICKED_UP: 'Picked Up',
  IN_CLEANING: 'Cleaning',
  READY_FOR_DELIVERY: 'Ready for Delivery',
  DELIVERED: 'Delivered',
};

export default function TrackOrderScreen() {
  const route = useRoute();
  const { orderId } = route.params;

  const [order, setOrder] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    axios
      .get(`/orders/track/${orderId}`)
      .then((res) => setOrder(res.data))
      .catch(() => setError('Failed to fetch order tracking details.'));
  }, [orderId]);

  if (error) {
    return (
      <View style={styles.center}>
        <Text style={styles.errorText}>{error}</Text>
      </View>
    );
  }

  if (!order) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#f97316" />
        <Text style={{ marginTop: 10 }}>Loading...</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Track Your Order</Text>

      <View style={styles.card}>
        <Text style={styles.label}>
          Order ID: <Text style={styles.value}>{order.orderId}</Text>
        </Text>
        <Text style={styles.label}>
          Pickup Date: <Text style={styles.value}>{order.pickupDate}</Text>
        </Text>
        <Text style={styles.label}>
          Pickup Time: <Text style={styles.value}>{order.pickupTime}</Text>
        </Text>
        <Text style={styles.label}>
          Current Status:{' '}
          <Text style={styles.value}>{statusLabels[order.status]}</Text>
        </Text>
      </View>

      <View style={styles.timelineWrapper}>
        <Text style={styles.timelineTitle}>Order Status Timeline</Text>
        <View style={styles.timeline}>
          {order.statusHistory.map((entry, index) => (
            <View key={index} style={styles.timelineItem}>
              <View style={styles.dot} />
              <View style={styles.timelineContent}>
                <Text style={styles.timestamp}>
                  {new Date(entry.changedAt).toLocaleString()}
                </Text>
                <Text style={styles.status}>
                  {statusLabels[entry.status] || entry.status}
                </Text>
              </View>
            </View>
          ))}
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    paddingBottom: 40,
    backgroundColor: '#fff',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#ea580c',
    marginBottom: 16,
    textAlign: 'center',
  },
  card: {
    backgroundColor: '#fff7ed',
    borderWidth: 1,
    borderColor: '#fdba74',
    borderRadius: 10,
    padding: 16,
    marginBottom: 20,
  },
  label: {
    fontWeight: '600',
    fontSize: 14,
    marginBottom: 6,
  },
  value: {
    fontWeight: 'normal',
  },
  timelineWrapper: {
    marginTop: 20,
  },
  timelineTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 12,
    color: '#444',
  },
  timeline: {
    borderLeftWidth: 2,
    borderColor: '#fdba74',
    paddingLeft: 12,
  },
  timelineItem: {
    flexDirection: 'row',
    marginBottom: 20,
    position: 'relative',
  },
  dot: {
    width: 10,
    height: 10,
    backgroundColor: '#ea580c',
    borderRadius: 5,
    position: 'absolute',
    left: -16,
    top: 6,
    zIndex: 1,
  },
  timelineContent: {
    marginLeft: 8,
  },
  timestamp: {
    fontSize: 12,
    color: '#888',
  },
  status: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#333',
  },
  errorText: {
    color: 'red',
    fontSize: 16,
  },
});
