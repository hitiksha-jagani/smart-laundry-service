import React, { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView, Alert } from 'react-native';
import axios from '../../utils/axiosInstance';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const MyProfileScreen = () => {
  const navigation = useNavigation();
  const [user, setUser] = useState({});
  const [orderStats, setOrderStats] = useState({ delivered: 0, cancelled: 0 });

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const userId = await AsyncStorage.getItem('userId');
        if (!userId) return;

        // Fetch user profile
        const userRes = await axios.get(`/user/${userId}`);
        setUser(userRes.data);

        // Fetch order stats
        const statsRes = await axios.get(`/orders/user/${userId}/stats`);
        setOrderStats({
          delivered: statsRes.data.completed,
          cancelled: statsRes.data.cancelled,
        });
      } catch (err) {
        console.error('Error fetching profile or stats', err);
        Alert.alert('Error', 'Something went wrong while loading your profile.');
      }
    };

    fetchProfile();
  }, []);

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>My Profile</Text>

      <View style={styles.card}>
        <Text style={styles.sectionHeader}>User Information</Text>
        <Text style={styles.info}><Text style={styles.label}>Name:</Text> {user.firstName} {user.lastName}</Text>
        <Text style={styles.info}><Text style={styles.label}>Email:</Text> {user.email}</Text>
        <Text style={styles.info}><Text style={styles.label}>Phone:</Text> {user.phoneNo}</Text>
        <Text style={styles.info}><Text style={styles.label}>Preferred Language:</Text> {user.preferredLanguage}</Text>
        <TouchableOpacity style={styles.button} onPress={() => navigation.navigate('UpdateProfile')}>
          <Text style={styles.buttonText}>Edit Profile</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.row}>
        <View style={styles.card}>
          <Text style={styles.sectionHeader}>Order Statistics</Text>
          <Text style={styles.info}>Delivered Orders: <Text style={styles.bold}>{orderStats.delivered}</Text></Text>
          <Text style={styles.info}>Cancelled Orders: <Text style={styles.bold}>{orderStats.cancelled}</Text></Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.sectionHeader}>Order History</Text>
          <Text style={styles.info}>View all your past orders with full details.</Text>
          <TouchableOpacity style={[styles.button, { marginTop: 10 }]} onPress={() => navigation.navigate('CustomerOrderHistory')}>
            <Text style={styles.buttonText}>View Order History</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
};

export default MyProfileScreen;

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#EA580C', // orange-600
    marginBottom: 20,
    textAlign: 'center',
  },
  card: {
    backgroundColor: '#F9FAFB',
    padding: 16,
    borderRadius: 10,
    marginBottom: 20,
    flex: 1,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 4,
    elevation: 3,
  },
  sectionHeader: {
    fontSize: 20,
    fontWeight: '600',
    color: '#EA580C',
    marginBottom: 10,
  },
  info: {
    fontSize: 16,
    color: '#374151',
    marginBottom: 4,
  },
  label: {
    fontWeight: '500',
    color: '#1F2937',
  },
  bold: {
    fontWeight: 'bold',
  },
  button: {
    backgroundColor: '#EA580C',
    paddingVertical: 10,
    paddingHorizontal: 14,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 12,
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  row: {
    flexDirection: 'column',
    gap: 10,
  },
});
