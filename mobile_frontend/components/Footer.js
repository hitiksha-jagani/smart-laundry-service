import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Linking,
  ScrollView,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';

const Footer = () => {
  const navigation = useNavigation();

  const navigateTo = (screenName) => {
    try {
      navigation.navigate(screenName);
    } catch (error) {
      console.warn('Navigation error:', error.message);
    }
  };

  return (
    <View style={styles.footer}>
      <ScrollView horizontal contentContainerStyle={styles.row}>
        {/* Company */}
        <View style={styles.column}>
          <Text style={styles.heading}>Company</Text>
          <TouchableOpacity onPress={() => navigateTo('CustomerDashboard')}>
            <Text style={styles.link}>Home</Text>
          </TouchableOpacity>
          {/* Contact screen not defined in App.js */}
          <TouchableOpacity onPress={() => navigateTo('CustomerOrderHistory')}>
            <Text style={styles.link}>Order History</Text>
          </TouchableOpacity>
        </View>

        {/* Support */}
        <View style={styles.column}>
          <Text style={styles.heading}>Support</Text>
          <TouchableOpacity onPress={() => navigateTo('TrackOrder')}>
            <Text style={styles.link}>Track Order</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => navigateTo('RaiseTicket')}>
            <Text style={styles.link}>Raise Ticket</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => navigateTo('Login')}>
            <Text style={styles.link}>Login</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => navigateTo('Register')}>
            <Text style={styles.link}>Sign Up</Text>
          </TouchableOpacity>
        </View>

        {/* Contact Us */}
        <View style={styles.column}>
          <Text style={styles.heading}>Contact Us</Text>
          <Text style={styles.text}>📍 123 Laundry Street, Mumbai</Text>
          <TouchableOpacity onPress={() => Linking.openURL('tel:+911234567890')}>
            <Text style={styles.link}>📞 +91 1234567890</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => Linking.openURL('mailto:info@smartlaundry.com')}>
            <Text style={styles.link}>📧 info@smartlaundry.com</Text>
          </TouchableOpacity>
        </View>

        {/* Legal */}
        <View style={styles.column}>
          <Text style={styles.heading}>Legal</Text>
          <TouchableOpacity onPress={() => navigateTo('TermsAndConditions')}>
            <Text style={styles.link}>Terms of Service</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => navigateTo('TermsAndConditions')}>
            <Text style={styles.link}>Privacy Policy</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => navigateTo('TermsAndConditions')}>
            <Text style={styles.link}>Refund Policy</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>

      <Text style={styles.copyright}>
        © {new Date().getFullYear()} Smart Laundry Service | All Rights Reserved
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  footer: {
    backgroundColor: '#1A1B41',
    paddingVertical: 20,
    paddingHorizontal: 12,
  },
  row: {
    flexGrow: 1,
    justifyContent: 'space-between',
    gap: 30,
  },
  column: {
    flex: 1,
    minWidth: 140,
  },
  heading: {
    fontWeight: '600',
    marginBottom: 10,
    color: '#FFD200',
    fontSize: 16,
  },
  link: {
    color: '#E0E0E0',
    marginBottom: 6,
    fontSize: 14,
  },
  text: {
    color: '#F8F9FA',
    marginBottom: 6,
    fontSize: 14,
  },
  copyright: {
    textAlign: 'center',
    color: '#F8F9FA',
    fontSize: 12,
    marginTop: 20,
  },
});

export default Footer;
