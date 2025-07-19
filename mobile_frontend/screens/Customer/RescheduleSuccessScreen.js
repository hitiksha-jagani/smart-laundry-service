import React, { useEffect } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';

export default function RescheduleSuccessScreen() {
  const navigation = useNavigation();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigation.navigate('CustomerDashboard');
    }, 5000);
    return () => clearTimeout(timer);
  }, [navigation]);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Order Rescheduled!</Text>
      <Text style={styles.message}>
        Your order has been successfully rescheduled.
      </Text>
      <Text style={styles.redirect}>
        Redirecting to your dashboard in 5 seconds...
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  title: {
    fontSize: 26,
    fontWeight: 'bold',
    color: 'green',
    marginBottom: 16,
  },
  message: {
    fontSize: 16,
    color: '#333',
    textAlign: 'center',
  },
  redirect: {
    marginTop: 10,
    fontSize: 14,
    color: '#777',
  },
});
