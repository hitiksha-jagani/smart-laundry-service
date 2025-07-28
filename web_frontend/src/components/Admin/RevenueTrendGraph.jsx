// Author : Hitiksha Jagani
// Desciption : Revenue trend graph for admin dashboard.

import React from 'react';
import { MdInbox } from 'react-icons/md';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

const RevenueTrendGraph = ({ title, data }) => {

    if (!data || data.length === 0) {

        return ( 
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                padding: '2rem',
                backgroundColor: '#fff',
                border: '1px dashed #ccc',
                borderRadius: '1rem',
                margin: '2rem 2rem',
                width: '70%',
                color: '#777',
                margin: '50px auto'
            }}>
                <MdInbox size={64} color="#ccc" />
                <h2 style={{ marginTop: '1rem' }}>No data available for the selected period.</h2>
                <p>Once data is added, it’ll appear here.</p>
            </div>
        );         

    }

    return (

        <div style={{ 
                width: '90%',           
                maxWidth: '1000px',
                height: 500,
                margin: '70px auto 0',   
                border: '1px solid #0EA5E9',
                borderRadius: '10px',
                background: '#fff'
            }}  
            className="donut-chart-container"
        >

            <h2 style={{ textAlign: 'center', textTransform: 'capitalize'  }}>{title}</h2>

            <ResponsiveContainer>

                <LineChart data={data}>

                    <CartesianGrid stroke="#ccc" strokeDasharray="3 3" />

                    <XAxis dataKey="label" />

                    <YAxis />

                    <Tooltip />

                    <Line type="monotone" dataKey="revenue" stroke="#0EA5E9" strokeWidth={2} />

                </LineChart>

            </ResponsiveContainer>

        </div>

    );

};

export default RevenueTrendGraph;
