// Author: Hitiksha Jagani
// Description: Edit profile screen for delivery agent (Mobile App)

import React, { useEffect, useState } from 'react';
import {
    View,
    Text,
    TextInput,
    StyleSheet,
    TouchableOpacity,
    ActivityIndicator,
    Alert,
    Dimensions,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import axios from 'axios';
import { useNavigation, useRoute } from '@react-navigation/native';
import * as DocumentPicker from 'expo-document-picker';
import { useAuth } from '../../context/AuthContext';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const screenWidth = Dimensions.get('window').width;

const Field = ({ label, value, onChangeText, editable = true, onPress }) => (

    <View style={styles.field}>

        <Text style={styles.label}>{label}</Text>

        {onPress ? (

            <TouchableOpacity onPress={onPress}>
                <Text style={styles.link}>Click Here...</Text>
            </TouchableOpacity>

        ) : editable ? (

            <TextInput
                style={styles.input}
                value={value}
                onChangeText={onChangeText}
                placeholder="Enter value"
            />
        ) : (

            <Text style={styles.value}>{value || 'N/A'}</Text>

        )}

  </View>

);

const EditAgentProfile = () => {
    const navigation = useNavigation();
    const route = useRoute();
    const agentData = route.params?.data;

    const { token, userId } = useAuth();
    const [user, setUser] = useState(null);
    const [formData, setFormData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [states, setStates] = useState([]);
    const [cities, setCities] = useState([]);
    const [selectedStateId, setSelectedStateId] = useState(null);

    const axiosInstance = axios.create({
        baseURL: 'http://192.168.1.7:8080',
        headers: { Authorization: `Bearer ${token}` }, 
    });

    useEffect(() => {
        axiosInstance
            .get(`/user-detail/${userId}`)
            .then((res) => {
                const userData = res.data;
                setUser(userData);
                setFormData({
                firstName: userData.firstName || '',
                lastName: userData.lastName || '',
                phoneNo: userData.phoneNo || '',
                email: userData.email || '',
                vehicleNumber: agentData.vehicleNumber || '',
                bankName: agentData.bankName || '',
                accountHolderName: agentData.accountHolderName || '',
                bankAccountNumber: agentData.bankAccountNumber || '',
                ifscCode: agentData.ifscCode || '',
                profilePhoto: agentData.profilePhoto || '',
                aadharCardPhoto: agentData.aadharCardPhoto || '',
                panCardPhoto: agentData.panCardPhoto || '',
                drivingLicensePhoto: agentData.drivingLicensePhoto || '',
                address: {
                    name: userData.address?.name || '',
                    areaName: userData.address?.areaName || '',
                    cityName: userData.address?.cityName || '',
                    pincode: userData.address?.pincode || '',
                },
            });
        })
        .catch((err) => {
            console.error('Error fetching user:', err);
            Alert.alert('Error', 'Failed to load profile');
        })
        .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        axios
        .get('http://192.168.1.7:8080/states')
        .then((res) => setStates(res.data))
        .catch((err) => console.error('Error fetching states', err));
    }, []);

    useEffect(() => {

        if (selectedStateId) {
            axios
                .get(`http://192.168.1.7:8080/cities/get/${selectedStateId}`)
                .then((res) => setCities(res.data))
                .catch((err) => console.error('Error fetching cities', err));
        } else {
            setCities([]);
        }

    }, [selectedStateId]);

    const handleChange = (name, value) => {

        if (['name', 'areaName', 'cityName', 'pincode'].includes(name)) {
            setFormData((prev) => ({
                ...prev,
                address: {
                ...prev.address,
                [name]: value,
                },
            }));
        } else {
            setFormData((prev) => ({ ...prev, [name]: value }));
        }

    };

    const pickFile = async (fieldName) => {

        const result = await DocumentPicker.getDocumentAsync({});
        if (result.assets?.[0]?.uri) {
            setFormData((prev) => ({ ...prev, [fieldName]: result.assets[0].uri }));
        }

    };

    const handleSave = () => {

        // axiosInstance
        // .put('/profile/detail/edit', formData)
        // .then(() => {
        //     Alert.alert('Success', 'Profile updated successfully');
        //     navigation.navigate('DeliveryAgentProfile');
        // })
        // .catch((err) => {
        //     console.error('Error saving profile:', err);
        //     Alert.alert('Error', 'Failed to update profile');
        // });

    };

    if (loading || !formData) {
        return (
        <View style={styles.centered}>
            <ActivityIndicator size="large" color="#4ADE80" />
        </View>
        );
    }

    return (

        <DeliveryAgentLayout>

            <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>
            
                <View style={deliveryAgentStyles.container}>

                    <Text style={[deliveryAgentStyles.h1Agent, styles.heading]}>EDIT PROFILE</Text>

                    <View style={styles.card}>

                        <View style={styles.box}>

                            {/* Basic Info */}
                            <View style={styles.row}>

                                <Field
                                    label="First Name"
                                    value={formData.firstName}
                                    onChangeText={(text) => setFormData({ ...formData, firstName: text })}
                                />

                                <Field
                                    label="Last Name"
                                    value={formData.lastName}
                                    onChangeText={(text) => setFormData({ ...formData, lastName: text })}
                                />

                            </View>

                            {/* Contact Info */}
                            <View style={styles.row}>

                                <View style={styles.field}>

                                    <Text style={styles.label}>Phone</Text>

                                    <Text style={styles.value} readOnly>
                                        {formData.phoneNo} 
                                    </Text>

                                </View>

                                <View style={styles.field}>

                                    <Text style={styles.label}>Email</Text>

                                    <Text style={styles.value} readOnly>
                                        {formData.email} 
                                    </Text>

                                </View>

                            </View>
            
                            {/* Address */}
                            <View style={styles.row}>

                                <View style={styles.field}>

                                    <Text style={styles.label}>Address</Text>

                                    <TextInput
                                        placeholder="Address Line"
                                        style={styles.uploadBtn}
                                        value={formData.address.name}
                                        onChangeText={(text) => handleChange('name', text)}
                                    />

                                    <TextInput
                                        placeholder="Area Name"
                                        style={styles.uploadBtn}
                                        value={formData.address.areaName}
                                        onChangeText={(text) => handleChange('areaName', text)}
                                    />

                                    <View style={styles.pickerWrapper}>

                                        <Picker
                                            selectedValue={selectedStateId}
                                            onValueChange={(itemValue) => setSelectedStateId(itemValue)}
                                            style={styles.picker}
                                        >
                                            {states.map((s) => (
                                            <Picker.Item key={s.stateId} label={s.name} value={s.stateId} />
                                            ))}
                                        </Picker>

                                    </View>

                                    <View style={styles.pickerWrapper}>

                                        <Picker
                                            selectedValue={formData.address.cityName}
                                            onValueChange={(value) => handleChange('cityName', value)}
                                            style={styles.picker}
                                            enabled={!!selectedStateId}
                                        >
                                            {cities.map((c) => (
                                            <Picker.Item key={c.cityId} label={c.name} value={c.name} />
                                            ))}
                                        </Picker>

                                    </View>

                                </View>

                            </View>
                            
                            {/* Bank Info */}
                            <View style={styles.row}>

                                <Field
                                    label="Vehicle Number"
                                    value={formData.vehicleNumber}
                                    onChangeText={(text) => setFormData({ ...formData, vehicleNumber: text })}
                                />

                                <Field
                                    label="Bank Name"
                                    value={formData.bankName}
                                    onChangeText={(text) => setFormData({ ...formData, bankName: text })}
                                />

                            </View>

                            <View style={styles.row}>

                                <Field
                                    label="Account Holder Name"
                                    value={formData.accountHolderName}
                                    onChangeText={(text) => setFormData({ ...formData, accountHolderName: text })}
                                />

                                <Field
                                    label="Bank Account Number"
                                    value={formData.bankAccountNumber}
                                    onChangeText={(text) => setFormData({ ...formData, bankAccountNumber: text })}
                                />

                            </View>

                            <View style={styles.row}>

                                <Field
                                    label="IFSC Code"
                                    value={formData.ifscCode}
                                    onChangeText={(text) => setFormData({ ...formData, ifscCode: text })}
                                />

                                <View style={styles.field}>

                                    <Text style={styles.label}>Aadhar Card</Text>

                                    <TouchableOpacity
                                        style={styles.uploadBtn}
                                        onPress={() => pickFile('aadharCardPhoto')}
                                    >
                                        <Text style={styles.uploadText}>
                                            Update Aadhar Card
                                        </Text>
                                    </TouchableOpacity>

                                </View>

                            </View>

                            <View style={styles.row}>

                                <View style={styles.field}>

                                    <Text style={styles.label}>Pan Card</Text>

                                    <TouchableOpacity
                                        style={styles.uploadBtn}
                                        onPress={() => pickFile('panCardPhoto')}
                                    >
                                        <Text style={styles.uploadText}>
                                            Update Pan Card
                                        </Text>
                                    </TouchableOpacity>

                                </View>

                                <View style={styles.field}>

                                    <Text style={styles.label}>Driving License</Text>

                                    <TouchableOpacity
                                        style={styles.uploadBtn}
                                        onPress={() => pickFile('drivingLicensePhoto')}
                                    >
                                        <Text style={styles.uploadText}>
                                            Update Driving License
                                        </Text>
                                    </TouchableOpacity>

                                </View>

                            </View>

                            <View style={styles.row}>

                                 <View style={styles.field}>

                                    <Text style={styles.label}>Profile Photo</Text>

                                    <TouchableOpacity
                                        style={styles.profilePhoto}
                                        onPress={() => pickFile('profilePhoto')}
                                    >
                                        <Text style={styles.uploadText}>
                                            Update Profile
                                        </Text>
                                    </TouchableOpacity>

                                </View>

                            </View>

                            {/* Buttons */}
                            <View style={styles.buttonRow}>

                                <TouchableOpacity
                                    style={styles.editBtn}
                                    onPress={handleSave()}
                                >
                                    <Text style={styles.btnText}>SAVE</Text> 
                                </TouchableOpacity>

                                <TouchableOpacity
                                    style={styles.resetBtn}
                                    onPress={() => navigation.navigate('DeliveryAgentProfile')}
                                >
                                    <Text style={styles.btnText}>CANCEL</Text>
                                </TouchableOpacity>
                                
                            </View>

                        </View>

                    </View>

                </View>

            </View>
        
        </DeliveryAgentLayout>

    );
};

const styles = StyleSheet.create({
    card: {
        backgroundColor: '#f0fdf4',
        padding: 20,
        borderColor: '#4ADE80',
        borderWidth: 1,
        borderRadius: 12,
        width: screenWidth * 0.9, 
        alignSelf: 'center', 
        marginVertical: '25',
    },
  container: {
    padding: 20,
    backgroundColor: '#fff',
    paddingHorizontal: 16,
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
  },
  heading: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#388E3C',
    marginBottom: 20,
    textAlign: 'center',
  },
  box: {
    backgroundColor: '#F0FDF4',
    gap: 15,     
    },

  row: {
    flexDirection: 'row',
    gap: 8,
  },
  fullRow: {
    flexDirection: 'column',
  },
  

  field: {
    flex: 1,
    fontSize: '1px',
    backgroundColor: '#E8F5E9',
    padding: 10,
    borderRadius: 10,
    borderColor: '#ccc',
    borderWidth: 1,
    marginBottom: 10,
  },
  label: {
    color: '#388E3C',
    fontWeight: '600',
    fontSize: 10,
    marginBottom: 4,
  },
  value: {
    color: '#555',
    fontSize: 13,
  },
  link: {
    fontSize: 13,
    color: '#2563EB',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 15,
  },
  editBtn: {
    backgroundColor: '#34D399',
    padding: 10,
    borderRadius: 8,
    width: '48%',
    alignItems: 'center',
    justifyContent: 'center'
  },
  resetBtn: {
    backgroundColor: '#F87171',
    padding: 5,
    borderRadius: 8,
    width: '48%',
    alignItems: 'center', 
  justifyContent: 'center', 
  },
  btnText: {
    color: '#fff',
    fontWeight: 'bold',
    textAlign: 'center'
  },
  uploadBtn: {
  flex: 1,
  backgroundColor: '#E8F5E9',
  padding: 10,
  borderRadius: 10,
  borderColor: '#ccc',
  borderWidth: 1,
  justifyContent: 'center',
  marginBottom: 10,
},
pickerWrapper: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    marginBottom: 10,
   
},

picker: {
  height: 50,
  color: '#000',
},
uploadText: {
  color: '#2563EB',
  fontSize: 13,
},

});

export default EditAgentProfile;
