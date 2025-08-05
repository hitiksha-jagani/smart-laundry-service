import React, { useEffect } from 'react';
import { View, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../context/AuthContext';

const AutoRedirectScreen = () => {
  const navigation = useNavigation();
  const { user } = useAuth(); 

  useEffect(() => {
    if (user) {
      navigation.replace('CustomerDashboard');
    } else {
      navigation.replace('Login');   
    }
  }, []);

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <ActivityIndicator size="large" color="#000" />
    </View>
  );
};

export default AutoRedirectScreen;
