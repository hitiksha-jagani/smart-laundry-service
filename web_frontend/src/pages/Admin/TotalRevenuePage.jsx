// Author : Hitiksha Jagani
// Description : Total revenue list for admin dashboard.

import { MdInbox } from 'react-icons/md';
import { useLocation } from 'react-router-dom';
import AdminDashboardLayout from '../../components/Layout/AdminDashboardLayout';
import RevenueMenu from '../../components/Admin/RevenueMenu';
import '../../styles/Admin/TotalRevenuePage.css';

const TotalRevenuePage = () => {

    const location = useLocation();
    const state = location.state || {};
    const user = state.user;
    const data = state.data || [];

    return (
    

            <AdminDashboardLayout user={user}>                   

                <h2 className='heading-admin h2-admin'>TOTAL PAYOUTS</h2>
    
                {(!data || data.length === 0) ? (
        
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
                            No total revenue data available for the selected period.
                        </h2>
        
                        <p>Once revenue is recorded, it’ll appear here.</p>

                    </div>
        
                ) : ( 
    
                <div className="admin-total-payouts-container">

                    <table className="admin-total-table">

                            <thead>

                                <tr>

                                    <th>No.</th>
                                    <th>Order ID</th>
                                    <th>Date</th>
                                    <th>Customer Name</th>
                                    <th>Total Paid</th>
                                    <th>Service Provider Payout</th>
                                    <th>Delivery Agent Payout</th>
                                    <th>Admin Revenue</th>

                                </tr>

                            </thead>

                            <tbody>
                                
                                {data.map((revenue, index) => (

                                    <tr key={revenue.orderId}>

                                        <td style={{ borderLeft: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{index + 1}</td> 
                                        <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.orderId}</td>
                                        <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.date}</td>
                                        <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.customerName}</td>
                                        <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.totalPaid}</td>
                                        <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.providerPayout}</td>
                                        <td style={{ borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.agentPayout ? revenue.agentPayout.toLocaleString() : '-'}</td>
                                        <td style={{ borderRight: '1px solid #0EA5E9', borderTop: '1px solid #0EA5E9', borderBottom: '1px solid #0EA5E9' }}>{revenue.adminRevenue}</td>
                                        
                                    </tr>

                                ))}

                            </tbody>

                    </table>

                </div>

            )}


                

            </AdminDashboardLayout>
    
    
    );

};

export default TotalRevenuePage;