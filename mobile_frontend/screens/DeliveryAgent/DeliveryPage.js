// Author: Hitiksha Patel
// Description: Delivery page for delivery agent dashboard (React Native)

import React, { useEffect, useState } from 'react';
import { useFonts } from 'expo-font';
import { View, Text, ActivityIndicator, ScrollView, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode'; 

import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 

import { useDrawer } from '../../context/DrawerContext';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';

const mockDeliveries = [
  {
    orderId: "ODR00001",
    deliveryType: "Customer -> Service Provider",
    deliveryEarning: 75,
    km: 4.2,
    pickupDate: "2025-06-23",
    pickupTime: "18:00:00",
    pickupName: "Customer A",
    pickupPhone: "9876543210",
    pickupAddress: "12, A-Block, CG Road, Ahmedabad, Gujarat, 380009",
    deliveryName: "Provider A",
    deliveryPhone: "9123456780",
    deliveryAddress: "18, B-Block, Maninagar, Ahmedabad, Gujarat, 380008",
    totalQuantity: 3,
    bookingItemDTOList: [
      {
        itemName: "Shirt",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      }
    ]
  },
  {
    orderId: "ODR00002",
    deliveryType: "Customer -> Service Provider",
    deliveryEarning: 75,
    km: 4.2,
    pickupDate: "2025-06-23",
    pickupTime: "18:00:00",
    pickupName: "Customer A",
    pickupPhone: "9876543210",
    pickupAddress: "12, A-Block, CG Road, Ahmedabad, Gujarat, 380009",
    deliveryName: "Provider A",
    deliveryPhone: "9123456780",
    deliveryAddress: "18, B-Block, Maninagar, Ahmedabad, Gujarat, 380008",
    totalQuantity: 3,
    bookingItemDTOList: [
      {
        itemName: "Shirt",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      }
    ]
  },
  {
    orderId: "ODR00003",
    deliveryType: "Customer -> Service Provider",
    deliveryEarning: 75,
    km: 4.2,
    pickupDate: "2025-06-23",
    pickupTime: "18:00:00",
    pickupName: "Customer A",
    pickupPhone: "9876543210",
    pickupAddress: "12, A-Block, CG Road, Ahmedabad, Gujarat, 380009",
    deliveryName: "Provider A",
    deliveryPhone: "9123456780",
    deliveryAddress: "18, B-Block, Maninagar, Ahmedabad, Gujarat, 380008",
    totalQuantity: 3,
    bookingItemDTOList: [
      {
        itemName: "Shirt",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      }
    ]
  }
];

const DeliveryPage = () => {
  const navigation = useNavigation();
  const { isDrawerOpen } = useDrawer();

  const [user, setUser] = useState(null);
  const [summary, setSummary] = useState([]);
  const [pending, setPending] = useState([]);
  const [today, setToday] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAllData = async () => {
      const token = await AsyncStorage.getItem('token');
      if (!token) return;

      let decoded;
      try {
        decoded = jwtDecode(token);
        console.log("userId:", decoded.userId || decoded.id);
      } catch (err) {
        console.error('Invalid token:', err);
        return;
      }

      const userId = decoded.userId || decoded.id;

      try {
        const axiosInstance = axios.create({
          baseURL: 'http://localhost:8080',
          headers: { Authorization: `Bearer ${token}` },
        });

        const [userRes, summaryRes, pendingRes, todayRes] = await Promise.all([
          axiosInstance.get(`/user-detail/${userId}`).catch(err => ({ data: null })),
          axiosInstance.get('/deliveries/summary').catch(err => ({ data: null })),
          axiosInstance.get('/deliveries/pending').catch(err => ({ data: null })),
          axiosInstance.get('/deliveries/today').catch(err => ({ data: null })),
        ]);

        setUser(userRes.data);
        setSummary(summaryRes.data);
        setPending(pendingRes.data || []);
        setToday(todayRes.data || []);

      } catch (err) {
        console.error('Fetch failed:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAllData();
  }, []);

  if (loading) {
    return (
      <View style={deliveryAgentStyles.centered}>
        <ActivityIndicator size="large" color="#388E3C" />
        <Text>Loading Dashboard...</Text>
      </View>
    );
  }

  return (
    <DeliveryAgentLayout>
      <View style={deliveryAgentStyles.container}>
        <Text style={[deliveryAgentStyles.h1Agent, styles.heading]}>DELIVERY DASHBOARD</Text>

        <View style={deliveryAgentStyles.summaryWrapper}>
          <View style={deliveryAgentStyles.summaryContainer}>
            <SummaryCard
              title="PENDING ORDERS"
              user={user}
              count={pending?.length || 0}
              link="PendingDeliveries"
              // data={pending}
              data={mockDeliveries}
            />
            <SummaryCard
              title="TODAY'S ORDERS"
              user={user}
              count={today?.length || 0}
              link="TodayDeliveries"
              // data={today}
              data={mockDeliveries}
            />
          </View>
        </View>
      </View>
    </DeliveryAgentLayout>


);
};

export default DeliveryPage;

const styles = StyleSheet.create({
  heading: {
    marginTop: '30px'
  },
  
});