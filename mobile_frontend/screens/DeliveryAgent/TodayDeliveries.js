// Author: Hitiksha Jagani
// Description: Today's delivery list in delivery agent mobile dashboard.

import React, { useState } from 'react';
import {
    View,
    Text,
    Button,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Dimensions,
} from 'react-native';
import { useRoute } from '@react-navigation/native';
import { Ionicons } from '@expo/vector-icons'; 
import TodayDeliveryCard from './TodayDeliveryCard';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const TodayDeliveries = () => {
    const route = useRoute();
    const { user, data = [] } = route.params || {};

    const [toast, setToast] = useState({ message: '', type: '', visible: false });
    const [currentIndex, setCurrentIndex] = useState(0);

    const hasPrev = currentIndex > 0;
    const hasNext = currentIndex < data.length - 1;

    return (

        <DeliveryAgentLayout>

            <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>

                <View style={deliveryAgentStyles.container}>

                    <Text style={styles.heading}>📦 TODAY'S DELIVERIES</Text>

                    {data.length === 0 ? (

                        <View style={styles.emptyBox}>

                            <Ionicons name="mail-open-outline" size={64} color="#ccc" />
                            <Text style={styles.noDeliveryText}>No Today's Deliveries Available</Text>
                            <Text style={styles.subText}>Once new deliveries are assigned to you, they’ll appear here.</Text>
                        
                        </View>

                    ) : (

                        <View style={{marginTop: '50px'}}>

                            <TodayDeliveryCard delivery={data[currentIndex]} />

                            <View style={styles.navButtons}>

                                <TouchableOpacity
                                    style={[styles.navBtn, !hasPrev && styles.disabledBtn]}
                                    onPress={() => setCurrentIndex(currentIndex - 1)}
                                    disabled={!hasPrev}
                                >

                                    <Text style={styles.navText}>⬅ Prev</Text>

                                </TouchableOpacity>

                                <TouchableOpacity
                                    style={[styles.navBtn, !hasNext && styles.disabledBtn]}
                                    onPress={() => setCurrentIndex(currentIndex + 1)}
                                    disabled={!hasNext}
                                >

                                    <Text style={styles.navText}>Next ➡</Text>

                                </TouchableOpacity>

                            </View>

                            <Text style={styles.orderStatus}>
                                Order {currentIndex + 1} of {data.length}
                            </Text>

                        </View>

                    )}

                    {toast.visible && (

                        <View style={[styles.toast, toast.type === 'error' ? styles.toastError : styles.toastSuccess]}>
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
    marginTop: '1',
    fontSize: 22,
    fontWeight: 'bold',
    color: '#047857',
    marginBottom: 15,
    textAlign: 'center',
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


export default TodayDeliveries;
