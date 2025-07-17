import React, { useEffect } from 'react';
import { View, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../context/AuthContext';

const RoleProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { isLoggedIn, role, loading } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    if (!loading) {
      if (!isLoggedIn) {
        navigation.replace('Login');
      } else if (!allowedRoles.includes(role)) {
        navigation.replace('NotAvailable'); // or 'UnauthorizedScreen' if you have one
      }
    }
  }, [isLoggedIn, role, loading]);

  if (loading || !isLoggedIn || !allowedRoles.includes(role)) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return children;
};

export default RoleProtectedRoute;
