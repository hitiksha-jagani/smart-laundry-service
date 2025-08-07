// PendingDeliveryCard.js
import React, { useEffect, useState } from 'react';
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Linking,
    Dimensions
} from 'react-native';
import axios from 'axios'; 
import { useAuth } from '../../context/AuthContext';
import { FontAwesome5 } from '@expo/vector-icons';

const screenWidth = Dimensions.get('window').width;

const PendingDeliveryCard = ({ data, onAccept, onReject }) => {
    const [agentLocation, setAgentLocation] = useState(null);
    const { token } = useAuth();

    const {
        orderId, deliveryType, deliveryEarning, km,
        pickupDate, pickupTime, pickupName, pickupPhone, pickupAddress,
        deliveryName, deliveryPhone, deliveryAddress,
        bookingItemDTOList, totalQuantity
    } = data;

    useEffect(() => {
        const fetchAgentLocation = async () => { 
        try {
            const response = await axios.get(`${BASE_URL}/delivery-agent/get-location`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },});
            setAgentLocation(response.data);
        } catch (error) {
            console.error('Failed to fetch agent location:', error);
        }
        };
        fetchAgentLocation();
    }, []);

    const openRoute = (origin, destination) => {
        const url = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}`;
        Linking.openURL(url);
    };

    const groupedItems = bookingItemDTOList?.reduce((grouped, item) => {
        if (!grouped[item.serviceName]) grouped[item.serviceName] = [];
        grouped[item.serviceName].push(item);
        return grouped;
    }, {});

  return (

        <>

            <View style={styles.card}>

                <Text style={styles.title}>Overview</Text>

                <View style={styles.contactBox}>

                    <Text style={styles.contactTitle}>Delivery Type</Text>
                    <Text style={styles.summaryValue}>{deliveryType}</Text>

                </View>
                <View style={styles.contactBox}>

                    <Text style={styles.contactTitle}>Pickup Date Time</Text>
                    <Text style={styles.summaryValue}>{pickupDate} {pickupTime}</Text>

                </View>

                <Text style={styles.title}>Contact Info</Text>

                <View style={styles.contactBox}>

                    <Text style={styles.contactTitle}>Pickup Contact</Text>
                    
                    <ContactInfo name={pickupName} phone={pickupPhone} address={pickupAddress} />

                    <TouchableOpacity
                        style={styles.routeBtn}
                        onPress={() => agentLocation && openRoute(`${agentLocation.latitude},${agentLocation.longitude}`, pickupAddress)}
                    >
                        <Text style={styles.routeText}>View Route (You ➝ Pickup)</Text>
                    </TouchableOpacity>

                </View>

                <View style={styles.contactBox}>
                
                    <Text style={styles.contactTitle}>Delivery Contact</Text>

                    <ContactInfo name={deliveryName} phone={deliveryPhone} address={deliveryAddress} />
                    
                    <TouchableOpacity
                        style={styles.routeBtn}
                        onPress={() => openRoute(pickupAddress, deliveryAddress)}
                    >
                        <Text style={styles.routeText}>View Route (Pickup ➝ Delivery)</Text>
                    
                    </TouchableOpacity>
                </View>

                <Text style={styles.title}>Item List</Text>

                {Object.entries(groupedItems || {}).map(([serviceName, items]) => (

                    <View key={serviceName} style={styles.serviceBox}>

                        <Text style={styles.serviceHeader}>{serviceName}</Text>

                        <View style={styles.itemRow}>

                            {items.map((item, index) => (

                                <View key={index} style={styles.itemCard}>

                                    <Text style={styles.itemName}>🧴 {item.itemName}</Text>
                                    <Text style={styles.itemQuantity}>Qty: {item.quantity}</Text>
                                
                                </View>

                            ))}

                        </View>

                    </View>

                ))}

                <Text style={styles.title}>Totals</Text>

                <View style={styles.summaryRow}>

                    <View style={styles.summaryBox}><Text>Total Items: {totalQuantity}</Text></View>
                    {/* <View style={styles.summaryBox}><Text>Total KM: {km}</Text></View> */}
                    <View style={styles.summaryBox}><Text>Earnings: ₹{deliveryEarning}</Text></View>
                
                </View>

                <View style={styles.actions}>

                    <TouchableOpacity style={styles.acceptBtn} onPress={() => onAccept(orderId)}>
                        <Text style={styles.actionText}>Accept</Text>
                    </TouchableOpacity>

                    <TouchableOpacity style={styles.rejectBtn} onPress={() => onReject(orderId)}>
                        <Text style={styles.actionText}>Reject</Text>
                    </TouchableOpacity>

                </View>

            </View>

        </>

    );

};

const ContactInfo = ({ name, phone, address }) => (
    <View style={styles.contactDetails}>
        <InfoLine icon="user" text={name} />
        <InfoLine icon="phone-alt" text={phone} />
        <InfoLine icon="map-marker-alt" text={address} />
    </View>
);

const InfoLine = ({ icon, text }) => (
    <View style={styles.infoLine}>
        <FontAwesome5 name={icon} size={14} color="#215a47" />
        <Text style={styles.infoValue}>{text}</Text>
    </View>
);

const styles = StyleSheet.create({
    scrollContainer: {
        flex: 1,
    },

    scrollContent: {
        padding: 20,
        paddingBottom: 80, 
    },

    card: {
        backgroundColor: '#f0fdf4',
        padding: 20,
        borderRadius: 12,
        width: screenWidth * 0.9, 
        alignSelf: 'center', 
        marginTop: 15,
    },

    title: {
        fontSize: 18,
        fontWeight: '700',
        color: '#388E3C',
        backgroundColor: '#ecfdf5',
        padding: 10,
        borderLeftWidth: 5,
        borderLeftColor: '#10b981',
        borderRadius: 8,
        marginVertical: 10,
    },

    summaryRow: {
        flexDirection: 'column',
        gap: 10,
    },

    summaryBox: {
        width: '100%',
        backgroundColor: '#ecfdf5',
        borderColor: '#a7f3d0',
        borderWidth: 1,
        borderRadius: 8,
        padding: 10,
    },

    summaryLabel: {
        color: '#047857',
        fontSize: 13,
    },

    summaryValue: {
        fontWeight: '600',
        color: '#065f46',
        fontSize: 15,
    },

    contactBox: {
        backgroundColor: '#fff',
        borderColor: '#d1fae5',
        borderWidth: 1,
        borderRadius: 10,
        padding: 15,
        marginVertical: 10,
    },

    contactTitle: {
        fontSize: 16,
        fontWeight: '600',
        color: '#10b981',
        borderBottomColor: '#d1fae5',
        borderBottomWidth: 2,
        marginBottom: 10,
    },

    contactDetails: {
        gap: 10,
    },

    infoLine: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 10,
        marginVertical: 2,
    },

    infoValue: {
        marginLeft: 10,
        color: '#374151',
        flexShrink: 1,
    },

    routeBtn: {
        backgroundColor: '#4ADE80',
        padding: 10,
        borderRadius: 8,
        marginTop: 15,
        alignItems: 'center',
    },

    routeText: {
        color: '#fff',
        fontWeight: '600',
    },

    serviceBox: {
        backgroundColor: '#fff',
        borderColor: '#d1fae5',
        borderWidth: 1,
        borderRadius: 10,
        padding: 15,
        marginVertical: 10,
    },

    serviceHeader: {
        fontSize: 16,
        fontWeight: '600',
        color: '#047857',
        borderBottomColor: '#a7f3d0',
        borderBottomWidth: 2,
        marginBottom: 10,
    },

    itemRow: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        gap: 10, 
    },

    itemCard: {
        backgroundColor: '#ecfdf5',
        borderColor: '#bbf7d0',
        borderWidth: 1,
        padding: 10,
        borderRadius: 8,
        marginBottom: 10,
        width: '48%', 
    },

    itemName: {
        fontSize: 15,
        fontWeight: '600',
        color: '#065f46',
    },

    itemQuantity: {
        color: '#059669',
        fontSize: 14,
    },

    actions: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        marginTop: 20,
    },

    acceptBtn: {
        backgroundColor: '#4ADE80',
        padding: 12,
        borderRadius: 10,
        width: '40%',
        alignItems: 'center',
    },

    rejectBtn: {
        backgroundColor: '#ef4444',
        padding: 12,
        borderRadius: 10,
        width: '40%',
        alignItems: 'center',
    },

    actionText: {
        color: '#fff',
        fontWeight: '700',
        fontSize: 16,
    },

});

export default PendingDeliveryCard;
