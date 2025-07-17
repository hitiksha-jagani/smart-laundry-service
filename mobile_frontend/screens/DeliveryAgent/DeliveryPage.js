// Author: Hitiksha Patel
// Description: Delivery page for delivery agent dashboard (React Native)

import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator, ScrollView, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode'; 

import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import DeliveryAgentHeaderDrawer from '../../components/DeliveryAgent/DeliveryAgentHeaderDrawer';

const DeliveryPage = () => {
  const navigation = useNavigation();

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
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#388E3C" />
        <Text>Loading Dashboard...</Text>
      </View>
    );
  }

  return (
    <View style={{ flex: 1 }}>
      {/* Custom Drawer Header */}
      <DeliveryAgentHeaderDrawer agent={user} />

      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.heading}>DELIVERY DASHBOARD</Text>

        <View style={styles.summaryContainer}>
          {/* Pending Orders */}
          <SummaryCard
            title="PENDING ORDERS"
            user={user}
            count={pending?.length || 0}
            link="PendingDeliveries"
            data={pending}
          />

          {/* Today's Orders */}
          <SummaryCard
            title="TODAY'S ORDERS"
            user={user}
            count={today?.length || 0}
            link="TodayDeliveries"
            data={today}
          />
        </View>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  heading: {
    fontSize: 24,
    fontWeight: 'bold',
    marginTop: 30,
    textAlign: 'center',
    color: '#333',
  },
  container: {
    paddingBottom: 50,
    paddingTop: 20,
    paddingHorizontal: 16,
    backgroundColor: '#fff',
    alignItems: 'center',
  },
  summaryContainer: {
    marginTop: 30,
    gap: 24,
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default DeliveryPage;
