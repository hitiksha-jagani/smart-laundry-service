// Author : Hitiksha Jagani
// Description : Profile detail page for admin.

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import { BASE_URL } from '../../utils/config';

const AdminProfile = () => {

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const navigate = useNavigate();

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
        headers: { Authorization: `Bearer ${token}` },
    });

    useEffect(() => {
    
            const fetchAllData = async () => {
    
                if (!token) {
                    navigate('/login');
                    return;
                }

            
                let decoded;
                try {
                    decoded = jwtDecode(token);
                    console.log("user Id : ", decoded.id)
                } catch (err) {
                    console.error('Invalid token:', err);
                    return;
                }
            
                const userId = decoded.userId || decoded.id;
            
                try {
        
                    // Fetch all data in parallel
                    const [userRes, dataRes] = await Promise.all([
                                
                        // Fetch user data
                        axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                            console.error("User detail fetch failed", err);
                            return { data: null };
                        }),
        
                        // Fetch admin profile data
                        axiosInstance.get("/admin-profile", {
                            headers: {
                                Authorization: `Bearer ${token}`
                            }
                        }).catch(err => {
                            console.error("Admin profile data fetch failed", err);
                            return { data: null };
                        }),
            
                    ]);
            
                    setUser(userRes.data);
                    console.log("User data : " ,userRes.data);
            
                    setData(dataRes.data || []);
                    console.log("Admin profile data:", dataRes.data);
                    console.log("Address : ", dataRes.data.addresses);

                } catch (error) {
                    console.error("Failed to fetch one or more data:", error);
                } finally {
                    setLoading(false);
                }
            };
            
            fetchAllData();
        
        }, []);
                            
        if (loading) return <p className="text-center">Loading...</p>;
        
    return  (

        <AdminDashboardLayout user={user}>

                    <h1 className="heading-admin h1-admin">MY DETAILS</h1>

                    <div 
                        style={{
                            flex: 1,
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            padding: '40px 20px', 
                            boxSizing: 'border-box',
                            marginTop: '-70px'
                        }}
                    > 

                        <div style={{ margin: '200px auto 0' }} className="provider-box" >

                            <div className="grid-row">
                                    <div className="field"><label>First Name</label><span>{data.firstName}</span></div>
                                    <div className="field"><label>Last Name</label><span>{data.lastName}</span></div>
                            </div>

                            <div className="grid-row">
                                    <div className="field"><label>Phone</label><span>{data.phone}</span></div>
                                    <div className="field"><label>Email</label><span>{data.email}</span></div>
                            </div>

                            <div className="grid-row" style={{ gridTemplateColumns: '1fr' }}>
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
                                        onClick={() => navigate('/admin-profile/edit')}
                                        style={{ marginRight: '10px', width: '210px' }}
                                    >
                                        EDIT
                                    </button>

                                    <button
                                        className="reset-btn"
                                        onClick={() => navigate('/admin-profile/change-password')}
                                        style={{ width: '210px' }}
                                    >
                                        CHANGE PASSWORD
                                    </button>
                            </div>

                        </div>

            </div>

        </AdminDashboardLayout>


    );

};

export default AdminProfile;