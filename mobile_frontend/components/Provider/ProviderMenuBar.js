import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Modal,
} from "react-native";
import { Feather } from "@expo/vector-icons";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../../context/AuthContext";
import axiosInstance from "../../utils/axiosInstance";

export default function ProviderMenuBar() {
  const { logout } = useAuth();
  const navigation = useNavigation();
  const [provider, setProvider] = useState(null);
  const [providerId, setProviderId] = useState(null);
  const [menuVisible, setMenuVisible] = useState(false);

  useEffect(() => {
    const fetchProviderId = async () => {
      const id = await AsyncStorage.getItem("providerId");
      setProviderId(id);
    };
    fetchProviderId();
  }, []);

  useEffect(() => {
    if (providerId) {
      axiosInstance
        .get(`/provider/${providerId}`)
        .then((res) => setProvider(res.data))
        .catch((err) => console.error("Failed to load provider:", err));
    }
  }, [providerId]);

  const handleLogout = () => {
    logout();
    navigation.navigate("Login");
  };

  const navigateTo = (screen) => {
    setMenuVisible(false);
    navigation.navigate(screen);
  };

  return (
    <View style={styles.header}>
      <Text style={styles.brand}>SmartLaundry</Text>
      <TouchableOpacity onPress={() => setMenuVisible(true)}>
        <Feather name="menu" size={24} color="black" />
      </TouchableOpacity>

      <Modal
        animationType="fade"
        transparent
        visible={menuVisible}
        onRequestClose={() => setMenuVisible(false)}
      >
        <TouchableOpacity
          style={styles.overlay}
          onPress={() => setMenuVisible(false)}
        >
          <View style={styles.menu}>
            {provider && (
              <View style={styles.providerInfo}>
                <Text style={styles.infoText}>
                  {provider.serviceProviderId}
                </Text>
                <Text style={styles.infoText}>{provider.email}</Text>
              </View>
            )}
            <TouchableOpacity
              style={styles.menuItem}
              onPress={() => navigateTo("ProviderDashboard")}
            >
              <Text>Home</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.menuItem}
              onPress={() => navigateTo("ActiveOrders")}
            >
              <Text>Active Orders</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.menuItem}
              onPress={() => navigateTo("DeliveredOrders")}
            >
              <Text>Delivered Orders</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.menuItem}
              onPress={() => navigateTo("PendingOrders")}
            >
              <Text>Pending Orders</Text>
            </TouchableOpacity>
             <TouchableOpacity
              style={styles.menuItem}
              onPress={() => navigateTo("OtpVerificationOrders")}
            >
              <Text>Verify Orders</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.menuItem, styles.logout]}
              onPress={handleLogout}
            >
              <Text style={{ fontWeight: "bold" }}>Log Out</Text>
            </TouchableOpacity>
          </View>
        </TouchableOpacity>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    height: 60,
    backgroundColor: "#fdba74",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingHorizontal: 16,
    elevation: 3,
  },
  brand: {
    fontSize: 18,
    fontWeight: "bold",
  },
  overlay: {
    flex: 1,
    justifyContent: "flex-start",
    alignItems: "flex-end",
    backgroundColor: "rgba(0,0,0,0.3)",
    paddingTop: 60,
    paddingRight: 10,
  },
  menu: {
    width: 220,
    backgroundColor: "#fff",
    borderRadius: 8,
    padding: 10,
    elevation: 5,
  },
  providerInfo: {
    marginBottom: 10,
    borderBottomWidth: 1,
    borderColor: "#eee",
    paddingBottom: 8,
  },
  infoText: {
    fontSize: 12,
    color: "#333",
    textAlign: "right",
  },
  menuItem: {
    paddingVertical: 10,
    borderBottomWidth: 0.5,
    borderColor: "#ccc",
  },
  logout: {
    backgroundColor: "#fca5a5",
    borderRadius: 4,
    marginTop: 8,
    paddingHorizontal: 8,
  },
});
