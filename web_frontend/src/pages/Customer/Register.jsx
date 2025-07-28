import React, { useState, useEffect } from "react";
import InputField from "../../components/InputField";
import SelectField from "../../components/SelectField";
import AuthLayout from "../../components/AuthLayout";
import { Eye, EyeOff } from "lucide-react";
import axios from "../../utils/axiosInstance";
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
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

useEffect(() => {
  axios.get("/roles")
    .then((res) => setRoles(res.data))
    .catch((err) => console.error("Error fetching roles:", err));

  axios.get("/cities")
    .then((res) => setCities(res.data))
    .catch((err) => console.error("Error fetching cities:", err));
}, []);



  const handleChange = (e) => {
    const { name, value } = e.target;
    setErrors({ ...errors, [name]: null });

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
  await axios.post("/register", {
    ...formData,
    addresses: {
      ...formData.addresses,
      cityId: parseInt(formData.addresses.cityId),
    },
  });
  alert("✅ Registration successful!");
} catch (error) {
  console.error("Error:", error);
  setErrors({ general: error.response?.data?.message || "Something went wrong" });
}

  };

  return (
    <AuthLayout title="Create Your Account" widthClass="max-w-4xl">
      <form onSubmit={handleSubmit} className="space-y-6 max-w-4xl mx-auto">
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
          <div>
            <label className="block font-medium mb-1">Password</label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter password"
                className="w-full border border-gray-300 rounded-lg px-4 py-2 pr-10 focus:outline-none focus:ring-2 focus:ring-purple-400"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
            {errors.password && (
              <p className="text-sm text-red-500 mt-1">{errors.password}</p>
            )}
          </div>

          <div>
            <label className="block font-medium mb-1">Confirm Password</label>
            <div className="relative">
              <input
                type={showConfirmPassword ? "text" : "password"}
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Re-enter password"
                className="w-full border border-gray-300 rounded-lg px-4 py-2 pr-10 focus:outline-none focus:ring-2 focus:ring-purple-400"
              />
              <button
                type="button"
                onClick={() =>
                  setShowConfirmPassword(!showConfirmPassword)
                }
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
              >
                {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
            {errors.confirmPassword && (
              <p className="text-sm text-red-500 mt-1">{errors.confirmPassword}</p>
            )}
          </div>
        </div>

        <SelectField
          name="role"
          value={formData.role}
          onChange={handleChange}
          options={roles.map((role) => ({
            value: role,
            label: role,
          }))}
          placeholder="Select Role"
        />

        <div className="pt-4 border-t border-gray-300">
          <h3 className="text-lg font-semibold text-purple-700 mb-3">
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
          <p className="text-sm text-red-500 text-center">{errors.general}</p>
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
