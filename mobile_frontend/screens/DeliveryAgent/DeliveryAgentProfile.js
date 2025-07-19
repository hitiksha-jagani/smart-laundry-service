// Author: Hitiksha Jagani
// Description: Delivery Agent Profile Page (Mobile version - React Native)

import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  Linking,
} from 'react-native';
import axios from 'axios';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const Field = ({ label, value, onPress }) => (

    <View style={styles.field}>

        <Text style={styles.label}>{label}</Text>

        {onPress ? (

            <TouchableOpacity onPress={onPress}>
                <Text style={styles.link}>{value}</Text>
            </TouchableOpacity>

        ) : (

            <Text style={styles.value}>{value || 'N/A'}</Text>

        )}

    </View>

);

const DeliveryAgentProfile = () => {
    const [user, setUser] = useState(null);
    const [data, setData] = useState({});
    const [loading, setLoading] = useState(true);
    const { token, userId } = useAuth();
    const navigation = useNavigation();

    useEffect(() => {
        const fetchAllData = async () => {

            const axiosInstance = axios.create({
                baseURL: 'http://localhost:8080',
                headers: { Authorization: `Bearer ${token}` },
            });

            try {
                const [userRes, dataRes] = await Promise.all([
                    axiosInstance.get(`/user-detail/${userId}`),
                    axiosInstance.get('/profile/detail'),
                ]);

                setUser(userRes.data);
                setData(dataRes.data || {});
            } catch (error) {
                Alert.alert('Error fetching profile data');
            } finally {
                setLoading(false);
            }
        };

        fetchAllData();
    }, []);

    const handleFileClick = (type) => {

        const userId = data.userId;

        if (type && userId) {
            const url = `http://localhost:8080/image/agent/${type}/${userId}`;
            Linking.openURL(url);
        } else {
            Alert.alert('Invalid image type or user ID');
        }

    };

    if (loading) {

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

                        <Text style={deliveryAgentStyles.h1Agent}>MY DETAILS</Text>

                        <View style={{ marginHorizontal: 20 }}>

                            <View style={styles.box}>

                                <View style={styles.row}>

                                    <Field label="First Name" value={data.firstName} />
                                    <Field label="Last Name" value={data.lastName} />

                                </View>

                                <View style={styles.row}>

                                    <Field label="Phone" value={data.phone} />
                                    <Field label="Email" value={data.email} />

                                </View>

                                <View style={styles.fullRow}>

                                    <Field
                                        label="Address"
                                        value={
                                        data.address
                                            ? `${data.address.name}, ${data.address.areaName}, ${data.address.cityName} - ${data.address.pincode}`
                                            : 'N/A'
                                        }
                                    />

                                </View>

                                <View style={styles.row}>

                                    <Field label="Date Of Birth" value={data.dateOfBirth} />
                                    <Field label="Vehicle Number" value={data.vehicleNumber} />

                                </View>

                                <View style={styles.row}>

                                    <Field label="Gender" value={data.gender} />
                                    <Field label="Bank Account No" value={data.bankAccountNumber} />

                                </View>

                                <View style={styles.row}>

                                    <Field label="Account Holder Name" value={data.accountHolderName} />
                                    <Field label="Bank Name" value={data.bankName} />

                                </View>

                                <View style={styles.row}>

                                    <Field
                                        label="IFSC Code"
                                        value={data.ifscCode}
                                    />
                                    
                                    <Field
                                        label="PAN Card"
                                        value={data.panCardPhoto ? "Click here..." : "N/A"}
                                        onPress={() => handleFileClick('pan')}
                                    />

                                </View>

                                <View style={styles.row}>

                                    <Field
                                        label="Aadhaar Card"
                                        value={data.aadharCardPhoto ? "Click here..." : "N/A"}
                                        onPress={() => handleFileClick('aadhar')}
                                    />
                                    <Field
                                        label="Driving License"
                                        value={data.drivingLicensePhoto ? "Click here..." : "N/A"}
                                        onPress={() => handleFileClick('license')}
                                    />

                                </View>

                                <View style={styles.row}>

                                    <Field
                                        label="Profile"
                                        value={data.profilePhoto ? "Click here..." : "N/A"}
                                        onPress={() => handleFileClick('profile')}
                                    />

                                </View>

                                <View style={styles.buttonRow}>

                                    <TouchableOpacity
                                        style={styles.editBtn}
                                        onPress={() => navigation.navigate('EditAgentProfile', { data })}
                                    >
                                        <Text style={styles.btnText}>EDIT</Text>
                                    </TouchableOpacity>

                                    <TouchableOpacity
                                        style={styles.resetBtn}
                                        onPress={() => navigation.navigate('ChangePassword')}
                                    >
                                        <Text style={styles.btnText}>CHANGE PASSWORD</Text>
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

export default DeliveryAgentProfile;
