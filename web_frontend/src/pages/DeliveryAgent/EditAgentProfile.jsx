// Author : Hitiksha Jagani
// Description : Edit profile page for delivery agent dashboard.

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';

const EditAgentProfilePage = () => {
    const [user, setUser] = useState(null);
    const [formData, setFormData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [states, setStates] = useState([]);
    const [selectedStateId, setSelectedStateId] = useState(null);
    const [cities, setCities] = useState([]);
    const [toast, setToast] = useState({ message: '', type: '', visible: false });
    const navigate = useNavigate();

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
        if (!token) return;

        let decoded;
        try {
            decoded = jwtDecode(token);
        } catch (err) {
            console.error('Invalid token:', err);
            return;
        }

        const userId = decoded.userId || decoded.id;

        axiosInstance.get(`/user-detail/${userId}`)
            .then((res) => {
                const userData = res.data;
                setUser(userData);
                setFormData({
                    firstName: userData.firstName || '',
                    lastName: userData.lastName || '',
                    addresses: {
                        name: userData.addresses?.name || '',
                        areaName: userData.addresses?.areaName || '',
                        cityName: userData.addresses?.cityName || '',
                        pincode: userData.addresses?.pincode || ''
                    }
                });
            })
            .catch((err) => {
                console.error("Failed to fetch user:", err);
                showToast("Failed to load profile", "error");
            })
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        axios.get('http://localhost:8080/states')
            .then((res) => setStates(res.data))
            .catch((err) => console.error("Failed to load states", err));
    }, []);

    useEffect(() => {
        if (selectedStateId) {
            console.log("state id : ", selectedStateId);
            axios.get(`http://localhost:8080/cities/get/${selectedStateId}`)
            .then((res) => setCities(res.data))
            .catch((err) => console.error("Failed to load cities", err));
        } else {
            setCities([]);
        }
    }, [selectedStateId]);

    const handleStateChange = (e) => {
        const stateId = e.target.value;
        setSelectedStateId(stateId);
        setFormData(prev => ({
            ...prev,
            addresses: {
            ...prev.addresses,
            cityName: ''
            }
        }));
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (['name', 'areaName', 'cityName', 'pincode'].includes(name)) {
            setFormData(prev => ({
                ...prev,
                addresses: {
                    ...prev.addresses,
                    [name]: value
                }
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const handleSave = () => {
        axiosInstance.put('/profile/detail/edit', formData)
            .then((res) => {
                showToast("Profile updated successfully!", "success");
                setTimeout(() => {
                    navigate('/profile/detail');
                }, 1000);
            })
            .catch((err) => {
                console.error("Failed to update profile:", err);
                showToast("Failed to update profile.", "error");
            });
    };

    const inputStyle = {
        width: '100%',
        padding: '8px',
        borderRadius: '10px',
        border: '1px solid #ccc',
        marginTop: '4px',
        marginBottom: '12px',
        fontSize: '14px',
    };

    if (loading || !formData) return <p className="text-center">Loading...</p>;

    return (

        <DeliveryAgentDashboardLayout user={user}>

            <h1 className="heading-agent h1-agent">EDIT PROFILE</h1>

            <div 
                style={{
                    flex: 1,
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    padding: '40px 20px', 
                    boxSizing: 'border-box',
                    marginTop: '50px'
                }}>

                <div className="agent-box" style={{ width: '100%', maxWidth: '700px' }}>

                    <div className="agent-grid-row">

                        <div className="agent-field">
                            <label>First Name</label>
                            <input name="firstName" value={formData.firstName} onChange={handleChange} className='agent-input-field' />
                        </div>

                        <div className="agent-field">
                            <label>Last Name</label>
                            <input name="lastName" value={formData.lastName} onChange={handleChange} className='agent-input-field' />
                        </div>

                    </div>

                    <div className="agent-grid-row">

                        <div className="agent-field">
                            <label>Phone</label>
                            <input name="phoneNo" value={user.phoneNo} disabled />
                        </div>

                        <div className="agent-field">
                            <label>Email</label>
                            <input name="email" value={user.email} disabled />
                        </div>

                    </div>

                    <div className="agent-grid-row" style={{ gridTemplateColumns: '1fr' }}>

                        <div className="agent-field full" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>

                            <label style={{ gridColumn: '1 / -1' }}>Address</label>

                            <input
                                name="name"
                                placeholder="Address Line"
                                value={formData.addresses.name}
                                onChange={handleChange}
                                className="agent-input-field"
                            />
                            <input
                                name="areaName"
                                placeholder="Area"
                                value={formData.addresses.areaName}
                                onChange={handleChange}
                                className="agent-input-field"
                            />

                            <select
                                name="stateId"
                                value={selectedStateId || ''}
                                onChange={handleStateChange}
                                className="agent-input-field"
                                
                            >

                                <option value="">Select State</option>
                                
                                {states.map((state, index) => (
                                    <option key={state.stateId || `state-${index}`} value={state.stateId}>
                                        {state.name}
                                    </option>
                                ))}

                            </select>

                            <select
                                name="cityName"
                                value={formData.addresses.cityName}
                                onChange={handleChange}
                                className="agent-input-field"
                                disabled={!selectedStateId}
                            >

                                {cities.map((city, index) => {

                                    if (!city.cityId) {
                                        console.warn("City has null or missing cityId:", city);
                                    }

                                    return (
                                        <option key={city.cityId || `city-${index}`} value={city.name}>
                                            {city.name}
                                        </option>
                                    );
                                })}

                            </select>

                            <input
                                name="pincode"
                                placeholder="Pincode"
                                value={formData.addresses.pincode}
                                onChange={handleChange} 
                                className="agent-input-field"
                            />

                        </div>

                    </div>

                    <div className="button-row">

                        <button className="admin-btn" onClick={handleSave} style={{ marginRight: '10px', width: '150px' }}>
                            SAVE
                        </button>

                        <button className="reset-btn" onClick={() => navigate('/profile/detail')} style={{ width: '150px' }}>
                            CANCEL
                        </button>

                    </div>

                </div>

                {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}
            
            </div>

        </DeliveryAgentDashboardLayout>

    );
};

export default EditAgentProfilePage;
