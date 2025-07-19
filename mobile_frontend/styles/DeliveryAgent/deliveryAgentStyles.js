// styles/DeliveryAgent/deliveryAgentStyles.js
// Common css for delivery agent dashboard

import { StyleSheet } from 'react-native';

export const deliveryAgentStyles = StyleSheet.create({

    deliveryAgentBody: {
        backgroundColor: '#E8F5E9',
        color: '#64748B',
    },

    dashboardHeading: {
        backgroundColor: '#F0FDF4',
        borderRadius: 10,
        padding: 5,
        margin: 5,  
        height: 60,
        width: '98%',         
        maxWidth: 500,       
        alignSelf: 'center',  
        justifyContent: 'center', 
        alignItems: 'center',  
    },

    container: {
        flex: 1,
        justifyContent: 'flex-start',
        alignItems: 'center',
        paddingTop: 20,
        paddingBottom: 50,
        paddingHorizontal: 16,
    },

    interFont: {
        fontFamily: 'Inter',
        fontWeight: '200',
        fontStyle: 'normal',
    },
    
    h1Agent: {
        fontWeight: '900',
        fontSize: 20,
        color: '#388E3C',
        textAlign: 'center',
    },

    h2Agent: {
        fontWeight: '700',
        fontSize: 15,
        color: '#388E3C',
        textAlign: 'center'
    },

    h3Agent: {
        fontWeight: '600',
        fontSize: 11,
    },

    h4Agent: {
        fontWeight: '500',
        fontSize: 8,
        color: '#1C1C1C',
    },

    inputAgent: {
        padding: 10,
        marginTop: 10,
        width: 250,
        borderRadius: 5,
        borderWidth: 1,
        borderColor: '#4ADE80',
        backgroundColor: '#E8F5E9',
    },

    inputField: {
        width: '100%',
        padding: 8,
        borderRadius: 10,
        borderWidth: 1,
        borderColor: '#ccc',
        fontSize: 14,
        backgroundColor: '#F0FDF4',
    },

    inputFieldFocus: {
        borderColor: '#4ADE80',
        borderWidth: 2,
        shadowColor: '#007bff',
        shadowOffset: { width: 0, height: 0 },
        shadowOpacity: 0.3,
        shadowRadius: 5,
    },

    agentBtn: {
        paddingVertical: 8,
        paddingHorizontal: 16,
        backgroundColor: '#4ADE80',
        borderRadius: 8,
        fontSize: 18,
        fontWeight: '900',
        color: 'white',
        textAlign: 'center',
    },

    hrAgent: {
        borderBottomWidth: 3,
        borderBottomColor: '#4ADE80',
        marginVertical: 10,
        width: '85%',
        alignSelf: 'center',
    },

    headingAgent: {
        textAlign: 'center',
        fontSize: 28,
        fontWeight: 'bold',
        marginBottom: 20,
    },

    summaryWrapper: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: '50px'
    },

    centeredContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        paddingHorizontal: 16,
    },

    summaryContainer: {
        gap: 24,
        alignItems: 'center',
        justifyContent: 'center',
    },

    centered: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },

});
