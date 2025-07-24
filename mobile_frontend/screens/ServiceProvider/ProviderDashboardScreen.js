import React from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import { FontAwesome5 } from "@expo/vector-icons";

export default function ProviderDashboardScreen() {
  const navigation = useNavigation();

  const dashboardItems = [
    {
      title: "Pending Orders",
      description: "Orders waiting for your confirmation",
      icon: "clock",
      route: "PendingOrders", // this should match the screen name in your navigator
      color: "#FEEBCB", // Tailwind: bg-orange-100
      textColor: "#EA580C",
    },
    {
      title: "Active Orders",
      description: "Orders in progress or picked up",
      icon: "running",
      route: "ActiveOrders",
      color: "#FED7AA", // Tailwind: bg-orange-200
      textColor: "#C2410C",
    },
    {
      title: "Completed / Delivered",
      description: "All completed and delivered orders",
      icon: "check-circle",
      route: "CompletedOrders",
      color: "#FDBA74", // Tailwind: bg-orange-300
      textColor: "#9A3412",
    },
    {
      title: "Verify OTPs",
      description: "Pickup & Delivery OTP verification",
      icon: "key",
      route: "OtpVerificationOrders",
      color: "#FEEBCB",
      textColor: "#7C2D12",
    },
  ];

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerText}>Service Provider Dashboard</Text>
      </View>

      <ScrollView contentContainerStyle={styles.cardsContainer}>
        {dashboardItems.map((item, index) => (
          <TouchableOpacity
            key={index}
            onPress={() => navigation.navigate(item.route)}
            style={[styles.card, { backgroundColor: item.color }]}
          >
            <View style={styles.cardTop}>
              <FontAwesome5 name={item.icon} size={28} color={item.textColor} />
              <Text style={[styles.cardTitle, { color: item.textColor }]}>
                {item.title}
              </Text>
            </View>
            <Text style={styles.cardDesc}>{item.description}</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFF7ED", // orange-50
  },
  header: {
    backgroundColor: "#EA580C", // orange-600
    paddingVertical: 16,
    alignItems: "center",
    justifyContent: "center",
    elevation: 3,
  },
  headerText: {
    fontSize: 24,
    color: "#fff",
    fontWeight: "bold",
  },
  cardsContainer: {
    padding: 16,
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
  },
  card: {
    width: "48%",
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
    elevation: 2,
  },
  cardTop: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 10,
    gap: 12, // Only works in newer React Native versions; if not supported, use marginRight on icon.
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: "600",
  },
  cardDesc: {
    fontSize: 13,
    color: "#374151", // gray-700
  },
});
