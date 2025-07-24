// Author: Hitiksha Patel
// Description: Summary card for delivery agent dashboard in React Native

import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const SummaryCard = ({ title, prefix, user, count, link, data, filterParams }) => {
    const navigation = useNavigation();

    const navigateTo = (targetRoute) => {
        closeDrawer();
        if (targetRoute !== route.name) {
        navigation.navigate(targetRoute);
        }
    };

    const handlePress = () => {
        if (link) {
        const linkState = {
            data,
            user,
            filter: filterParams?.filter,
            ...(filterParams?.filter === 'custom' && {
                startDate: filterParams.startDate,
                endDate: filterParams.endDate,
            }),
        };

        navigation.navigate(link, linkState); 
        }
    };

    const Content = (
        <>
            <Text style={styles.title}>{title}</Text>
            <Text style={styles.count}>{prefix}{count}</Text>
        </>
    );

    return (

        <View style={styles.card}>

            {link ? (

                <TouchableOpacity onPress={handlePress} style={{ alignItems: 'center' }}>
                    {Content}
                </TouchableOpacity>

            ) : (

                <View style={{ alignItems: 'center' }}>
                    {Content}
                </View>

            )}

        </View>

    );
};

const DrawerLink = ({ onPress }) => (

  <TouchableOpacity onPress={onPress}>
    
    <Text style={[styles.drawerLinkText, isActive && styles.activeDrawerLinkText]}>
      {text}
    </Text>

  </TouchableOpacity>

);

const styles = StyleSheet.create({

    card: {
        backgroundColor: '#f0fdf4',
        padding: 16,
        borderRadius: 12,
        borderWidth: 1,
        borderColor: '#388E3C',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 6,
        elevation: 4,
        marginTop: 20,
        marginBottom: 16,
        width: 300,
        height: 200,
        justifyContent: 'center',
        alignItems: 'center',
    },
    title: {
        fontSize: 18,
        color: '#333',
        fontWeight: '600',
        marginBottom: 10,
    },
    count: {
        color: '#388E3C',
        fontSize: 35,
        fontWeight: 'bold',
    },
});

export default SummaryCard;
