import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function AutoRedirect() {
  const { isLoggedIn, role, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (loading) return;

    if (!isLoggedIn) {
      navigate("/login");
    } else {
      switch (role) {
        case "CUSTOMER":
          navigate("/customer/dashboard");
          break;
        case "SERVICE_PROVIDER":
          navigate("/provider/dashboard");
          break;
        case "DELIVERY_AGENT":
          navigate("/deliveries/summary");
          break;
        case "ADMIN":
          navigate("/admin/dashboard");
          break;
        default:
          navigate("/login");
      }
    }
  }, [isLoggedIn, role, loading, navigate]);

  return null;
}
