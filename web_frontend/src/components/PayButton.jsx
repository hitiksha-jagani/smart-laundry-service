// import React from "react";
// import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
// import axios from "../utils/axiosInstance";
// import { useNavigate } from "react-router-dom";
// //components/PayButton.jsx
// export default function PayButton({ billId, finalPrice, orderId }) {
//   const navigate = useNavigate();

//   const handleCreateOrder = () => {
//     return axios
//       .post(`/payments/create?billId=${billId}`)
//       .then(res => res.data.orderID);
//   };

//   const handleApprove = (data, actions) => {
//     return axios
//       .get(`/payments/success?orderId=${data.orderID}&billId=${billId}`)
//       .then(() => {
//         // ✅ Redirect back to the bill page for the order
//         navigate(`/orders/${orderId}/bill`);
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
//         createOrder={handleCreateOrder}
//         onApprove={handleApprove}
//       />
//     </PayPalScriptProvider>
//   );
// }