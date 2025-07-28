import React from 'react';
import { View, Text, ScrollView, StyleSheet } from 'react-native';

export default function TermsAndConditionsScreen() {
  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Terms and Conditions for Customers</Text>

      <Text style={styles.meta}>
        Smart Laundry Service{'\n'}
        <Text style={styles.bold}>Effective Date:</Text> 15/5/2025{'\n'}
        <Text style={styles.bold}>Last Updated:</Text> 23/5/2025
      </Text>

      <Text style={styles.paragraph}>
        Welcome to Smart Laundry Service! These Terms and Conditions (“Terms”) govern your use of our laundry services as a registered customer (“you” or “Customer”) on our platform (“Smart Laundry Service”, “we”, “our”, or “the Company”). By using our services, you agree to these Terms in full.
      </Text>

      {sections.map((section, index) => (
        <View key={index} style={styles.section}>
          <Text style={styles.heading}>{index + 1}. {section.title}</Text>
          {section.items ? (
            section.items.map((item, i) => (
              <Text key={i} style={styles.bullet}>• {item}</Text>
            ))
          ) : (
            <Text style={styles.paragraph}>{section.description}</Text>
          )}
        </View>
      ))}

      <View style={styles.section}>
        <Text style={styles.heading}>12. Contact Us</Text>
        <Text style={styles.paragraph}>
          If you have any questions or concerns, please reach out to us at:{'\n'}
          <Text style={styles.bold}>Email:</Text> support@smartlaundryservice.com{'\n'}
          <Text style={styles.bold}>Phone:</Text> +91 1234567890
        </Text>
      </View>
    </ScrollView>
  );
}

const sections = [
  {
    title: 'Account Registration',
    items: [
      'You must be at least 18 years of age to register.',
      'You agree to provide accurate, current, and complete information.',
      'You are responsible for maintaining the confidentiality of your login credentials.',
    ],
  },
  {
    title: 'Laundry Services',
    items: [
      'You may request pickup, laundering, and delivery of your items through our platform.',
      'Service availability may vary based on your location.',
      'We reserve the right to reject or reschedule orders due to capacity or logistical constraints.',
    ],
  },
  {
    title: 'Pickups and Deliveries',
    items: [
      'You must ensure items are ready for pickup at the scheduled time.',
      'If you are unavailable, you may authorize someone else to hand over or receive items.',
      'Missed pickups or deliveries may incur a rescheduling fee.',
    ],
  },
  {
    title: 'Garment Care and Liability',
    items: [
      'We follow standard industry practices for cleaning and handling garments.',
      'We are not responsible for items with missing or unclear care labels.',
      'We are not responsible for damage from normal wear and tear or inherent fabric weaknesses.',
      'We are not responsible for pre-existing damages or color bleeding.',
      'You must notify us of any damage or missing items within 24 hours of delivery.',
    ],
  },
  {
    title: 'Pricing and Payments',
    items: [
      'Prices are listed on the platform and are subject to change.',
      'All payments must be made online through our secure payment gateway.',
      'You agree to pay all charges incurred under your account.',
    ],
  },
  {
    title: 'Cancellations and Refunds',
    items: [
      'Cancellations are allowed up to 1 hour before scheduled pickup without penalty.',
      'Late cancellations may incur a fee.',
    ],
  },
  {
    title: 'Customer Conduct',
    items: [
      'You agree to use the platform for lawful purposes only.',
      'You will not misuse the service or harass staff, delivery agents, or service providers.',
      'You will not attempt to reverse engineer, damage, or hack the system.',
    ],
  },
  {
    title: 'Account Termination',
    items: [
      'We may suspend or terminate your account if you breach any of these Terms.',
      'We may suspend or terminate your account for misuse of service or inappropriate behavior.',
      'We may suspend or terminate your account for fraudulent activity.',
    ],
  },
  {
    title: 'Limitation of Liability',
    items: [
      'Our liability is limited to the value of the affected laundry order.',
      'We are not liable for indirect or consequential damages, including emotional distress, lost profits, or missed events.',
    ],
  },
  {
    title: 'Privacy Policy',
    description:
      'We value your privacy. Your personal and payment information is handled according to our Privacy Policy.',
  },
  {
    title: 'Changes to These Terms',
    description:
      'We may update these Terms from time to time. Continued use of the platform indicates your acceptance of the revised Terms.',
  },
];

const styles = StyleSheet.create({
  container: {
    padding: 20,
    paddingBottom: 40,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#4B00B5',
    textAlign: 'center',
    marginBottom: 16,
  },
  meta: {
    color: '#666',
    fontSize: 13,
    marginBottom: 20,
    textAlign: 'left',
  },
  bold: {
    fontWeight: 'bold',
  },
  paragraph: {
    fontSize: 15,
    color: '#333',
    marginBottom: 10,
    textAlign: 'justify',
  },
  section: {
    marginBottom: 20,
  },
  heading: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    color: '#111',
  },
  bullet: {
    fontSize: 14,
    color: '#444',
    marginLeft: 10,
    marginBottom: 6,
  },
});
