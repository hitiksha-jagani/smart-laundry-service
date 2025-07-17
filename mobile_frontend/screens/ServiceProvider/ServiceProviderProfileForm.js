import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import * as DocumentPicker from 'expo-document-picker';
import axios from '../../utils/axiosInstance';
import { jwtDecode } from 'jwt-decode';
import { Picker } from '@react-native-picker/picker';
import { styles } from '../../styles/ServiceProviderStyles';
export default function ServiceProviderProfileForm({ userId: propUserId }) {
  const [userId, setUserId] = useState(propUserId || '');
  const [loading, setLoading] = useState(true);

  const [formData, setFormData] = useState({
    businessName: '',
    businessLicenseNumber: '',
    gstNumber: '',
    needOfDeliveryAgent: false,
    schedulePlans: [],
    bankAccount: {
      bankName: '',
      ifscCode: '',
      bankAccountNumber: '',
      accountHolderName: '',
    },
    priceDTO: [],
  });

  const [items, setItems] = useState([]);
  const [services, setServices] = useState([]);
  const [subServices, setSubServices] = useState([]);
  const [schedulePlanOptions, setSchedulePlanOptions] = useState([]);

  const [selectedService, setSelectedService] = useState('');
  const [selectedSubService, setSelectedSubService] = useState('');
  const [selectedItem, setSelectedItem] = useState('');
  const [itemPrice, setItemPrice] = useState('');

  const [fileUploads, setFileUploads] = useState({
    aadharCard: null,
    panCard: null,
    utilityBill: null,
    profilePhoto: null,
  });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (propUserId) {
      setUserId(propUserId);
      setLoading(false);
    } else if (token) {
      try {
        const decoded = jwtDecode(token);
        if (decoded.exp * 1000 < Date.now()) throw new Error('Token expired');
        if (decoded.id) {
          setUserId(decoded.id);
          setLoading(false);
        } else {
          throw new Error('ID missing in token');
        }
      } catch (e) {
        console.error('Invalid or expired token', e);
        localStorage.removeItem('token');
        Alert.alert('Session expired. Please log in again.');
      }
    }

    axios.get('/items/all').then((res) => {
      const allItems = res.data;
      setItems(allItems);

      const uniqueServices = [];
      const uniqueSubServices = [];

      allItems.forEach((item) => {
        if (
          item.service &&
          !uniqueServices.some((s) => s.serviceId === item.service.serviceId)
        ) {
          uniqueServices.push(item.service);
        }
        if (
          item.subService &&
          !uniqueSubServices.some(
            (s) => s.subServiceId === item.subService.subServiceId
          )
        ) {
          uniqueSubServices.push(item.subService);
        }
      });

      setServices(uniqueServices);
      setSubServices(uniqueSubServices);
    });

    axios
      .get('/schedule-plans')
      .then((res) => setSchedulePlanOptions(res.data));
  }, [propUserId]);

  const filteredItems = items.filter((item) => {
    const matchService = selectedService
      ? item.service?.serviceId === selectedService
      : true;
    const matchSubService = selectedSubService
      ? item.subService?.subServiceId === selectedSubService
      : true;
    return matchService && matchSubService;
  });

  const pickFile = async (type) => {
    const result = await DocumentPicker.getDocumentAsync({ type: '*/*' });
    if (!result.canceled) {
      setFileUploads({ ...fileUploads, [type]: result.assets[0] });
    }
  };

  const handlePriceAdd = () => {
    const item = items.find((i) => i.itemId === selectedItem);
    if (item && itemPrice) {
      const exists = formData.priceDTO.some(
        (p) => p.item.itemId === selectedItem
      );
      if (!exists) {
        setFormData((prev) => ({
          ...prev,
          priceDTO: [
            ...prev.priceDTO,
            { item: { itemId: item.itemId }, price: parseInt(itemPrice) },
          ],
        }));
        setSelectedItem('');
        setItemPrice('');
      } else {
        Alert.alert('Item already added.');
      }
    }
  };

  const handleSubmit = async () => {
    if (!userId) return Alert.alert('User ID not found. Please log in again.');

    const data = new FormData();
    data.append(
      'data',
      JSON.stringify(formData)
    );
    if (fileUploads.aadharCard) data.append('aadharCard', fileUploads.aadharCard);
    if (fileUploads.utilityBill) data.append('utilityBill', fileUploads.utilityBill);
    if (fileUploads.profilePhoto) data.append('profilePhoto', fileUploads.profilePhoto);
    if (fileUploads.panCard) data.append('panCard', fileUploads.panCard);

    try {
      await axios.post(`/sp/complete-sp-profile/${userId}`, data, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      Alert.alert('Success', 'Profile submitted successfully!');
    } catch (err) {
      Alert.alert('Error', 'Profile submission failed.');
      console.error(err);
    }
  };

  if (loading) return <ActivityIndicator style={{ marginTop: 50 }} size="large" />;

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.header}>Service Provider Profile</Text>

      <TextInput
        placeholder="Business Name"
        style={styles.input}
        onChangeText={(text) =>
          setFormData({ ...formData, businessName: text })
        }
      />
      <TextInput
        placeholder="License Number"
        style={styles.input}
        onChangeText={(text) =>
          setFormData({ ...formData, businessLicenseNumber: text })
        }
      />
      <TextInput
        placeholder="GST Number"
        style={styles.input}
        onChangeText={(text) => setFormData({ ...formData, gstNumber: text })}
      />

      {/* Schedule Plan */}
      <Text style={styles.subHeader}>Schedule Plans</Text>
      <View style={styles.checkboxContainer}>
        {schedulePlanOptions.map((plan) => (
          <TouchableOpacity
            key={plan}
            style={styles.checkbox}
            onPress={() => {
              const selected = formData.schedulePlans.includes(plan);
              setFormData((prev) => ({
                ...prev,
                schedulePlans: selected
                  ? prev.schedulePlans.filter((p) => p !== plan)
                  : [...prev.schedulePlans, plan],
              }));
            }}
          >
            <Text>{formData.schedulePlans.includes(plan) ? '✅' : '⬜'} {plan}</Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Item Price */}
      <Text style={styles.subHeader}>Add Item Price</Text>
      <Picker selectedValue={selectedService} onValueChange={setSelectedService}>
        <Picker.Item label="Select Service" value="" />
        {services.map((s) => (
          <Picker.Item key={s.serviceId} label={s.serviceName} value={s.serviceId} />
        ))}
      </Picker>

      <Picker selectedValue={selectedSubService} onValueChange={setSelectedSubService}>
        <Picker.Item label="Select Sub-Service" value="" />
        {subServices.map((sub) => (
          <Picker.Item
            key={sub.subServiceId}
            label={sub.subServiceName}
            value={sub.subServiceId}
          />
        ))}
      </Picker>

      <Picker selectedValue={selectedItem} onValueChange={setSelectedItem}>
        <Picker.Item label="Select Item" value="" />
        {filteredItems.map((item) => (
          <Picker.Item key={item.itemId} label={item.itemName} value={item.itemId} />
        ))}
      </Picker>

      <TextInput
        placeholder="Price"
        keyboardType="numeric"
        value={itemPrice}
        onChangeText={setItemPrice}
        style={styles.input}
      />
      <TouchableOpacity onPress={handlePriceAdd} style={styles.button}>
        <Text style={styles.buttonText}>Add Price</Text>
      </TouchableOpacity>

      {/* Show Added Items */}
      {formData.priceDTO.map((p) => {
        const item = items.find((i) => i.itemId === p.item.itemId);
        return (
          <View key={p.item.itemId} style={styles.itemRow}>
            <Text>
              {item?.itemName || 'Item'} — ₹{p.price}
            </Text>
            <TouchableOpacity
              onPress={() =>
                setFormData((prev) => ({
                  ...prev,
                  priceDTO: prev.priceDTO.filter(
                    (x) => x.item.itemId !== p.item.itemId
                  ),
                }))
              }
            >
              <Text style={{ color: 'red' }}>Remove</Text>
            </TouchableOpacity>
          </View>
        );
      })}

      {/* Bank Details */}
      <Text style={styles.subHeader}>Bank Details</Text>
      {['accountHolderName', 'bankName', 'ifscCode', 'bankAccountNumber'].map((field) => (
        <TextInput
          key={field}
          placeholder={field.replace(/([A-Z])/g, ' $1')}
          style={styles.input}
          onChangeText={(text) =>
            setFormData({
              ...formData,
              bankAccount: { ...formData.bankAccount, [field]: text },
            })
          }
        />
      ))}

      {/* File Uploads */}
      <Text style={styles.subHeader}>Upload Documents</Text>
      {['aadharCard', 'panCard', 'utilityBill', 'profilePhoto'].map((type) => (
        <TouchableOpacity
          key={type}
          style={styles.uploadButton}
          onPress={() => pickFile(type)}
        >
          <Text>{fileUploads[type]?.name || `Upload ${type}`}</Text>
        </TouchableOpacity>
      ))}

      <TouchableOpacity style={styles.submitButton} onPress={handleSubmit}>
        <Text style={styles.submitButtonText}>Submit Profile</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
