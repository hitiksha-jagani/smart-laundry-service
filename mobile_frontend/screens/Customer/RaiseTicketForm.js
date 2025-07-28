import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import { Picker } from '@react-native-picker/picker';
import * as ImagePicker from 'expo-image-picker';
import axios from '../../utils/axiosInstance';
import { useNavigation } from '@react-navigation/native';

export default function RaiseTicketForm() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('GENERAL');
  const [photo, setPhoto] = useState(null);
  const [loading, setLoading] = useState(false);

  const navigation = useNavigation();

  const pickImage = async () => {
    const permission = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!permission.granted) {
      Alert.alert('Permission required', 'Media access permission is needed to upload a photo.');
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.8,
    });

    if (!result.cancelled) {
      setPhoto(result.assets[0]);
    }
  };

  const handleSubmit = async () => {
    if (!title || !description) {
      Alert.alert('Validation Error', 'Title and description are required.');
      return;
    }

    setLoading(true);

    const ticketData = {
      title,
      description,
      category,
      status: 'NOT_RESPONDED',
      submittedAt: new Date().toISOString(),
    };

    const formData = new FormData();
    formData.append('ticket', JSON.stringify(ticketData));

    if (photo) {
      formData.append('photo', {
        uri: photo.uri,
        name: 'ticket-photo.jpg',
        type: 'image/jpeg',
      });
    }

    try {
      await axios.post('/ticket/raise', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      Alert.alert('Success', 'Ticket raised successfully.');
      navigation.navigate('CustomerDashboard');
    } catch (err) {
      console.error(err);
      Alert.alert('Error', 'Failed to raise ticket. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>Raise a Ticket</Text>

      <Text style={styles.label}>Title</Text>
      <TextInput
        value={title}
        onChangeText={setTitle}
        style={styles.input}
        placeholder="Enter ticket title"
      />

      <Text style={styles.label}>Description</Text>
      <TextInput
        value={description}
        onChangeText={setDescription}
        style={[styles.input, { height: 100 }]}
        placeholder="Describe your issue"
        multiline
      />

      <Text style={styles.label}>Category</Text>
      <View style={styles.pickerWrapper}>
        <Picker
          selectedValue={category}
          onValueChange={(value) => setCategory(value)}
        >
          <Picker.Item label="General" value="GENERAL" />
          <Picker.Item label="Payment" value="PAYMENT" />
          <Picker.Item label="Order" value="ORDER" />
          <Picker.Item label="Delivery" value="DELIVERY" />
          <Picker.Item label="Other" value="OTHER" />
        </Picker>
      </View>

      <TouchableOpacity onPress={pickImage} style={styles.uploadButton}>
        <Text style={styles.uploadButtonText}>
          {photo ? '✅ Photo Selected' : 'Upload Optional Photo'}
        </Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[styles.submitButton, loading && { opacity: 0.6 }]}
        onPress={handleSubmit}
        disabled={loading}
      >
        {loading ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.submitButtonText}>Submit Ticket</Text>
        )}
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    marginTop: 20,
    backgroundColor: '#fff',
    borderRadius: 10,
    marginHorizontal: 12,
    elevation: 3,
  },
  heading: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#6B21A8',
    marginBottom: 16,
    textAlign: 'center',
  },
  label: {
    marginBottom: 6,
    fontWeight: '600',
    color: '#374151',
  },
  input: {
    borderWidth: 1,
    borderColor: '#D1D5DB',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginBottom: 12,
  },
  pickerWrapper: {
    borderWidth: 1,
    borderColor: '#D1D5DB',
    borderRadius: 8,
    marginBottom: 16,
  },
  uploadButton: {
    backgroundColor: '#E0E7FF',
    padding: 10,
    borderRadius: 6,
    marginBottom: 16,
    alignItems: 'center',
  },
  uploadButtonText: {
    color: '#4F46E5',
    fontWeight: '600',
  },
  submitButton: {
    backgroundColor: '#6B21A8',
    paddingVertical: 12,
    borderRadius: 6,
    alignItems: 'center',
  },
  submitButtonText: {
    color: '#fff',
    fontWeight: '600',
  },
});
