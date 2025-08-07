// Author : Hitiksha Jagani
// Description : Track location of delivery agent and send to backend

import React, { useEffect, useRef } from 'react';
import { Alert, Platform } from 'react-native';
import { useAuth } from '../../context/AuthContext';
import axios from 'axios';
import * as Location from 'expo-location';

const LocationTracker = ({ isAvailable }) => {
    const intervalRef = useRef(null);
    const { token, userId } = useAuth();

    useEffect(() => {
        const updateLocation = async () => {
            if (!isAvailable) return;

            try {
                
                let { status } = await Location.requestForegroundPermissionsAsync();
                if (status !== 'granted') {
                    Alert.alert('Permission denied', 'Location permission is required.');
                    return;
                }

                const location = await Location.getCurrentPositionAsync({
                    accuracy: Location.Accuracy.High,
                });

                const { latitude, longitude } = location.coords;

                await axios.put(
                `${BASE_URL}/delivery-agent/update-location`,
                { latitude, longitude },
                {
                    headers: {
                    Authorization: `Bearer ${token}`,
                    },
                }
                );
            } catch (err) {
                console.error('Error updating location:', err);
            }
        };

        const startLocationTracking = () => {
            console.log("Waiting 2 minutes to start location tracking...");
            updateLocation(); 
            intervalRef.current = setInterval(updateLocation, 5000); 
        };

        if (isAvailable) {
            const delayTimeout = setTimeout(startLocationTracking, 2 * 60 * 1000); 

            return () => {
                clearTimeout(delayTimeout);
                if (intervalRef.current) clearInterval(intervalRef.current);
            };
        }

    }, [isAvailable]);

    return null;
};

export default LocationTracker;
