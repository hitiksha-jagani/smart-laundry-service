import React from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Platform,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import { useAuth } from "../context/AuthContext";
import { useTranslation } from "react-i18next";

export default function Navbar() {
  const navigation = useNavigation();
  const { isLoggedIn, logout } = useAuth();
  const { i18n } = useTranslation();

  const handleLogout = () => {
    logout();
    navigation.navigate("Login");
  };

  const changeLanguage = (lang) => {
    i18n.changeLanguage(lang);
  };

  return (
    <View style={styles.container}>
      <View style={styles.headerTop}>
        <Text style={styles.logo}>Smart Laundry</Text>
      </View>

      <ScrollView
        horizontal
        contentContainerStyle={styles.navLinks}
        showsHorizontalScrollIndicator={false}
      >
        <TouchableOpacity onPress={() => navigation.navigate("CustomerDashboard")}>
          <Text style={styles.link}>Home</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => navigation.navigate("NearbyServiceProviders")}>
          <Text style={styles.link}>Book</Text>
        </TouchableOpacity>

        {isLoggedIn && (
          <TouchableOpacity onPress={() => navigation.navigate("MyProfile")}>
            <Text style={styles.link}>Profile</Text>
          </TouchableOpacity>
        )}

        {!isLoggedIn ? (
          <>
            <TouchableOpacity onPress={() => navigation.navigate("Login")}>
              <Text style={styles.link}>Login</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => navigation.navigate("Register")}>
              <Text style={styles.link}>Sign Up</Text>
            </TouchableOpacity>
          </>
        ) : (
          <TouchableOpacity onPress={handleLogout}>
            <Text style={styles.logout}>Logout</Text>
          </TouchableOpacity>
        )}
      </ScrollView>
    </View>
  );
}
const styles = StyleSheet.create({
  container: {
    backgroundColor: "#A566FF",
    paddingTop: Platform.OS === "ios" ? 50 : 30,
    paddingBottom: 10,
    paddingHorizontal: 12,
    elevation: 4,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.3,
    shadowRadius: 3,
  },
  headerTop: {
    marginBottom: 10,
    alignItems: "center",
  },
  logo: {
    color: "#fff",
    fontSize: 20,
    fontWeight: "700",
  },
  navLinks: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
  },
  link: {
    color: "#fff",
    fontSize: 14,
    fontWeight: "500",
    marginRight: 16,
  },
  logout: {
    color: "#FFD200",
    fontSize: 14,
    fontWeight: "600",
    marginRight: 16,
  },
});
