import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  ActivityIndicator,
  StyleSheet,
  Alert,
} from 'react-native';
import { useAuth } from '../../context/AuthContext';
import axios from 'axios';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 

const DeliveryAgentPayout = () => {
  const [filterParams, setFilterParams] = useState({ filter: 'overall' });
  const [user, setUser] = useState(null);
  const { token, userId } = useAuth();
  const [summary, setSummary] = useState({});
  const [paid, setPaid] = useState([]);
  const [pending, setPending] = useState([]);
  const [all, setAll] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchAllData = async () => {
    try {
      
      const axiosInstance = axios.create({
          baseURL: 'http://localhost:8080', 
          headers: { Authorization: `Bearer ${token}` },
        });

      const [userRes, summaryRes, paidRes, pendingRes, allRes] = await Promise.all([
        axiosInstance.get(`/user-detail/${userId}`),
        axiosInstance.get('/payouts/summary', { params: filterParams }),
        axiosInstance.get('/payouts/paid', { params: filterParams }),
        axiosInstance.get('/payouts/pending', { params: filterParams }),
        axiosInstance.get('/payouts/all', { params: filterParams }),
      ]);

      setUser(userRes.data);
      setSummary(summaryRes.data);
      setPaid(paidRes.data);
      setPending(pendingRes.data);
      setAll(allRes.data);
    } catch (err) {
      console.error('Error fetching data:', err);
      Alert.alert('Error', 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllData();
  }, [filterParams]);

  if (loading) return <ActivityIndicator size="large" color="#10b981" style={{ flex: 1 }} />;

  return (
    <DeliveryAgentLayout>
      <View style={deliveryAgentStyles.container}>
      <Text style={styles.heading}>Payout Dashboard</Text>

      {/* Summary Cards */}
      <SummaryCard
        title="TOTAL PAYOUTS"
        user={user}
        count={summary?.totalEarnings || 0}
        link="PendingDeliveries"
        data={pending}
      />
      <SummaryCard
        title="PAID PAYOUTS"
        user={user}
        count={summary?.paidPayouts || 0}
        link="PendingDeliveries"
        data={pending}
      />
      <SummaryCard
        title="PENDING PAYOUTS"
        user={user}
        count={summary?.pendingPayouts || 0}
        link="PendingDeliveries"
        data={pending}
      />

    </View>
    </DeliveryAgentLayout>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#F0FDF4',
  },
  heading: {
    fontSize: 22,
    fontWeight: '700',
    marginBottom: 20,
    color: '#333',
  },
  card: {
    backgroundColor: '#d1fae5',
    padding: 16,
    borderRadius: 12,
    marginBottom: 15,
    elevation: 3,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    color: '#065f46',
  },
  cardAmount: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#047857',
  },
});

export default DeliveryAgentPayout;
