import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

const SetupAdmin = () => {
  const [showForm, setShowForm]         = useState(false);
  const [checked, setChecked]           = useState(false);
  const [formData, setFormData]         = useState({
    firstName: '', lastName: '', email: '', phone: '', password: '',
  });
  const [message, setMessage]           = useState('');

  useEffect(() => {
    console.log("Full Fetch URL:", `${process.env.REACT_APP_API_URL}/setup-admin`);
    console.log("API URL:", process.env.REACT_APP_API_URL);
    const API_URL = process.env.REACT_APP_API_URL;
if (!API_URL) {
  throw new Error("REACT_APP_API_URL is not defined in your .env file");
}

    fetch(`${process.env.REACT_APP_API_URL}/setup-admin`)
               // ← correct URL
      .then(res => {
        console.log('GET /setup-admin status:', res.status);
        console.log('GET /setup-admin raw:', res);
        return res.json();
      })
      .then(setupRequired => {
        console.log('setupRequired:', setupRequired);
        setShowForm(setupRequired === true);
      })
      .catch(err => {
        console.error('Error checking setup status', err);
        setShowForm(false);
      })
      .finally(() => {
        setChecked(true);
      });
  }, []);

  // While we’re waiting for the check to finish…
  if (!checked) {
    return <div className="p-8 text-center">Checking setup status…</div>;
  }

  // If setup is already done, redirect to login
  if (!showForm) {
    return <Navigate to="/login" replace />;
  }

  const handleChange = e => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      const res = await fetch(`${process.env.REACT_APP_API_URL}/setup-admin`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      const text = await res.text();
      if (res.ok) {
        setMessage(text || 'Admin created! Redirecting to login…');
        setTimeout(() => {
          window.location.href = '/login';
        }, 1500);
      } else {
        setMessage(text);
      }
    } catch (err) {
      console.error('Submission error', err);
      setMessage('Failed to create admin');
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 border rounded shadow-md">
      <h2 className="text-2xl font-bold mb-4 text-center">🛠️ Setup Admin</h2>
      {message && <div className="mb-4 text-sm text-blue-600">{message}</div>}
      <form onSubmit={handleSubmit} className="space-y-4">
        {['firstName','lastName','email','phone','password'].map(field => (
          <input
            key={field}
            name={field}
            type={field === 'email' ? 'email' : field === 'password' ? 'password' : 'text'}
            placeholder={field.charAt(0).toUpperCase() + field.slice(1)}
            value={formData[field]}
            onChange={handleChange}
            required
            className="w-full p-2 border rounded"
          />
        ))}
        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
        >
          Create Admin
        </button>
      </form>
    </div>
  );
};

export default SetupAdmin;
