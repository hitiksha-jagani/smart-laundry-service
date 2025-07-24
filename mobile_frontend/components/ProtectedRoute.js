import React, { useEffect } from 'react';
import { View, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children }) => {
  const { isLoggedIn, loading } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    if (!loading && !isLoggedIn) {
      navigation.replace('Login'); // Redirect to Login screen if not logged in
    }
  }, [isLoggedIn, loading]);

  if (loading || !isLoggedIn) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return children;
};

export default ProtectedRoute;
