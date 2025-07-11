// Author : Hitiksha Jagani
// Desciption : Revenue breakdown using table for admin dashboard.

import React from 'react';
import { MdInbox } from 'react-icons/md';
import '../../styles/Admin/RevenuePage.css';

const RevenueBreakdownTable = ({ data }) => {

    const isEmpty = !data || (
        !data.serviceProviderRevenue &&
        !data.deliveryAgentRevenue &&
        !data.adminRevenue
    );

    return (

        <>

            {isEmpty ? (

                <div
                    style={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        padding: '2rem',
                        backgroundColor: '#fff',
                        border: '1px dashed #ccc',
                        borderRadius: '1rem',
                        margin: '2rem auto',
                        width: '70%',
                        color: '#777',
                    }}
                >
                    
                    <MdInbox size={64} color="#ccc" />

                    <h2 style={{ marginTop: '1rem' }}>
                        No revenue data available for the selected period.
                    </h2>

                    <p>Once revenue is recorded, it’ll appear here.</p>
                </div>

            ) : (

                <div className='admin-pending-payouts-container donut-chart-container'>

                    <h2 className='h2-agent' style={{ color: '#64748B', textAlign: 'center', fontSize: '20px' }}>TABLE</h2>

                    <table className="admin-payouts-table" >

                        <thead>

                            <tr>

                                <th>Revenue Source</th>
                                <th>Amount (₹)</th>

                            </tr>

                        </thead>

                        <tbody>
 
                            <tr>
                                <td style={{ borderLeft: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>From Service Provider</td>
                                
   
                                <td style={{ borderRight: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>
                                    ₹{data.serviceProviderRevenue?.toFixed(2) ?? '0.00'}
                                </td>

                            </tr>

                            <tr>

                                <td style={{ borderLeft: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>From Delivery Agent</td>
                                
                                <td style={{ borderRight: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>
                                    ₹{data.deliveryAgentRevenue?.toFixed(2) ?? '0.00'}
                                </td>

                            </tr>

                            <tr>
                                
                                <td style={{ borderLeft: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>
                                    Total Admin Revenue
                                </td>

                                <td style={{ borderRight: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>
                                    ₹{data.adminRevenue?.toFixed(2) ?? '0.00'}
                                </td>

                            </tr>

                        </tbody>

                    </table>

                </div>

            )}

        </>

    );
};

export default RevenueBreakdownTable;
