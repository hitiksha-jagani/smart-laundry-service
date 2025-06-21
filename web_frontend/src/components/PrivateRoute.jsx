import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function PrivateRoute({ children, roles = [] }) {
  const { isLoggedIn, role } = useAuth();

  // Not logged in
  if (!isLoggedIn) return <Navigate to="/login" replace />;

  // Logged in but role not allowed
  if (roles.length > 0 && !roles.includes(role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
