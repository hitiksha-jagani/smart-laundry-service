// import React, { useLayoutEffect } from "react";
// import {
//   View,
//   Text,
//   StyleSheet,
//   TouchableOpacity,
//   ScrollView,
// } from "react-native";
// import { useNavigation } from "@react-navigation/native";
// import { FontAwesome5, Feather } from "@expo/vector-icons";

// export default function ProviderDashboardScreen() {
//   const navigation = useNavigation();

//   // Show hamburger icon in header to open drawer
//   useLayoutEffect(() => {
//     navigation.setOptions({
//       headerLeft: () => (
//         <TouchableOpacity
//           style={{ marginLeft: 16 }}
//           onPress={() => navigation.toggleDrawer()}
//         >
//           <Feather name="menu" size={24} color="#fff" />
//         </TouchableOpacity>
//       ),
//       headerTitleAlign: "center", // optional: center the title
//     });
//   }, [navigation]);

//   const dashboardItems = [
//     {
//       title: "Pending Orders",
//       description: "Orders waiting for your confirmation",
//       icon: "clock",
//       route: "PendingOrders",
//       color: "#FEEBCB",
//       textColor: "#EA580C",
//     },
//     {
//       title: "Active Orders",
//       description: "Orders in progress or picked up",
//       icon: "running",
//       route: "ActiveOrders",
//       color: "#FED7AA",
//       textColor: "#C2410C",
//     },
//     {
//       title: "Completed / Delivered",
//       description: "All completed and delivered orders",
//       icon: "check-circle",
//       route: "DeliveredOrders", // make sure this matches drawer screen name
//       color: "#FDBA74",
//       textColor: "#9A3412",
//     },
//     {
//       title: "Verify OTPs",
//       description: "Pickup & Delivery OTP verification",
//       icon: "key",
//       route: "OtpVerificationOrders",
//       color: "#FEEBCB",
//       textColor: "#7C2D12",
//     },
//   ];

//   return (
//     <View style={styles.container}>
//       <View style={styles.header}>
//         <Text style={styles.headerText}>Service Provider Dashboard</Text>
//       </View>

//       <ScrollView contentContainerStyle={styles.cardsContainer}>
//         {dashboardItems.map((item, index) => (
//           <TouchableOpacity
//             key={index}
//             onPress={() => navigation.navigate(item.route)}
//             style={[styles.card, { backgroundColor: item.color }]}
//           >
//             <View style={styles.cardTop}>
//               <FontAwesome5 name={item.icon} size={28} color={item.textColor} />
//               <Text style={[styles.cardTitle, { color: item.textColor }]}>
//                 {item.title}
//               </Text>
//             </View>
//             <Text style={styles.cardDesc}>{item.description}</Text>
//           </TouchableOpacity>
//         ))}
//       </ScrollView>
//     </View>
//   );
// }

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     backgroundColor: "#FFF7ED", // orange-50
//   },
//   header: {
//     backgroundColor: "#EA580C", // orange-600
//     paddingVertical: 16,
//     alignItems: "center",
//     justifyContent: "center",
//     elevation: 3,
//   },
//   headerText: {
//     fontSize: 24,
//     color: "#fff",
//     fontWeight: "bold",
//   },
//   cardsContainer: {
//     padding: 16,
//     flexDirection: "row",
//     flexWrap: "wrap",
//     justifyContent: "space-between",
//   },
//   card: {
//     width: "48%",
//     borderRadius: 16,
//     padding: 16,
//     marginBottom: 16,
//     elevation: 2,
//   },
//   cardTop: {
//     flexDirection: "row",
//     alignItems: "center",
//     marginBottom: 10,
//     // Replace gap: 12 with marginRight for compatibility
//   },
//   cardTitle: {
//     fontSize: 18,
//     fontWeight: "600",
//     marginLeft: 12,
//   },
//   cardDesc: {
//     fontSize: 13,
//     color: "#374151", // gray-700
//   },
// });



import React from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
} from "react-native";
import { FontAwesome5 } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import ProviderMenuBar from "../../components/Provider/ProviderMenuBar";

export default function ProviderDashboardScreen() {
  const navigation = useNavigation();

  const dashboardItems = [
    {
      title: "Pending Orders",
      description: "Orders waiting for your confirmation",
      icon: "clock",
      route: "PendingOrders",
      color: "#FEEBCB",
      textColor: "#EA580C",
    },
    {
      title: "Active Orders",
      description: "Orders in progress or picked up",
      icon: "running",
      route: "ActiveOrders",
      color: "#FED7AA",
      textColor: "#C2410C",
    },
    {
      title: "Completed / Delivered",
      description: "All completed and delivered orders",
      icon: "check-circle",
      route: "DeliveredOrders",
      color: "#FDBA74",
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
      {/* ✅ Include the top menu bar */}
      <ProviderMenuBar />

      {/* Page heading */}
      <View style={styles.header}>
        <Text style={styles.headerText}>Service Provider Dashboard</Text>
      </View>

      {/* Dashboard cards */}
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
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: "600",
    marginLeft: 12,
  },
  cardDesc: {
    fontSize: 13,
    color: "#374151", // gray-700
  },
});
