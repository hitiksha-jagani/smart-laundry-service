import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../../components/AuthLayout";
import { useAuth } from "../../context/AuthContext";
import { jwtDecode } from "jwt-decode";
import { Eye, EyeOff } from "lucide-react";

const Login = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [formData, setFormData] = useState({ username: "", password: "" });
  const [otp, setOtp] = useState("");
  const [step, setStep] = useState(1);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState(""); 
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setSuccessMessage("");

    try {
      const res = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      const data = await res.text();

      if (res.ok) {
        setSuccessMessage(data); // ✅ show async message
        setTimeout(() => setSuccessMessage(""), 5000); // clear after 5 sec
        setStep(2);
      } else {
        setError(data);
      }
    } catch (err) {
      console.error(err);
      setError("Something went wrong. Try again.");
    }
  };

  const handleOtpVerify = async (e) => {
    e.preventDefault();
    setError("");
    setSuccessMessage("");

    try {
      const url = `http://localhost:8080/verify-otp?username=${formData.username}&otp=${otp}`;
      const res = await fetch(url, { method: "POST" });

      const rawText = await res.text();

      if (!res.ok) {
        setError(rawText || "OTP verification failed.");
        return;
      }

      let data;
      try {
        data = JSON.parse(rawText);
      } catch (e) {
        console.error("Invalid JSON:", rawText);
        setError("Unexpected response from server.");
        return;
      }

      const decoded = jwtDecode(data.jwtToken);
      const userId = decoded.id;

      if (!userId) {
        setError("User ID missing from token.");
        return;
      }

      localStorage.setItem("token", data.jwtToken);
      localStorage.setItem("userId", userId);

     if (data.role === "SERVICE_PROVIDER") {
  try {
    const providerRes = await fetch(
      `http://localhost:8080/provider/orders/from-user/${userId}`,
      {
        headers: {
          Authorization: `Bearer ${data.jwtToken}`,
        },
      }
    );

    if (providerRes.ok) {
      const providerId = await providerRes.text();
      localStorage.setItem("providerId", providerId);
      login(data.jwtToken, data.role, userId, providerId);
      navigate("/provider/dashboard");
    } else if (providerRes.status === 404) {
      login(data.jwtToken, data.role, userId);
      navigate("/provider/completeProfile");
    } else {
      throw new Error("Failed to check service provider status.");
    }
  } catch (err) {
    console.error("Error checking service provider status:", err);
    setError("Unable to retrieve service provider info.");
  }
}
 else if (data.role === "DELIVERY_AGENT") {
  login(data.jwtToken, data.role, userId); 

  try {
    const agentRes = await fetch(`http://localhost:8080/profile/exist/${userId}`);


    if (!agentRes.ok) {
      throw new Error("Failed to check agent existence.");
    }

    const exists = await agentRes.json();

    if (exists) {
      navigate("/deliveries/summary");
    } else {
      navigate("/profile/complete");
    }
  } catch (err) {
    console.error("Error checking delivery agent existence:", err);
    setError("Unable to verify agent profile. Try again.");
  }
}
 else {
        login(data.jwtToken, data.role, userId);
        switch (data.role) {
          case "CUSTOMER":
            navigate("/customer/dashboard");
            break;
          case "ADMIN":
            navigate("/revenue/summary");
            break;
          default:
            setError("Unknown user role");
        }
      }
    } catch (err) {
      console.error(err);
      setError("Error verifying OTP.");
    }
  };

  return (
    <AuthLayout title="Login to Your Account">
      {step === 1 ? (
        <form onSubmit={handleLogin} className="space-y-6">
          <div>
            <label className="block font-medium text-gray-700 mb-1">
              Phone or Email
            </label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
              placeholder="Enter phone or email"
              required
            />
          </div>

          <div>
            <label className="block font-medium text-gray-700 mb-1">Password</label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                autoComplete="current-password"
                value={formData.password}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 pr-12 focus:outline-none focus:ring-2 focus:ring-purple-400"
                placeholder="Enter password"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 focus:outline-none"
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
          </div>

          {error && <p className="text-sm text-red-500 text-center">{error}</p>}
          {successMessage && (
            <p className="text-sm text-green-600 text-center">{successMessage}</p>
          )}

          <button
            type="submit"
            className="w-full py-3 bg-[#A566FF] text-white font-semibold rounded-lg hover:bg-[#914be3] transition"
          >
            Send OTP
          </button>

          <p className="text-center text-sm text-muted mt-4">
            Don’t have an account?{" "}
            <a
              href="/register"
              className="text-[#A566FF] hover:text-[#FF6AC2] font-medium"
            >
              Register here
            </a>
          </p>
        </form>
      ) : (
        <form onSubmit={handleOtpVerify} className="space-y-6">
          <div>
            <label className="block font-medium text-gray-700 mb-1">Enter OTP</label>
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
              placeholder="Enter 6-digit OTP"
              required
            />
          </div>
          {error && <p className="text-sm text-red-500 text-center">{error}</p>}
          {successMessage && (
            <p className="text-sm text-green-600 text-center">{successMessage}</p>
          )}
          <button
            type="submit"
            className="w-full py-3 bg-[#A566FF] text-white font-semibold rounded-lg hover:bg-[#914be3] transition"
          >
            Verify OTP & Login
          </button>
        </form>
      )}
    </AuthLayout>
  );
};

export default Login;