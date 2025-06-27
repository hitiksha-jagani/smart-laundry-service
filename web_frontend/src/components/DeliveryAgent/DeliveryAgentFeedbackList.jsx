// Author : Hitiksha Jagani
// Description : Feedback list page for delivery agent dashboard.

import { MdInbox } from 'react-icons/md';
import { useLocation } from 'react-router-dom';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import '../../styles/DeliveryAgent/PendingPayouts.css';

const DeliveryAgentFeedbackList = () => {

    const location = useLocation();
    const state = location.state || {};
    const user = state.user;
    const data = state.data || [];
    const { filter, startDate, endDate } = state;

    return (

        <>

            <DeliveryAgentDashboardLayout user={user}>

            <h2 className='pending-heading heading-agent h2-agent'>REVIEW LIST</h2>

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
                    <h2 style={{ marginTop: '1rem' }}>No Reviews Available</h2>
                    <p>Once new reviews arrive, they’ll appear here.</p>
                </div>
            ) : (

                <div className="pending-payouts-container">

                    <table className="payouts-table">

                        <thead>

                            <tr>

                                <th>No.</th>
                                <th>Order Id</th>
                                <th>Customer Name</th>
                                <th>Rating</th>
                                <th>Feedback</th>
                                <th>Date & Time</th>

                            </tr>

                        </thead>

                        <tbody>
                            
                            {data.map((payout, index) => (

                                <tr key={payout.payoutId}>

                                    <td>{index + 1}</td> 
                                    <td>{payout.orderId}</td>
                                    <td>{payout.customerName}</td>
                                    <td>{payout.rating}</td>
                                    <td>{payout.review}</td>
                                    <td>{payout.createdAt}</td>

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

export default DeliveryAgentFeedbackList;