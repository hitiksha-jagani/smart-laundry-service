import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator, StyleSheet, Alert, TouchableOpacity } from 'react-native';
import { Picker } from '@react-native-picker/picker'; // Install with `npm install @react-native-picker/picker`
import axios from '../../utils/axiosInstance';

export default function PromotionSelector({ orderId, onPromoApplied, currentSummary }) {
  const [promotions, setPromotions] = useState([]);
  const [selectedPromoId, setSelectedPromoId] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const isPromoAlreadyApplied = currentSummary?.isPromotionApplied;

  useEffect(() => {
    if (isPromoAlreadyApplied) return;

    axios
      .get(`/orders/available-promotions?orderId=${orderId}`)
      .then((res) => setPromotions(res.data))
      .catch((err) => {
        console.error('Failed to fetch promotions:', err);
        setPromotions([]);
      });
  }, [orderId, isPromoAlreadyApplied]);

  const applyPromo = async () => {
    if (!selectedPromoId || isPromoAlreadyApplied) return;

    setIsLoading(true);
    setMessage('');

    try {
      const res = await axios.post(`/orders/apply-promo`, null, {
        params: { orderId, promotionId: selectedPromoId },
      });

      if (res.data.isPromotionApplied) {
        setMessage('✅ Promo applied successfully!');
      } else {
        setMessage(`⚠️ ${res.data.promotionMessage}`);
      }

      onPromoApplied(res.data);
    } catch (err) {
      console.error(err);
      setMessage('❌ Failed to apply promotion.');
    } finally {
      setIsLoading(false);
    }
  };

  if (isPromoAlreadyApplied) {
    return (
      <View style={styles.appliedContainer}>
        <Text style={styles.appliedText}>
          ✅ Promotion <Text style={{ fontWeight: 'bold' }}>{currentSummary.appliedPromoCode}</Text> is already applied.
        </Text>
      </View>
    );
  }

  if (promotions.length === 0) {
    return (
      <View style={styles.messageContainer}>
        <Text style={styles.infoText}>No available promotions.</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Available Promotions</Text>

      <View style={styles.pickerWrapper}>
        <Picker
          selectedValue={selectedPromoId}
          onValueChange={(itemValue) => setSelectedPromoId(itemValue)}
          enabled={!isLoading}
        >
          <Picker.Item label="-- Select Promo Code --" value="" />
          {promotions.map((p) => (
            <Picker.Item
              key={p.promotionId}
              label={`${p.promoCode} - ${p.description}`}
              value={p.promotionId}
            />
          ))}
        </Picker>
      </View>

      <TouchableOpacity
        onPress={applyPromo}
        disabled={isLoading || !selectedPromoId}
        style={[
          styles.button,
          (isLoading || !selectedPromoId) && styles.buttonDisabled,
        ]}
      >
        <Text style={styles.buttonText}>
          {isLoading ? 'Applying...' : 'Apply Promotion'}
        </Text>
      </TouchableOpacity>

      {message !== '' && <Text style={styles.messageText}>{message}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginTop: 20,
    padding: 16,
    backgroundColor: '#F9FAFB',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#D1D5DB',
  },
  title: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 10,
  },
  pickerWrapper: {
    borderWidth: 1,
    borderColor: '#D1D5DB',
    borderRadius: 8,
    marginBottom: 12,
    overflow: 'hidden',
  },
  button: {
    backgroundColor: '#16A34A',
    paddingVertical: 10,
    borderRadius: 6,
    alignItems: 'center',
  },
  buttonDisabled: {
    backgroundColor: '#9CA3AF',
  },
  buttonText: {
    color: '#FFFFFF',
    fontWeight: '600',
  },
  messageText: {
    marginTop: 10,
    color: '#2563EB',
    fontSize: 14,
  },
  appliedContainer: {
    marginTop: 20,
    padding: 12,
    backgroundColor: '#E0F2F1',
    borderRadius: 8,
    borderColor: '#10B981',
    borderWidth: 1,
  },
  appliedText: {
    color: '#047857',
    fontSize: 14,
  },
  messageContainer: {
    padding: 12,
    marginTop: 20,
    borderRadius: 8,
    backgroundColor: '#F3F4F6',
    borderColor: '#D1D5DB',
    borderWidth: 1,
  },
  infoText: {
    fontSize: 14,
    color: '#6B7280',
  },
});
