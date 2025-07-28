import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  StyleSheet,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';

export default function OtpVerificationOrdersScreen() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigation = useNavigation();

  useEffect(() => {
    axios
      .get('/provider/orders/pending-otp-verification')
      .then((res) => {
        setOrders(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Failed to fetch OTP orders', err);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#fb923c" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerText}>Pending OTP Verifications</Text>
      </View>

      <ScrollView contentContainerStyle={styles.scrollContainer}>
        {orders.length === 0 ? (
          <Text style={styles.noData}>No OTP verifications pending.</Text>
        ) : (
          orders.map((order) => (
            <View key={order.orderId} style={styles.card}>
              <Text style={styles.detail}>
                <Text style={styles.label}>Order ID:</Text> {order.orderId}
              </Text>
              <Text style={styles.detail}>
                <Text style={styles.label}>Customer:</Text> {order.customerName}
              </Text>
              <Text style={styles.detail}>
                <Text style={styles.label}>Status:</Text>{' '}
                <Text style={styles.status}>{order.status}</Text>
              </Text>

              <View style={styles.buttonRow}>
                {order.status === 'ACCEPTED_BY_PROVIDER' && (
                  <TouchableOpacity
                    onPress={() =>
                      navigation.navigate('VerifyPickupOtp', { orderId: order.orderId })
                    }
                    style={[styles.button, styles.blue]}
                  >
                    <Text style={styles.buttonText}>Verify Pickup OTP</Text>
                  </TouchableOpacity>
                )}
                {order.status === 'READY_FOR_DELIVERY' && (
                  <TouchableOpacity
                    onPress={() =>
                      navigation.navigate('VerifyDeliveryOtp', { orderId: order.orderId })
                    }
                    style={[styles.button, styles.green]}
                  >
                    <Text style={styles.buttonText}>Verify Delivery OTP</Text>
                  </TouchableOpacity>
                )}
              </View>
            </View>
          ))
        )}
      </ScrollView>
    </View>
  );
}

// 🔽 ADD STYLES RIGHT HERE 🔽
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFF7ED', // orange-50
  },
  header: {
    backgroundColor: '#EA580C', // orange-600
    paddingVertical: 16,
    alignItems: 'center',
    elevation: 3,
  },
  headerText: {
    fontSize: 20,
    color: 'white',
    fontWeight: 'bold',
  },
  scrollContainer: {
    padding: 16,
  },
  card: {
    backgroundColor: 'white',
    borderRadius: 16,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#FED7AA', // orange-200
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 5,
    elevation: 1,
  },
  detail: {
    fontSize: 14,
    color: '#374151', // gray-700
    marginBottom: 4,
  },
  label: {
    fontWeight: 'bold',
  },
  status: {
    color: '#EA580C',
    fontWeight: '500',
  },
  noData: {
    textAlign: 'center',
    color: '#6B7280', // gray-500
    marginTop: 40,
  },
  buttonRow: {
    flexDirection: 'row',
    marginTop: 12,
    gap: 10,
  },
  button: {
    paddingVertical: 8,
    paddingHorizontal: 14,
    borderRadius: 6,
  },
  blue: {
    backgroundColor: '#3B82F6', // blue-500
  },
  green: {
    backgroundColor: '#22C55E', // green-500
  },
  buttonText: {
    color: 'white',
    fontSize: 13,
    fontWeight: '600',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
  },
});
