import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const RequestPendingScreen = () => {
    return (
        <View style={styles.container}>
            <Text style={styles.title}>Request Under Review</Text>
            <Text style={styles.message}>
                Your request to become a Delivery Agent is still pending approval.
            </Text>
            <Text style={styles.message}>
                Once approved, you will be able to access your dashboard.
            </Text>
        </View>
    );
};

export default RequestPendingScreen;

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 20,
        backgroundColor: '#fff',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 20,
        color: '#4CAF50',
    },
    message: {
        fontSize: 16,
        textAlign: 'center',
        marginBottom: 10,
        color: '#333',
    },
});
