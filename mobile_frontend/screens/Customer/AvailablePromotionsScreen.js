import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  ActivityIndicator,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';
import axios from '../../utils/axiosInstance';
import { useRoute, useNavigation } from '@react-navigation/native';

export default function AvailablePromotionsScreen() {
  const route = useRoute();
  const navigation = useNavigation();
  const { orderId } = route.params;

  const [promotions, setPromotions] = useState([]);
  const [appliedPromoCode, setAppliedPromoCode] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applyingPromoId, setApplyingPromoId] = useState(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchPromotions();
  }, []);

  const fetchPromotions = async () => {
    try {
      setLoading(true);

      const summaryRes = await axios.get(`/orders/${orderId}/summary`);
      setAppliedPromoCode(summaryRes.data.appliedPromoCode || null);

      const promoRes = await axios.get(`/orders/available-promotions`, {
        params: { orderId },
      });
      setPromotions(promoRes.data);
    } catch (err) {
      console.error(err);
      setError('Failed to load promotions.');
    } finally {
      setLoading(false);
    }
  };

  const handleApplyPromotion = async (promotionId) => {
    if (appliedPromoCode) {
      setMessage('⚠️ A promotion is already applied.');
      return;
    }

    setApplyingPromoId(promotionId);
    setMessage('');

    try {
      const res = await axios.post(`/orders/apply-promo`, null, {
        params: { orderId, promotionId },
      });

      const summary = res.data;
      if (summary.isPromotionApplied) {
        navigation.navigate('OrderSummary', { orderId });
      } else {
        setMessage(summary.promotionMessage || '⚠️ Promotion could not be applied.');
      }
    } catch (err) {
      console.error(err);
      setMessage('❌ Failed to apply promotion.');
    } finally {
      setApplyingPromoId(null);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Available Promotions</Text>

      {loading && <ActivityIndicator size="large" color="#2563eb" />}
      {error ? <Text style={styles.error}>{error}</Text> : null}
      {message ? <Text style={styles.message}>{message}</Text> : null}

      {appliedPromoCode && (
        <Text style={styles.applied}>
          ✅ Promotion <Text style={styles.bold}>{appliedPromoCode}</Text> is already applied.
        </Text>
      )}

      {!loading && promotions.length === 0 && (
        <Text style={styles.noPromos}>No active promotions available for this order.</Text>
      )}

      {!loading &&
        promotions.map((promo) => (
          <View
            key={promo.promotionId}
            style={[
              styles.card,
              appliedPromoCode === promo.promoCode && styles.appliedCard,
            ]}
          >
            <Text style={styles.promoCode}>{promo.promoCode}</Text>
            <Text style={styles.description}>{promo.description}</Text>
            <Text style={styles.validity}>
              Valid from{' '}
              <Text style={styles.bold}>
                {new Date(promo.startDate).toLocaleDateString()}
              </Text>{' '}
              to{' '}
              <Text style={styles.bold}>
                {new Date(promo.endDate).toLocaleDateString()}
              </Text>
            </Text>

            <TouchableOpacity
              onPress={() => handleApplyPromotion(promo.promotionId)}
              disabled={!!appliedPromoCode || applyingPromoId === promo.promotionId}
              style={[
                styles.button,
                (!!appliedPromoCode || applyingPromoId === promo.promotionId) && styles.buttonDisabled,
              ]}
            >
              <Text style={styles.buttonText}>
                {applyingPromoId === promo.promotionId
                  ? 'Applying...'
                  : appliedPromoCode
                  ? 'Already Applied'
                  : 'Apply'}
              </Text>
            </TouchableOpacity>
          </View>
        ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#f9fafb',
    minHeight: '100%',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  card: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: '#d1d5db',
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  appliedCard: {
    borderColor: '#16a34a',
    backgroundColor: '#ecfdf5',
  },
  promoCode: {
    fontSize: 18,
    fontWeight: '600',
    color: '#2563eb',
  },
  description: {
    fontSize: 14,
    color: '#374151',
    marginTop: 4,
  },
  validity: {
    fontSize: 12,
    color: '#6b7280',
    marginTop: 6,
  },
  bold: {
    fontWeight: '600',
  },
  button: {
    marginTop: 12,
    backgroundColor: '#16a34a',
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 6,
  },
  buttonDisabled: {
    backgroundColor: '#9ca3af',
  },
  buttonText: {
    color: '#fff',
    textAlign: 'center',
    fontWeight: '500',
  },
  message: {
    color: '#2563eb',
    marginVertical: 8,
    fontSize: 14,
  },
  error: {
    color: '#dc2626',
    fontSize: 14,
    marginBottom: 8,
  },
  applied: {
    color: '#16a34a',
    fontSize: 14,
    fontWeight: '500',
    marginBottom: 10,
  },
  noPromos: {
    fontSize: 14,
    color: '#4b5563',
  },
});
