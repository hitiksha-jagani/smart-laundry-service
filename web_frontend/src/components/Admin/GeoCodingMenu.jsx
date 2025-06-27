// Author : Hitiksha Jagani
// Side header for admin dashboard geo coding menu.

import React from 'react';
import { NavLink } from 'react-router-dom';
import '../../styles/Admin/ConfigurationMenu.css';

export const geocodingRoutes = [
    '/configurations/save',
    '/configurations/history'
];

const geoMenuOptions = [
  { label: "Save/Update", path: "/configurations/save" },
  { label: "Geocoding API List", path: "/configurations/history" }
];;

const GeoCodingMenu = () => {

    return (

        <div className="floating-submenu">

            <ul className="geo-submenu">
            
                {geoMenuOptions.map(link => (
            
                    <li 
                        className="sidebar-menu-with-submenu"
                        key={link.path}>

                        <NavLink
                            to={link.path}
                            className={({ isActive }) =>
                                `config-submenu-link ${isActive ? "active" : ""}`
                            }
                        >
                            {link.label}
                        </NavLink>

                    </li>

                ))}

            </ul>

        </div>

    );
};

export default GeoCodingMenu;
