// Author: Hitiksha Jagani
// Reusable Pie Chart Component for Region-wise or any categorical data

import React from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { MdInbox } from 'react-icons/md';

const COLORS = [
    '#3B82F6',
    '#0EA5E9', // Sky Blue
    '#38BDF8', // Lighter Blue
    '#A5F3FC', // Pale Cyan
    '#5EEAD4', // Aqua Green
    '#60A5FA', // Indigo Blue
    '#93C5FD', // Light Periwinkle
    '#C4B5FD', // Lavender
    '#FCD34D', // Light Gold (contrast)
];

const PieChartComp = ({
    title = "Pie Chart",
    data = [],
    dataKey = "value",
    nameKey = "label",
    height = 300
}) => {

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
                margin: '2rem auto',
                width: '70%',
                color: '#777'
            }}>
                <MdInbox size={64} color="#ccc" />
                <h2 style={{ marginTop: '1rem' }}>No data available to display pie chart.</h2>
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

            <ResponsiveContainer width="100%" height={height}>
                <PieChart>
                    <Pie
                        data={data}
                        dataKey={dataKey} 
                        nameKey={nameKey}
                        cx="50%"
                        cy="50%"
                        outerRadius={100}
                        label
                    >
                        {data.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                </PieChart>
            </ResponsiveContainer>

        </div>
    );
};

export default PieChartComp;
