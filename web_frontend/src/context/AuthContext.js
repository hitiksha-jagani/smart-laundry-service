import React, { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(null);
  const [role, setRole] = useState(null);
  const [userId, setUserId] = useState(null);
  const [providerId, setProviderId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    const storedRole = localStorage.getItem("role");
    const storedUserId = localStorage.getItem("userId");
    const storedProviderId = localStorage.getItem("providerId");

    if (storedToken) setToken(storedToken);
    if (storedRole) setRole(storedRole);
    if (storedUserId) setUserId(storedUserId);
    if (storedProviderId) setProviderId(storedProviderId);

    setLoading(false);
  }, []);

  const login = (jwtToken, userRole, userIdFromToken, providerIdFromDb) => {
    localStorage.setItem("token", jwtToken);
    localStorage.setItem("role", userRole);
    localStorage.setItem("userId", userIdFromToken);

    if (providerIdFromDb) {
      localStorage.setItem("providerId", providerIdFromDb);
      setProviderId(providerIdFromDb);
    }

    setToken(jwtToken);
    setRole(userRole);
    setUserId(userIdFromToken);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("userId");
    localStorage.removeItem("providerId");

    setToken(null);
    setRole(null);
    setUserId(null);
    setProviderId(null);
  };

  const isLoggedIn = !!token;

  return (
    <AuthContext.Provider
      value={{
        token,
        role,
        userId,
        providerId,
        isLoggedIn,
        login,
        logout,
        loading,
      }}
    >
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
