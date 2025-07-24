import React from 'react';
import { View, Text, ScrollView, StyleSheet, SafeAreaView } from 'react-native';
import Navbar from './Navbar';
import Footer from './Footer';

const AuthLayout = ({ title, children, widthClass }) => {
  return (
    <SafeAreaView style={styles.safeArea}>
      <Navbar />

      <ScrollView contentContainerStyle={styles.container}>
        <View style={styles.card}>
          <Text style={styles.title}>{title}</Text>
          {children}
        </View>
      </ScrollView>

      <Footer />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#F9FAFB', // gray-50
  },
  container: {
    flexGrow: 1,
    justifyContent: 'center',
    paddingHorizontal: 16,
    paddingVertical: 32,
  },
  card: {
    width: '100%',
    maxWidth: 400, // optional: mimic `max-w-sm`
    backgroundColor: '#FFFFFF',
    padding: 24,
    borderRadius: 16,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 6,
    borderWidth: 1,
    borderColor: '#E5E7EB', // border-gray-200
    alignSelf: 'center',
  },
  title: {
    fontSize: 24,
    color: '#7C3AED', // text-purple-700
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 16,
  },
});

export default AuthLayout;
