import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
  ActivityIndicator,
} from 'react-native';
import axios from '../../utils/axiosInstance';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native';

export default function UpdateProfileScreen() {
  const navigation = useNavigation();
  const [formData, setFormData] = useState({
    userId: '',
    firstName: '',
    lastName: '',
    email: '',
    phoneNo: '',
    preferredLanguage: '',
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUserData = async () => {
      const storedUserId = await AsyncStorage.getItem('userId');
      if (storedUserId) {
        setFormData((prev) => ({ ...prev, userId: storedUserId }));
        try {
          const res = await axios.get(`/user/${storedUserId}`);
          const user = res.data;
          setFormData({
            userId: user.userId,
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phoneNo: user.phoneNo,
            preferredLanguage: user.preferredLanguage || '',
          });
        } catch (err) {
          Alert.alert('Error', 'Failed to fetch profile');
        }
      }
      setLoading(false);
    };
    fetchUserData();
  }, []);

  const handleChange = (name, value) => {
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async () => {
    try {
      await axios.put('/customer/profile/update', formData);
      Alert.alert('Success', 'Profile updated successfully');
      setTimeout(() => {
        navigation.navigate('CustomerDashboard'); // Adjust route name if needed
      }, 2000);
    } catch (err) {
      Alert.alert('Error', err.response?.data || 'Something went wrong.');
    }
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#f97316" />
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Update Profile</Text>

      <TextInput
        style={styles.input}
        placeholder="First Name"
        value={formData.firstName}
        onChangeText={(text) => handleChange('firstName', text)}
      />

      <TextInput
        style={styles.input}
        placeholder="Last Name"
        value={formData.lastName}
        onChangeText={(text) => handleChange('lastName', text)}
      />

      <TextInput
        style={styles.input}
        placeholder="Email"
        value={formData.email}
        keyboardType="email-address"
        onChangeText={(text) => handleChange('email', text)}
      />

      <TextInput
        style={styles.input}
        placeholder="Phone Number"
        value={formData.phoneNo}
        keyboardType="phone-pad"
        onChangeText={(text) => handleChange('phoneNo', text)}
      />

      <Text style={styles.label}>Preferred Language</Text>
      {['ENGLISH', 'HINDI', 'MARATHI'].map((lang) => (
        <TouchableOpacity
          key={lang}
          style={[
            styles.languageOption,
            formData.preferredLanguage === lang && styles.selectedLanguage,
          ]}
          onPress={() => handleChange('preferredLanguage', lang)}
        >
          <Text style={styles.languageText}>{lang}</Text>
        </TouchableOpacity>
      ))}

      <TouchableOpacity style={styles.button} onPress={handleSubmit}>
        <Text style={styles.buttonText}>Update Profile</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
    paddingBottom: 60,
    backgroundColor: '#fff',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#ea580c',
    marginBottom: 20,
    textAlign: 'center',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 12,
    borderRadius: 8,
    marginBottom: 12,
  },
  label: {
    fontWeight: '600',
    marginBottom: 8,
    marginTop: 12,
  },
  languageOption: {
    padding: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ccc',
    marginBottom: 8,
  },
  selectedLanguage: {
    backgroundColor: '#fde68a',
    borderColor: '#f59e0b',
  },
  languageText: {
    textTransform: 'capitalize',
  },
  button: {
    backgroundColor: '#ea580c',
    padding: 14,
    borderRadius: 8,
    marginTop: 20,
    alignItems: 'center',
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});
