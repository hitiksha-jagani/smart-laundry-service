import { useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import axios from "../utils/axiosInstance";

export default function PaymentSuccess() {
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get("orderId");
  const billId = searchParams.get("billId");

  useEffect(() => {
    if (orderId && billId) {
      axios
        .get(`/payments/success?orderId=${orderId}&billId=${billId}`)
        .then(() => alert("Payment Success 🎉"))
        .catch(() => alert("Error saving payment"));
    }
  }, [orderId, billId]);

  return <div className="p-6 text-center text-xl">Processing your payment...</div>;
}
