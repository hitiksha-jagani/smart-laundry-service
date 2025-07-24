import React, { useEffect } from 'react';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../context/AuthContext';
import { View, ActivityIndicator } from 'react-native';

export default function PrivateRoute({ children, roles = [] }) {
  const { isLoggedIn, role, loading } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    if (loading) return;

    if (!isLoggedIn) {
      navigation.replace('Login');
    } else if (roles.length > 0 && !roles.includes(role)) {
      navigation.replace('NotAvailable'); // Or home screen if needed
    }
  }, [isLoggedIn, role, roles, loading]);

  if (loading || !isLoggedIn || (roles.length > 0 && !roles.includes(role))) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return children;
}
