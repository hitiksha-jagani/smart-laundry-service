// Author : Hitiksha Jagani
// Description : Delivery agent profile complete detail for admin dashboard.

import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import { MdInbox } from 'react-icons/md'; 
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RequestsMenu from "./RequestsMenu";
import '../../styles/Admin/ServiceProviderRequests.css';

const DeliveryAgentTableMoreDetailPage = () => {
    // const [user, setUser] = useState(null);
    // const location = useLocation();
    // const data = location.state?.data;
    // const [toast, setToast] = useState({ message: '', type: '', visible: false });

    // const showToast = (message, type = 'success') => {
    //     setToast({ message, type, visible: true });
    //     setTimeout(() => {
    //         setToast({ message: '', type: '', visible: false });
    //     }, 5000);
    // };

    // const token = localStorage.getItem("token");

    // const axiosInstance = axios.create({
    //     baseURL: "http://localhost:8080",
    //     headers: { Authorization: `Bearer ${token}` },
    // });

    // useEffect(() => {
    
    //         const fetchAllData = async () => {
    
    //             if (!token) return;
            
    //             let decoded;
    //             try {
    //                 decoded = jwtDecode(token);
    //                 console.log("user Id : ", decoded.id)
    //             } catch (err) {
    //                 console.error('Invalid token:', err);
    //                 return;
    //             }
            
    //             const userId = decoded.userId || decoded.id;
            
    //             try {
        
    //                 // Fetch all data in parallel
    //                 const [userRes, dataRes] = await Promise.all([
                                
    //                     // Fetch user data
    //                     axiosInstance.get(`/user-detail/${userId}`).catch(err => {
    //                         console.error("User detail fetch failed", err);
    //                         return { data: null };
    //                     }),
        
    //                     // Fetch delivery agent profile data
    //                     axiosInstance.get("/agent-requests", {
    //                         headers: {
    //                             Authorization: `Bearer ${token}`
    //                         }
    //                     }).catch(err => {
    //                         console.error("Delivery agent data fetch failed", err);
    //                         return { data: null };
    //                     }),
            
    //                 ]);
            
    //                 setUser(userRes.data);
    //                 console.log("User data : " ,userRes.data);
            
    //                 setData(dataRes.data || []);
    //                 console.log("Delivery agent profile data:", dataRes.data);

    //             } catch (error) {
    //                 console.error("Failed to fetch one or more data:", error);
    //             } finally {
    //                 setLoading(false);
    //             }
    //         };
            
    //         fetchAllData();
        
    //     }, []);

    // const handleFileClick = (type) => {
    //     const userId = currentAgent.userId;

    //     if (type && userId) {
    //         const url = `http://localhost:8080/image/agent/${type}/${userId}`;
    //         window.open(url, '_blank'); 
    //     } else {
    //         showToast("Invalid image type or user ID", "error");
    //     }
    // };
                      
    // if (loading) return <p className="text-center">Loading...</p>;

    // return (

    //     <AdminDashboardLayout user={user}>

    //         <div style={{ display: 'flex' }}>

    //             <div style={{ flex: 1, paddingRight: '260px' }}>

    //                 <h1 className='heading-admin h1-admin'>USER DASHBOARD</h1>

    //                 <p className='p-admin' style={{ padding: '0 30px' }}>View, manage, and analyze all platform users including customers, service providers, and delivery agents.</p>

    //                 <h2 className="h2-admin">DELIVERY AGENT DETAIL DATA</h2>

    //                 {(!data || data.length === 0) ? (
                    
    //                     <div style={{
    //                         display: 'flex',
    //                         flexDirection: 'column',
    //                         alignItems: 'center',
    //                         padding: '2rem',
    //                         backgroundColor: '#fff',
    //                         border: '1px dashed #ccc',
    //                         borderRadius: '1rem',
    //                         margin: '2rem auto',
    //                         width: '80%',
    //                         color: '#777'
    //                     }}>
                    
    //                         <MdInbox size={64} color="#ccc" />
    //                         <h2 style={{ marginTop: '1rem' }}>No Requests Available</h2>
    //                         <p>Once request arrived, they’ll appear here.</p>
                    
    //                     </div>
                    
    //                 ) : (

    //                     <div>

    //                         <div className="provider-box">

    //                             <div className="grid-row">
    //                                 <div className="field"><label>First Name</label><span>{data.firstName}</span></div>
    //                                 <div className="field"><label>Last Name</label><span>{data.lastName}</span></div>
    //                             </div>

    //                             <div className="grid-row">
    //                                 <div className="field"><label>Phone</label><span>{data.phoneNo}</span></div>
    //                                 <div className="field"><label>Email</label><span>{data.email}</span></div>
    //                             </div>

    //                             <div className="grid-row full">
    //                                 <div className="field full">
    //                                     <label>Address</label>
    //                                     <span>
    //                                     {data.addresses
    //                                         ? `${data.addresses.name}, ${data.addresses.areaName}, ${data.addresses.cityName} - ${data.addresses.pincode}`
    //                                         : 'N/A'}
    //                                     </span>
    //                                 </div>
    //                             </div>

    //                             <div className="grid-row">
    //                                 <div className="field"><label>Date Of Birth</label><span>{data.dateOfBirth}</span></div>
    //                                 <div className="field"><label>Vehicle Number</label><span>{data.vehicleNumber}</span></div>
    //                             </div>

    //                             <div className="grid-row">
    //                                 <div className="field"><label>Gender</label><span>{data.gender}</span></div>
    //                                 <div className="field"><label>Bank Account No</label><span>{data.bankAccountNumber}</span></div>
    //                             </div>

    //                             <div className="grid-row">
    //                                 <div className="field"><label>Account Holder Name</label><span>{data.accountHolderName}</span></div>
    //                                 <div className="field"><label>Bank Name</label><span>{data.bankName}</span></div>
    //                             </div>

    //                             <div className="grid-row">
    //                                 <div className="field"><label>IFSC Code</label><span>{data.ifscCode}</span></div>
    //                                 <div className="field">
    //                                     <label>PAN Card</label>
    //                                     <button onClick={() => handleFileClick('pan')} className="link-btn">
    //                                         Click here...
    //                                     </button>
    //                                 </div>
    //                             </div>

    //                             <div className="grid-row">
    //                                 <div className="field">
    //                                     <label>Aadhaar Card</label>
    //                                     <button onClick={() => handleFileClick('aadhar')} className="link-btn">
    //                                         Click here...
    //                                     </button>
    //                                 </div>
    //                                 <div className="field">
    //                                     <label>Driving License</label>
    //                                     <button onClick={() => handleFileClick('license')} className="link-btn">
    //                                         Click here...
    //                                     </button>
    //                                 </div>
    //                             </div>

    //                             <div className="grid-row">
                                    
    //                                 <div className="field">
    //                                     <label>Profile</label>
    //                                     <button onClick={() => handleFileClick('profile')} className="link-btn">
    //                                         Click here...
    //                                     </button>
    //                                 </div>
    //                             </div>

    //                             <div className="button-row"> 
    //                                 <button
    //                                     className="admin-btn"
    //                                     onClick={() => handleAccept(currentAgent.userId)}
    //                                     style={{ marginRight: '10px', width: '150px' }}
    //                                 >
    //                                     Accept
    //                                 </button>

    //                                 <button
    //                                     className="reset-btn"
    //                                     onClick={() => handleReject(currentAgent.userId)}
    //                                     style={{ width: '150px' }}
    //                                 >
    //                                     Reject
    //                                 </button>
    //                             </div>

    //                         </div>

    //                         <div style={{ marginTop: '1rem', display: 'flex', justifyContent: 'center', gap: '1rem' }}>
                                
    //                             <button
    //                                 className="admin-nav-btn nav-btn"
    //                                 onClick={() => setCurrentIndex(currentIndex - 1)}
    //                                 disabled={!hasPrev}
    //                             >
    //                                 ⬅ Prev
    //                             </button>

    //                             <button
    //                                 className="admin-nav-btn nav-btn"
    //                                 onClick={() => setCurrentIndex(currentIndex + 1)}
    //                                 disabled={!hasNext}
    //                             >
    //                                 Next ➡
    //                             </button>

    //                         </div>
                     
    //                         <p
    //                             style={{
    //                                 marginTop: '0.3rem',
    //                                 color: '#555',
    //                                 textAlign: 'center',
    //                                 fontSize: '20px',
    //                                 fontWeight: '900',
    //                                 marginBottom: '50px'
    //                             }}
    //                         >
    //                             Request {currentIndex + 1} of {data.length}
    //                         </p>

    //                     </div>
                   
    //                 )}

    //                 {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

    //                 {/* Right Sidebar */}
    //                 <RequestsMenu />

    //             </div>

    //         </div>

    //     </AdminDashboardLayout>


    // );

};

export default DeliveryAgentTableMoreDetailPage;