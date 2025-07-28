import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  Button,
  Alert,
  StyleSheet,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import { useRoute, useNavigation } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';
import { useAuth } from '../../context/AuthContext';

export default function FeedbackForm() {
  const { token } = useAuth();
  const route = useRoute();
  const navigation = useNavigation();
  const { orderId } = route.params;

  const [providerId, setProviderId] = useState('');
  const [hasAgent, setHasAgent] = useState(false);

  const [providerRating, setProviderRating] = useState('');
  const [providerReview, setProviderReview] = useState('');

  const [agentRating, setAgentRating] = useState('');
  const [agentReview, setAgentReview] = useState('');

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get(`/orders/${orderId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(res => {
        const order = res.data;
        setProviderId(order.serviceProviderId);
        setHasAgent(!!order.deliveryAgentId);
      })
      .catch(err => {
        console.error('Failed to fetch order:', err);
        Alert.alert('Error', 'Unable to load order details.');
      })
      .finally(() => {
        setLoading(false);
      });
  }, [orderId, token]);

  const handleSubmit = async () => {
    if (!providerRating || !providerReview || (hasAgent && (!agentRating || !agentReview))) {
      Alert.alert('Validation', 'Please fill all required fields.');
      return;
    }

    try {
      await axios.post(
        `/orders/provider-feedback/${orderId}`,
        {
          serviceProviderId: providerId,
          rating: parseFloat(providerRating),
          review: providerReview,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (hasAgent) {
        await axios.post(
          `/orders/agent-feedback/${orderId}`,
          {
            rating: parseFloat(agentRating),
            review: agentReview,
          },
          { headers: { Authorization: `Bearer ${token}` } }
        );
      }

      Alert.alert('Success', 'Thank you! Feedback submitted.', [
        { text: 'OK', onPress: () => navigation.navigate('CustomerDashboard') },
      ]);
    } catch (err) {
      console.error('Feedback submission failed:', err);
      Alert.alert('Error', 'Failed to submit feedback. Please try again.');
    }
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color="#7C3AED" />
        <Text>Loading...</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.heading}>Submit Your Feedback</Text>

      {/* Provider Feedback */}
      <View style={styles.section}>
        <Text style={styles.subHeading}>Service Provider</Text>

        <Text style={styles.label}>Rating (1–5)</Text>
        <TextInput
          style={styles.input}
          value={providerRating}
          onChangeText={setProviderRating}
          keyboardType="decimal-pad"
          placeholder="e.g. 4.5"
        />

        <Text style={styles.label}>Review</Text>
        <TextInput
          style={styles.textarea}
          value={providerReview}
          onChangeText={setProviderReview}
          multiline
          numberOfLines={4}
          placeholder="Write your review here"
        />
      </View>

      {/* Agent Feedback (Optional) */}
      {hasAgent && (
        <View style={styles.section}>
          <Text style={styles.subHeading}>Delivery Agent</Text>

          <Text style={styles.label}>Rating (1–5)</Text>
          <TextInput
            style={styles.input}
            value={agentRating}
            onChangeText={setAgentRating}
            keyboardType="decimal-pad"
            placeholder="e.g. 4.0"
          />

          <Text style={styles.label}>Review</Text>
          <TextInput
            style={styles.textarea}
            value={agentReview}
            onChangeText={setAgentReview}
            multiline
            numberOfLines={4}
            placeholder="Write your review here"
          />
        </View>
      )}

      <Button title="Submit Feedback" color="#7C3AED" onPress={handleSubmit} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#fff',
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  heading: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#7C3AED',
    textAlign: 'center',
    marginBottom: 20,
  },
  section: {
    marginBottom: 30,
  },
  subHeading: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 10,
  },
  label: {
    marginBottom: 4,
    color: '#333',
  },
  input: {
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 6,
    padding: 10,
    marginBottom: 12,
  },
  textarea: {
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 6,
    padding: 10,
    minHeight: 80,
    textAlignVertical: 'top',
  },
});
