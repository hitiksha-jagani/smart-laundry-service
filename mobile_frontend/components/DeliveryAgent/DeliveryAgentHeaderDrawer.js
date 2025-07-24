// Author: Hitiksha Patel

import React, { useState } from 'react';
import {
  Image,
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Animated,
  Dimensions,
  StatusBar, 
  Platform
} from 'react-native';

import { useDrawer } from '../../context/DrawerContext';
import { useNavigation, useRoute } from '@react-navigation/native';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import profileIcon from '../../assets/avatar-icon.png';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const SCREEN_WIDTH = Dimensions.get('window').width;

const DeliveryAgentHeaderDrawer = ({ agent, showBackButton = false, onBackPress }) => {
  const { isDrawerOpen, setIsDrawerOpen } = useDrawer();
  const slideAnim = useState(new Animated.Value(SCREEN_WIDTH))[0];
  const navigation = useNavigation();
  const route = useRoute(); 
  const { logout } = useAuth();

 const openDrawer = () => {
    setIsDrawerOpen(true);
    Animated.timing(slideAnim, {
      toValue: 0,
      duration: 300,
      useNativeDriver: true,
    }).start();
  };

  const closeDrawer = () => {
    Animated.timing(slideAnim, {
      toValue: SCREEN_WIDTH,
      duration: 300,
      useNativeDriver: true,
    }).start(() => {
      setIsDrawerOpen(false);
    });
  };


  const toggleDrawer = () => {
    if (isDrawerOpen) {
      closeDrawer();
    } else {
      openDrawer();
    }
  };


  const handleLogout = () => {
    logout();
    navigation.navigate('Login');
  };

  const navigateTo = (targetRoute) => {
    closeDrawer();
    if (targetRoute !== route.name) {
      navigation.navigate(targetRoute);
    }
  };

  return (

    <>

      {/* Header */}
      <View style={styles.header}>

        <Text style={[styles.headerTitle, deliveryAgentStyles.h1Agent]}>Smart Laundry</Text>

        <TouchableOpacity onPress={toggleDrawer} style={styles.iconContainer}>

          <Ionicons
            name={isDrawerOpen ? 'close' : 'menu'}
            size={28}
            color="#64748B"
          />

        </TouchableOpacity>

      </View>

      {/* Overlay */}
      {isDrawerOpen && (
        <TouchableOpacity style={styles.overlay} onPress={closeDrawer} activeOpacity={1} />
      )}

      {showBackButton && (
        <TouchableOpacity onPress={onBackPress} style={styles.backButton}>
          <Ionicons name="arrow-back" size={24} color="white" />
        </TouchableOpacity>
      )}

      {/* Drawer */}
      <Animated.View
        style={[
          styles.drawer,
          { transform: [{ translateX: slideAnim }] },
        ]}
      >

        {/* Close Icon inside drawer */}
        <TouchableOpacity style={styles.drawerCloseIcon} onPress={closeDrawer}>
          <Ionicons name="close" size={26} color="#000" />
        </TouchableOpacity>

        <View style={styles.linkContainer}>

          <DrawerLink
            text="Deliveries"
            onPress={() => navigateTo('DeliveryPage')}
            isActive={route.name === 'DeliveryPage'} 
          />
          <DrawerLink
            text="Manage Availability"
            onPress={() => navigateTo('AvailabilityScreen')}
            isActive={route.name === 'AvailabilityScreen'}
          />
          <DrawerLink
            text="Payouts"
            onPress={() => navigateTo('DeliveryAgentPayout')}
            isActive={route.name === 'DeliveryAgentPayout'}
          />
          <DrawerLink
            text="Feedback"
            onPress={() => navigateTo('NotAvailableInThisVersion')}
            isActive={route.name === 'NotAvailable'}
          />
          <DrawerLink
            text="Raise a Ticket"
            onPress={() => navigateTo('NotAvailableInThisVersion')}
            isActive={route.name === 'NotAvailable'}
          />
          <DrawerLink
            text="My Profile"
            onPress={() => navigateTo('DeliveryAgentProfile')}
            isActive={route.name === 'DeliveryAgentProfile'}
          />

          <TouchableOpacity  onPress={handleLogout}>
            <Text style={[ styles.agentBtn]}>Logout</Text>
          </TouchableOpacity>

        </View>

        <View style={styles.sidebarBottom}>
          <View style={deliveryAgentStyles.hrAgent} />

          <View style={styles.userName}>
            <Image source={profileIcon} style={styles.sidebarIcon} />
            <Text style={[styles.agentName, deliveryAgentStyles.h2Agent]}>
              {agent?.firstName} {agent?.lastName}
            </Text>
          </View>

          <View style={deliveryAgentStyles.hrAgent} />

          <View style={styles.contactInfo}>
            <Text style={styles.agentDetail}>{agent?.phoneNo || 'Loading...'}</Text>
            <Text style={styles.agentDetail}>{agent?.email || 'Loading...'}</Text>
          </View>
        </View>

      </Animated.View>

    </>

  );

};

const DrawerLink = ({ text, onPress, isActive }) => (

  <TouchableOpacity onPress={onPress} style={[styles.drawerLink, isActive && styles.activeDrawerLink]}>
    
    <Text style={[styles.drawerLinkText, isActive && styles.activeDrawerLinkText]}>
      {text}
    </Text>

  </TouchableOpacity>

);

const styles = StyleSheet.create({

  agentBtn: {
        paddingVertical: 8,
        paddingHorizontal: 16,
        borderRadius: 8,
        fontSize: 18,
        fontWeight: '900',
        color: '#4ADE80',
        textAlign: 'center',
    },

  container: {
    flex: 1,
    paddingTop: Platform.OS === 'android' ? StatusBar.currentHeight : 44,
  },
  
  sidebar: {
    width: 250,
    backgroundColor: '#F0FDF4',
    height: '100%',
    padding: 16,
    position: 'fixed', 
    left: 0,
    top: 0,
    zIndex: 1000,
  },

  header: {
    position: 'absolute',
    top: Platform.OS === 'android' ? StatusBar.currentHeight : 44,
    left: 0,
    right: 0,
    height: 60,
    width: '100%',
    backgroundColor: '#F0FDF4',
    paddingHorizontal: 16,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    zIndex: 998,
    elevation: 4,
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
  top: Platform.OS === 'android' ? StatusBar.currentHeight : 44,
  width: SCREEN_WIDTH * 0.6,
  height: '100%',
  backgroundColor: '#F0FDF4',
  zIndex: 999,
  paddingHorizontal: 16,
  paddingTop: 60,
  overflow: 'hidden', 
},
linkContainer: {
  flexGrow: 0,
  flexShrink: 1,
  gap: 5
},


  title: {
    fontSize: 20,
    fontWeight: 'bold',
  },

  iconContainer: {
    padding: 4,
  },

  drawerCloseIcon: {
    position: 'absolute',
    top: 16,
    right: 16,
    zIndex: 3,
  },

  drawerLink: {
    paddingVertical: 12,
    paddingHorizontal: 12,
    alignItems: 'center',
  },

  drawerLinkText: {
    fontSize: 16,
    color: '#333',
  },

  activeDrawerLink: {
    backgroundColor: '#ACFFB2',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 12,
  },

  activeDrawerLinkText: {
    fontWeight: 'bold',
    color: '#047857',
  },

  sidebarIcon: {
    width: 24,
    height: 24,
    resizeMode: 'contain',
  },

  sidebarBottom: {
    position: 'absolute',
    bottom: 20,
    left: 16,
    right: 16,
    flexDirection: 'column',
    alignItems: 'center',
  },

  hr: {
    color: '#4ADE80'
  },

  userName: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    justifyContent: 'center',
    marginVertical: 10,
  },

  contactInfo: {
    alignItems: 'center',
  },

  agentName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },

  agentDetail: {
    fontSize: 15.5,
    fontWeight: '500',
    color: '#333',
    marginVertical: 1,
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
