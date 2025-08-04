import React, { useEffect, useState } from 'react';
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    Image,
    StyleSheet,
    Alert,
    ActivityIndicator,
    Dimensions,
} from 'react-native';
import axiosInstance from '../../utils/axiosInstance';
import { useNavigation, useRoute } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';
import eyeOpen from '../../assets/eye-icon.png';
import eyeClosed from '../../assets/eye-icon.png'; 

const screenWidth = Dimensions.get('window').width;

const ChangePasswordScreen = () => {
    const [user, setUser] = useState(null);
    const navigation = useNavigation();
    const { token, userId } = useAuth();
    const [loading, setLoading] = useState(true);
    const [formData, setFormData] = useState({
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [visible, setVisible] = useState({
        oldPassword: false,
        newPassword: false,
        confirmPassword: false,
    });
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
        setToast({ message: '', type: '', visible: false });
        }, 3000);
    };

    useEffect(() => {
        const fetchUser = async () => {
        
            try {
                const [userRes] = await Promise.all([
                    axiosInstance.get(`/user-detail/${userId}`),
                ]);

                setUser(userRes.data);
            } catch (error) {
                console.error('Error fetching user:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, []); 

    const handleChange = (name, value) => {
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const toggleVisibility = (field) => {
        setVisible((prev) => ({ ...prev, [field]: !prev[field] }));
    };

    const validateFields = () => {
        const passwordRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;

        const { oldPassword, newPassword, confirmPassword } = formData;

        if (!oldPassword || !newPassword || !confirmPassword) {
            showToast("Please fill all fields", "error");
            return false;
        }

        if (newPassword !== confirmPassword) {
            showToast("Password and confirm password do not match", "error");
            return false;
        }

        if (!passwordRegex.test(newPassword)) {
            showToast("New password must be at least 8 characters long, include 1 uppercase letter and 1 special character", "error");
            return false;
        }

        return true;
    };

    const handleSave = async () => {

        if (!validateFields()) return;

        try {
        const res = await axiosInstance.put(
            'http://192.168.1.7:8080/profile/detail/change-password',
            formData
        );

            Alert.alert('Success', res.data);
            navigation.navigate('ProfileDetail');
        } catch (err) {
            console.error('Change password error:', err);
            const msg = err.response?.data?.message || 'Failed to change password.';
            Alert.alert('Error', msg);
        }
    };

    if (loading) return <ActivityIndicator size="large" style={{ flex: 1 }} />;

    return (

        <DeliveryAgentLayout>

            <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>
            
                <View style={deliveryAgentStyles.container}>

                    <Text style={[deliveryAgentStyles.h1Agent, styles.heading]}>CHANGE PASSWORD</Text>

                    <View style={styles.card}>
                    
                        <View style={styles.box}>

                            {['oldPassword', 'newPassword', 'confirmPassword'].map((field) => (
                                <View key={field} style={styles.inputContainer}>
                                <Text style={styles.label}>
                                    {field === 'oldPassword' && 'Old Password'}
                                    {field === 'newPassword' && 'New Password'}
                                    {field === 'confirmPassword' && 'Confirm Password'}
                                </Text>

                                <View style={styles.passwordInputWrapper}>
                                    <TextInput
                                    secureTextEntry={!visible[field]}
                                    style={styles.input}
                                    value={formData[field]}
                                    onChangeText={(text) => handleChange(field, text)}
                                    placeholder={`Enter ${field}`}
                                    />
                                    <TouchableOpacity onPress={() => toggleVisibility(field)}>
                                    <Image source={visible[field] ? eyeOpen : eyeClosed} style={styles.eyeIcon} />
                                    </TouchableOpacity>
                                </View>
                                </View>
                            ))}

                            <View style={styles.buttonRow}>

                                <TouchableOpacity style={styles.editBtn} onPress={handleSave}>
                                    <Text style={styles.btnText}>SAVE</Text>
                                </TouchableOpacity>

                                <TouchableOpacity style={styles.resetBtn} onPress={() => navigation.navigate('DeliveryAgentProfile')}>
                                    <Text style={styles.btnText}>CANCEL</Text>
                                </TouchableOpacity>

                            </View>

                        </View>

                    </View>

                </View>

            </View>

            {toast.visible && (
                <View style={[styles.toast, toast.type === 'error' && styles.toastError]}>
                    <Text style={styles.toastText}>{toast.message}</Text>
                </View>
            )}
        
        </DeliveryAgentLayout>

    );
};

const styles = StyleSheet.create({
    heading: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#388E3C',
    marginBottom: 20,
    textAlign: 'center',
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
  container: {
    padding: 20,
    justifyContent: 'center',
    flexGrow: 1,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 30,
  },
  inputContainer: {
    marginBottom: 20,
  },
  label: {
    marginBottom: 6,
    fontSize: 16,
  },
  passwordInputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#999',
    borderRadius: 8,
    paddingHorizontal: 10,
  },
  input: {
    flex: 1,
    height: 45,
  },
  eyeIcon: {
    width: 20,
    height: 20,
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
    fontSize: 13,
  },
});

export default ChangePasswordScreen;
