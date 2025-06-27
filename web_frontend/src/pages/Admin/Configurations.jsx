import { useEffect, useState } from "react";
import { jwtDecode } from 'jwt-decode';
import axios from "axios";
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout.jsx';
import '../../styles/Admin/AdminForm.css';

const Configurations = () => {
    const [user, setUser] = useState(null);
    const [apiKey, setApiKey] = useState("");
    const [loading, setLoading] = useState(true);
     const [providers, setProviders] = useState([]);
    const [selectedProvider, setSelectedProvider] = useState('');
    // const [apiKey, setApiKey] = useState('');
    const [message, setMessage] = useState('');

    const token = localStorage.getItem("token");
    console.log(token);

    const axiosInstance = axios.create({
        baseURL: "http://localhost:8080",
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
                    const [userRes, providerRes] = await Promise.all([
                            
                        axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                            console.error("User detail fetch failed", err);
                            return { data: null };
                        }),
                        axiosInstance.get("/configurations/providers", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                        }).catch(err => {
                            console.error("Provider data fetch failed", err);
                            return { data: null };
                        })
        
                    ]);
        
                    setUser(userRes.data);
                    console.log("User data : " ,userRes.data);
    
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

  const handleSave = async () => {
    await axiosInstance.post("/configurations/save-google-key", { apiKey });
    // console.log("Submitting API key:", apiKey);
    alert("API key saved successfully.");
  };

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


  if (loading) return <div>Loading...</div>;

    return (

        <>

            <AdminDashboardLayout user={user}>

            <div className="admin-form-wrapper">
      
                <div className="admin-form-box">
        
                    {/* <h1 className="heading inter-font">UPDATE STATUS</h1> */}

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

            </AdminDashboardLayout>

        </>

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

export default Configurations;