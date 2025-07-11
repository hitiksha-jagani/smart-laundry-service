// Author : Hitiksha Jagani
// Description : List of all payouts for delivery agent dashboard.

import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import { MdInbox } from 'react-icons/md';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/PendingPayouts.css';

const AllPayouts = () => {

    const location = useLocation();
    const state = location.state || {};
    const user = state.user;
    const data = state.data || [];

    return (

        <>

            <DeliveryAgentDashboardLayout user={user}>

                <h2 className='pending-heading heading-agent h2-agent'>TOTAL PAYOUTS</h2>

                {(!data || data.length === 0) ? (

                    <div style={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        padding: '2rem',
                        backgroundColor: '#fff',
                        border: '1px dashed #ccc',
                        borderRadius: '1rem',
                        margin: '2rem auto',
                        width: '80%',
                        color: '#777'
                    }}>

                        <MdInbox size={64} color="#ccc" />
                        <h2 style={{ marginTop: '1rem' }}>No Payouts Available</h2>
                        <p>Once payouts arrived, they’ll appear here.</p>

                    </div>

                ) : (

                    <div className="pending-payouts-container">

                        <table className="payouts-table">

                            <thead>

                                <tr>

                                    <th>No.</th>
                                    <th>Order ID</th>
                                    <th>Delivery Earning (₹)</th>
                                    <th>Charge (₹)</th>
                                    <th>Final Amount (₹)</th>
                                    <th>Date & Time</th>
                                    <th>Status</th>

                                </tr>

                            </thead>

                            <tbody>
                                
                                {data.map((payout, index) => (

                                    <tr key={payout.payoutId}>

                                        <td>{index + 1}</td> 
                                        <td>{payout.orderId}</td>
                                        <td>{payout.deliveryEarning ? payout.deliveryEarning?.toFixed(2) : '-'}</td>
                                        <td>{payout.charge ? payout.charge?.toFixed(2) : '-'}</td>
                                        <td>{payout.finalAmount?.toFixed(2)}</td>
                                        <td>{payout.dateTime ? new Date(payout.dateTime).toLocaleString() : '-'}</td>
                                        <td>{payout.payoutStatus}</td>
                                        
                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </div>

                )}

            </DeliveryAgentDashboardLayout>

        </>

    );

};

export default AllPayouts;