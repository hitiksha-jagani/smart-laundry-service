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
    ScrollView,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import axios from 'axios';
import { useNavigation, useRoute } from '@react-navigation/native';
import * as DocumentPicker from 'expo-document-picker';
import { useAuth } from '../../context/AuthContext';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

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
        baseURL: 'http://localhost:8080',
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
        .get('http://localhost:8080/states')
        .then((res) => setStates(res.data))
        .catch((err) => console.error('Error fetching states', err));
    }, []);

    useEffect(() => {

        if (selectedStateId) {
            axios
                .get(`http://localhost:8080/cities/get/${selectedStateId}`)
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

        axiosInstance
        .put('/profile/detail/edit', formData)
        .then(() => {
            Alert.alert('Success', 'Profile updated successfully');
            navigation.navigate('DeliveryAgentProfile');
        })
        .catch((err) => {
            console.error('Error saving profile:', err);
            Alert.alert('Error', 'Failed to update profile');
        });

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

                    <Text style={deliveryAgentStyles.h1Agent}>EDIT PROFILE</Text>

                    <View style={{ marginHorizontal: 20 }}>

                        <View style={styles.box}>

                            <View style={styles.row}>

                                <Text>First Name</Text>
                                <TextInput
                                    // style={styles.input}
                                    value={formData.firstName}
                                    onChangeText={(text) => handleChange('firstName', text)}
                                />

                            {/* </View>
                            <View style={styles.row}> */}
                            
                                <Text>Last Name</Text>
                                <TextInput
                                    // style={styles.input}
                                    value={formData.lastName}
                                    onChangeText={(text) => handleChange('lastName', text)}
                                />

                            {/* <Field label="First Name" value={data.firstName} />
                            <Field label="Last Name" value={data.lastName} /> */}
                            </View>

                            <View className="agent-box">
            {/* Basic Info */}
            <View className="agent-grid-row">
                <View className="agent-field">
                <Text>First Name</Text>
                <TextInput
                    style={styles.input}
                    value={formData.firstName}
                    onChangeText={(text) => handleChange('firstName', text)}
                />
                </View>

                <View className="agent-field">
                <Text>Last Name</Text>
                <TextInput
                    style={styles.input}
                    value={formData.lastName}
                    onChangeText={(text) => handleChange('lastName', text)}
                />
                </View>
            </View>

                        </View>

            

            {/* Contact Info */}
            <View className="agent-grid-row">
                <View className="agent-field">
                <Text>Phone</Text>
                <TextInput style={styles.input} value={user.phoneNo} editable={false} />
                </View>
                <View className="agent-field">
                <Text>Email</Text>
                <TextInput style={styles.input} value={user.email} editable={false} />
                </View>
            </View>

            {/* Address */}
            <Text style={styles.subHeading}>Address</Text>
            <View className="agent-field">
                <TextInput
                placeholder="Address Line"
                style={styles.input}
                value={formData.address.name}
                onChangeText={(text) => handleChange('name', text)}
                />
                <TextInput
                placeholder="Area"
                style={styles.input}
                value={formData.address.areaName}
                onChangeText={(text) => handleChange('areaName', text)}
                />
                <Picker
                selectedValue={selectedStateId}
                onValueChange={(itemValue) => setSelectedStateId(itemValue)}
                style={styles.input}
                >
                <Picker.Item label="Select State" value="" />
                {states.map((s) => (
                    <Picker.Item key={s.stateId} label={s.name} value={s.stateId} />
                ))}
                </Picker>
                <Picker
                selectedValue={formData.address.cityName}
                onValueChange={(value) => handleChange('cityName', value)}
                style={styles.input}
                enabled={!!selectedStateId}
                >
                {cities.map((c) => (
                    <Picker.Item key={c.cityId} label={c.name} value={c.name} />
                ))}
                </Picker>
                <TextInput
                placeholder="Pincode"
                style={styles.input}
                value={formData.address.pincode}
                onChangeText={(text) => handleChange('pincode', text)}
                keyboardType="numeric"
                />
            </View>

            {/* Bank Info */}
            <View className="agent-grid-row">
                <View className="agent-field">
                <Text>Vehicle Number</Text>
                <TextInput
                    style={styles.input}
                    value={formData.vehicleNumber}
                    onChangeText={(text) => handleChange('vehicleNumber', text)}
                />
                </View>
                <View className="agent-field">
                <Text>Bank Name</Text>
                <TextInput
                    style={styles.input}
                    value={formData.bankName}
                    onChangeText={(text) => handleChange('bankName', text)}
                />
                </View>
            </View>

            <View className="agent-grid-row">
                <View className="agent-field">
                <Text>Account Holder Name</Text>
                <TextInput
                    style={styles.input}
                    value={formData.accountHolderName}
                    onChangeText={(text) => handleChange('accountHolderName', text)}
                />
                </View>
                <View className="agent-field">
                <Text>Bank Account Number</Text>
                <TextInput
                    style={styles.input}
                    value={formData.bankAccountNumber}
                    onChangeText={(text) => handleChange('bankAccountNumber', text)}
                />
                </View>
            </View>

            <View className="agent-grid-row">
                <View className="agent-field">
                <Text>IFSC Code</Text>
                <TextInput
                    style={styles.input}
                    value={formData.ifscCode}
                    onChangeText={(text) => handleChange('ifscCode', text)}
                />
                </View>
            </View>

            {/* File Uploads */}
            {['aadharCardPhoto', 'panCardPhoto', 'drivingLicensePhoto', 'profilePhoto'].map((field) => (
                <TouchableOpacity key={field} style={styles.uploadBtn} onPress={() => pickFile(field)}>
                <Text>Upload {field.replace(/([A-Z])/g, ' $1')}</Text>
                </TouchableOpacity>
            ))}

            {/* Buttons */}
            <View style={styles.buttonRow}>
                <TouchableOpacity onPress={handleSave} style={styles.saveBtn}>
                <Text style={styles.btnText}>SAVE</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => navigation.goBack()} style={styles.cancelBtn}>
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
//   container: {
//     padding: 20,
//   },
//   centered: {
//     flex: 1,
//     justifyContent: 'center',
//   },
//   heading: {
//     fontSize: 22,
//     fontWeight: 'bold',
//     color: '#34D399',
//     marginBottom: 20,
//   },
//   subHeading: {
//     fontSize: 18,
//     marginVertical: 10,
//     fontWeight: '600',
//   },
//   input: {
//     borderColor: '#ccc',
//     borderWidth: 1,
//     padding: 10,
//     borderRadius: 8,
//     marginBottom: 12,
//   },
//   uploadBtn: {
//     padding: 10,
//     backgroundColor: '#E0F2FE',
//     marginVertical: 5,
//     borderRadius: 6,
//     alignItems: 'center',
//   },
//   buttonRow: {
//     flexDirection: 'row',
//     justifyContent: 'space-between',
//     marginTop: 20,
//   },
//   saveBtn: {
//     backgroundColor: '#34D399',
//     padding: 12,
//     borderRadius: 8,
//     width: '48%',
//     alignItems: 'center',
//   },
//   cancelBtn: {
//     backgroundColor: '#F87171',
//     padding: 12,
//     borderRadius: 8,
//     width: '48%',
//     alignItems: 'center',
//   },
//   btnText: {
//     color: '#fff',
//     fontWeight: 'bold',
//   },
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
    padding: 20,
    borderRadius: 12,
    borderColor: '#4ADE80',
    borderWidth: 1,
    gap: 15,
    marginTop: 50,           
    marginHorizontal: 20        
    },

  row: {
    flexDirection: 'row',
    gap: 10,
  },
  fullRow: {
    flexDirection: 'column',
  },
  field: {
    flex: 1,
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
    fontSize: 15,
    marginBottom: 4,
  },
  value: {
    color: '#555',
    fontSize: 16,
  },
  link: {
    fontSize: 16,
    color: '#2563EB',
    textDecorationLine: 'underline',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 15,
  },
  editBtn: {
    backgroundColor: '#34D399',
    padding: 12,
    borderRadius: 8,
    width: '48%',
    alignItems: 'center',
  },
  resetBtn: {
    backgroundColor: '#F87171',
    padding: 12,
    borderRadius: 8,
    width: '48%',
    alignItems: 'center',
  },
  btnText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default EditAgentProfile;
