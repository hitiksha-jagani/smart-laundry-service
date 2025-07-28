import React, { useEffect } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';

export default function OrderSuccessScreen() {
  const navigation = useNavigation();

  useEffect(() => {
    const timeout = setTimeout(() => {
      navigation.navigate('CustomerDashboard'); // or 'Home' or whichever screen
    }, 5000);

    return () => clearTimeout(timeout);
  }, [navigation]);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>🎉 Order Placed Successfully!</Text>
      <Text style={styles.subtitle}>
        You will be redirected to the homepage shortly...
      </Text>
      <Text style={styles.secondary}>
        If not redirected,{' '}
        <TouchableOpacity onPress={() => navigation.navigate('CustomerDashboard')}>
          <Text style={styles.link}>click here</Text>
        </TouchableOpacity>
        .
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#D1FAE5', // green-100
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  title: {
    fontSize: 26,
    fontWeight: 'bold',
    color: '#047857', // green-700
    marginBottom: 16,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#065F46', // green-800
    textAlign: 'center',
  },
  secondary: {
    marginTop: 12,
    fontSize: 14,
    color: '#6B7280', // gray-500
    textAlign: 'center',
  },
  link: {
    color: '#2563EB', // blue-600
    textDecorationLine: 'underline',
  },
});
