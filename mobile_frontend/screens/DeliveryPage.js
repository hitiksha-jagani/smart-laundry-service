// screens/DeliveryPage.js
import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, ActivityIndicator, StyleSheet } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import jwtDecode from 'jwt-decode';
import SummaryCard from '../components/SummaryCard';
import DeliveryAgentDashboardLayout from '../components/DeliveryAgentDashboardLayout';

const DeliveryPage = () => {
  const [user, setUser] = useState(null);
  const [summary, setSummary] = useState({});
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
        console.log('User ID:', decoded.id);
      } catch (err) {
        console.error('Invalid token:', err);
        return;
      }

      const userId = decoded.userId || decoded.id;

      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        headers: { Authorization: `Bearer ${token}` },
      });

      try {
        const [userRes, summaryRes, pendingRes, todayRes] = await Promise.all([
          axiosInstance.get(`/user-detail/${userId}`),
          axiosInstance.get('/deliveries/summary'),
          axiosInstance.get('/deliveries/pending'),
          axiosInstance.get('/deliveries/today'),
        ]);

        setUser(userRes.data);
        setSummary(summaryRes.data);
        setPending(pendingRes.data);
        setToday(todayRes.data);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchAllData();
  }, []);

  if (loading) {
    return <ActivityIndicator size="large" color="#007bff" style={styles.loading} />;
  }

  return (
    <DeliveryAgentDashboardLayout user={user}>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.heading}>DELIVERY DASHBOARD</Text>

        <View style={styles.summaryContainer}>
          <SummaryCard
            title="PENDING ORDERS"
            count={pending.length}
            user={user}
            data={pending}
          />

          <SummaryCard
            title="TODAY'S ORDERS"
            count={summary?.todayDeliveries ?? today.length}
            user={user}
            data={today}
          />
        </View>
      </ScrollView>
    </DeliveryAgentDashboardLayout>
  );
};

export default DeliveryPage;

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#fff',
  },
  heading: {
    fontSize: 24,
    fontWeight: 'bold',
    marginVertical: 20,
    textAlign: 'center',
  },
  summaryContainer: {
    gap: 20,
  },
  loading: {
    flex: 1,
    justifyContent: 'center',
  },
});
