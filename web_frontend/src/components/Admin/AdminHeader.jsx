// Author: Hitiksha Patel
// Description: Header bar with logo on left and user info + logout on right

import React from "react";
import { FiLogOut } from "react-icons/fi"; 
import Profile  from "../../assets/avatar-icon.png";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import "../../styles/Admin/AdminHeader.css";

const AdminHeader = ({ userName }) => { 
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
    
    <div className="admin-app-header inter-font">

        <div className="header-left">
            <h1 className="h1-admin">Smart Laundry</h1>
        </div>

        <div className="admin-header-right">
            
            <img className="admin-profile-image" src={Profile} alt="" />
            <span className="admin-user-name">{userName || "User"}</span>
            <FiLogOut className="admin-logout-icon" onClick={handleLogout} />

        </div>

    </div>

  );
};

export default AdminHeader;
