 // Author : Hitiksha Jagani
// Description : User role filter in admin dashboard.

import React, { useState } from "react";
import sidebar from '../../assets/sliders-icon.svg'
import '../../styles/Admin/TimeFilter.css';

const UserRoleFilter = ({ onChange }) => { 
    const [filter, setFilter] = useState("overall");
    const [showFilterOptions, setShowFilterOptions] = useState(false);

    const toggleDropdown = () => setShowFilterOptions(prev => !prev);

    const handleFilterChange = (e) => {

        const selected = e.target.value;
        setFilter(selected);

        onChange(selected);
        

    };

    return (
        <div className="time-filter-container">

            <button className="time-filter-toggle" onClick={toggleDropdown}>
                <img className="sidebar-icon" src={sidebar} alt="" />
                <span style={{fontSize: '20px'}}>User Role Filter</span>
            </button>

            {showFilterOptions && (

                <div className="time-filter-dropdown">

                    <select value={filter} onChange={handleFilterChange}>

                        <option value="service provider">Service Provider</option>
                        <option value="delivery agent">Delivery Agent</option>

                    </select>

                </div>

            )}

        </div>

    );
};

export default UserRoleFilter;

