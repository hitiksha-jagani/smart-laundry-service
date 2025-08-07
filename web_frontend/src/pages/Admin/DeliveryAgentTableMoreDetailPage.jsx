// Author : Hitiksha Jagani
// Description : Delivery agent profile complete detail for admin dashboard.

import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RequestsMenu from "./RequestsMenu";
import '../../styles/Admin/ServiceProviderRequests.css';
import { BASE_URL } from '../../utils/config';

const DeliveryAgentTableMoreDetailPage = () => {
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
        baseURL: `${BASE_URL}`,
        headers: { Authorization: `Bearer ${token}` },
    });

    const handleToggleBlock = async (id) => {
        const endpoint = isBlocked ? `/users/delivery-agents/table/unblock/${id}` : `/users/delivery-agents/table/block/${id}`;
        const action = isBlocked ? "unblock" : "block";

        try {
            const res = await axiosInstance.put(endpoint);
            showToast(res.data || `Delivery agent ${action}ed successfully`, "success");
            setIsBlocked(!isBlocked); // Toggle state
        } catch (error) {
            console.error(`${action} failed:`, error);
            const message = error?.response?.data?.message || `Something went wrong while trying to ${action} the Delivery agent.`;
            showToast(message, "error");
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this delivery agent?")) return;

        try {
            const res = await axiosInstance.delete(`/users/delivery-agents/table/delete/${id}`);
            showToast(res.data || "Delivery agent deleted successfully", "success");

            setTimeout(() => {
                window.location.href = "/users/delivery-agents/table";
            }, 1000);
        } catch (error) {
            console.error("Delete failed:", error);

            let message = "Something went wrong while deleting the delivery agent.";
            if (error?.response?.data) {
                if (typeof error.response.data === 'string') {
                    message = error.response.data;
                } else if (typeof error.response.data.message === 'string') {
                    message = error.response.data.message;
                }
            }

            showToast(message, "error");
        }
    };

    const handleFileClick = (type) => {
        const userId = data.userId;

        if (type && userId) {
            const url = `${BASE_URL}/image/agent/${type}/${userId}`;
            window.open(url, '_blank'); 
        } else {
            showToast("Invalid image type or user ID", "error");
        }
    };
                      
    return (

        <AdminDashboardLayout user={data}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px', marginBottom: '50px' }}>

                    <h1 className='heading-admin h1-admin'>USER DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>View, manage, and analyze all platform users including customers, service providers, and delivery agents.</p>

                    <h2 className="h2-admin">DELIVERY AGENT DETAIL DATA</h2>

                        <div
                            className='div-agent'
                        >

                            <div className="provider-box">

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
                                        {data.address
                                            ? `${data.address.name}, ${data.address.areaName}, ${data.address.cityName} - ${data.address.pincode}`
                                            : 'N/A'}
                                        </span>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Date Of Birth</label><span>{data.dateOfBirth}</span></div>
                                    <div className="field"><label>Vehicle Number</label><span>{data.vehicleNumber}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Gender</label><span>{data.gender}</span></div>
                                    <div className="field"><label>Bank Account No</label><span>{data.bankAccountNumber}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Account Holder Name</label><span>{data.accountHolderName}</span></div>
                                    <div className="field"><label>Bank Name</label><span>{data.bankName}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>IFSC Code</label><span>{data.ifscCode}</span></div>
                                    <div className="field">
                                        <label>PAN Card</label>
                                        <button onClick={() => handleFileClick('pan')} className="link-btn">
                                            {data.panCardPhoto ? 'Click here...' : 'N/A'}
                                        </button>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    <div className="field">
                                        <label>Aadhaar Card</label>
                                        <button onClick={() => handleFileClick('aadhar')} className="link-btn">
                                            {data.aadharCardPhoto ? 'Click here...' : 'N/A'}
                                        </button>
                                    </div>
                                    <div className="field">
                                        <label>Driving License</label>
                                        <button onClick={() => handleFileClick('license')} className="link-btn">
                                            {data.drivingLicensePhoto ? 'Click here...' : 'N/A'}
                                        </button>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    
                                    <div className="field">
                                        <label>Profile</label>
                                        <button onClick={() => handleFileClick('profile')} className="link-btn">
                                            {data.profilePhoto ? 'Click here...' : 'N/A'}
                                        </button>
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
                                        onClick={() => handleDelete(data.deliveryAgentId)}
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

export default DeliveryAgentTableMoreDetailPage;