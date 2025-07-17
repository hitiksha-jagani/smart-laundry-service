// Author : Hitiksha Jagani
// Description : Customer profile complete detail for admin dashboard.

import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RequestsMenu from "./RequestsMenu";
import '../../styles/Admin/ServiceProviderRequests.css';

const CustomerTableMoreDetailPage = () => {
    const location = useLocation();
    const { data } = location.state || {};
    const [isBlocked, setIsBlocked] = useState(data.blocked);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });
    
    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 5000);
    };

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: "http://localhost:8080",
        headers: { Authorization: `Bearer ${token}` },
    });

    const handleToggleBlock = async (id) => {
        const endpoint = isBlocked ? `/users/customers/table/unblock/${id}` : `/users/customers/table/block/${id}`;
        const action = isBlocked ? "unblock" : "block";

        try {
            const res = await axiosInstance.put(endpoint);
            showToast(res.data || `Customer ${action}ed successfully`, "success");
            setIsBlocked(!isBlocked); // Toggle state
        } catch (error) {
            console.error(`${action} failed:`, error);
            const message = error?.response?.data?.message || `Something went wrong while trying to ${action} the customer.`;
            showToast(message, "error");
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this customer?")) return;

        try {
            const res = await axiosInstance.delete(`/users/customer/table/delete/${id}`);
            showToast(res.data || "Customer deleted successfully", "success");

            setTimeout(() => {
                window.location.href = "/users/customers/table";
            }, 1000);
        } catch (error) {
            console.error("Delete failed:", error);
            const message = error?.response?.data || "Something went wrong while deleting the customer.";
            showToast(message, "error");
        }
    };

    return (

        <AdminDashboardLayout user={data}> 

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px' }}>

                    <h1 className='heading-admin h1-admin'>USER DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>View, manage, and analyze all platform users including customers, service providers, and delivery agents.</p>

                    <h2 className="h2-admin">CUSTOMER DETAIL DATA</h2>
                    
                        <div>

                            <div className="provider-box" style={{ marginTop: '50px' }}>

                                <div className="grid-row">
                                    <div className="field"><label>First Name</label><span>{data.firstName}</span></div>
                                    <div className="field"><label>Last Name</label><span>{data.lastName}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Phone</label><span>{data.phone}</span></div>
                                    <div className="field"><label>Email</label><span>{data.email}</span></div>
                                </div>

                                <div className="grid-row full">
                                    <div className="field full">
                                        <label>Address</label>
                                        <span>
                                            {data.addresses
                                                ? `${data.addresses.name}, ${data.addresses.areaName}, ${data.addresses.cityName} - ${data.addresses.pincode}`
                                                : 'N/A'}
                                        </span>
                                    </div>
                                </div>

                                <div className="button-row"> 
                                    <button
                                        className="admin-btn"
                                        onClick={() => handleToggleBlock(data.userId)}
                                        style={{ marginRight: '10px', width: '150px' }}
                                    >
                                        {isBlocked ? "UNBLOCK" : "BLOCK"}
                                    </button>
                                
                                    <button
                                        className="reset-btn"
                                        onClick={() => handleDelete(data.userId)}
                                        style={{ width: '150px' }}
                                    >
                                        DELETE
                                    </button>
                                </div>

                            </div>

                        </div>

                    {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

                    {/* Right Sidebar */}
                    <RequestsMenu />

                </div>

            </div>

        </AdminDashboardLayout>


    );
};

export default CustomerTableMoreDetailPage;