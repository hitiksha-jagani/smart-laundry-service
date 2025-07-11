// Author : Hitiksha Jagani
// Description : Customer profile detail for admin dashboard.

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom'; 
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { MdInbox } from 'react-icons/md';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import UserMenu from '../../components/Admin/UserMenu.jsx';

const CustomerTablePage = () => {
    const navigate = useNavigate(); 
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   
    const [data, setData] = useState(null);
    const [keyword, setKeyword] = useState("");
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: "http://localhost:8080",
        headers: { Authorization: `Bearer ${token}` },
    });

    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 3000);
    };

    useEffect(() => {
                    
        const fetchAllData = async () => {
                        
            if (!token) {
                return};
            
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
                                
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    // Fetch customer data
                    axiosInstance.get("/users/customers/table", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }).catch(err => {
                        console.error("Customer table data fetch failed", err);
                        return { data: null };
                    }),
            
                ]);
            
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setData(dataRes.data);
                console.log("Customer table data : ", dataRes.data);
                    
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        
        };
            
        fetchAllData();
        
    }, []);

    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px', marginBottom: '50px' }}>

                    <h1 className='heading-admin h1-admin'>USER DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>View, manage, and analyze all platform users including customers, service providers, and delivery agents.</p>

                    <h2 className="h2-admin">CUSTOMER DATA</h2>

                    {(!data || data.length === 0) ? (
                                
                        <div
                            style={{
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                padding: '2rem',
                                backgroundColor: '#fff',
                                border: '1px dashed #ccc',
                                borderRadius: '1rem',
                                margin: '100px auto',
                                width: '70%',
                                color: '#777',
                            }}
                        >
                                                    
                            <MdInbox size={64} color="#ccc" />
                                
                            <h2 style={{ marginTop: '1rem' }}>
                                No customer data available for the selected period.
                            </h2>
                                
                            <p>Once data is recorded, it’ll appear here.</p>
                    
                        </div>
                                
                    ) : ( 

                        <div className="admin-total-payouts-container" style={{ marginTop: '30px'}}>

                            <table className="admin-total-table">

                                <thead>

                                    <tr style={{ backgroundColor: '#f1f5f9'}}>

                                        <th>No.</th>
                                        <th>User Id</th>
                                        <th>First Name</th>
                                        <th>Last Name</th>
                                        <th>Phone</th>
                                        <th>Email</th>
                                        <th>Action</th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {data.map((customer, index) => (
                                    
                                    <tr key={customer.userId}>

                                        <td>{index + 1}</td>
                                        <td>{customer.userId}</td>
                                        <td>{customer.firstName}</td>
                                        <td>{customer.lastName}</td>
                                        <td>{customer.phone}</td>
                                        <td>{customer.email}</td>
                                        <td>
                                            <button
                                                onClick={ () => 
                                                    navigate('/users/customer/table/more', {
                                                        state: { data: customer }
                                                    })
                                                }
                                                className="admin-btn"
                                            >
                                                MORE
                                            </button>
                                        </td>

                                    </tr>

                                    ))}

                                </tbody>

                            </table>

                        </div>

                    )}

                    {/* Right Sidebar */}
                    <UserMenu />

                </div>

            </div>

            {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

        </AdminDashboardLayout>

    );
};

export default CustomerTablePage;