import { useEffect } from "react";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../context/AuthContext";

export default function AutoRedirect() {
  const { token, role, loading } = useAuth();
  const navigation = useNavigation();

  useEffect(() => {
    if (loading) return;

    if (!token) {
      navigation.reset({
        index: 0,
        routes: [{ name: "Login" }],
      });
    } else {
      switch (role) {
        case "CUSTOMER":
          navigation.reset({
            index: 0,
            routes: [{ name: "CustomerDashboard" }],
          });
          break;
        case "SERVICE_PROVIDER":
          navigation.reset({
            index: 0,
            routes: [{ name: "ProviderDrawer" }],
          });
          break;
        case "DELIVERY_AGENT":
          navigation.reset({
            index: 0,
            routes: [{ name: "DeliveryStack" }],
          });
          break;
        case "ADMIN":
          navigation.reset({
            index: 0,
            routes: [{ name: "AdminStack" }],
          });
          break;
        default:
          navigation.reset({
            index: 0,
            routes: [{ name: "Login" }],
          });
      }
    }
  }, [token, role, loading, navigation]);

  return null;
}
