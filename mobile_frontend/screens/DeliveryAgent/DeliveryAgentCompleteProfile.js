// Author: Hitiksha Jagani
// Description: Complete profile page for delivery agent

import React, { useEffect, useState } from 'react';
import { View, Text, TextInput, Dimensions, ActivityIndicator,  Alert, StyleSheet, Pressable, ScrollView, TouchableOpacity } from 'react-native';
import * as DocumentPicker from 'expo-document-picker';
import DateTimePickerModal from "react-native-modal-datetime-picker";
import RNPickerSelect from 'react-native-picker-select';
import axios from 'axios';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';
import { BASE_URL } from '../../config';
import { Platform } from 'react-native';

const screenWidth = Dimensions.get('window').width;

const DeliveryAgentCompleteProfile = () => {

    const [loading, setLoading] = useState(false);
    const { token, } = useAuth();
    const navigation = useNavigation();
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const [isDatePickerVisible, setDatePickerVisibility] = useState(false);
    const [selectedDate, setSelectedDate] = useState(null);

    const showDatePicker = () => {
        setDatePickerVisibility(true);
    };

    const hideDatePicker = () => {
        setDatePickerVisibility(false);
    };

    const handleConfirm = (date) => {
        const formatted = date.toISOString().split('T')[0];
        setSelectedDate(formatted);
        handleInputChange('dateOfBirth', formatted); 
        hideDatePicker();
    };
    
    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 3000);
    };

    const genderItems = [
        { label: 'Male', value: 'MALE' },
        { label: 'Female', value: 'FEMALE' },
        { label: 'Other', value: 'OTHER' }
    ];

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
        headers: { Authorization: `Bearer ${token}` }, 
    });

    const [formValues, setFormValues] = useState({
        dateOfBirth: '',
        vehicleNumber: '',
        bankName: '',
        accountHolderName: '',
        bankAccountNumber: '',
        ifscCode: '',
        gender: '',
    });

    const [files, setFiles] = useState({
        aadharCard: null,
        panCard: null,
        drivingLicense: null,
        profilePhoto: null,
    });

    const handleInputChange = (name, value) => {
        setFormValues((prev) => ({ ...prev, [name]: value }));
    };

    const pickFile = async (name, mimeTypes) => {
        try {
            const result = await DocumentPicker.getDocumentAsync({
                type: mimeTypes || '*/*',
                copyToCacheDirectory: true,
            });

            if (result?.assets?.length > 0) {
                const file = result.assets[0]; 
                console.log('Selected file:', file);

                setFiles((prev) => ({
                    ...prev,
                    [name]: {
                        uri: file.uri,
                        name: file.name,
                        mimeType: file.mimeType || 'application/octet-stream',
                    },
                }));
            } else if (result.type === 'success') {
                console.log('Legacy file:', result);
                setFiles((prev) => ({
                    ...prev,
                    [name]: {
                        uri: result.uri, 
                        name: result.name,
                        mimeType: result.mimeType || 'application/octet-stream',
                    },
                }));
            }
        } catch (err) {
            console.log('Error picking file:', err);
        }
    };

    const handleSubmit = async () => {

        try {

            const {
                dateOfBirth,
                vehicleNumber,
                bankName,
                accountHolderName,
                bankAccountNumber,
                ifscCode,
                gender,
            } = formValues;

            // Required text fields validation
            const missingFields = [];
            if (!dateOfBirth) missingFields.push('Date of Birth');
            if (!vehicleNumber) missingFields.push('Vehicle Number');
            if (!bankName) missingFields.push('Bank Name');
            if (!accountHolderName) missingFields.push('Account Holder Name');
            if (!bankAccountNumber) missingFields.push('Bank Account Number');
            if (!ifscCode) missingFields.push('IFSC Code');

            const ifscRegex = /^[A-Z]{4}0[A-Z0-9]{6}$/;
            if (!ifscRegex.test(ifscCode)) {
                showToast('Invalid IFSC code format', 'error');
                return;
            }

            if (!gender) missingFields.push('Gender');

            if (missingFields.length > 0) {
                showToast(`Please fill in: ${missingFields.join(', ')}`, 'error');
                return;
            }

            const requiredFiles = ['aadharCard', 'drivingLicense', 'profilePhoto'];
            for (const key of requiredFiles) {
                if (!files[key]) {
                    showToast(`Please upload ${key} before submitting.`, 'error');
                    return;
                }
            }

            const formData = new FormData();

            formData.append('data', JSON.stringify(formValues));

            Object.keys(files).forEach((key) => {
                if (files[key]) {
                formData.append(key, {
                    uri: files[key].uri,
                    name: files[key].name,
                    type: files[key].mimeType || 'application/octet-stream',
                });
            }});

            const response = await axios.post(`${BASE_URL}/profile/complete`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${token}`,
                },
            });

            showToast('Congratulations! Your request to become a Delivery Agent has been submitted for review.');

            setFormValues({
                dateOfBirth: '',
                vehicleNumber: '',
                bankName: '',
                accountHolderName: '',
                bankAccountNumber: '',
                ifscCode: '',
                gender: '',
            });

            setFiles({
                aadharCard: null,
                panCard: null,
                drivingLicense: null,
                profilePhoto: null,
            });

            navigation.navigate('CustomerDashboard');

        } catch (error) {
            const message =
            error?.response?.data?.message ||
            error?.response?.data?.error ||
            error?.message ||
            'Something went wrong.';

            showToast(message.toString(), 'error');
        }
    };

    const handleLogout = () => {
        Alert.alert(
            "Cancel Profile Completion?",
            "Are you sure you do not want to send a request to become a delivery agent? You can send it whenever you want.",
            [
                { text: "Cancel", style: "cancel" },
                {
                    text: "Yes",
                    onPress: () => {
                        if (navigation && navigation.navigate) {
                            navigation.navigate('Login');
                        } else {
                            console.warn('Navigation is undefined');
                        }
                    }
                },
            ],
            { cancelable: true }
            );
    };

    if (loading) {
        return (
            <View style={styles.centered}>
                <ActivityIndicator size="large" color="#4ADE80" />
            </View>
        );
    }

    return (

        <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>

            <ScrollView
              style={deliveryAgentStyles.deliveryAgentBody}
              contentContainerStyle={styles.scrollContent}
              showsHorizontalScrollIndicator={false}
            >

                <Text style={[deliveryAgentStyles.h1Agent, styles.heading]}>COMPLETE PROFILE</Text>

                <View style={styles.card}>

                    <View style={styles.box}>
        
                        <View>

                            <View className="agentField">

                                <Text>Date Of Birth <Text style={{ color: 'red' }}>*</Text></Text>
                                {/* <TouchableOpacity onPress={showDatePicker} className="border p-2 rounded bg-white">
                                <Text style={styles.uploadBtn}>{selectedDate || 'Select date'}</Text>
                                </TouchableOpacity>
                                <DateTimePickerModal
                                    isVisible={isDatePickerVisible}
                                    mode="date"
                                    onConfirm={handleConfirm}
                                    onCancel={hideDatePicker}
                                /> */}
                                {Platform.OS === 'web' ? (
                                    <TextInput
                                        style={styles.uploadBtn}
                                        placeholder="YYYY-MM-DD"
                                        value={formValues.dateOfBirth}
                                        onChangeText={(val) => {
                                            setSelectedDate(val);
                                            handleInputChange('dateOfBirth', val);
                                        }}
                                    />
                                ) : (
                                    <>
                                        <TouchableOpacity onPress={showDatePicker} style={styles.uploadBtn}>
                                            <Text>{selectedDate || 'Select date'}</Text>
                                        </TouchableOpacity>
                                        <DateTimePickerModal
                                            isVisible={isDatePickerVisible}
                                            mode="date"
                                            onConfirm={handleConfirm}
                                            onCancel={hideDatePicker}
                                        />
                                    </>
                                )}

                            </View>

                            <View className="agentField">

                                <Text>Vehicle Number <Text style={{ color: 'red' }}>*</Text></Text>
                                <TextInput
                                    className="agent-input-field"
                                    value={formValues.vehicleNumber}
                                    style={styles.uploadBtn}
                                    onChangeText={(val) => handleInputChange('vehicleNumber', val)}
                                />

                            </View>

                            <View className="agentField">

                                <Text>Bank Name <Text style={{ color: 'red' }}>*</Text></Text>
                                <TextInput
                                    className="agent-input-field"
                                    value={formValues.bankName}
                                    style={styles.uploadBtn}
                                    onChangeText={(val) => handleInputChange('bankName', val)}
                                />

                            </View>

                            <View className="agentField">

                                <Text>Account Holder Name <Text style={{ color: 'red' }}>*</Text></Text>
                                <TextInput
                                    className="agent-input-field"
                                    value={formValues.accountHolderName}
                                    style={styles.uploadBtn}
                                    onChangeText={(val) => handleInputChange('accountHolderName', val)}
                                />

                            </View>

                            <View className="agentField">

                                <Text>Bank Account Number <Text style={{ color: 'red' }}>*</Text></Text>
                                <TextInput
                                    className="agent-input-field"
                                    value={formValues.bankAccountNumber}
                                    style={styles.uploadBtn}
                                    onChangeText={(val) => handleInputChange('bankAccountNumber', val)}
                                    keyboardType="number-pad"
                                />

                            </View>

                            <View className="agentField">

                                <Text>IFSC Code <Text style={{ color: 'red' }}>*</Text></Text>
                                <TextInput
                                    className="agent-input-field"
                                    value={formValues.ifscCode}
                                    style={styles.uploadBtn}
                                    onChangeText={(val) => handleInputChange('ifscCode', val)}
                                />

                            </View>

                            <View className="agentField">
                                
                                <Text>Gender <Text style={{ color: 'red' }}>*</Text></Text>
                                <View style={{
                                    backgroundColor: '#E8F5E9',
                                    borderWidth: 1,
                                    borderColor: '#ccc',
                                    borderRadius: 10,
                                    marginVertical: 10,
                                }}>
                                    <RNPickerSelect
                                        onValueChange={(value) => handleInputChange('gender', value)}
                                        items={genderItems}
                                        placeholder={{ label: 'Select Gender', value: null }}
                                        style={{
                                            placeholder: {
                                                color: '#888',
                                            }
                                        }}
                                    />
                                </View>

                            </View>

                            <View className="agentField">

                                <Text>Aadhar Card <Text style={{ color: 'red' }}>*</Text></Text>
                                <Pressable onPress={() => pickFile('aadharCard', ['image/*', 'application/pdf'])}>
                                    <Text style={styles.uploadBtn}>
                                        {files.aadharCard ? files.aadharCard.name : 'Choose File'}
                                    </Text>
                                </Pressable>

                            </View>

                            <View className="agentField">

                                <Text>PAN Card</Text>
                                <Pressable onPress={() => pickFile('panCard', ['image/*', 'application/pdf'])}>
                                    <Text style={styles.uploadBtn}>
                                        {files.panCard ? files.panCard.name : 'Choose File'}
                                    </Text>
                                </Pressable>

                            </View>

                            <View className="agentField">

                                <Text>Driving License <Text style={{ color: 'red' }}>*</Text></Text>
                                <Pressable onPress={() => pickFile('drivingLicense', ['image/*', 'application/pdf'])}>
                                    <Text style={styles.uploadBtn}>
                                        {files.drivingLicense ? files.drivingLicense.name : 'Choose File'}
                                    </Text>
                                </Pressable>

                            </View>

                            <View className="agentField">

                                <Text>Profile Photo <Text style={{ color: 'red' }}>*</Text></Text>
                                <Pressable onPress={() => pickFile('profilePhoto', ['image/*'])}>
                                    <Text style={styles.uploadBtn}>
                                        {files.profilePhoto ? files.profilePhoto.name : 'Choose File'}
                                    </Text>
                                </Pressable>

                            </View>

                            <View style={styles.buttonRow}>

                                <TouchableOpacity
                                    style={styles.editBtn}
                                    onPress={handleSubmit}
                                >
                                    <Text style={styles.btnText}>SAVE</Text> 
                                </TouchableOpacity>

                                <TouchableOpacity
                                    style={styles.resetBtn}
                                    onPress={handleLogout}
                                >
                                    <Text style={styles.btnText}>CANCEL</Text>
                                </TouchableOpacity>
                                
                            </View>

                        </View>

                    </View>

                </View>

                {toast.visible && (
        
                    <View style={[styles.toast, toast.type === 'error' ? styles.toastError : styles.toastSuccess]}>
                        <Text style={styles.toastText}>{toast.message}</Text>
                    </View>
        
                )}

            </ScrollView>

        </View>

    );
};

export default DeliveryAgentCompleteProfile;

const styles = StyleSheet.create({

    container: {
        flex: 1,
        overflow: 'hidden', 
    },

    heading: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#388E3C',
        marginBottom: 20,
        textAlign: 'center',
    },

    scrollContent: {
        paddingTop: 80, 
        paddingHorizontal: 16, 
        paddingBottom: 16, 
    },

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

    box: {
        backgroundColor: '#F0FDF4',
        gap: 15,     
    },

    agentField: {
        margin: 10,
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
    toast: {
        position: 'absolute',
        bottom: 30,
        left: '10%',
        right: '10%',
        backgroundColor: '#4ade80',
        padding: 15,
        borderRadius: 10,
        zIndex: 1000,
        alignItems: 'center',
        elevation: 4,
    },

    toastError: {
        backgroundColor: '#ef4444',
    },

    toastText: {
        color: 'white',
        fontWeight: '600',
        fontSize: 16,
    },

});