import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import axios from "../../utils/axiosInstance";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../../context/AuthContext";
import { FontAwesome5 } from "@expo/vector-icons";

export default function ProviderDrawerContent() {
  const navigation = useNavigation();
  const { logout } = useAuth();
  const [provider, setProvider] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProvider = async () => {
      try {
        const providerId = await AsyncStorage.getItem("providerId");
        if (providerId) {
          const res = await axios.get(`/provider/${providerId}`);
          setProvider(res.data);
        }
      } catch (error) {
        console.error("Failed to fetch provider:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProvider();
  }, []);

  const handleLogout = async () => {
    logout();
    navigation.replace("Login");
  };

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.brand}>SmartLaundry</Text>
        {loading ? (
          <ActivityIndicator color="#EA580C" />
        ) : provider ? (
          <>
            <Text style={styles.subtext}>Provider ID: {provider.serviceProviderId}</Text>
            <Text style={styles.subtext}>{provider.phoneNumber}</Text>
            <Text style={styles.subtext}>{provider.email}</Text>
            <View style={styles.statusRow}>
              <View style={styles.statusDot} />
              <Text style={styles.statusText}>Active</Text>
            </View>
          </>
        ) : (
          <Text style={styles.subtext}>No data</Text>
        )}
      </View>

      {/* Navigation */}
      <View style={styles.navSection}>
        <DrawerItem label="Home" icon="home" onPress={() => navigation.navigate("ProviderDashboard")} />
        <DrawerItem label="Manage Workload" icon="tasks" onPress={() => navigation.navigate("ActiveOrders")} />
        <DrawerItem label="Order History" icon="history" onPress={() => navigation.navigate("CompletedOrders")} />
        <DrawerItem label="My Profile" icon="user" onPress={() => navigation.navigate("EditServiceProviderProfile")} />
        <DrawerItem label="Log Out" icon="sign-out-alt" danger onPress={handleLogout} />
      </View>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>© {new Date().getFullYear()} SmartLaundry</Text>
      </View>
    </View>
  );
}

function DrawerItem({ label, icon, onPress, danger }) {
  return (
    <TouchableOpacity
      onPress={onPress}
      style={[styles.drawerItem, danger && { backgroundColor: "#F87171" }]}
    >
      <FontAwesome5
        name={icon}
        size={18}
        color={danger ? "#fff" : "#EA580C"}
        style={{ marginRight: 12 }}
      />
      <Text style={[styles.drawerText, danger && { color: "#fff", fontWeight: "600" }]}>
        {label}
      </Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFEDD5", // orange-100
    paddingTop: 40,
    paddingHorizontal: 16,
  },
  header: {
    borderBottomWidth: 1,
    borderColor: "#FDBA74",
    paddingBottom: 12,
    marginBottom: 20,
  },
  brand: {
    fontSize: 20,
    fontWeight: "bold",
    color: "#EA580C",
    marginBottom: 6,
  },
  subtext: {
    fontSize: 12,
    color: "#444",
  },
  statusRow: {
    flexDirection: "row",
    alignItems: "center",
    marginTop: 6,
  },
  statusDot: {
    width: 8,
    height: 8,
    backgroundColor: "#16A34A",
    borderRadius: 4,
    marginRight: 6,
  },
  statusText: {
    fontSize: 12,
    fontWeight: "bold",
    color: "#16A34A",
  },
  navSection: {
    flex: 1,
    marginTop: 10,
  },
  drawerItem: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 10,
    paddingHorizontal: 8,
    borderRadius: 8,
    marginBottom: 8,
    backgroundColor: "#fff",
  },
  drawerText: {
    fontSize: 15,
    color: "#333",
  },
  footer: {
    borderTopWidth: 1,
    borderColor: "#FDBA74",
    paddingVertical: 10,
    alignItems: "center",
  },
  footerText: {
    fontSize: 12,
    color: "#777",
  },
});
