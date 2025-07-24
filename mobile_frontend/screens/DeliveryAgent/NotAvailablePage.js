// Author: Hitiksha Jagani
// Description: Service is not available in version 1.

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome'; // FaTools equivalent

const NotAvailablePage = () => {
  return (
    <View style={styles.container}>
      <Icon name="wrench" size={64} color="#FACC15" style={styles.icon} />
      <Text style={styles.heading}>Feature Not Available in Version 1</Text>
      <Text style={styles.paragraph}>
        This feature is currently under development and will be available in a future release. 
        We appreciate your patience and are working hard to bring it to you soon.
      </Text>
    </View>
  );
};

export default NotAvailablePage;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB', 
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 20,
    textAlign: 'center',
  },
  icon: {
    marginBottom: 20,
  },
  heading: {
    fontSize: 20,
    fontWeight: '600',
    color: '#1F2937', 
    marginBottom: 10,
    textAlign: 'center',
  },
  paragraph: {
    fontSize: 16,
    color: '#4B5563',
    textAlign: 'center',
    maxWidth: 300,
  },
});
