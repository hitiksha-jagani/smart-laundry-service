// import React from "react";
// import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
// import axios from "../utils/axiosInstance";

// export default function PayButton({ billId, finalPrice }) {
//   const handleCreateOrder = () => {
//     return axios
//       .post(`/payments/create?billId=${billId}`)
//       .then(res => res.data.orderID); // must return order ID
//   };

//   const handleApprove = (data, actions) => {
//     return axios
//       .get(`/payments/success?orderId=${data.orderID}&billId=${billId}`) // ✅ fixed param name
//       .then(() => {
//         alert("Payment Successful ✅");
//         // Optional: navigate or update bill/payment status
//       })
//       .catch(() => {
//         alert("Payment was successful on PayPal, but server update failed.");
//       });
//   };

//   return (
//     <PayPalScriptProvider options={{
//       "client-id": "AVaLNEbVoQZUqaGlFNnNyPZbABbLLLWEffn5cv987YzCHMeM9Jg4mkMs_N6vTkRcv-VfP5TTf1eEFcYU",
//       currency: "USD"
//     }}>
//       <PayPalButtons
//         style={{ layout: "vertical" }}
//         createOrder={(data, actions) => handleCreateOrder()}
//         onApprove={(data, actions) => handleApprove(data, actions)}
//       />
//     </PayPalScriptProvider>
//   );
// }


// PayButton.jsx
import React from "react";
import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
import axios from "../utils/axiosInstance";
import { useNavigate } from "react-router-dom";

export default function PayButton({ billId, finalPrice, orderId }) {
  const navigate = useNavigate();

  const handleCreateOrder = () => {
    return axios
      .post(`/payments/create?billId=${billId}`)
      .then(res => res.data.orderID);
  };

  const handleApprove = (data, actions) => {
    return axios
      .get(`/payments/success?orderId=${data.orderID}&billId=${billId}`)
      .then(() => {
        // ✅ Redirect back to the bill page for the order
        navigate(`/orders/${orderId}/bill`);
      })
      .catch(() => {
        alert("Payment was successful on PayPal, but server update failed.");
      });
  };

  return (
    <PayPalScriptProvider options={{
      "client-id": "AVaLNEbVoQZUqaGlFNnNyPZbABbLLLWEffn5cv987YzCHMeM9Jg4mkMs_N6vTkRcv-VfP5TTf1eEFcYU",
      currency: "USD"
    }}>
      <PayPalButtons
        style={{ layout: "vertical" }}
        createOrder={handleCreateOrder}
        onApprove={handleApprove}
      />
    </PayPalScriptProvider>
  );
}
