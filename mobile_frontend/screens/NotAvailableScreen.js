// screens/Common/NotAvailableScreen.js

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { MaterialCommunityIcons } from '@expo/vector-icons'; // Using a toolbox icon from this package

const NotAvailableScreen = () => {
  return (
    <View style={styles.container}>
      <MaterialCommunityIcons name="tools" size={80} color="#F59E0B" style={styles.icon} />
      <Text style={styles.title}>Feature Not Available in Version 1</Text>
      <Text style={styles.description}>
        This feature is currently under development and will be available in a future release. 
        We appreciate your patience and are working hard to bring it to you soon.
      </Text>
    </View>
  );
};

export default NotAvailableScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB', // Tailwind's gray-50
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  icon: {
    marginBottom: 20,
  },
  title: {
    fontSize: 22,
    fontWeight: '600',
    color: '#1F2937', // Tailwind's gray-800
    textAlign: 'center',
    marginBottom: 12,
  },
  description: {
    fontSize: 16,
    color: '#4B5563', // Tailwind's gray-600
    textAlign: 'center',
    maxWidth: 340,
  },
});
