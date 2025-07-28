// Author : Hitiksha Jagani
// Description : Revenue type filter for revenue trend page.


import React, { useState } from "react";
import sidebar from '../../assets/sliders-icon.svg'
import '../../styles/Admin/TimeFilter.css';

const RevenueTypeFilter = ({ onChange }) => {
    const [filter, setFilter] = useState("gross sales");
    const [showFilterOptions, setShowFilterOptions] = useState(false);

    const toggleDropdown = () => setShowFilterOptions(prev => !prev);

    const handleFilterChange = (e) => {
        const selected = e.target.value;
        setFilter(selected);
        onChange(selected); // Directly pass value to parent
    };

    return (
        <div className="time-filter-container">
            <button className="time-filter-toggle" onClick={toggleDropdown}>
                <img className="sidebar-icon" src={sidebar} alt="" />
                <span style={{ fontSize: '20px' }}>Revenue Type</span>
            </button>

            {showFilterOptions && (
                <div className="time-filter-dropdown">
                    <select value={filter} onChange={handleFilterChange}>
                        <option value="gross sales">Gross Sales</option>
                        <option value="admin revenue">Admin Revenue</option>
                        <option value="provider payout">Service Provider Payout</option>
                        <option value="delivery payout">Delivery Agent Payout</option>
                        <option value="total payout">Total Payout</option>
                    </select>
                </div>
            )}
        </div>
    );
};

export default RevenueTypeFilter;
