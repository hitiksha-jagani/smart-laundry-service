// Author : Hitiksha Jagani 
// Description : Track location of delivery agent send to the backend.

import React, { useEffect, useRef } from 'react';
import axios from 'axios';

const LocationTracker = ({ isAvailable }) => {
  const intervalRef = useRef(null);

  useEffect(() => {
    const updateLocation = async () => {
      if (!navigator.geolocation || !isAvailable) return;

      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const { latitude, longitude } = position.coords;

          try {
            await axios.put(
              'http://localhost:8080/delivery-agent/update-location',
              { latitude, longitude },
              {
                headers: {
                  Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
              }
            );
            // console.log('Location updated:', latitude, longitude);
          } catch (err) {
            console.error('Error updating location:', err);
          }
        },
        (err) => {
          console.warn('Geolocation error:', err);
        },
        {
          enableHighAccuracy: true,
          maximumAge: 0,
          timeout: 5000,
        }
      );
    };

    if (isAvailable) {
      intervalRef.current = setInterval(updateLocation, 5000); // every 5 seconds
    }

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [isAvailable]);

  return null; // This component runs in background
};

export default LocationTracker;
