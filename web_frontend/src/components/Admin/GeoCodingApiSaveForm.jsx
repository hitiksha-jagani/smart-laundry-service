// Author : Hitiksha Jagani
// Description : Geocoding api saving form for admin dashboard.

import { useEffect, useState } from "react";
import { jwtDecode } from 'jwt-decode';
import axios from "axios";
import { BASE_URL } from '../../utils/config';

const GeoCodingApiSavingForm = () => {
    const [apiKey, setApiKey] = useState("");
    const [providers, setProviders] = useState([]);
    const [selectedProvider, setSelectedProvider] = useState('');
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(true); 

    const token = localStorage.getItem("token");
    console.log(token); 

    const axiosInstance = axios.create({
        baseURL: `${BASE_URL}`,
        headers: { Authorization: `Bearer ${token}` },
    });

    useEffect(() => {
    
        const fetchAllData = async () => {
                        
                    if (!token) return;
            
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
                        const [ providerRes] = await Promise.all([
                                
                            axiosInstance.get("/configurations/providers", {
                            headers: {
                                Authorization: `Bearer ${token}`
                            }
                            }).catch(err => {
                                console.error("Provider data fetch failed", err);
                                return { data: null };
                            })
            
                        ]);
                    
                        setProviders(providerRes.data)
                        console.log("Provider data : " ,providerRes.data);
                                    
                    } catch (error) {
                        console.error("Failed to fetch one or more data:", error);
                    } finally {
                        setLoading(false);
                    }
        
                };
            
                fetchAllData();
    
    
    
    }, []);

    const handleSubmit = async () => {

        if (!selectedProvider || !apiKey) {
            setMessage("❌ Please select a provider and enter the API key.");
            return;
        }

        try {

            await axiosInstance.post("/configurations/save", {
                provider: selectedProvider,
                apiKey: apiKey
            });

            setMessage("✅ API key saved successfully!");
            setApiKey("");

        } catch (error) {
            console.error("Error saving config:", error);
            setMessage("❌ Failed to save configuration.");
        }
    };

    return (

        <div className="admin-form-wrapper">
      
            <div className="admin-form-box">

                <h2 className='heading-admin h2-admin'>SAVE GEO CODING API KEY</h2>
        
                <label style={styles.label}>Select Provider:</label>
                <select
                    value={selectedProvider}
                    onChange={(e) => setSelectedProvider(e.target.value)}
                    style={styles.select}
                >

                    {providers.map((provider) => (
                    <option key={provider.name} value={provider.name}>
                        {provider.label || provider.name}
                    </option>
                    ))}

                </select>

                <label style={styles.label}>API Key:</label>
                <input
                    type="text"
                    value={apiKey}
                    onChange={(e) => setApiKey(e.target.value)}
                    placeholder="Enter your API key"
                    style={styles.input}
                />

                <button onClick={handleSubmit} style={styles.button}>
                    Save / Update
                </button>

                {message && <p style={styles.message}>{message}</p>}

            </div>

        </div>

    );

};

const styles = {
  container: {
    maxWidth: '500px',
    margin: 'auto',
    padding: '2rem',
    border: '1px solid #ddd',
    borderRadius: '10px',
    backgroundColor: '#f9fafb',
  },
  heading: {
    textAlign: 'center',
    marginBottom: '1rem',
  },
  label: {
    display: 'block',
    marginTop: '1rem',
    fontWeight: 'bold',
  },
  select: {
    width: '100%',
    padding: '0.5rem',
    marginTop: '0.5rem',
  },
  input: {
    width: '100%',
    padding: '0.5rem',
    marginTop: '0.5rem',
  },
  button: {
    marginTop: '1.5rem',
    width: '100%',
    padding: '0.7rem',
    backgroundColor: '#2563eb',
    color: '#fff',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
  },
  message: {
    marginTop: '1rem',
    textAlign: 'center',
    fontWeight: 'bold',
  },
};


export default GeoCodingApiSavingForm;