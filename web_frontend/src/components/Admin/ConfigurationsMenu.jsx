// Author : Hitiksha Jagani
// Side header for admin dashboard configuration menu.

import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import GeoCodingMenu, { geocodingRoutes } from './GeoCodingMenu';
import '../../styles/Admin/ConfigurationMenu.css';
import '../../styles/Admin/AdminSidebar.css';

export const configRoutes = [
    '/configurations/save',
    '/configurations/history'
];

const ConfigurationsMenu = () => {
    const location = useLocation();
    const [isGeoMenuOpen, setGeoMenuOpen] = useState(false);
    const isConfigActive = geocodingRoutes.some(path => location.pathname.startsWith(path));
    
    const [isConfigMenuOpen, setConfigMenuOpen] = useState(false);
    
    const handleConfigClick = () => {
        setConfigMenuOpen(prev => !prev);
    };

    const toggleGeoMenu = () => setGeoMenuOpen(prev => !prev);
    const handleGeoEnter = () => setGeoMenuOpen(true);
    const handleGeoLeave = () => setGeoMenuOpen(false);

    const isGeoActive = ['/configurations/save', '/configurations/history']
        .some(path => location.pathname.startsWith(path));

    return (

        <div className="floating-submenu">

        <ul className="geo-submenu">

            {/* <li
                className="nested-menu sidebar-menu-with-submenu"
                onMouseEnter={handleGeoEnter}
                onMouseLeave={handleGeoLeave}
                onClick={toggleGeoMenu}
            >
                <span className="config-submenu-link">Geocoding Settings ▸</span>
                {isGeoMenuOpen && <GeoCodingMenu />}
            </li> */}

            <li
                className="sidebar-menu-with-submenu"
                onMouseEnter={handleGeoEnter}
                onMouseLeave={handleGeoLeave}
                style={{ padding:'10px'}}
            >
                <div
                    className={`admin-sidebar-link submenu-parent ${isConfigActive ? 'active' : ''}`}
                    onClick={handleConfigClick}
                >
                    <span>Geocoding</span> 
                </div>

                {isGeoMenuOpen && <GeoCodingMenu />}
            </li>

                        

        </ul>

        </div>

  );
};

export default ConfigurationsMenu;
