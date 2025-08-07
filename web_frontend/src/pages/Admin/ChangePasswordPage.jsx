// Author : Hitiksha Jagani
// Description : Change password page for admin dashboard.

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { BASE_URL } from '../../utils/config';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';

import eyeOpen from '../../assets/eye-icon.png';
import eyeClosed from '../../assets/eye-icon.png'; 

const ChangePasswordPage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });

    const [formData, setFormData] = useState({
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
    });

    const [visible, setVisible] = useState({
        oldPassword: false,
        newPassword: false,
        confirmPassword: false,
    });

    const navigate = useNavigate();

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
                const [userRes] = await Promise.all([
                                    
                    // Fetch user data
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    })
            
                ]);
                
                setUser(userRes.data)
                console.log("User data : " ,userRes.data);
                    
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

    const toggleVisibility = (field) => {
        setVisible(prev => ({ ...prev, [field]: !prev[field] }));
    };
 
    const validateFields = () => {
        const passwordRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;

        const { oldPassword, newPassword, confirmPassword } = formData;

        if (!oldPassword || !newPassword || !confirmPassword) {
            showToast("Please fill all fields", "error");
            return false;
        } 

        if (newPassword !== confirmPassword) {
            showToast("Password and confirm password do not match", "error");
            return false;
        }

        if (!passwordRegex.test(newPassword)) {
            showToast("New password must be at least 8 characters long, include 1 uppercase letter and 1 special character", "error");
            return false;
        }

        return true;
    };

    const handleSave = () => {
        if (!validateFields()) return;

        axiosInstance.put('/admin-profile/change-password', formData)
            .then((res) => {
                showToast(res.data || "Password changed successfully!", "success");
                setTimeout(() => {
                    navigate("/admin-profile"); 
                }, 1500);
            })
            .catch((err) => {
                const message = err.response?.data?.message || "Failed to change password.";
                showToast(message, "error");
                console.error("Change password error:", err);
            });
    };
             
    if (loading) return <p className="text-center">Loading...</p>;

    return (

        <AdminDashboardLayout user={user}>

            <h1 className="heading-admin h1-admin">CHANGE PASSWORD</h1>

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

                    {['oldPassword', 'newPassword', 'confirmPassword'].map((field) => (

                        <div className="field" key={field} style={{ marginBottom: '20px', position: 'relative' }}>
                            
                            <label style={{ display: 'block', marginBottom: '6px' }}>
                                {field === 'oldPassword' && 'Old Password'}
                                {field === 'newPassword' && 'New Password'}
                                {field === 'confirmPassword' && 'Confirm Password'}
                            </label>

                            <input
                                name={field}
                                type={visible[field] ? "text" : "password"}
                                value={formData[field]}
                                onChange={handleChange}
                                className="input-field"
                            />

                            <img
                                src={visible[field] ? eyeOpen : eyeClosed}
                                alt="toggle visibility"
                                style={{
                                    position: 'absolute',
                                    top: '50%',
                                    right: '30px',
                                    cursor: 'pointer',
                                    width: '20px',
                                    height: '20px',
                                }}
                                onClick={() => toggleVisibility(field)}
                            />

                        </div>

                    ))}

                    <div className="button-row">

                        <button className="admin-btn" onClick={handleSave} style={{ marginRight: '10px', width: '150px' }}>
                            SAVE
                        </button>

                        <button className="reset-btn" onClick={() => navigate('/admin-profile')} style={{ width: '150px' }}>
                            CANCEL
                        </button>

                    </div>

                </div>

            </div>

            {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}


        </AdminDashboardLayout>

    );

};

export default ChangePasswordPage;