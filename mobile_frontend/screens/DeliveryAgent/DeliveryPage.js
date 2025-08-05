// Author: Hitiksha Patel
// Description: Delivery page for delivery agent dashboard (React Native)

import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { View, Text, ActivityIndicator, ScrollView, StyleSheet } from 'react-native';

import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 

import axiosInstance from '../../utils/axiosInstance';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';

const DeliveryPage = () => {
  const [user, setUser] = useState(null);
  const { userId } = useAuth();
  const [summary, setSummary] = useState([]);
  const [pending, setPending] = useState([]);
  const [today, setToday] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAllData = async () => {
      
      try {
        
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

        console.log("Fetched summary:", summaryRes.data);
        console.log("Fetched pending deliveries:", pendingRes.data);

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

        <View style={[deliveryAgentStyles.summaryWrapper, styles.summaryData]}>
          <View style={deliveryAgentStyles.summaryContainer}>
            <SummaryCard
              title="PENDING ORDERS"
              user={user}
              count={pending?.length || 0}
              link="PendingDeliveries"
              data={pending ?? []}
              // data={mockDeliveries}
            />
            <SummaryCard
              title="TODAY'S ORDERS"
              user={user}
              count={today?.length || 0}
              link="TodayDeliveries"
              data={today ?? []}
              // data={mockDeliveries}
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
    marginTop: '30'
  },

  summaryData: {
    paddingTop: '50'
  }
  
});