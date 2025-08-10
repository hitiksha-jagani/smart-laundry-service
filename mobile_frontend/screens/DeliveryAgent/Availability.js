// Author : Hitiksha Jagani
// Description : Availability Page for delivery agent mobile app.

import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import { BASE_URL } from '../../config';
import axios from 'axios';

import { useAuth } from '../../context/AuthContext';
import ManageAvailability from './ManageAvailability'; 
import SavedAvailability from './SavedAvailability';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const AvailabilityScreen = () => {
    const [loading, setLoading] = useState(true);
    const [availabilities, setAvailabilities] = useState([]);
    const { token, userId } = useAuth();

    useEffect(() => {
        const fetchAllData = async () => {
        
            try {
                const axiosInstance = axios.create({
                    baseURL: `${BASE_URL}`,
                    headers: { Authorization: `Bearer ${token}` },
                });

                const [availabilitiesRes] = await Promise.all([
                
                    axiosInstance.get('/availability/saved',{
                         headers: {
                                Authorization: `Bearer ${token}`
                            }
                    }).catch(err => {
                        console.error('Saved availability fetch failed', err);
                        return { data: [] };
                    }),

                ]);

                setAvailabilities(availabilitiesRes.data);
            } catch (error) {
                console.error('Failed to fetch one or more data:', error);
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
    <DeliveryAgentLayout>

      <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>

          <View style={deliveryAgentStyles.container}>

            <Text style={[deliveryAgentStyles.h1Agent, styles.heading]}>AVAILABILITY DASHBOARD</Text>

                <Text style={styles.subheading}>SAVE</Text>
                <ManageAvailability />

                <Text style={styles.subheading}>LIST OF SAVED AVAILABILITY FOR THIS WEEK</Text>
                <SavedAvailability availabilities={availabilities} />
                
          </View>

      </View>

    </DeliveryAgentLayout>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 16,
    paddingBottom: 40,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#0f172a',
    textAlign: 'center',
    marginBottom: 20,
  },
  subheading: {
    fontSize: 18,
    fontWeight: '600',
    color: '#64748B',
    textAlign: 'center',
    marginTop: 30,
    marginBottom: 10,
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default AvailabilityScreen;
