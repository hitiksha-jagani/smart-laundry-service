import React, { useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  Platform,
} from 'react-native';
import DateTimePicker from '@react-native-community/datetimepicker';
import { useNavigation, useRoute } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';

export default function RescheduleOrderScreen() {
  const navigation = useNavigation();
  const route = useRoute();
  const { orderId } = route.params;

  const [date, setDate] = useState(null);
  const [time, setTime] = useState(null);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [showTimePicker, setShowTimePicker] = useState(false);
  const [error, setError] = useState('');

  const handleDateChange = (event, selectedDate) => {
    setShowDatePicker(false);
    if (selectedDate) setDate(selectedDate);
  };

  const handleTimeChange = (event, selectedTime) => {
    setShowTimePicker(false);
    if (selectedTime) setTime(selectedTime);
  };

  const formatDate = (date) =>
    date.toISOString().split('T')[0]; // YYYY-MM-DD

  const formatTime = (date) =>
    date.toTimeString().slice(0, 5); // HH:mm

  const handleReschedule = async () => {
    if (!date || !time) {
      setError('Please select both date and time.');
      return;
    }

    try {
      await axios.post(`/orders/reschedule/${orderId}`, {
        date: formatDate(date),
        slot: formatTime(time),
      });

      Alert.alert('Success', 'Order rescheduled successfully!', [
        { text: 'OK', onPress: () => navigation.navigate('RescheduleSuccess') },
      ]);
    } catch (err) {
      setError(err.response?.data || 'Failed to reschedule the order.');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Reschedule Order</Text>

      <View style={styles.pickerGroup}>
        <Text style={styles.label}>New Pickup Date</Text>
        <TouchableOpacity
          style={styles.input}
          onPress={() => setShowDatePicker(true)}
        >
          <Text>
            {date ? formatDate(date) : 'Select Date'}
          </Text>
        </TouchableOpacity>
        {showDatePicker && (
          <DateTimePicker
            mode="date"
            value={date || new Date()}
            display={Platform.OS === 'ios' ? 'spinner' : 'default'}
            minimumDate={new Date()}
            onChange={handleDateChange}
          />
        )}
      </View>

      <View style={styles.pickerGroup}>
        <Text style={styles.label}>New Time Slot</Text>
        <TouchableOpacity
          style={styles.input}
          onPress={() => setShowTimePicker(true)}
        >
          <Text>
            {time ? formatTime(time) : 'Select Time'}
          </Text>
        </TouchableOpacity>
        {showTimePicker && (
          <DateTimePicker
            mode="time"
            value={time || new Date()}
            display={Platform.OS === 'ios' ? 'spinner' : 'default'}
            onChange={handleTimeChange}
          />
        )}
      </View>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <TouchableOpacity style={styles.button} onPress={handleReschedule}>
        <Text style={styles.buttonText}>Submit Reschedule Request</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 24,
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#4B00B5',
    marginBottom: 24,
    textAlign: 'center',
  },
  pickerGroup: {
    marginBottom: 16,
  },
  label: {
    fontWeight: '500',
    marginBottom: 6,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 12,
    borderRadius: 8,
  },
  error: {
    color: 'red',
    marginTop: 4,
    marginBottom: 12,
  },
  button: {
    backgroundColor: '#4B00B5',
    padding: 14,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 12,
  },
  buttonText: {
    color: '#fff',
    fontWeight: '600',
  },
});
