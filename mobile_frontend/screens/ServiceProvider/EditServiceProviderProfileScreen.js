import React, { useEffect, useState } from 'react';
import {
  View, Text, TextInput, ScrollView, TouchableOpacity, Alert, ActivityIndicator, Platform, Image
} from 'react-native';
import * as DocumentPicker from 'expo-document-picker';
import { useNavigation } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';
import { Calendar } from 'react-native-calendars';
import * as ImagePicker from 'expo-image-picker';

export default function EditServiceProviderProfileScreen() {
  const [profile, setProfile] = useState(null);
  const [items, setItems] = useState([]);
  const [selectedPrices, setSelectedPrices] = useState([]);
  const [selectedDates, setSelectedDates] = useState({});
  const [loading, setLoading] = useState(true);
  const navigation = useNavigation();

  useEffect(() => {
    axios.get("/sp/sp-profile")
      .then(res => {
        setProfile(res.data);
        setSelectedPrices(res.data.priceDTO || []);

        if (res.data.serviceProviderId) {
          axios.get(`/sp/block-days?providerId=${res.data.serviceProviderId}`)
            .then(res => {
              const marked = {};
              res.data.forEach(dateStr => {
                marked[dateStr] = { selected: true, marked: true, selectedColor: '#FDBA74' };
              });
              setSelectedDates(marked);
            });
        }

        setLoading(false);
      })
      .catch(() => {
        Alert.alert("Error", "Failed to load profile");
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (profile?.serviceId) {
      axios.get(`/items?serviceId=${profile.serviceId}&subServiceId=${profile.subServiceId || ""}`)
        .then(res => setItems(res.data))
        .catch(() => Alert.alert("Error", "Failed to fetch items"));
    }
  }, [profile?.serviceId, profile?.subServiceId]);

  const toggleDate = (dateStr) => {
    setSelectedDates(prev => ({
      ...prev,
      [dateStr]: prev[dateStr] ? undefined : { selected: true, marked: true, selectedColor: '#FDBA74' }
    }));
  };

  const handleChange = (field, value) => {
    setProfile(prev => ({ ...prev, [field]: value }));
  };

  const handleAddressChange = (field, value) => {
    setProfile(prev => ({
      ...prev,
      address: {
        ...prev.address,
        [field]: value
      }
    }));
  };

  const handleBankChange = (field, value) => {
    setProfile(prev => ({
      ...prev,
      bankAccount: {
        ...prev.bankAccount,
        [field]: value
      }
    }));
  };

  const handleFilePick = async (field) => {
    const result = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ImagePicker.MediaTypeOptions.Images });
    if (!result.canceled) {
      handleChange(field, {
        uri: result.assets[0].uri,
        name: `${field}.jpg`,
        type: 'image/jpeg',
      });
    }
  };

  const handleSubmit = async () => {
    try {
      const completeProfile = {
        ...profile,
        priceDTO: selectedPrices
      };

      const formData = new FormData();
      formData.append("profile", JSON.stringify(completeProfile));

      // Files
      ["aadharCard", "panCard", "utilityBill", "profilePhoto"].forEach(key => {
        if (profile[key]) {
          formData.append(key, {
            uri: profile[key].uri,
            name: profile[key].name,
            type: profile[key].type,
          });
        }
      });

      await axios.put("/sp/sp-profile/edit", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });

      const blockedDates = Object.keys(selectedDates);
      await axios.post("/sp/block-days", {
        providerId: profile.serviceProviderId,
        dates: blockedDates
      });

      Alert.alert("Success", "Profile updated successfully");
      navigation.navigate("ServiceProviderDashboard");
    } catch (err) {
      Alert.alert("Error", "Failed to update profile");
    }
  };

  if (loading) {
    return <View style={{ flex: 1, justifyContent: 'center' }}><ActivityIndicator size="large" color="#fb923c" /></View>;
  }

  return (
    <ScrollView style={{ padding: 16, backgroundColor: '#fff' }}>
      <Text style={{ fontSize: 24, fontWeight: 'bold', color: '#fb923c', marginBottom: 16 }}>Edit Profile</Text>

      {/* Personal Info */}
      {["firstName", "lastName", "businessName", "businessLicenseNumber", "gstNumber"].map(field => (
        <TextInput
          key={field}
          placeholder={field.replace(/([A-Z])/g, ' $1')}
          value={profile[field] || ''}
          onChangeText={val => handleChange(field, val)}
          style={{ borderWidth: 1, marginBottom: 10, padding: 10, borderRadius: 8 }}
        />
      ))}

      {/* File Upload */}
      {["aadharCard", "panCard", "utilityBill", "profilePhoto"].map(doc => (
        <TouchableOpacity key={doc} onPress={() => handleFilePick(doc)} style={{ marginVertical: 6 }}>
          <Text style={{ color: '#fb923c' }}>Upload {doc}</Text>
        </TouchableOpacity>
      ))}

      {/* Address */}
      <Text style={{ marginTop: 16, fontWeight: 'bold', color: '#fb923c' }}>Address</Text>
      {["name", "areaName", "pincode", "cityName"].map(field => (
        <TextInput
          key={field}
          placeholder={field}
          value={profile.address?.[field] || ''}
          onChangeText={val => handleAddressChange(field, val)}
          style={{ borderWidth: 1, marginBottom: 10, padding: 10, borderRadius: 8 }}
        />
      ))}

      {/* Bank */}
      <Text style={{ marginTop: 16, fontWeight: 'bold', color: '#fb923c' }}>Bank Details</Text>
      {["bankName", "ifscCode", "bankAccountNumber", "accountHolderName"].map(field => (
        <TextInput
          key={field}
          placeholder={field}
          value={profile.bankAccount?.[field] || ''}
          onChangeText={val => handleBankChange(field, val)}
          style={{ borderWidth: 1, marginBottom: 10, padding: 10, borderRadius: 8 }}
        />
      ))}

      {/* Prices */}
      <Text style={{ marginTop: 16, fontWeight: 'bold', color: '#fb923c' }}>Set Your Prices</Text>
      {items.map(item => {
        const price = selectedPrices.find(p => p.itemId === item.itemId)?.price || '';
        return (
          <View key={item.itemId} style={{ marginBottom: 10 }}>
            <Text>{item.itemName}</Text>
            <TextInput
              keyboardType="numeric"
              value={String(price)}
              onChangeText={val => {
                const newPrice = parseFloat(val);
                setSelectedPrices(prev => {
                  const filtered = prev.filter(p => p.itemId !== item.itemId);
                  return [...filtered, { itemId: item.itemId, itemName: item.itemName, price: newPrice }];
                });
              }}
              style={{ borderWidth: 1, padding: 8, borderRadius: 6 }}
            />
          </View>
        );
      })}

      {/* Calendar */}
      <Text style={{ marginTop: 16, fontWeight: 'bold', color: '#fb923c' }}>Select Off-Days</Text>
      <Calendar
        onDayPress={(day) => toggleDate(day.dateString)}
        markedDates={selectedDates}
      />

      <TouchableOpacity
        onPress={handleSubmit}
        style={{
          marginTop: 24,
          backgroundColor: '#fb923c',
          padding: 14,
          borderRadius: 8,
          alignItems: 'center'
        }}
      >
        <Text style={{ color: '#fff', fontWeight: 'bold' }}>Update Profile</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
