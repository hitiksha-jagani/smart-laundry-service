// import React, { createContext, useContext, useEffect, useState } from 'react';
// import AsyncStorage from '@react-native-async-storage/async-storage';

// const AuthContext = createContext();

// export const AuthProvider = ({ children }) => {
//   const [token, setToken] = useState(null);
//   const [role, setRole] = useState(null);
//   const [userId, setUserId] = useState(null);
//   const [providerId, setProviderId] = useState(null);
//   const [agentId, setAgentId] = useState(null);
//   const [loading, setLoading] = useState(true); // for splash or initial check

//   useEffect(() => {
//     const loadStoredData = async () => {
//       try {
//         const storedToken = await AsyncStorage.getItem('token');
//         const storedRole = await AsyncStorage.getItem('role');
//         const storedUserId = await AsyncStorage.getItem('userId');
//         const storedProviderId = await AsyncStorage.getItem('providerId');

//         if (storedToken) setToken(storedToken);
//         if (storedRole) setRole(storedRole);
//         if (storedUserId) setUserId(storedUserId);
//         if (storedProviderId) setProviderId(storedProviderId);
//       } catch (e) {
//         console.error('Failed to load auth data from storage', e);
//       } finally {
//         setLoading(false);
//       }
//     };

//     loadStoredData();
//   }, []);

//   const login = async (jwtToken, userRole, userIdFromToken, providerIdFromDb) => {
//     try {
//       await AsyncStorage.setItem('token', jwtToken);
//       await AsyncStorage.setItem('role', userRole);
//       await AsyncStorage.setItem('userId', userIdFromToken);

//       if (providerIdFromDb) {
//         await AsyncStorage.setItem('providerId', providerIdFromDb);
//         setProviderId(providerIdFromDb);
//       }

//       setToken(jwtToken);
//       setRole(userRole);
//       setUserId(userIdFromToken);
//     } catch (e) {
//       console.error('Error storing login data', e);
//     }
//   };

//   const logout = async () => {
//     try {
//       await AsyncStorage.removeItem('token');
//       await AsyncStorage.removeItem('role');
//       await AsyncStorage.removeItem('userId');
//       await AsyncStorage.removeItem('providerId');

//       setToken(null);
//       setRole(null);
//       setUserId(null);
//       setProviderId(null);
//     } catch (e) {
//       console.error('Error clearing auth data', e);
//     }
//   };

//   const isLoggedIn = !!token;

//   return (
//     <AuthContext.Provider
//       value={{
//         token,
//         role,
//         userId,
//         providerId,
//         isLoggedIn,
//         login,
//         logout,
//         loading,
//       }}
//     >
//       {!loading && children}
//     </AuthContext.Provider>
//   );
// };

// export const useAuth = () => useContext(AuthContext);


import React, { createContext, useContext, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(null);
  const [role, setRole] = useState(null);
  const [userId, setUserId] = useState(null);
  const [providerId, setProviderId] = useState(null);
  const [agentId, setAgentId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadAuthData = async () => {
      try {
        const storedToken = await AsyncStorage.getItem('token');
        const storedRole = await AsyncStorage.getItem('role');
        const storedUserId = await AsyncStorage.getItem('userId');
        const storedProviderId = await AsyncStorage.getItem('providerId');
        const storedAgentId = await AsyncStorage.getItem('agentId');

        if (storedToken) setToken(storedToken);
        if (storedRole) setRole(storedRole);
        if (storedUserId) setUserId(storedUserId);
        if (storedProviderId) setProviderId(storedProviderId);
        if (storedAgentId) setAgentId(storedAgentId);
      } catch (error) {
        console.error('Failed to load auth data:', error);
      } finally {
        setLoading(false);
      }
    };

    loadAuthData();
  }, []);

  const login = async (jwtToken, userRole, userIdFromToken, providerIdFromDb, agentIdFromDb) => {
    try {
      await AsyncStorage.setItem('token', jwtToken);
      await AsyncStorage.setItem('role', userRole);
      await AsyncStorage.setItem('userId', userIdFromToken);

      setToken(jwtToken);
      setRole(userRole);
      setUserId(userIdFromToken);

      if (providerIdFromDb) {
        await AsyncStorage.setItem('providerId', providerIdFromDb);
        setProviderId(providerIdFromDb);
      }

      if (agentIdFromDb) {
        await AsyncStorage.setItem('agentId', agentIdFromDb);
        setAgentId(agentIdFromDb);
      }
    } catch (error) {
      console.error('Failed to store auth data:', error);
    }
  };

  const logout = async () => {
    try {
      await AsyncStorage.multiRemove(['token', 'role', 'userId', 'providerId', 'agentId']);

      setToken(null);
      setRole(null);
      setUserId(null);
      setProviderId(null);
      setAgentId(null);
    } catch (error) {
      console.error('Failed to logout:', error);
    }
  };

  const isLoggedIn = !!token;

  return (
    <AuthContext.Provider
      value={{
        token,
        role,
        userId,
        providerId,
        agentId,
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
