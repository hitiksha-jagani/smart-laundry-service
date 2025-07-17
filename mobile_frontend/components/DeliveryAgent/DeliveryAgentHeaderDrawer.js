// Author: Hitiksha Patel
// Description: Mobile Header with slide-in drawer menu (React Native)

import React, { useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Image,
  StyleSheet,
  Animated,
  Dimensions,
  Linking,
} from 'react-native';

import { useNavigation } from '@react-navigation/native';
import { Ionicons } from '@expo/vector-icons'; 
import { useAuth } from '../../context/AuthContext';

const SCREEN_WIDTH = Dimensions.get('window').width;

const DeliveryAgentHeaderDrawer = ({ agent }) => {
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const slideAnim = useState(new Animated.Value(SCREEN_WIDTH))[0];
    const navigation = useNavigation();
    const { logout } = useAuth();

    const toggleDrawer = () => {

        if (isDrawerOpen) {

            Animated.timing(slideAnim, {

                toValue: SCREEN_WIDTH,
                duration: 300,
                useNativeDriver: true,

            }).start(() => setIsDrawerOpen(false));

        } else {

            setIsDrawerOpen(true);
            Animated.timing(slideAnim, {

                toValue: 0,
                duration: 300,
                useNativeDriver: true,

            }).start();

        }

    };

    const handleLogout = () => {
        logout();
        navigation.navigate('Login');
    };

    const navigateTo = (route) => {
        toggleDrawer();
        navigation.navigate(route);
    };

    return (

        <>

            {/* Header */}
            <View style={styles.header}>

                <TouchableOpacity onPress={toggleDrawer}>
                <Ionicons name="menu" size={28} color="#fff" />
                </TouchableOpacity>
                <Text style={styles.headerTitle}>Smart Laundry</Text>
                <View style={{ width: 28 }} /> {/* Spacer for symmetry */}

            </View>

            {/* Overlay */}
            {isDrawerOpen && (
                <TouchableOpacity style={styles.overlay} onPress={toggleDrawer} />
            )}

            {/* Drawer */}
            <Animated.View style={[styles.drawer, { transform: [{ translateX: slideAnim }] }]}>

                <View style={styles.header}>

                    <Text style={styles.headerTitle}>Smart Laundry</Text>

                    <TouchableOpacity onPress={toggleDrawer}>
                        <Ionicons name="menu" size={28} color="#64748B" />
                    </TouchableOpacity>

                </View>

                <View style={styles.linkContainer}>

                <DrawerLink text="Deliveries" onPress={() => navigateTo('DeliveriesSummary')} />
                <DrawerLink text="Manage Availability" onPress={() => navigateTo('ManageAvailability')} />
                <DrawerLink text="Payouts" onPress={() => navigateTo('PayoutsSummary')} />
                <DrawerLink text="Feedback" onPress={() => navigateTo('NotAvailable')} />
                <DrawerLink text="Raise a Ticket" onPress={() => navigateTo('NotAvailable')} />
                <DrawerLink text="My Profile" onPress={() => navigateTo('ProfileDetail')} />

                </View>

                <View style={styles.agentInfo}>

                <Text style={styles.agentName}>{agent?.firstName} {agent?.lastName}</Text>
                <Text style={styles.agentDetail}>{agent?.phoneNo}</Text>
                <Text style={styles.agentDetail}>{agent?.email}</Text>

                </View>

                <TouchableOpacity style={styles.logoutBtn} onPress={handleLogout}>
                <Text style={styles.logoutText}>Logout</Text>
                </TouchableOpacity>

            </Animated.View>

        </>

    );
};

const DrawerLink = ({ text, onPress }) => (
  <TouchableOpacity onPress={onPress} style={styles.drawerLink}>
    <Text style={styles.drawerLinkText}>{text}</Text>
  </TouchableOpacity>
);

const styles = StyleSheet.create({
  header: {
  paddingHorizontal: 20,
  paddingTop: 50,
  height: 60 + 50, // header height + top padding
  flexDirection: 'row',
  justifyContent: 'space-between',
  alignItems: 'center',
  backgroundColor: '#F0FDF4',
  borderBottomWidth: 1,
  borderBottomColor: '#ddd',
  elevation: 0, // Android shadow removal
  shadowOpacity: 0, // iOS shadow removal
},

headerTitle: {
  fontSize: 20,
  fontWeight: 'bold',
  color: '#64748B',
},

  overlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: SCREEN_WIDTH,
    height: '100%',
    backgroundColor: 'rgba(0,0,0,0.4)',
    zIndex: 1,
  },
  drawer: {
    position: 'absolute',
    right: 0,
    top: 0,
    width: SCREEN_WIDTH * 0.8,
    height: '100%',
    backgroundColor: '#fff',
    zIndex: 2,
    paddingHorizontal: 16,
    paddingTop: 60,
  },
  drawerHeader: {
    alignItems: 'flex-end',
  },
  linkContainer: {
    marginTop: 30,
  },
  drawerLink: {
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  drawerLinkText: {
    fontSize: 16,
    color: '#333',
  },
  agentInfo: {
    marginTop: 30,
    borderTopWidth: 1,
    borderTopColor: '#ccc',
    paddingTop: 16,
  },
  agentName: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 4,
  },
  agentDetail: {
    fontSize: 14,
    color: '#666',
  },
  logoutBtn: {
    marginTop: 30,
    backgroundColor: '#ff4d4f',
    padding: 12,
    borderRadius: 6,
    alignItems: 'center',
  },
  logoutText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default DeliveryAgentHeaderDrawer;
