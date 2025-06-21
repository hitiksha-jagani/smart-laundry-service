// import React, { useState, useEffect } from "react";
// import InputField from "../components/InputField";
// import SelectField from "../components/SelectField";

// const Register = () => {
//   const [formData, setFormData] = useState({
//     firstName: "",
//     lastName: "",
//     email: "",
//     phone: "",
//     password: "",
//     confirmPassword: "",
//     role: "CUSTOMER",
//     addresses: {
//       name: "",
//       areaName: "",
//       pincode: "",
//       cityId: "",
//     },
//   });

//   const [roles, setRoles] = useState([]);
//   const [cities, setCities] = useState([]);
//   const [error, setError] = useState("");

//   useEffect(() => {
//     fetch("http://localhost:8080/roles")
//       .then((res) => res.json())
//       .then((data) => setRoles(data))
//       .catch((err) => console.error("Error fetching roles:", err));

//     fetch("http://localhost:8080/cities")
//       .then((res) => res.json())
//       .then((data) => {
//         console.log("Fetched cities:", data);
//         setCities(data);
//       })
//       .catch((err) => console.error("Error fetching cities:", err));
//   }, []);

//   const handleChange = (e) => {
//     const { name, value } = e.target;
//     if (["name", "areaName", "pincode", "cityId"].includes(name)) {
//       setFormData((prev) => ({
//         ...prev,
//         addresses: { ...prev.addresses, [name]: value },
//       }));
//     } else {
//       setFormData((prev) => ({ ...prev, [name]: value }));
//     }
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     setError("");

//     if (!formData.addresses.cityId) {
//       setError("Please select a city.");
//       return;
//     }

//     try {
//       const res = await fetch("http://localhost:8080/register", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({
//           ...formData,
//           addresses: {
//             ...formData.addresses,
//             cityId: parseInt(formData.addresses.cityId),
//           },
//         }),
//       });

//       if (res.ok) {
//         await res.json();
//         alert("✅ Registration successful!");
//       } else {
//         const errorData = await res.json();
//         setError(errorData.message || "Registration failed");
//       }
//     } catch (error) {
//       console.error("Error:", error);
//       setError("Something went wrong");
//     }
//   };

//   return (
//     <div className="min-h-screen flex items-center justify-center bg-background px-4">
//       <div className="w-full max-w-xl p-8 bg-white shadow-2xl rounded-2xl">
//         <h2 className="text-3xl font-bold text-center text-accent mb-6">
//           Create an Account
//         </h2>
//         <form onSubmit={handleSubmit} className="space-y-4">
//           <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//             <InputField
//               label="First Name"
//               name="firstName"
//               value={formData.firstName}
//               onChange={handleChange}
//               placeholder="Enter your first name"
//             />
//             <InputField
//               label="Last Name"
//               name="lastName"
//               value={formData.lastName}
//               onChange={handleChange}
//               placeholder="Enter your last name"
//             />
//           </div>

//           <InputField
//             label="Email"
//             name="email"
//             type="email"
//             value={formData.email}
//             onChange={handleChange}
//             placeholder="Enter your email (optional)"
//           />

//           <InputField
//             label="Phone Number"
//             name="phone"
//             value={formData.phone}
//             onChange={handleChange}
//             placeholder="Enter 10-digit phone number"
//           />

//           <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//             <InputField
//               label="Password"
//               name="password"
//               type="password"
//               value={formData.password}
//               onChange={handleChange}
//               placeholder="Enter password"
//             />
//             <InputField
//               label="Confirm Password"
//               name="confirmPassword"
//               type="password"
//               value={formData.confirmPassword}
//               onChange={handleChange}
//               placeholder="Re-enter password"
//             />
//           </div>

//           <div>
//             <label className="block text-text mb-1 font-medium">Role</label>
//             <select
//               name="role"
//               value={formData.role}
//               onChange={handleChange}
//               className="input"
//             >
//               <option value="">Select role</option>
//               {roles.map((role) => (
//                 <option key={role} value={role}>
//                   {role}
//                 </option>
//               ))}
//             </select>
//           </div>

//           <div className="pt-2 border-t border-gray-200">
//             <h3 className="text-lg font-semibold text-accent mb-2">
//               Address Info
//             </h3>

//             <InputField
//               label="Name"
//               name="name"
//               value={formData.addresses.name}
//               onChange={handleChange}
//               placeholder="Contact name"
//             />
//             <InputField
//               label="Area Name"
//               name="areaName"
//               value={formData.addresses.areaName}
//               onChange={handleChange}
//               placeholder="Colony, street, etc."
//             />
//             <InputField
//               label="Pincode"
//               name="pincode"
//               value={formData.addresses.pincode}
//               onChange={handleChange}
//               placeholder="6-digit pincode"
//             />
//             <div>
//               <label className="block text-text mb-1 font-medium">City</label>
//               <select
//                 name="cityId"
//                 value={formData.addresses.cityId}
//                 onChange={handleChange}
//                 className="input"
//               >
//                 <option value="">Select City</option>
//                 {cities.map((city) => (
//                   <option key={city.cityId} value={city.cityId}>
//                     {city.name}, {city.state?.name}
//                   </option>
//                 ))}
//               </select>
//             </div>
//           </div>

//           {error && <p className="text-red-600">{error}</p>}

//           <button
//             type="submit"
//             className="w-full py-3 mt-4 bg-accent text-white font-semibold rounded-lg hover:bg-highlight transition"
//           >
//             Register
//           </button>

//           <p className="text-center text-sm text-gray-600 mt-4">
//             Already have an account?{" "}
//             <a
//               href="/login"
//               className="text-accent font-medium hover:underline"
//             >
//               Login here
//             </a>
//           </p>
//         </form>
//       </div>
//     </div>
//   );
// };

// export default Register;

import React, { useState, useEffect } from "react";
import InputField from "../../components/InputField";
import SelectField from "../../components/SelectField";
import AuthLayout from "../../components/AuthLayout";

const Register = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    password: "",
    confirmPassword: "",
    role: "CUSTOMER",
    addresses: {
      name: "",
      areaName: "",
      pincode: "",
      cityId: "",
    },
  });

  const [roles, setRoles] = useState([]);
  const [cities, setCities] = useState([]);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    fetch("http://localhost:8080/roles")
      .then((res) => res.json())
      .then((data) => setRoles(data))
      .catch((err) => console.error("Error fetching roles:", err));

    fetch("http://localhost:8080/cities")
      .then((res) => res.json())
      .then((data) => setCities(data))
      .catch((err) => console.error("Error fetching cities:", err));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setErrors({ ...errors, [name]: null }); // clear individual field error
    if (["name", "areaName", "pincode", "cityId"].includes(name)) {
      setFormData((prev) => ({
        ...prev,
        addresses: { ...prev.addresses, [name]: value },
      }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const validateFields = () => {
    const newErrors = {};
    if (!formData.firstName) newErrors.firstName = "First name is required";
    if (!formData.phone || formData.phone.length !== 10)
      newErrors.phone = "Valid 10-digit phone is required";
    if (formData.password.length < 6)
      newErrors.password = "Password must be at least 6 characters";
    if (formData.password !== formData.confirmPassword)
      newErrors.confirmPassword = "Passwords do not match";
    if (!formData.addresses.cityId)
      newErrors.cityId = "City selection is required";
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const fieldErrors = validateFields();
    if (Object.keys(fieldErrors).length > 0) {
      setErrors(fieldErrors);
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...formData,
          addresses: {
            ...formData.addresses,
            cityId: parseInt(formData.addresses.cityId),
          },
        }),
      });

      if (res.ok) {
        await res.json();
        alert("✅ Registration successful!");
      } else {
        const errorData = await res.json();
        setErrors({ general: errorData.message || "Registration failed" });
      }
    } catch (error) {
      console.error("Error:", error);
      setErrors({ general: "Something went wrong" });
    }
  };

  return (
    <AuthLayout title="Create Your Account" widthClass="max-w-md">
      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <InputField
            label="First Name"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            placeholder="Enter your first name"
            error={errors.firstName}
          />
          <InputField
            label="Last Name"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            placeholder="Enter your last name"
          />
        </div>

        <InputField
          label="Email"
          name="email"
          type="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="Enter your email (optional)"
        />
        <InputField
          label="Phone Number"
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          placeholder="Enter 10-digit phone number"
          error={errors.phone}
        />

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <InputField
            label="Password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Enter password"
            error={errors.password}
          />
          <InputField
            label="Confirm Password"
            name="confirmPassword"
            type="password"
            value={formData.confirmPassword}
            onChange={handleChange}
            placeholder="Re-enter password"
            error={errors.confirmPassword}
          />
        </div>

        <SelectField
          name="role"
          value={formData.role}
          onChange={handleChange}
          options={roles.map((role) => ({ value: role, label: role }))}
          placeholder="Select Role"
        />

        <div className="pt-4 border-t border-gray-300">
          <h3 className="text-lg font-semibold text-[#4B00B5] mb-3">
            Address Info
          </h3>
          <InputField
            label="Name"
            name="name"
            value={formData.addresses.name}
            onChange={handleChange}
            placeholder="Full name for delivery contact"
          />
          <InputField
            label="Area Name"
            name="areaName"
            value={formData.addresses.areaName}
            onChange={handleChange}
            placeholder="Street, society, etc."
          />
          <InputField
            label="Pincode"
            name="pincode"
            value={formData.addresses.pincode}
            onChange={handleChange}
            placeholder="6-digit pincode"
          />
          <SelectField
            name="cityId"
            value={formData.addresses.cityId}
            onChange={handleChange}
            options={cities.map((city) => ({
              value: city.cityId,
              label: `${city.name}, ${city.state?.name}`,
            }))}
            placeholder="Select City"
            error={errors.cityId}
          />
        </div>

        {errors.general && (
          <p className="text-sm text-red-500 text-center mt-2">{errors.general}</p>
        )}

        <button
          type="submit"
          className="w-full py-3 bg-[#A566FF] text-white font-semibold rounded-lg hover:bg-[#914be3] transition"
        >
          Register
        </button>

        <p className="text-center text-sm text-muted mt-4">
          Already have an account?{" "}
          <a
            href="/login"
            className="text-[#A566FF] hover:text-[#FF6AC2] font-medium"
          >
            Login here
          </a>
        </p>
      </form>
    </AuthLayout>
  );
};

export default Register;
