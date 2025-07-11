import React from 'react';
import { View, Text, Image, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { Feather } from '@expo/vector-icons';

import convenience from '../../assets/convenience.png';
import quality from '../../assets/quality.png';
support = require('../../assets/support.png'); // in case of default image import issues

export default function CustomerHomePage() {
  const navigation = useNavigation();

  const features = [
    {
      icon: convenience,
      title: 'Pickup & Delivery',
      desc: 'We pick up your clothes and deliver them fresh & clean.',
    },
    {
      icon: quality,
      title: 'Professional Cleaning',
      desc: 'Handled by experienced providers with top-grade equipment.',
    },
    {
      icon: support,
      title: 'Easy Support',
      desc: 'Track orders, raise tickets, and get support anytime.',
    },
  ];

  return (
    <ScrollView contentContainerStyle={styles.container}>
      {/* HERO SECTION */}
      <View style={styles.heroSection}>
        <Text style={styles.heroTitle}>Smart Laundry at Your Fingertips</Text>
        <Text style={styles.heroSubtitle}>
          Schedule pickups, track orders, and enjoy doorstep delivery.
        </Text>
        <TouchableOpacity
          style={styles.bookNowButton}
          onPress={() => navigation.navigate('ServiceProviders')}
        >
          <Text style={styles.bookNowText}>Book Now</Text>
          <Feather name="arrow-right" size={16} color="#A566FF" style={{ marginLeft: 6 }} />
        </TouchableOpacity>
      </View>

      {/* FEATURES */}
      <View style={styles.featuresSection}>
        <Text style={styles.featuresTitle}>Why Choose Us?</Text>
        <Text style={styles.featuresSubtitle}>Experience next-gen laundry with these awesome features</Text>

        {features.map((item) => (
          <View key={item.title} style={styles.card}>
            <Image source={item.icon} style={styles.icon} />
            <Text style={styles.cardTitle}>{item.title}</Text>
            <Text style={styles.cardDesc}>{item.desc}</Text>
          </View>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    paddingVertical: 30,
    paddingHorizontal: 16,
    backgroundColor: '#FFF5FD',
  },
  heroSection: {
    backgroundColor: '#A566FF',
    borderRadius: 12,
    padding: 24,
    marginBottom: 30,
    alignItems: 'center',
  },
  heroTitle: {
    fontSize: 26,
    fontWeight: 'bold',
    color: 'white',
    textAlign: 'center',
    marginBottom: 10,
  },
  heroSubtitle: {
    fontSize: 16,
    color: 'white',
    textAlign: 'center',
    marginBottom: 16,
  },
  bookNowButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'white',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 20,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 5,
    elevation: 3,
  },
  bookNowText: {
    color: '#A566FF',
    fontWeight: 'bold',
  },
  featuresSection: {
    marginTop: 10,
  },
  featuresTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#4B00B5',
    textAlign: 'center',
    marginBottom: 8,
  },
  featuresSubtitle: {
    fontSize: 14,
    color: '#555',
    textAlign: 'center',
    marginBottom: 20,
  },
  card: {
    backgroundColor: 'white',
    borderRadius: 16,
    padding: 20,
    marginBottom: 16,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 3 },
    shadowRadius: 5,
    elevation: 2,
  },
  icon: {
    width: 50,
    height: 50,
    marginBottom: 12,
    resizeMode: 'contain',
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#4B00B5',
    marginBottom: 4,
  },
  cardDesc: {
    fontSize: 14,
    color: '#555',
    textAlign: 'center',
  },
});
