import { createContext, useContext } from "react";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const login = (token) => {
    localStorage.setItem("token", token);
  };

  return (
    <AuthContext.Provider value={{ login }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
