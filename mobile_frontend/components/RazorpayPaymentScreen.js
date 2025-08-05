import React, { useEffect, useState, useRef } from "react";
import { View, ActivityIndicator, Alert } from "react-native";
import { WebView } from "react-native-webview";
import { useNavigation, useRoute } from "@react-navigation/native";
import axiosInstance from "../utils/axiosInstance";

const RazorpayPaymentScreen = () => {
  const route = useRoute();
  const navigation = useNavigation();
  const webViewRef = useRef(null);

  const { invoiceNumber, finalPrice, orderId } = route.params;
  const [checkoutUrl, setCheckoutUrl] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initiateRazorpayOrder = async () => {
      if (!invoiceNumber) {
        Alert.alert("Error", "Invalid invoice number.");
        return;
      }

      try {
        const res = await axiosInstance.post(`api/payments/create/${invoiceNumber}`);
        const { orderId: razorpayOrderId, amount } = res.data;

        const key = "rzp_test_oBh8qMRL8F61TR"; // Replace with your real Razorpay key

        const checkoutHTML = `
          <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
              <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
            </head>
            <body>
              <script>
                var options = {
                  key: "${key}",
                  amount: "${amount}",
                  currency: "INR",
                  name: "Smart Laundry Service",
                  description: "Payment for Invoice ${invoiceNumber}",
                  order_id: "${razorpayOrderId}",
                  handler: function (response) {
                    window.ReactNativeWebView.postMessage(JSON.stringify({
                      success: true,
                      razorpay_payment_id: response.razorpay_payment_id
                    }));
                  },
                  prefill: {
                    name: "Smart Laundry Customer",
                    email: "customer@example.com",
                    contact: "9000090000"
                  },
                  notes: {
                    invoiceNumber: "${invoiceNumber}",
                    appOrderId: "${orderId}"
                  },
                  theme: {
                    color: "#0d6efd"
                  }
                };
                var rzp = new Razorpay(options);
                rzp.on('payment.failed', function (response) {
                  window.ReactNativeWebView.postMessage(JSON.stringify({
                    success: false,
                    error: response.error.description
                  }));
                });
                rzp.open();
              </script>
            </body>
          </html>
        `;

        const encodedHtml = `data:text/html;base64,${btoa(unescape(encodeURIComponent(checkoutHTML)))}`;
        setCheckoutUrl(encodedHtml);
        setLoading(false);
      } catch (err) {
        console.error("Error creating Razorpay order:", err);
        Alert.alert("Error", "Failed to create payment session.");
        setLoading(false);
      }
    };

    initiateRazorpayOrder();
  }, []);

  const handleMessage = async (event) => {
    const data = JSON.parse(event.nativeEvent.data);

    if (data.success) {
      try {
        await axiosInstance.post("api/payments/success", {
          paymentId: data.razorpay_payment_id,
          invoiceNumber,
          method: "RAZORPAY",
        });

        Alert.alert("Success", "Payment successful!");
        navigation.replace("CustomerDashboard");
      } catch (err) {
        console.error("Error saving payment:", err);
        Alert.alert("Error", "Payment succeeded, but saving failed.");
        navigation.replace("CustomerDashboard");
      }
    } else {
      Alert.alert("Payment Failed", data.error || "Payment was unsuccessful. Try again.");
      navigation.goBack();
    }
  };

  return loading ? (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <ActivityIndicator size="large" color="#0d6efd" />
    </View>
  ) : (
    <WebView
      ref={webViewRef}
      originWhitelist={["*"]}
      source={{ uri: checkoutUrl }}
      onMessage={handleMessage}
      javaScriptEnabled
    />
  );
};

export default RazorpayPaymentScreen;
