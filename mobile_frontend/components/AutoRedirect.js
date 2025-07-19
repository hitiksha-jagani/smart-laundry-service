import { useEffect } from 'react';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext'; 

export default function AutoRedirectScreen() {
  const { isLoggedIn, role, loading } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    if (loading) return;

    if (!isLoggedIn) {
      navigation.replace('Login');
    } else {
      switch (role) {
        case 'CUSTOMER':
          navigation.replace('CustomerDashboard');
          break;
        case 'SERVICE_PROVIDER':
          navigation.replace('ProviderDrawer');
          break;
        case 'DELIVERY_AGENT':
          navigation.replace('DeliverySummary');
          break;
        case 'ADMIN':
          navigation.replace('NotAvailable'); // or 'AdminDashboard' when implemented
          break;
        default:
          navigation.replace('Login');
      }
    }
  }, [isLoggedIn, role, loading]);

  return null;
}
