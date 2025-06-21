// Author: Hitiksha Patel
// Description: Header bar with logo on left and user info + logout on right

import React from "react";
import { FiLogOut } from "react-icons/fi"; 
import Profile  from "../../assets/avatar-icon.png";
import { FiAvr } from "react-icons/ai";
import "../../styles/DeliveryAgent/Header.css";

const Header = ({ userName }) => {

    const handleLogout = () => {
        localStorage.clear();
        window.location.href = "/login";
    };

  return (
    
    <div className="app-header inter-font">

        <div className="header-left">
            <h1>Smart Laundry</h1>
        </div>

        <div className="header-right">
            {/* <span className="user-name">{userName || "User"}</span> */}
            <img className="profile-image" src={Profile} alt="" />
            <span className="user-name">{userName || "User"}</span>
            <FiLogOut className="logout-icon" onClick={handleLogout} />
        </div>

    </div>

  );
};

export default Header;
