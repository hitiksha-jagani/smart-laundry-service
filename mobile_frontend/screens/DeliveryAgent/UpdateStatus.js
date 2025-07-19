// UpdateStatus.js

import React, { useEffect, useState } from 'react';
import { View, Text, TextInput, StyleSheet, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { useRoute } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const UpdateStatus = () => {
    const [otp, setOtp] = useState('');
    const [loading, setLoading] = useState(false);
    const [delivery, setDelivery] = useState(null);
    const { token, userId } = useAuth();
    const [user, setUser] = useState(null);
    const route = useRoute();

    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
        setToast({ message: '', type: '', visible: false });
        }, 3000);
    };

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
                const axiosInstance = axios.create({
                    baseURL: "http://localhost:8080",
                    headers: { Authorization: `Bearer ${token}` },
                });

                const response = await axiosInstance.get(`/user-detail/${userId}`);
                setUser(response.data);
            } catch (err) {
                console.error("Failed to fetch user:", err);
            }
        };

        init();
    }, []);

    const handleUpdateStatus = async () => {

        if (!otp || !delivery) return;
        setLoading(true);

        const token = await AsyncStorage.getItem("token");

        const axiosInstance = axios.create({
            baseURL: "http://localhost:8080",
            headers: { Authorization: `Bearer ${token}` },
        });

        const status = delivery.orderStatus;
        const orderId = delivery.orderId;

        let endpoint = '';
        const payload = { orderId, otp };

        switch (status) {
            case 'ACCEPTED_BY_AGENT': endpoint = '/emailotp/verify-pickup'; break;
            case 'PICKED_UP': endpoint = '/emailotp/verify-handover'; break;
            case 'READY_FOR_DELIVERY': endpoint = '/emailotp/verify-confirm-for-cloths'; break;
            case 'OUT_FOR_DELIVERY': endpoint = '/emailotp/verify-delivery'; break;
            default:
                Alert.alert("Error", "Unsupported order status: " + status);
                setLoading(false);
                return;
        }

        try {
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

    return (

        <DeliveryAgentLayout>

            <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>

                <View style={deliveryAgentStyles.container}>

                    <Text style={deliveryAgentStyles.h1Agent}>Update Delivery Status</Text>

                    <View style={styles.card}>

                        <TextInput
                            style={styles.input}
                            placeholder="Enter OTP"
                            value={otp}
                            onChangeText={setOtp}
                            keyboardType="numeric"
                        />

                        <TouchableOpacity
                            style={[styles.routeBtn, (!otp || loading) && { opacity: 0.6 }]}
                            onPress={handleUpdateStatus}
                            disabled={!otp || loading}
                        >

                            {loading ? (
                                <ActivityIndicator color="#fff" />
                            ) : (

                                <Text style={styles.routeText}>Verify OTP & Update</Text>
                                
                            )}

                        </TouchableOpacity>

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
        width: '98%',
        marginTop: '50px',
        borderColor: '#a7f3d0',
    },
  wrapper: {
    flex: 1,
    backgroundColor: '#f0fdf4',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  box: {
    width: '100%',
    backgroundColor: '#F0FDF4',
    padding: 30,
    borderRadius: 12,
    elevation: 4,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 8,
  },
  heading: {
    marginTop: '1',
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
        marginTop: 15,
        alignItems: 'center',
    },

    routeText: {
        color: '#fff',
        fontWeight: '900',
        height: '20px'
    },
  
});

export default UpdateStatus;
