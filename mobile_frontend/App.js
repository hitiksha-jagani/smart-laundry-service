import React from 'react';
import { ActivityIndicator, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { StatusBar } from 'react-native';
import { AuthProvider } from './context/AuthContext';
import { createDrawerNavigator } from '@react-navigation/drawer';

import PrivateRoute from './components/PrivateRoute';
import './i18n';

// Screens
import AutoRedirect from './components/AutoRedirect';
import AutoRedirectScreen from './screens/AutoRedirectScreen';
import NotFoundScreen from './screens/NotFoundScreen';
import LoginScreen from './screens/Customer/LoginScreen';
import RegisterScreen from './screens/Customer/RegisterScreen';
import CustomerHomePage from './screens/Customer/CustomerHomePage';
import OrderBookingWizardScreen from './screens/Customer/OrderBookingWizardScreen';
import NearbyServiceProvidersScreen from './screens/Customer/NearbyServiceProvidersScreen';
import ProviderDetailScreen from './screens/Customer/ProviderDetailScreen';
import OrderSummary from './screens/Customer/OrderSummary';
import CustomerOrderHistory from './screens/Customer/CustomerOrderHistory';
import OrderSuccessScreen from './screens/Customer/OrderSuccessScreen';
import RaiseTicketForm from './screens/Customer/RaiseTicketForm';
import TermsAndConditionsScreen from './screens/Customer/TermsAndConditionsScreen';
import CancelOrderScreen from './screens/Customer/CancelOrderScreen';
import RescheduleOrderScreen from './screens/Customer/RescheduleOrderScreen';
import RescheduleSuccessScreen from './screens/Customer/RescheduleSuccessScreen';
import UpdateProfileScreen from './screens/Customer/UpdateProfileScreen';
import MyProfileScreen from './screens/Customer/MyProfileScreen';
import TrackOrderScreen from './screens/Customer/TrackOrderScreen';
import OrderBill from './screens/Customer/OrderBill';
import InitialOrderScreen from './screens/Customer/InitialOrderScreen';
import SchedulePlanScreen from './screens/Customer/SchedulePlanScreen';
import ContactInfoScreen from './screens/Customer/ContactInfoScreen';
import ReviewAndConfirmScreen from './screens/Customer/ReviewAndConfirmScreen';
import FeedbackForm from './screens/Customer/FeedbackForm';
import PayPalPaymentScreen from './screens/Customer/PayPalPaymentScreen';
import PaymentSuccessHandler from './screens/Customer/PaymentSuccessHandler';
import AvailablePromotionsScreen from './screens/Customer/AvailablePromotionsScreen';
// Service Provider
import PendingOrdersScreen from './screens/ServiceProvider/PendingOrdersScreen';
import ActiveOrdersScreen from './screens/ServiceProvider/ActiveOrdersScreen';
import DeliveredOrdersScreen from './screens/ServiceProvider/DeliveredOrdersScreen';
import ProviderDashboardScreen from './screens/ServiceProvider/ProviderDashboardScreen';
import VerifyPickupOtpScreen from './screens/ServiceProvider/VerifyPickupOtpScreen';
import OtpVerificationOrdersScreen from './screens/ServiceProvider/OtpVerificationOrdersScreen';
import NotAvailableScreen from './screens/NotAvailableScreen';
import VerifyDeliveryOtpScreen from './screens/ServiceProvider/VerifyDeliveryOtpScreen';
import EditServiceProviderProfileScreen from './screens/ServiceProvider/EditServiceProviderProfileScreen';
import ServiceProviderProfileForm from './screens/ServiceProvider/ServiceProviderProfileForm';

// Delivery Agent
import RequestPendingScreen from './components/DeliveryAgent/RequestPendingScreen';
import DeliveryAgentCompleteProfile from './screens/DeliveryAgent/DeliveryAgentCompleteProfile';
import { DrawerProvider } from './context/DrawerContext';
import DeliveryPage from './screens/DeliveryAgent/DeliveryPage';
import NotAvailablePage from './screens/DeliveryAgent/NotAvailablePage';
import PendingDeliveries from './screens/DeliveryAgent/PendingDeliveries';
import TodayDeliveries from './screens/DeliveryAgent/TodayDeliveries';
import UpdateStatus from './screens/DeliveryAgent/UpdateStatus';
import AvailabilityScreen from './screens/DeliveryAgent/Availability';
import DeliveryAgentPayout from './screens/DeliveryAgent/DeliveryAgentPayout';
import AllPayouts from './screens/DeliveryAgent/AllPayouts';
import PaidPayouts from './screens/DeliveryAgent/PaidPayouts';
import PendingPayouts from './screens/DeliveryAgent/PendingPayouts';
import DeliveryAgentProfile from './screens/DeliveryAgent/DeliveryAgentProfile';
import EditAgentProfile from './screens/DeliveryAgent/EditAgentProfilePage';
import ChangeAgentPasswordPage from './screens/DeliveryAgent/ChangeAgentPasswordPage';

const Stack = createNativeStackNavigator(); 
const Drawer = createDrawerNavigator();

// Drawer for Service Providers
const ProviderDrawer = () => (
  <Drawer.Navigator screenOptions={{ headerShown: false }}>
    <Drawer.Screen name="ProviderDashboard" component={ProviderDashboardScreen} />
    <Drawer.Screen name="PendingOrders" component={PendingOrdersScreen} />
    <Drawer.Screen name="ActiveOrders" component={ActiveOrdersScreen} />
    <Drawer.Screen name="OtpVerificationOrders" component={OtpVerificationOrdersScreen} />
    <Drawer.Screen name="VerifyPickupOtp" component={VerifyPickupOtpScreen} />
  </Drawer.Navigator>
);

const FeedbackScreen = () => (
  <PrivateRoute roles={['CUSTOMER']}>
    <FeedbackForm />
  </PrivateRoute>
);

// Deep linking config
const linking = {
  prefixes: ['smartlaundry://', 'https://yourapp.com', 'yourapp://'],
  config: {
    screens: {
      AutoRedirect: '/',
      Login: '/login',
      Register: '/register',
      CustomerDashboard: '/CustomerDashboard',
      OrderBooking: '/order/book',
      NearbyServiceProviders: '/service-providers',
      ProviderDetail: 'provider/:providerId',
      OrderBill: 'orders/:orderId/bill',
      AvailablePromotions: 'orders/:orderId/promotions',
      CustomerOrderHistory: '/customer/Orderhistory',
      OrderSummary: 'orders/:orderId/summary',
      TrackOrder: 'orders/:orderId/track',
      OrderSuccess: '/order/success',
      RaiseTicket: '/ticket/raise',
      TermsAndConditions: '/terms',
      CancelOrder: 'orders/:orderId/cancel',
      RescheduleOrder: 'orders/:orderId/reschedule',
      RescheduleSuccess: '/order/reschedule-success',
      UpdateProfile: '/update-profile',
      MyProfile: '/my-profile',
      Feedback: 'orders/:orderId/feedback',
      PayPalPayment: 'payment/start',
      PaymentSuccessHandler: 'payment/success',
      PayPalCancel: 'payment/cancel',
      ProviderDrawer: '/provider/drawer',
      ProviderDashboard: '/provider/dashboard',
      PendingOrders: '/provider/pending-orders',
      ActiveOrders: '/provider/active-orders',
      DeliveredOrders: '/provider/completed-orders',
      ServiceProviderProfileForm: '/provider/completeprofile',
      EditServiceProviderProfile: '/sp/edit-profile',
      VerifyPickupOtp: 'provider/otp/verify/pickup/:orderId',
      VerifyDeliveryOtp: 'provider/otp/verify/delivery/:orderId',
      OtpVerificationOrders: '/provider/orders/verify-otps',

      DeliveryAgentCompleteProfile: '/profile/complete',
      DeliveryPage: '/delivery/summary',
      PendingDeliveries: '/deliveries/pending',
      TodayDeliveries: '/deliveries/today',
      UpdateStatus: '/update-status',
      AvailabilityScreen: '/availability/manage',
      DeliveryAgentPayout: '/payouts/summary',
      AllPayouts: '/payouts/all',
      PaidPayouts: '/payouts/paid',
      PendingPayouts: '/payouts/pending',
      DeliveryAgentProfile: '/profile/detail',
      EditAgentProfile: '/profile/detail/edit',
      ChangeAgentPasswordPage: '/profile/detail/change-password',

      NotAvailable: '/not-available',
      NotFound: '*',
    },
  },
};

// ✅ Main Navigation App
function MainApp() {
  const { loading } = useAuth();

  if (loading) {
    // Show splash screen or loading indicator
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <NavigationContainer linking={linking} fallback={<NotFoundScreen />}>
      <Stack.Navigator initialRouteName="AutoRedirect" screenOptions={{ headerShown: false }}>
        <Stack.Screen name="AutoRedirect" component={AutoRedirect} />
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="Register" component={RegisterScreen} />
        <Stack.Screen name="CustomerDashboard" component={CustomerHomePage} />
        <Stack.Screen name="OrderBooking" component={OrderBookingWizardScreen} />
        <Stack.Screen name="NearbyServiceProviders" component={NearbyServiceProvidersScreen} />
        <Stack.Screen name="ProviderDetail" component={ProviderDetailScreen} />
        <Stack.Screen name="OrderBill" component={OrderBill} />
        <Stack.Screen name="AvailablePromotions" component={AvailablePromotionsScreen} />
        <Stack.Screen name="CustomerOrderHistory" component={CustomerOrderHistory} />
        <Stack.Screen name="OrderSummary" component={OrderSummary} />
        <Stack.Screen name="TrackOrder" component={TrackOrderScreen} />
        <Stack.Screen name="OrderSuccess" component={OrderSuccessScreen} />
        <Stack.Screen name="RaiseTicket" component={RaiseTicketForm} />
        <Stack.Screen name="TermsAndConditions" component={TermsAndConditionsScreen} />
        <Stack.Screen name="CancelOrder" component={CancelOrderScreen} />
        <Stack.Screen name="RescheduleOrder" component={RescheduleOrderScreen} />
        <Stack.Screen name="RescheduleSuccess" component={RescheduleSuccessScreen} />
        <Stack.Screen name="UpdateProfile" component={UpdateProfileScreen} />
        <Stack.Screen name="MyProfile" component={MyProfileScreen} />
        <Stack.Screen name="Feedback" component={FeedbackScreen} />
        <Stack.Screen name="PayPalPayment" component={PayPalPaymentScreen} />
        <Stack.Screen name="PayPalCancel" component={NotAvailableScreen} />
        <Stack.Screen name="PaymentSuccessHandler" component={PaymentSuccessHandler} />
        <Stack.Screen name="InitialOrder" component={InitialOrderScreen} />
        <Stack.Screen name="SchedulePlan" component={SchedulePlanScreen} />
        <Stack.Screen name="ContactInfo" component={ContactInfoScreen} />
        <Stack.Screen name="ReviewAndConfirm" component={ReviewAndConfirmScreen} />
        <Stack.Screen name="ProviderDrawer" component={ProviderDrawer} />
        <Stack.Screen name="DeliveredOrders" component={DeliveredOrdersScreen} />
        <Stack.Screen name="EditServiceProviderProfile" component={EditServiceProviderProfileScreen} />
        <Stack.Screen name="ServiceProviderProfileForm" component={ServiceProviderProfileForm} />
        <Stack.Screen name="VerifyPickupOtp" component={VerifyPickupOtpScreen} />
        <Stack.Screen name="VerifyDeliveryOtp" component={VerifyDeliveryOtpScreen} />
        <Stack.Screen name="OtpVerificationOrders" component={OtpVerificationOrdersScreen} />
        <Stack.Screen name="DeliverySummary" component={DeliveryPage} />
        <Stack.Screen name="NotAvailable" component={NotAvailableScreen} />
        <Stack.Screen name="NotFound" component={NotFoundScreen} />
      </Stack.Navigator>
      <StatusBar style="auto" />
    </NavigationContainer>
  );
}

// ✅ Root Component
export default function App() {
  return (
    <AuthProvider>
      <NavigationContainer linking={linking} fallback={<NotFoundScreen />}>
        <Stack.Navigator initialRouteName="AutoRedirect" screenOptions={{ headerShown: false }}>
          <Stack.Screen name="AutoRedirect" component={AutoRedirect} />
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />

          {/* Customer Flow */}
          <Stack.Screen name="CustomerDashboard" component={CustomerHomePage} />
          <Stack.Screen name="OrderBooking" component={OrderBookingWizardScreen} />
          <Stack.Screen name="NearbyServiceProviders" component={NearbyServiceProvidersScreen} />
          <Stack.Screen name="ProviderDetail" component={ProviderDetailScreen} />
          <Stack.Screen name="OrderBill" component={OrderBill} />
          <Stack.Screen name="AvailablePromotions" component={OrderBookingWizardScreen} />
          <Stack.Screen name="CustomerOrderHistory">
                  {() => (
                    <PrivateRoute roles={['CUSTOMER']}>
                      <CustomerOrderHistory />
                    </PrivateRoute>
                  )}
                </Stack.Screen>

          <Stack.Screen name="OrderSummary">
              {() => <OrderSummary />}
            </Stack.Screen>

          <Stack.Screen name="TrackOrder" component={TrackOrderScreen} />
          <Stack.Screen name="OrderSuccess" component={OrderSuccessScreen} />
          <Stack.Screen name="RaiseTicket" component={RaiseTicketForm} />
          <Stack.Screen name="TermsAndConditions" component={TermsAndConditionsScreen} />
          <Stack.Screen name="CancelOrder" component={CancelOrderScreen} />
          <Stack.Screen name="RescheduleOrder" component={RescheduleOrderScreen} />
          <Stack.Screen name="RescheduleSuccess" component={RescheduleSuccessScreen} />
          <Stack.Screen name="UpdateProfile" component={UpdateProfileScreen} />
          <Stack.Screen name="MyProfile" component={MyProfileScreen} />
          <Stack.Screen name="Feedback" component={FeedbackScreen} />
          <Stack.Screen name="PayPalPayment" component={PayPalPaymentScreen} />
          <Stack.Screen name="PayPalSuccess" component={PayPalPaymentScreen} />
          <Stack.Screen name="PayPalCancel" component={NotAvailableScreen} />

          {/* Order Wizard */}
          <Stack.Screen name="InitialOrder" component={InitialOrderScreen} />
          <Stack.Screen name="SchedulePlan" component={SchedulePlanScreen} />
          <Stack.Screen name="ContactInfo" component={ContactInfoScreen} />
          <Stack.Screen name="ReviewAndConfirm" component={ReviewAndConfirmScreen} />

          {/* Service Provider Flow */}
          <Stack.Screen name="ProviderDrawer" component={ProviderDrawer} />
          <Stack.Screen name="DeliveredOrders" component={DeliveredOrdersScreen} />
          <Stack.Screen name="EditServiceProviderProfile" component={EditServiceProviderProfileScreen} />
          <Stack.Screen name="ServiceProviderProfileForm" component={ServiceProviderProfileForm} />
          <Stack.Screen name="VerifyPickupOtp" component={VerifyPickupOtpScreen} />
          <Stack.Screen name="VerifyDeliveryOtp" component={VerifyDeliveryOtpScreen} />
          <Stack.Screen name="OtpVerificationOrders" component={OtpVerificationOrdersScreen} />

          {/* Delivery */}
          <Stack.Screen name="RequestPending" component={RequestPendingScreen} />

          {/* Delivery Page */}
          <Stack.Screen name="DeliveryPage">
            {() => (
              <DrawerProvider>
                <DeliveryPage />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="PendingDeliveries">
            {(props) => (
              <DrawerProvider>
                <PendingDeliveries {...props} />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="TodayDeliveries">
            {(props) => (
              <DrawerProvider>
                <TodayDeliveries {...props} />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="UpdateStatus">
            {() => (
              <DrawerProvider>
                <UpdateStatus />
              </DrawerProvider>
            )}
          </Stack.Screen>

          {/* Availability Page */}
          <Stack.Screen name="AvailabilityScreen">
            {() => (
              <DrawerProvider>
                <AvailabilityScreen />
              </DrawerProvider>
            )}
          </Stack.Screen>

          {/* Payout Page */}
          <Stack.Screen name="DeliveryAgentPayout">
            {() => (
              <DrawerProvider>
                <DeliveryAgentPayout />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="AllPayouts">
            {() => (
              <DrawerProvider>
                <AllPayouts />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="PaidPayouts">
            {() => (
              <DrawerProvider>
                <PaidPayouts />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="PendingPayouts">
            {() => (
              <DrawerProvider>
                <PendingPayouts />
              </DrawerProvider>
            )}
          </Stack.Screen>

          {/* Profile Page */}
          <Stack.Screen name="DeliveryAgentCompleteProfile" component={DeliveryAgentCompleteProfile} />
          <Stack.Screen name="DeliveryAgentProfile">
            {() => (
              <DrawerProvider>
                <DeliveryAgentProfile />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="EditAgentProfile">
            {() => (
              <DrawerProvider>
                <EditAgentProfile />
              </DrawerProvider>
            )}
          </Stack.Screen>
          <Stack.Screen name="ChangeAgentPasswordPage">
            {() => (
              <DrawerProvider>
                <ChangeAgentPasswordPage />
              </DrawerProvider>
            )}
          </Stack.Screen>
          
          <Stack.Screen name="NotAvailableInThisVersion">
            {() => (
              // <DrawerProvider>
                <NotAvailablePage />
              // </DrawerProvider>
            )}
          </Stack.Screen>

          

          {/* Utility */}
          <Stack.Screen name="NotAvailable" component={NotAvailableScreen} />
          <Stack.Screen name="NotFound" component={NotFoundScreen} />
        </Stack.Navigator>
        {/* <StatusBar style="auto" /> */}
      </NavigationContainer>
    </AuthProvider>
  );
}
