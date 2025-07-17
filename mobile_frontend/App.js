import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from './screens/Customer/LoginScreen';
import CustomerHomePage from './screens/Customer/CustomerHomePage'; 
import DeliveryAgentStack from './navigation/DeliveryAgentStack';
import { AuthProvider } from './context/AuthContext';

const Stack = createNativeStackNavigator();

// 🔑 Dummy login function (replace with AsyncStorage/context if needed)
const login = (token, role, userId, providerId = null, navigation) => {
  console.log('User Logged In:', { token, role, userId, providerId });

  switch (role) {
    case 'CUSTOMER':
      navigation.navigate('CustomerDashboard');
      break;
    case 'DELIVERY_AGENT':
      navigation.navigate('DeliveryAgentRoutes  ');
      break;
    case 'SERVICE_PROVIDER':
      navigation.navigate('ProviderDashboard'); 
      break;
    default:
      console.warn('Unknown role:', role);
  }
};

export default function App() {
  return (
    <AuthProvider>
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
          name="CustomerDashboard"
          component={CustomerHomePage}
          options={{ headerShown: false }}
        />

        <Stack.Screen
          name="DeliveryAgentRoutes"
          component={DeliveryAgentStack}
          options={{ headerShown: false }}
        />

      </Stack.Navigator>
      <StatusBar style="auto" />
    </NavigationContainer>
    </AuthProvider>
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
