// import React, { useState } from "react";
// import { View, Button, ActivityIndicator, Alert } from "react-native";
// import axios from "../../utils/axiosInstance";
// import { WebView } from "react-native-webview";
// import { useNavigation } from "@react-navigation/native";

// export default function PayPalPaymentScreen({ route }) {
//   const { billId, orderId } = route.params;
//   const [approvalUrl, setApprovalUrl] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const navigation = useNavigation();

//   const getParamFromUrl = (url, key) => {
//     const params = new URLSearchParams(url.split("?")[1]);
//     return params.get(key);
//   };

//   const startPayment = async () => {
//     try {
//       setLoading(true);
//       const response = await axios.post(`/payments/create?billId=${billId}`);
//       const paypalOrderId = response.data.orderID;

//       const approvalLink = `https://www.sandbox.paypal.com/checkoutnow?token=${paypalOrderId}`;
//       setApprovalUrl(approvalLink);
//     } catch (error) {
//       console.error("PayPal start error:", error);
//       Alert.alert("Error", "Failed to initiate PayPal payment.");
//     } finally {
//       setLoading(false);
//     }
//   };

//   const handleNavigationChange = async (navState) => {
//     const { url } = navState;

//     if (url.includes("success")) {
//       const paypalOrderId = getParamFromUrl(url, "token");
//       setApprovalUrl(null);

//       try {
//         await axios.get(`/payments/success?orderId=${paypalOrderId}&billId=${billId}`);
//         Alert.alert("Success", "Payment completed successfully.");
//         navigation.navigate("OrderBill", { orderId }); // ✅ Adjust this if needed
//       } catch (error) {
//         console.error("Backend confirm failed:", error);
//         Alert.alert("Warning", "Payment successful on PayPal but not confirmed on server.");
//       }
//     }

//     if (url.includes("cancel")) {
//       setApprovalUrl(null);
//       Alert.alert("Cancelled", "Payment was cancelled.");
//     }
//   };

//   return (
//     <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
//       {approvalUrl ? (
//         <WebView
//           source={{ uri: approvalUrl }}
//           onNavigationStateChange={handleNavigationChange}
//           startInLoadingState
//           javaScriptEnabled
//           style={{ flex: 1 }}
//         />
//       ) : loading ? (
//         <ActivityIndicator size="large" color="#0000ff" />
//       ) : (
//         <Button title="Pay with PayPal" onPress={startPayment} />
//       )}
//     </View>
//   );
// }

// import React, { useEffect, useState } from "react";
// import { Alert, Linking, ActivityIndicator, View } from "react-native";
// import axios from "../../utils/axiosInstance";
// import { useNavigation, useRoute } from "@react-navigation/native";

// export default function PayPalPaymentScreen() {
//   const { billId, orderId } = useRoute().params;
//   const navigation = useNavigation();
//   const [polling, setPolling] = useState(false);

//   useEffect(() => {
//     const startPayment = async () => {
//       try {
//         const res = await axios.post(`/payments/create?billId=${billId}&client=mobile`);
//         const orderID = res.data.orderID;
//         const approvalUrl = `https://www.sandbox.paypal.com/checkoutnow?token=${orderID}`;

//         // Open in browser
//         await Linking.openURL(approvalUrl);

//         // Start polling after delay
//         setTimeout(() => setPolling(true), 5000);
//       } catch (err) {
//         console.error("Start payment error:", err);
//         Alert.alert("Error", "Failed to initiate PayPal payment.");
//       }
//     };

//     startPayment();
//   }, []);

//   useEffect(() => {
//     let interval;
//     if (polling) {
//       interval = setInterval(async () => {
//         try {
//           const res = await axios.get(`/bills/${billId}`);
//           if (res.data.status === "PAID") {
//             clearInterval(interval);
//             Alert.alert("Success", "Payment confirmed.");
//             navigation.navigate("OrderBill", { orderId });
//           }
//         } catch (err) {
//           console.error("Polling failed:", err);
//         }
//       }, 3000);
//     }

//     return () => interval && clearInterval(interval);
//   }, [polling]);

//   return (
//     <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
//       <ActivityIndicator size="large" />
//     </View>
//   );
// }

import { useEffect } from 'react';
import { Linking, Alert } from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import axios from '../../utils/axiosInstance';

export default function PayPalPaymentScreen() {
  const route = useRoute();
  const navigation = useNavigation();
  const { billId } = route.params;

  useEffect(() => {
    const createAndRedirect = async () => {
      try {
        const res = await axios.post(`/payments/create?billId=${billId}&client=mobile`);
        const { approvalUrl } = res.data;

        if (!approvalUrl) {
          console.error("Missing approvalUrl from server");
          Alert.alert("Error", "Could not get PayPal link");
          return;
        }

        Linking.openURL(approvalUrl);
      } catch (err) {
        console.error('Failed to create PayPal order', err);
        Alert.alert("Payment Error", "Unable to start PayPal payment.");
      }
    };

    const handleDeepLink = ({ url }) => {
      const parsed = Linking.parse(url);
      const { token, billId: returnedBillId } = parsed.queryParams || {};

      if (parsed.hostname === 'paypal-success' && token && returnedBillId) {
        axios
          .get(`/payments/success?orderId=${token}&billId=${returnedBillId}`)
          .then(() => {
            navigation.navigate('OrderBill', { orderId: returnedBillId });
          })
          .catch(err => {
            console.error("Payment confirmation failed", err);
            Alert.alert("Error", "Payment confirmation failed.");
          });
      }
    };

    createAndRedirect();

    const subscription = Linking.addEventListener('url', handleDeepLink);
    return () => subscription.remove();
  }, []);

  return null;
}

