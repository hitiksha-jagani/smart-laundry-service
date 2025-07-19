// screens/Customer/PaymentCancelScreen.js

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const PaymentCancelScreen = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>
        Payment was cancelled or failed.
      </Text>
    </View>
  );
};

export default PaymentCancelScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    backgroundColor: '#fff',
  },
  text: {
    fontSize: 18,
    color: '#DC2626', // Tailwind red-600
    textAlign: 'center',
  },
});
