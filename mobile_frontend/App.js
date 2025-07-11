import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import DeliveryPage from './screens/DeliveryPage';
import LoginScreen from './screens/Customer/LoginScreen';
import CustomerHomePage from './screens/Customer/CustomerHomePage'; // ✅ Import your actual dashboard

const Stack = createNativeStackNavigator();

// 🔑 Dummy login function (replace with AsyncStorage/context if needed)
const login = (token, role, userId, providerId = null, navigation) => {
  console.log('User Logged In:', { token, role, userId, providerId });

  switch (role) {
    case 'CUSTOMER':
      navigation.navigate('CustomerDashboard');
      break;
    case 'DELIVERY_AGENT':
      navigation.navigate('DeliverySummary');
      break;
    case 'SERVICE_PROVIDER':
      navigation.navigate('ProviderDashboard'); // add later if needed
      break;
    case 'ADMIN':
      navigation.navigate('RevenueSummary'); // add later if needed
      break;
    default:
      console.warn('Unknown role:', role);
  }
};

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Login">
        <Stack.Screen name="Login" options={{ headerShown: false }}>
          {(props) => (
            <LoginScreen
              {...props}
              login={(token, role, userId, providerId = null) =>
                login(token, role, userId, providerId, props.navigation)
              }
            />
          )}
        </Stack.Screen>

        <Stack.Screen
          name="DeliverySummary"
          component={DeliveryPage}
          options={{ title: 'Delivery Summary' }}
        />

        <Stack.Screen
          name="CustomerDashboard"
          component={CustomerHomePage}
          options={{ headerShown: false }}
        />
      </Stack.Navigator>
      <StatusBar style="auto" />
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
