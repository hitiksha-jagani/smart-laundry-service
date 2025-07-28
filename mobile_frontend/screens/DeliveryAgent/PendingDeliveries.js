// PendingDeliveries.js
import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    Alert,
    ScrollView,
} from 'react-native';
import axios from 'axios';
import { MaterialIcons } from '@expo/vector-icons';
import PendingDeliveryCard from './PendingDeliveryCard';
import { useAuth } from '../../context/AuthContext';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const PendingDeliveries = ({ route }) => {
  const { user, data = [] } = route?.params || {};
  const [orders, setOrders] = useState(data);
  const { token, userId } = useAuth();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
        setToast({ message: '', type: '', visible: false });
        }, 3000);
    };

    const handleAccept = async (orderId) => {
        try {
            const response = await axios.post(
                `http://localhost:8080/deliveries/accept/${orderId}`,
                {},
                { headers: { Authorization: `Bearer ${token}` } }
            );
            showToast('Order accepted successfully.');
            setOrders((prev) => prev.filter((o) => o.orderId !== orderId));
        } catch (error) {
            const msg = error.response?.data?.message || 'Error accepting order.';
            showToast(msg, 'error');
        }
    };

    const handleReject = async (orderId) => {
        try {
            const response = await axios.post(
                `http://localhost:8080/deliveries/reject/${orderId}`,
                {},
                { headers: { Authorization: `Bearer ${token}` } }
            );
            showToast('Order rejected successfully.');
            setOrders((prev) => prev.filter((o) => o.orderId !== orderId));
        } catch (error) {
            const msg = error.response?.data?.message || 'Error rejecting order.';
            showToast(msg, 'error');
        }
    };

    const hasPrev = currentIndex > 0;
    const hasNext = currentIndex < orders.length - 1;

    return (

        <DeliveryAgentLayout> 

            <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>

                <View style={deliveryAgentStyles.container}>

                    <Text style={[deliveryAgentStyles.h1Agent, styles.heading]}>📦 Pending Deliveries</Text>

                    {orders.length === 0 ? (

                        <View style={styles.emptyBox}>

                            <MaterialIcons name="inbox" size={64} color="#ccc" />

                            <Text style={styles.emptyTitle}>No Deliveries Available</Text>

                            <Text style={styles.emptySubtitle}>
                                Once new deliveries are assigned to you, they’ll appear here.
                            </Text>

                        </View>

                    ) : (

                        <View style={{marginTop: '20'}}>

                            <PendingDeliveryCard
                                data={orders[currentIndex]}
                                onAccept={handleAccept}
                                onReject={handleReject}
                            />

                            <View style={styles.navButtons}>

                                <TouchableOpacity
                                    style={[styles.navBtn, !hasPrev && styles.disabledBtn]}
                                    onPress={() => hasPrev && setCurrentIndex(currentIndex - 1)}
                                    disabled={!hasPrev}
                                >

                                    <Text style={styles.navText}>⬅ Prev</Text>
                                
                                </TouchableOpacity>

                                <TouchableOpacity
                                    style={[styles.navBtn, !hasNext && styles.disabledBtn]}
                                    onPress={() => hasNext && setCurrentIndex(currentIndex + 1)}
                                    disabled={!hasNext}
                                >
                                    
                                    <Text style={styles.navText}>Next ➡</Text>

                                </TouchableOpacity>

                            </View>

                            <Text style={styles.orderStatus}>
                                Order {currentIndex + 1} of {orders.length}
                            </Text>

                        </View>

                    )}

                    {toast.visible && (
                        <View style={[styles.toast, toast.type === 'error' && styles.toastError]}>
                        <Text style={styles.toastText}>{toast.message}</Text>
                        </View>
                    )}

                </View>

            </View>

        </DeliveryAgentLayout>

    );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#ecfdf5',
    flexGrow: 1,
  },
  heading: {
    marginTop: '30px'
  },
  emptyBox: {
    alignItems: 'center',
    padding: 30,
    backgroundColor: '#fff',
    borderStyle: 'dashed',
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 16,
    marginVertical: 30,
  },
  emptyTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginTop: 10,
    color: '#555',
  },
  emptySubtitle: {
    marginTop: 8,
    color: '#777',
    textAlign: 'center',
  },
  navButtons: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 15,
    marginTop: 20,
  },
  navBtn: {
    backgroundColor: '#4ade80',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 10,
  },
  disabledBtn: {
    backgroundColor: '#d1fae5',
  },
  navText: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 16,
  },
  orderStatus: {
    textAlign: 'center',
    marginTop: 10,
    fontSize: 18,
    fontWeight: '700',
    color: '#374151',
    marginBottom: 50,
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

export default PendingDeliveries;
