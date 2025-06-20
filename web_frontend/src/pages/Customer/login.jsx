// import React, { useState } from "react";
// import { useNavigate } from "react-router-dom";

// const Login = () => {
//   const navigate = useNavigate();

//   const [formData, setFormData] = useState({
//     username: "",
//     password: "",
//   });

//   const [otp, setOtp] = useState("");
//   const [step, setStep] = useState(1);
//   const [error, setError] = useState("");

//   const handleChange = (e) => {
//     setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
//   };

//   const handleLogin = async (e) => {
//     e.preventDefault();
//     setError("");

//     try {
//       const res = await fetch("http://localhost:8080/login", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify(formData),
//       });

//       const data = await res.text();

//       if (res.ok) {
//         alert(data);
//         setStep(2);
//       } else {
//         setError(data);
//       }
//     } catch (err) {
//       setError("Something went wrong. Try again.");
//       console.error(err);
//     }
//   };

//   const handleOtpVerify = async (e) => {
//     e.preventDefault();
//     setError("");

//     try {
//       const url = `http://localhost:8080/verify-otp?username=${formData.username}&otp=${otp}`;
//       const res = await fetch(url, { method: "POST" });

//       const data = await res.json();

//       if (res.ok) {
//         alert("Login successful!");
//         localStorage.setItem("token", data.jwtToken);
//         localStorage.setItem("role", data.role);

//         switch (data.role) {
//           case "CUSTOMER":
//             navigate("/customer/dashboard");
//             break;
//           case "SERVICE_PROVIDER":
//             navigate("/provider/dashboard");
//             break;
//           case "DELIVERY_AGENT":
//             navigate("/agent/dashboard");
//             break;
//           case "ADMIN":
//             navigate("/admin/dashboard");
//             break;
//           default:
//             alert("Unknown user role");
//         }
//       } else {
//         setError(data.message || "OTP verification failed.");
//       }
//     } catch (err) {
//       setError("Error verifying OTP.");
//       console.error(err);
//     }
//   };

//   return (
//     <div className="min-h-screen flex items-center justify-center bg-background p-4">
//       <div className="max-w-md w-full bg-card p-8 rounded-2xl shadow-2xl border border-border">
//         <h2 className="text-3xl font-bold text-center text-primary mb-6">
//           Login
//         </h2>

//         {step === 1 ? (
//           <form onSubmit={handleLogin} className="space-y-4">
//             <div>
//               <label className="block font-medium text-text mb-1">
//                 Phone or Email
//               </label>
//               <input
//                 type="text"
//                 name="username"
//                 value={formData.username}
//                 onChange={handleChange}
//                 className="input"
//                 required
//               />
//             </div>

//             <div>
//               <label className="block font-medium text-text mb-1">
//                 Password
//               </label>
//               <input
//                 type="password"
//                 name="password"
//                 value={formData.password}
//                 onChange={handleChange}
//                 className="input"
//                 required
//               />
//             </div>

//             {error && <p className="text-error text-sm">{error}</p>}

//             <button
//               type="submit"
//               className="w-full py-3 bg-primary text-white font-semibold rounded-lg hover:bg-hover transition"
//             >
//               Send OTP
//             </button>
//           </form>
//         ) : (
//           <form onSubmit={handleOtpVerify} className="space-y-4">
//             <div>
//               <label className="block font-medium text-text mb-1">
//                 Enter OTP
//               </label>
//               <input
//                 type="text"
//                 value={otp}
//                 onChange={(e) => setOtp(e.target.value)}
//                 className="input"
//                 required
//               />
//             </div>

//             {error && <p className="text-error text-sm">{error}</p>}

//             <button
//               type="submit"
//               className="w-full py-3 bg-primary text-white font-semibold rounded-lg hover:bg-hover transition"
//             >
//               Verify OTP & Login
//             </button>
//           </form>
//         )}
//       </div>
//     </div>
//   );
// };

// export default Login;



import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../../components/AuthLayout";


const Login = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [otp, setOtp] = useState("");
  const [step, setStep] = useState(1);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const res = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      const data = await res.text();

      if (res.ok) {
        alert(data);
        setStep(2);
      } else {
        setError(data);
      }
    } catch (err) {
      setError("Something went wrong. Try again.");
      console.error(err);
    }
  };

  const handleOtpVerify = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const url = `http://localhost:8080/verify-otp?username=${formData.username}&otp=${otp}`;
      const res = await fetch(url, { method: "POST" });

      const data = await res.json();

      if (res.ok) {
        alert("Login successful!");
        localStorage.setItem("token", data.jwtToken);
        localStorage.setItem("role", data.role);

        switch (data.role) {
          case "CUSTOMER":
            navigate("/customer/dashboard");
            break;
          case "SERVICE_PROVIDER":
            navigate("/provider/dashboard");
            break;
          case "DELIVERY_AGENT":
            navigate("/agent/dashboard");
            break;
          case "ADMIN":
            navigate("/admin/dashboard");
            break;
          default:
            alert("Unknown user role");
        }
      } else {
        setError(data.message || "OTP verification failed.");
      }
    } catch (err) {
      setError("Error verifying OTP.");
      console.error(err);
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
            <label className="block font-medium text-gray-700 mb-1">
              Password
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
              placeholder="Enter password"
              required
            />
          </div>

          {error && <p className="text-sm text-red-500 text-center">{error}</p>}

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
            <label className="block font-medium text-gray-700 mb-1">
              Enter OTP
            </label>
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
