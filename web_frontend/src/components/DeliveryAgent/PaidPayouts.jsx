// Author : Hitiksha Jagani
// Description : Paid payout list for delivery agent dashboard.

import { MdInbox } from 'react-icons/md';
import { useLocation } from 'react-router-dom';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/PendingPayouts.css';

const PaidPayouts = () => {

    const location = useLocation();
    const state = location.state || {};
    const user = state.user;
    const data = state.data || [];
    const { filter, startDate, endDate } = state;

    return (

        <>

            <DeliveryAgentDashboardLayout user={user}>

            <h2 className='pending-heading heading-agent h2-agent'>PAID PAYOUTS</h2>

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
                    <h2 style={{ marginTop: '1rem' }}>No Paid Payouts Available</h2>
                    <p>Once new Paid payouts arrive, they’ll appear here.</p>
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

                            </tr>

                        </thead>

                        <tbody>
                            
                            {data.map((payout, index) => (

                                <tr key={payout.payoutId}>

                                    <td>{index + 1}</td> 
                                    <td>{payout.orderId}</td>
                                    <td>{payout.deliveryEarning?.toFixed(2)}</td>
                                    <td>{payout.charge?.toFixed(2)}</td>
                                    <td>{payout.finalAmount?.toFixed(2)}</td>
                                    <td>{payout.dateTime ? new Date(payout.dateTime).toLocaleString() : '-'}</td>

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

export default PaidPayouts;