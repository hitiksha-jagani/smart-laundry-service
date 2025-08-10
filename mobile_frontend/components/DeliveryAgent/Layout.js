// Author: Hitiksha Patel

import React, { useEffect, useState } from 'react';
import { useNavigation } from '@react-navigation/native';
import { View, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import DeliveryAgentHeader from './DeliveryAgentHeaderDrawer';
import axios from 'axios';
import { useAuth } from '../../context/AuthContext';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';
import LocationTracker from './LocationTracker';
import { BASE_URL } from '../../config';

const Layout = ({ children }) => {

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAvailable, setIsAvailable] = useState(false);
  const { token, userId } = useAuth();

  const navigation = useNavigation();

  const showBackButton = navigation.canGoBack();

  useEffect(() => {
    const fetchAllData = async () => {
      try {

        const axiosInstance = axios.create({
          baseURL: `${BASE_URL}`,
          headers: { Authorization: `Bearer ${token}` },
        });

        const [userRes, availabilitiesRes] = await Promise.all([
          axiosInstance.get(`/user-detail/${userId}`),
          axiosInstance.get(`/availability/check-availability`),
        ]);

        setUser(userRes.data);
        setIsAvailable(availabilitiesRes.data === true);
        console.log("Availability", availabilitiesRes.data)
        
      } catch (err) {
        console.error('Invalid token or fetch failed:', err);
        setIsAvailable(false); 
      } finally {
          setLoading(false); 
      }
    };

    fetchAllData();
  }, []);

  if (loading) {
    return (
      <View style={styles.loading}>
        <ActivityIndicator size="large" color="#16a34a" />
      </View>
    );
  }

  return (
    <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>

      <LocationTracker isAvailable={isAvailable} />

      <DeliveryAgentHeader agent={user} showBackButton={showBackButton}
        onBackPress={() => navigation.goBack()}/>

      <ScrollView
        style={deliveryAgentStyles.deliveryAgentBody}
        contentContainerStyle={styles.scrollContent}
        showsHorizontalScrollIndicator={false}
      >
        {children}
      </ScrollView>

    </View>
  );
};

export default Layout;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    overflow: 'hidden', 
  },
  loading: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  scrollContent: {
    paddingTop: 80, 
    paddingHorizontal: 16, 
    paddingBottom: 16, 
  },

});
