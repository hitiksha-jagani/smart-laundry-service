import React from 'react';
import {
  createDrawerNavigator,
  DrawerContentScrollView,
  DrawerItem,
} from '@react-navigation/drawer';
import { View, Text, StyleSheet, Image } from 'react-native';
import { Feather, MaterialIcons } from '@expo/vector-icons';

// Screens (adjusted to match your App.js stack)
import ProviderDashboardScreen from '../screens/ServiceProvider/ProviderDashboardScreen';
import PendingOrdersScreen from '../screens/ServiceProvider/PendingOrdersScreen';
import ActiveOrdersScreen from '../screens/ServiceProvider/ActiveOrdersScreen';
import DeliveredOrdersScreen from '../screens/ServiceProvider/DeliveredOrdersScreen';
import OtpVerificationOrdersScreen from '../screens/ServiceProvider/OtpVerificationOrdersScreen';
import VerifyPickupOtpScreen from '../screens/ServiceProvider/VerifyPickupOtpScreen';
import VerifyDeliveryOtpScreen from '../screens/ServiceProvider/VerifyDeliveryOtpScreen';
import EditServiceProviderProfileScreen from '../screens/ServiceProvider/EditServiceProviderProfileScreen';

const Drawer = createDrawerNavigator();

const CustomDrawerContent = (props) => {
  return (
    <DrawerContentScrollView {...props} contentContainerStyle={{ flex: 1 }}>
      <View style={styles.profileSection}>
        <Image
          source={require('../../assets/avatar.png')}
          style={styles.profileImage}
        />
        <Text style={styles.name}>SmartLaundry Provider</Text>
        <Text style={styles.email}>provider@example.com</Text>
      </View>

      <View style={styles.menuSection}>
        <DrawerItem
          label="Dashboard"
          icon={({ color, size }) => <Feather name="home" color={color} size={size} />}
          onPress={() => props.navigation.navigate('ProviderDashboard')}
        />
        <DrawerItem
          label="Pending Orders"
          icon={({ color, size }) => <Feather name="clock" color={color} size={size} />}
          onPress={() => props.navigation.navigate('PendingOrders')}
        />
        <DrawerItem
          label="Active Orders"
          icon={({ color, size }) => <Feather name="activity" color={color} size={size} />}
          onPress={() => props.navigation.navigate('ActiveOrders')}
        />
        <DrawerItem
          label="Completed Orders"
          icon={({ color, size }) => <Feather name="check-circle" color={color} size={size} />}
          onPress={() => props.navigation.navigate('DeliveredOrders')}
        />
        <DrawerItem
          label="Verify OTPs"
          icon={({ color, size }) => <MaterialIcons name="vpn-key" color={color} size={size} />}
          onPress={() => props.navigation.navigate('OtpVerificationOrders')}
        />
        <DrawerItem
          label="Verify Pickup OTP"
          icon={({ color, size }) => <Feather name="key" color={color} size={size} />}
          onPress={() => props.navigation.navigate('VerifyPickupOtp')}
        />
        <DrawerItem
          label="Verify Delivery OTP"
          icon={({ color, size }) => <MaterialIcons name="vpn-key" color={color} size={size} />}
          onPress={() => props.navigation.navigate('VerifyDeliveryOtp')}
        />
        <DrawerItem
          label="Edit Profile"
          icon={({ color, size }) => <Feather name="edit-3" color={color} size={size} />}
          onPress={() => props.navigation.navigate('EditServiceProviderProfile')}
        />
      </View>
    </DrawerContentScrollView>
  );
};

const ProviderDrawerNavigator = () => {
  return (
    <Drawer.Navigator
      drawerContent={(props) => <CustomDrawerContent {...props} />}
      screenOptions={{
        headerShown: true,
        drawerActiveTintColor: '#f97316',
        drawerInactiveTintColor: '#333',
        drawerLabelStyle: { fontSize: 14 },
      }}
    >
      <Drawer.Screen name="ProviderDashboard" component={ProviderDashboardScreen} />
      <Drawer.Screen name="PendingOrders" component={PendingOrdersScreen} />
      <Drawer.Screen name="ActiveOrders" component={ActiveOrdersScreen} />
      <Drawer.Screen name="DeliveredOrders" component={DeliveredOrdersScreen} />
      <Drawer.Screen name="OtpVerificationOrders" component={OtpVerificationOrdersScreen} />
      <Drawer.Screen name="VerifyPickupOtp" component={VerifyPickupOtpScreen} />
      <Drawer.Screen name="VerifyDeliveryOtp" component={VerifyDeliveryOtpScreen} />
      <Drawer.Screen name="EditServiceProviderProfile" component={EditServiceProviderProfileScreen} />
    </Drawer.Navigator>
  );
};

export default ProviderDrawerNavigator;

const styles = StyleSheet.create({
  profileSection: {
    padding: 20,
    backgroundColor: '#f97316',
    alignItems: 'center',
  },
  profileImage: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: '#fff',
    marginBottom: 8,
  },
  name: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 16,
  },
  email: {
    color: '#fff',
    fontSize: 12,
  },
  menuSection: {
    flex: 1,
    paddingTop: 10,
  },
});
