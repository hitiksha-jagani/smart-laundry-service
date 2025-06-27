// Author : Hitiksha Jagani
// Description : Time filter in admin dashboard.

import React, { useState } from "react";
import sidebar from '../../assets/sliders-icon.svg'
import '../../styles/Admin/TimeFilter.css';

const TimeFilter = ({ onChange }) => {
    const [filter, setFilter] = useState("overall");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [showFilterOptions, setShowFilterOptions] = useState(false);

    const toggleDropdown = () => setShowFilterOptions(prev => !prev);

    const handleFilterChange = (e) => {
        const selected = e.target.value;
        setFilter(selected);
        if (selected !== "custom") {
            onChange({ filter: selected });
        } else if (startDate && endDate) {
            onChange({ filter: selected, startDate, endDate });
        }
    };

    const handleDateChange = (type, value) => {
        if (type === "start") {
            setStartDate(value);
            if (filter === "custom" && value && endDate) {
                onChange({ filter, startDate: value, endDate });
            }
        } else {
            setEndDate(value);
            if (filter === "custom" && startDate && value) {
                onChange({ filter, startDate, endDate: value });
            }
        }
    };

    return (
        <div className="time-filter-container">
            <button className="time-filter-toggle admin-sidebar-link" onClick={toggleDropdown}>
                <img className="sidebar-icon" src={sidebar} alt="" />
                <span style={{fontSize: '20px'}}>Time Filter</span>
            </button>

            {showFilterOptions && (
                <div className="time-filter-dropdown">
                    <select value={filter} onChange={handleFilterChange}>
                        <option value="overall">Overall</option>
                        <option value="today">Today</option>
                        <option value="this week">This Week</option>
                        <option value="this month">This Month</option>
                        <option value="custom">Custom Range</option>
                    </select>

                    {filter === "custom" && (
                        <div className="date-range-inputs">
                            <input
                                type="date"
                                value={startDate}
                                onChange={(e) => handleDateChange("start", e.target.value)}
                            />
                            <span>to</span>
                            <input
                                type="date"
                                value={endDate}
                                onChange={(e) => handleDateChange("end", e.target.value)}
                            />
                        </div>
                    )}
                </div>
            )}
        </div>

    );
};

export default TimeFilter;

