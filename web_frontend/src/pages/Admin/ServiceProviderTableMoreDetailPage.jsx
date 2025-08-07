// Author : Hitiksha Jagani
// Description : Service provider profile complete detail for admin dashboard.

import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RequestsMenu from "./RequestsMenu";
import '../../styles/Admin/ServiceProviderRequests.css';
import { BASE_URL } from '../../utils/config';

const ServiceProviderTableMoreDetailPage = () => {
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
        const endpoint = isBlocked ? `/users/service-providers/table/unblock/${id}` : `/users/service-providers/table/block/${id}`;
        const action = isBlocked ? "unblock" : "block";

        try {
            const res = await axiosInstance.put(endpoint);
            showToast(res.data || `Service provider ${action}ed successfully`, "success");
            setIsBlocked(!isBlocked); // Toggle state
        } catch (error) {
            console.error(`${action} failed:`, error);
            const message = error?.response?.data?.message || `Something went wrong while trying to ${action} the service provider.`;
            showToast(message, "error");
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this service provider?")) return;
        console.log("Provider id : " + id);

        try {
            const res = await axiosInstance.delete(`/users/service-providers/table/delete/${id}`);
            showToast(res.data || "Service provider deleted successfully", "success");

            setTimeout(() => {
                window.location.href = "/users/service-providers/table";
            }, 1000);
        } catch (error) {
            console.error("Delete failed:", error);

            let message = "Something went wrong while deleting the service provider.";
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
            const url = `${BASE_URL}/image/provider/${type}/${userId}`;
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

                    <h2 className="h2-admin">SERVICE PROVIDER DETAIL DATA</h2>
                    
                        <div>

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
                                            {data.addresses
                                                ? `${data.addresses.name}, ${data.addresses.areaName}, ${data.addresses.cityName} - ${data.addresses.pincode}`
                                                : 'N/A'}
                                        </span>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Business Name</label><span>{data.businessName}</span></div>
                                    <div className="field"><label>License Number</label><span>{data.businessLicenseNumber}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>GST Number</label><span>{data.gstNumber}</span></div>
                                    <div className="field"><label>Bank Account No</label><span>{data.bankAccount.bankAccountNumber}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>Account Holder Name</label><span>{data.bankAccount.accountHolderName}</span></div>
                                    <div className="field"><label>Bank Name</label><span>{data.bankAccount.bankName}</span></div>
                                </div>

                                <div className="grid-row">
                                    <div className="field"><label>IFSC Code</label><span>{data.bankAccount.ifscCode}</span></div>
                                    <div className="field">
                                        <label>PAN Card</label>
                                        <button onClick={() => handleFileClick('pan')} className="link-btn">
                                            {data.panCardPhoto != null ? 'Click here...' : 'N/A'}
                                        </button>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    <div className="field">
                                        <label>Aadhaar Card</label>
                                        <button onClick={() => handleFileClick('aadhar')} className="link-btn">
                                            {data.aadharCardPhoto != null ? 'Click here...' : 'N/A'}
                                        </button>
                                    </div>
                                    <div className="field">
                                        <label>Business Utility Bill / Rent Agreement</label>
                                        <button onClick={() => handleFileClick('utilitybill')} className="link-btn">
                                            {data.businessUtilityBillPhoto != null ? 'Click here...' : 'N/A'}
                                        </button>
                                    </div>
                                </div>

                                <div className="grid-row">
                                    
                                    <div className="field">
                                        <label>Profile</label>
                                        <button onClick={() => handleFileClick('profile')} className="link-btn">
                                            {data.profilePhoto != null ? 'Click here...' : 'N/A'}
                                        </button>
                                    </div>
                                    <div className="field"><label>Take Agent Service?</label><span>{data.needOfDeliveryAgent ? "Yes" : "No"}</span></div>
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
                                        onClick={() => handleDelete(data.serviceProviderId)}
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

export default ServiceProviderTableMoreDetailPage;