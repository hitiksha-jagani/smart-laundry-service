// Author : Hitiksha Jagani
// Description : Donut chart to display revenue breakdown for admin dashboard with styled legend.

import React from 'react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import { MdInbox } from 'react-icons/md';
import '../../styles/Admin/RevenueBreakdownDonutChart.css';

const COLORS = ['#0EA5E9', '#F3E8FF'];

const RevenueBreakdownDonutChart = ({ data }) => {
 
    const isEmpty =
        !data ||
        (!data.serviceProviderRevenue && !data.deliveryAgentRevenue) ||
        (data.serviceProviderRevenue === 0 && data.deliveryAgentRevenue === 0);

    if (isEmpty) {

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
                color: '#777'
            }}>

                <MdInbox size={64} color="#ccc" />
                <h2 style={{ marginTop: '1rem' }}>No revenue data available for the selected period.</h2>
                <p>Once revenue arrived, they’ll appear here.</p>

            </div>

        );

    }

    const pieData = [
      { name: 'Service Provider', value: data.serviceProviderRevenue },
      { name: 'Delivery Agent', value: data.deliveryAgentRevenue }
    ];

    return (

        <div className="donut-chart-container">

            <h2 className='h2-agent' style={{ color: '#64748B', textAlign: 'center', fontSize: '20px' }}>GRAPH</h2>
              
            <ResponsiveContainer width="80%" height={200}>

                <PieChart>

                    <Pie
                        data={pieData}
                        dataKey="value"
                        nameKey="name"
                        outerRadius={100}
                        innerRadius={60}
                        label
                    >

                        {pieData.map((_, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index]} />
                        ))}

                    </Pie>

                    <Tooltip />

                </PieChart>

            </ResponsiveContainer>

            {/* Stylish Overview */}
            <div className="revenue-overview">

                {pieData.map((entry, index) => (

                    <div key={entry.name} className="revenue-item">

                        <div
                            className="revenue-color-dot"
                            style={{ backgroundColor: COLORS[index] }}
                        />

                        <div>
                            {entry.name}: <strong>₹{entry.value.toLocaleString()}</strong>
                        </div>
                      
                    </div>

                ))}

            </div>

        </div>

    );
};

export default RevenueBreakdownDonutChart;
