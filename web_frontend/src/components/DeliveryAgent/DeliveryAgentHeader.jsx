// Author: Hitiksha Patel
// Description: Header bar with logo on left and user info + logout on right

import React from "react";
import { FiLogOut } from "react-icons/fi"; 
import Profile  from "../../assets/avatar-icon.png";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import "../../styles/DeliveryAgent/DeliveryAgentCommon.css";
import "../../styles/DeliveryAgent/DeliveryAgentHeader.css";

const DeliveryAgentHeader = ({ userName }) => {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
    
    <div className="agent-app-header inter-font">

        <div className="header-left">
            <h1 className="h1-agent">Smart Laundry</h1>
        </div>

        <div className="agent-header-right">
            
            <img className="agent-profile-image" src={Profile} alt="" />
            <span className="agent-user-name">{userName || "User"}</span>
            <FiLogOut className="agent-logout-icon" onClick={handleLogout} />

        </div>

    </div>

  );
};

export default DeliveryAgentHeader;
