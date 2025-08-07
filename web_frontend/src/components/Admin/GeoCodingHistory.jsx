// Author: Hitiksha Jagani
// Description: Show history of all added geocoding API providers.

import React, { useEffect, useState } from "react";
import axios from "axios";
import '../../styles/Admin/GeoCodingHistory.css';

const GeoCodingHistory = ({ token }) => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  const axiosInstance = axios.create({
    baseURL: `${BASE_URL}`,
    headers: { Authorization: `Bearer ${token}` },
  });

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await axiosInstance.get("/configurations/history");
        setHistory(response.data || []);
      } catch (error) {
        console.error("Error fetching history:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [token]);

  if (loading) return <p>Loading history...</p>;

  return (
    <div className="admin-form-wrapper">
      <h2>📜 API Provider History</h2>
      <table className="admin-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Provider</th>
            <th>API Key</th>
            <th>Added By</th>
            <th>Created At</th>
          </tr>
        </thead>
        <tbody>
          {history.map((config, index) => (
            <tr key={config.id}>
              <td>{index + 1}</td>
              <td>{config.apiProvider}</td>
              <td style={{ maxWidth: '200px', wordBreak: 'break-all' }}>{config.apiKey}</td>
              <td>{config.users?.firstName} {config.users?.lastName}</td>
              <td>{new Date(config.createdAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default GeoCodingHistory;
