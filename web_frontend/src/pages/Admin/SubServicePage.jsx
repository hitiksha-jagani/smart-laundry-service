// Author : Hitiksha Jagani
// Description : Sub service page for service dashboard.

import React, { useEffect, useState } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import ServiceMenu from '../../components/Admin/ServiceMenu.jsx';
import { BASE_URL } from '../../utils/config';

const SubServicePage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   
    const [services, setServices] = useState([]);
    const [subServices, setSubServices] = useState([]);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const [formData, setFormData] = useState({
        serviceName: '',
        subServiceName: ''
    });

    const token = localStorage.getItem("token");

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
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
                const [userRes, serviceRes] = await Promise.all([
                                
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),

                    axiosInstance.get('/service/get-services').catch(err => {
                        console.error("Service list fetch failed", err);
                        return { data: [] };
                    }),
            
                ]);
            
                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setServices(serviceRes.data); 
                console.log("Services:", serviceRes.data);
                    
            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        
        };
            
        fetchAllData();
        
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSave = () => {
        if (!formData.serviceName) {
            showToast("Please fill service name fields", "error");
            return;
        }

         if (!formData.subServiceName) {
            showToast("Please fill sub service name fields", "error");
            return;
        }

        axiosInstance.post('/service/add-subservices', formData)
            .then((res) => {
                showToast(res.data, "success");
                handleResetAll();
            }) 
            .catch((err) => {
                const message = err.response?.data?.message || "Failed to add sub service.";
                showToast(message, "error");
                console.error("Add sub service error:", err);
            });
    };

    const handleResetAll = () => {
        setFormData({
            serviceName: '',
            subServiceName: '',
        });
        setSubServices([]);
    };

    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <AdminDashboardLayout user={user}>

            <div style={{ display: 'flex' }}>

                <div style={{ flex: 1, paddingRight: '260px' }}>

                    <h1 className='heading-admin h1-admin'>SERVICE LISTING DASHBOARD</h1>

                    <p className='p-admin' style={{ padding: '0 30px' }}>Organize and assign clothing items under the appropriate services offered.</p>

                    <h2 className="h2-admin">ADD NEW SUB-SERVICE</h2>

                    <div 
                        style={{
                            flex: 1,
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            padding: '40px 20px', 
                            boxSizing: 'border-box',
                            marginTop: '60px'
                    }}>

                        <div className="provider-box" style={{ width: '500px', maxWidth: '700px' }}>

                            {['serviceName', 'subServiceName'].map((field) => (

                                <div className="field" key={field} style={{ marginBottom: '20px', position: 'relative' }}>
                                    
                                    <label style={{ display: 'block', marginBottom: '6px' }}>
                                        {field === 'serviceName' && 'Service Name'}
                                        {field === 'subServiceName' && 'Sub Service Name'}
                                    </label>

                                    {field === 'serviceName' ? (
                                        <select
                                            name="serviceName"
                                            value={formData.serviceName}
                                            onChange={handleChange}
                                            className="input-field"
                                        >
                                            <option value="">-- Select Service --</option>
                                            {services.map((service, index) => (
                                                <option key={index} value={service}>
                                                    {service}
                                                </option>
                                            ))}
                                        </select>
                                    ) : (
                                        <input
                                            name={field}
                                            type="text"
                                            value={formData[field]}
                                            onChange={handleChange}
                                            className="input-field"
                                        />
                                    )}

                                </div>

                            ))}

                            <div className="button-row">

                                <button className="admin-btn" onClick={handleSave} style={{ marginRight: '10px', width: '150px' }}>
                                    SAVE
                                </button>

                                <button className="reset-btn" onClick={handleResetAll} style={{ width: '150px' }}>
                                    RESET
                                </button>

                            </div>

                        </div>

                    </div>

                    {/* Right Sidebar */}
                    <ServiceMenu />

                </div>

            </div>

            {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

        </AdminDashboardLayout>


    );

};

export default SubServicePage;