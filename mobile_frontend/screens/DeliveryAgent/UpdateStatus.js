// UpdateStatus.js

import React, { useEffect, useState } from 'react';
import { View, Text, TextInput, StyleSheet, TouchableOpacity, Alert, ActivityIndicator, Dimensions } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useRoute } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import axiosInstance from '../../utils/axiosInstance';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const screenWidth = Dimensions.get('window').width;

const UpdateStatus = () => {
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [delivery, setDelivery] = useState(null);
  const { userId } = useAuth();
  const [user, setUser] = useState(null);
  const route = useRoute();

  useEffect(() => {
    const init = async () => {
      let deliveryData = route.params?.delivery;

      if (!deliveryData) {
        const storedDelivery = await AsyncStorage.getItem("selectedDelivery");
        if (storedDelivery) {
          deliveryData = JSON.parse(storedDelivery);
        }
      }

      setDelivery(deliveryData);

      try {
        const response = await axiosInstance.get(`/user-detail/${userId}`);
        setUser(response.data);
      } catch (err) {
        console.error("Failed to fetch user:", err);
      }
    };

    init();
  }, []);

  const verifyStatus = async (endpoint) => {
    if (!otp || !delivery) return;

    setLoading(true);
    const payload = { orderId: delivery.orderId, otp };

    try {
      console.log(`Calling endpoint: ${endpoint}`);
      await axiosInstance.post(endpoint, payload);
      Alert.alert("Success", "Status updated successfully.");
      setOtp('');
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        'Failed to update status';

      if (msg.toLowerCase().includes('expired')) {
        Alert.alert("OTP Expired", "Please request a new one.");
      } else if (msg.toLowerCase().includes('invalid')) {
        Alert.alert("Invalid OTP", "Please try again.");
      } else {
        Alert.alert("Error", msg);
      }
    } finally {
      setLoading(false);
    }
  };

  if (!delivery) {
    return (
      <View style={styles.wrapper}>
        <Text>No delivery data found.</Text>
      </View>
    );
  }

  const status = delivery.orderStatus;

  const getButtonForStatus = () => {
    switch (status) {
      case 'ACCEPTED_BY_AGENT':
        return (
          <TouchableOpacity style={styles.routeBtn} onPress={() => verifyStatus('/emailotp/verify-pickup')} disabled={loading || !otp}>
            {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.routeText}>Verify Pickup</Text>}
          </TouchableOpacity>
        );
      case 'PICKED_UP':
        return (
          <TouchableOpacity style={styles.routeBtn} onPress={() => verifyStatus('/emailotp/verify-handover')} disabled={loading || !otp}>
            {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.routeText}>Verify Handover</Text>}
          </TouchableOpacity>
        );
      case 'READY_FOR_DELIVERY':
        return (
          <TouchableOpacity style={styles.routeBtn} onPress={() => verifyStatus('/emailotp/verify-confirm-for-cloths')} disabled={loading || !otp}>
            {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.routeText}>Confirm for Clothes</Text>}
          </TouchableOpacity>
        );
      case 'OUT_FOR_DELIVERY':
        return (
          <TouchableOpacity style={styles.routeBtn} onPress={() => verifyStatus('/emailotp/verify-delivery')} disabled={loading || !otp}>
            {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.routeText}>Verify Delivery</Text>}
          </TouchableOpacity>
        );
      default:
        return <Text style={{ textAlign: 'center', marginTop: 20 }}>Unsupported status: {status}</Text>;
    }
  };

  return (
    <DeliveryAgentLayout>
      <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>
        <View style={deliveryAgentStyles.container}>
          <Text style={styles.heading}>Update Delivery Status</Text>
          <View style={styles.card}>
            <TextInput
              style={styles.input}
              placeholder="Enter OTP"
              value={otp}
              onChangeText={setOtp}
              keyboardType="numeric"
            />
            {getButtonForStatus()}
          </View>
        </View>
      </View>
    </DeliveryAgentLayout>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#ecfdf5',
    flexGrow: 1,
  },
  card: {
    backgroundColor: '#f0fdf4',
    padding: 20,
    borderRadius: 12,
    width: screenWidth * 0.9,
    alignSelf: 'center',
    marginTop: 50,
    borderColor: '#a7f3d0',
  },
  wrapper: {
    flex: 1,
    backgroundColor: '#f0fdf4',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  heading: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#047857',
    marginBottom: 15,
    textAlign: 'center',
  },
  input: {
    backgroundColor: '#E8F5E9',
    padding: 14,
    borderRadius: 10,
    fontSize: 16,
    borderColor: '#d1fae5',
    borderWidth: 1,
    marginBottom: 20,
  },
  routeBtn: {
    backgroundColor: '#4ADE80',
    padding: 10,
    borderRadius: 8,
    alignItems: 'center',
  },
  routeText: {
    color: '#fff',
    fontWeight: '600',
  },
});

export default UpdateStatus;
