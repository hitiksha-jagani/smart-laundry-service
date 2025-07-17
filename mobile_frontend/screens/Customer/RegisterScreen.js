import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  Alert,
} from 'react-native';
import { Eye, EyeOff } from 'lucide-react-native';
import { useNavigation } from '@react-navigation/native';
import { BASE_URL } from '../../config'; // adjust path as needed

export default function RegisterScreen() {
  const navigation = useNavigation();

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    role: 'CUSTOMER',
    addresses: {
      name: '',
      areaName: '',
      pincode: '',
      cityId: '',
    },
  });

  const [roles, setRoles] = useState([]);
  const [cities, setCities] = useState([]);
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  useEffect(() => {
    fetch(`${BASE_URL}/roles`)
      .then((res) => res.json())
      .then(setRoles)
      .catch((err) => console.error('Error fetching roles:', err));

    fetch(`${BASE_URL}/cities`)
      .then((res) => res.json())
      .then(setCities)
      .catch((err) => console.error('Error fetching cities:', err));
  }, []);

  const handleChange = (name, value) => {
    setErrors((prev) => ({ ...prev, [name]: null }));

    if (['name', 'areaName', 'pincode', 'cityId'].includes(name)) {
      setFormData((prev) => ({
        ...prev,
        addresses: { ...prev.addresses, [name]: value },
      }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const validateFields = () => {
    const newErrors = {};
    if (!formData.firstName) newErrors.firstName = 'First name is required';
    if (!formData.phone || formData.phone.length !== 10)
      newErrors.phone = 'Valid 10-digit phone is required';
    if (formData.password.length < 6)
      newErrors.password = 'Password must be at least 6 characters';
    if (formData.password !== formData.confirmPassword)
      newErrors.confirmPassword = 'Passwords do not match';
    if (!formData.addresses.cityId)
      newErrors.cityId = 'City selection is required';
    return newErrors;
  };

  const handleSubmit = async () => {
    const fieldErrors = validateFields();
    if (Object.keys(fieldErrors).length > 0) {
      setErrors(fieldErrors);
      return;
    }

    try {
      const res = await fetch(`${BASE_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          addresses: {
            ...formData.addresses,
            cityId: parseInt(formData.addresses.cityId),
          },
        }),
      });

      if (res.ok) {
        Alert.alert('Success', 'Registration successful!');
        navigation.navigate('Login');
      } else {
        const data = await res.json();
        setErrors({ general: data.message || 'Registration failed' });
      }
    } catch (err) {
      console.error(err);
      setErrors({ general: 'Something went wrong' });
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Create Your Account</Text>

      <View style={styles.row}>
        <TextInput
          style={styles.input}
          placeholder="First Name"
          value={formData.firstName}
          onChangeText={(val) => handleChange('firstName', val)}
        />
        <TextInput
          style={styles.input}
          placeholder="Last Name"
          value={formData.lastName}
          onChangeText={(val) => handleChange('lastName', val)}
        />
      </View>

      <TextInput
        style={styles.input}
        placeholder="Email (optional)"
        value={formData.email}
        onChangeText={(val) => handleChange('email', val)}
      />
      <TextInput
        style={styles.input}
        placeholder="Phone"
        keyboardType="numeric"
        value={formData.phone}
        onChangeText={(val) => handleChange('phone', val)}
      />
      {errors.phone && <Text style={styles.error}>{errors.phone}</Text>}

      <View style={styles.row}>
        <View style={styles.passwordWrapper}>
          <TextInput
            placeholder="Password"
            secureTextEntry={!showPassword}
            style={styles.input}
            value={formData.password}
            onChangeText={(val) => handleChange('password', val)}
          />
          <TouchableOpacity
            onPress={() => setShowPassword((prev) => !prev)}
            style={styles.eyeIcon}
          >
            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          </TouchableOpacity>
        </View>

        <View style={styles.passwordWrapper}>
          <TextInput
            placeholder="Confirm Password"
            secureTextEntry={!showConfirmPassword}
            style={styles.input}
            value={formData.confirmPassword}
            onChangeText={(val) => handleChange('confirmPassword', val)}
          />
          <TouchableOpacity
            onPress={() => setShowConfirmPassword((prev) => !prev)}
            style={styles.eyeIcon}
          >
            {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          </TouchableOpacity>
        </View>
      </View>

      {errors.password && <Text style={styles.error}>{errors.password}</Text>}
      {errors.confirmPassword && (
        <Text style={styles.error}>{errors.confirmPassword}</Text>
      )}

      <Text style={styles.label}>Role</Text>
      <View style={styles.select}>
        {roles.map((role) => (
          <TouchableOpacity
            key={role}
            onPress={() => handleChange('role', role)}
            style={[
              styles.option,
              formData.role === role && styles.optionSelected,
            ]}
          >
            <Text>{role}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={styles.sectionTitle}>Address Info</Text>
      <TextInput
        style={styles.input}
        placeholder="Name"
        value={formData.addresses.name}
        onChangeText={(val) => handleChange('name', val)}
      />
      <TextInput
        style={styles.input}
        placeholder="Area Name"
        value={formData.addresses.areaName}
        onChangeText={(val) => handleChange('areaName', val)}
      />
      <TextInput
        style={styles.input}
        placeholder="Pincode"
        keyboardType="numeric"
        value={formData.addresses.pincode}
        onChangeText={(val) => handleChange('pincode', val)}
      />

      <Text style={styles.label}>City</Text>
      <View style={styles.select}>
        {cities.map((city) => (
          <TouchableOpacity
            key={city.cityId}
            onPress={() => handleChange('cityId', String(city.cityId))}
            style={[
              styles.option,
              formData.addresses.cityId === String(city.cityId) &&
                styles.optionSelected,
            ]}
          >
            <Text>{`${city.name}, ${city.state?.name}`}</Text>
          </TouchableOpacity>
        ))}
      </View>
      {errors.cityId && <Text style={styles.error}>{errors.cityId}</Text>}

      {errors.general && <Text style={styles.error}>{errors.general}</Text>}

      <TouchableOpacity style={styles.button} onPress={handleSubmit}>
        <Text style={styles.buttonText}>Register</Text>
      </TouchableOpacity>

      <Text style={styles.footer}>
        Already have an account?{' '}
        <Text style={styles.link} onPress={() => navigation.navigate('Login')}>
          Login here
        </Text>
      </Text>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
    paddingBottom: 50,
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  row: {
    flexDirection: 'row',
    gap: 10,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 12,
    borderRadius: 8,
    marginVertical: 10,
  },
  passwordWrapper: {
    flex: 1,
    position: 'relative',
  },
  eyeIcon: {
    position: 'absolute',
    right: 12,
    top: 20,
  },
  select: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    marginBottom: 10,
  },
  option: {
    padding: 10,
  },
  optionSelected: {
    backgroundColor: '#e0d5ff',
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginVertical: 10,
  },
  label: {
    marginTop: 10,
    fontWeight: '500',
  },
  error: {
    color: 'red',
    fontSize: 13,
    marginTop: -8,
    marginBottom: 8,
  },
  button: {
    backgroundColor: '#A566FF',
    padding: 14,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 12,
  },
  buttonText: {
    color: 'white',
    fontWeight: 'bold',
  },
  footer: {
    marginTop: 20,
    textAlign: 'center',
    fontSize: 14,
  },
  link: {
    color: '#A566FF',
    fontWeight: '500',
  },
});
