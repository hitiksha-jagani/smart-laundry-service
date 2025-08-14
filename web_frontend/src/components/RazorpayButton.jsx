import axios from "../utils/axiosInstance";
import { loadRazorpayScript } from "../utils/razorpay";

const RazorpayButton = ({ invoiceNumber, finalPrice, orderId }) => {
  const handlePayment = async () => {
    if (!invoiceNumber) {
      alert("Invalid invoice number. Cannot proceed with payment.");
      return;
    }

    const loaded = await loadRazorpayScript();

    if (!loaded || !window.Razorpay) {
      alert("Failed to load Razorpay SDK. Please check your internet.");
      return;
    }

    try {
      const res = await axios.post(
      `${process.env.REACT_APP_API_URL}/payments/create/${invoiceNumber}`);
      console.log("Invoice Number:",invoiceNumber);
      const { orderId: razorpayOrderId, amount } = res.data;
      const key = process.env.REACT_APP_RAZORPAY_KEY;
       console.log("Key:",key);
      const options = {
        key,
        amount,
        currency: "INR",
        name: "Smart Laundry Service",
        description: `Payment for Invoice ${invoiceNumber}`,
        order_id: razorpayOrderId,
        handler: async function (response) {
          const payload = {
            paymentId: response.razorpay_payment_id,
            invoiceNumber,
            method: "RAZORPAY",
          };

          try {
            await axios.post(`${process.env.REACT_APP_API_URL}/payments/success`, payload);
            alert("✅ Payment successful! Redirecting to your bill...");

            // Wait 2 seconds then redirect to bill page
            setTimeout(() => {
              window.location.href = `/orders/${orderId}/bill`;
            }, 2000);
          } catch (err) {
            console.error("❌ Payment storing failed:", err);
            alert("Payment processed, but something went wrong saving it.");
          }
        }
        ,
        prefill: {
          name: "Smart Laundry Customer",
          email: "customer@example.com",
          contact: "9000090000",
        },
        notes: {
          invoiceNumber,
          appOrderId: orderId || "N/A",
        },
        theme: {
          color: "#0d6efd",
        },
      };

      const rzp = new window.Razorpay(options);
      rzp.on("payment.failed", function (response) {
        alert("Payment failed or cancelled. Please try again.");
        console.warn("Payment failed:", response);
      });

      rzp.open();
    } catch (err) {
      console.error("❌ Error creating Razorpay order:", err);
      alert("Something went wrong while initiating the payment.");
    }
  };

  return (
    <button
      onClick={handlePayment}
      className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded"
    >
      Pay ₹{finalPrice} with Razorpay
    </button>
  );
};

export default RazorpayButton;