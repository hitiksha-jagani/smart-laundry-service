// Author : Hitiksha Jagani
// Description : Service is not available in version 1.

import React from 'react';
import { FaTools } from 'react-icons/fa';

const NotAvailablePage = () => {

    return (
        
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 px-4 text-center">
            <FaTools className="text-6xl text-yellow-500 mb-4" />
            <h1 className="text-3xl font-semibold text-gray-800 mb-2">
                Feature Not Available in Version 1
            </h1>
            <p className="text-lg text-gray-600 max-w-xl">
                This feature is currently under development and will be available in a future release. 
                We appreciate your patience and are working hard to bring it to you soon.
            </p>
        </div>

    );
};

export default NotAvailablePage;