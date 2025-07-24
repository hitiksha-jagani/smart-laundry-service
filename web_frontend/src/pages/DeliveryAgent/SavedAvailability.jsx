// Author : Hitiksha Jagani
// Description : List of saved availabilities for delivery agent dashboard.

import React, { useState } from 'react';
import { MdInbox } from 'react-icons/md';
import axiosInstance from '../../utils/axiosInstance';
import '../../styles/DeliveryAgent/PendingPayouts.css';
import '../../styles/DeliveryAgent/ManageAvailability.css';
import '../../styles/DeliveryAgent/SaveAvailability.css';
import '../../styles/DeliveryAgent/PendingDeliveryCard.css';
import '../../styles/Toast.css';

const SavedAvailability = ({ availabilities }) => {
    const [repeatNextWeek, setRepeatNextWeek] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [editData, setEditData] = useState(null);
    const [id, setId] = useState(null);

    const [toast, setToast] = useState({ message: '', type: '', visible: false });
            
    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
            
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 5000);
    };

    // Edit existing availability
    const handleEditClick = (entry) => {
        setEditData({
            id: entry.availabilityId,
            dayOfWeek: entry.dayOfWeek,
            startTime: entry.startTime || "09:00",
            endTime: entry.endTime || "17:00",
            holiday: entry.holiday
        });
        setEditModalOpen(true);
    };

    const handleEditSave = async () => {
        const dto = {
            startTime: editData.holiday ? null : editData.startTime,
            endTime: editData.holiday ? null : editData.endTime,
            holiday: editData.holiday
        };

        try {
            await axiosInstance.put(`/availability/manage/edit/${editData.id}`, dto);
            showToast('Availability saved successfully!', "success");
            setEditModalOpen(false);
            window.location.reload();
        } catch (err) {
            console.error("Update failed", err);
            showToast('Failed to update availability.', "error");
        }
    };

    // Delete Saved Availability
    const handleDeleteClick = (id) => {
        if (window.confirm("Are you sure you want to delete this availability?")) {
            handleDelete(id);
        }
    };

    const handleDelete = async (id) => {
        try {
            await axiosInstance.delete(`/availability/manage/delete/${id}`);
            showToast('Availability delete successfully!', "success");
            window.location.reload();
        } catch (err) {
            console.error("Delete failed", err);
            showToast('Failed to delete availability.', "error");
        }
    };

    const handleRepeatSubmit = async () => {
        if (!repeatNextWeek) return;

        try {
            setSubmitting(true);
            await axiosInstance.post('/availability/repeat-next-week');
            showToast('Schedule repeated for next week successfully!', 'success');
            setTimeout(() => window.location.reload(), 1000);
        } catch (err) {
            console.error("Error repeating schedule:", err);
            showToast('Failed to apply schedule for next week.', 'error');
        } finally {
            setSubmitting(false);
        }
    };

    return (

        <>

            {(!availabilities || availabilities.length === 0) ? (

                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '2rem', backgroundColor: '#fff', border: '1px dashed #ccc', borderRadius: '1rem', margin: '2rem auto', width: '80%', color: '#777' }}>

                    <MdInbox size={64} color="#ccc" />
                    <h2 style={{ marginTop: '1rem' }}>No Availabilities</h2>
                    <p>Your schedule will appear here once added.</p>

                </div>

            ) : (

                <div className="pending-payouts-container">

                    <table className="payouts-table">

                        <thead>

                            <tr>

                                <th>No.</th>
                                <th>Date</th>
                                <th>Day Of Week</th>
                                <th>Holiday</th>
                                <th>Start Time</th>
                                <th>End Time</th>
                                <th>Action</th>

                            </tr>

                        </thead>

                        <tbody>

                            {availabilities.map((a, index) => (

                                <tr key={index}>

                                    <td>{index + 1}</td>
                                    <td>{a.date}</td>
                                    <td>{a.dayOfWeek}</td>
                                    <td>{a.holiday ? "Yes" : "No"}</td>
                                    <td>{a.startTime || '-'}</td>
                                    <td>{a.endTime || '-'}</td>
                                    <td>
                                        <button className='agent-btn' onClick={() => handleEditClick(a)} style={{ marginRight: '10px', paddingLeft: '20px', paddingRight: '20px' }}>EDIT</button>
                                        <button className='reset-btn' onClick={() => handleDeleteClick(a.availabilityId)}>DELETE</button>
                                    </td>

                                </tr>

                            ))}

                        </tbody>

                    </table>

                    <div style={{ marginTop: '20px', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '10px' }}>
                        
                        <input
                            type="radio"
                            name="repeatNextWeek"
                            id="repeatNextWeek"
                            checked={repeatNextWeek}
                            onChange={() => setRepeatNextWeek(!repeatNextWeek)}
                        />

                        <label htmlFor="repeatNextWeek">Apply this schedule for next week also</label>
                    
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '50px', marginTop: '20px' }}>
                        
                        <button
                            onClick={handleRepeatSubmit}
                            disabled={!repeatNextWeek || submitting}
                            className='agent-btn'
                        >
                            {submitting ? 'Applying...' : 'Confirm'}
                        </button>

                    </div>

                </div>

            )}

            {editModalOpen && (

                <div
                    style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0,0,0,0.5)',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        zIndex: 999,
                        padding: '1rem', 
                    }}
                >

                    <div
                        className="contact-box"
                        style={{
                            backgroundColor: '#ffffff',
                            width: '100%',
                            maxWidth: '600px',
                            padding: '2rem',
                            borderRadius: '0.75rem',
                            boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
                        }}
                    >

                        <h2
                            style={{ 
                                textAlign: 'center',
                                fontSize: '1.2rem',
                                color: '#10b981',
                                fontWeight: '600',
                                marginBottom: '1rem',
                                borderBottom: '2px solid #d1fae5',
                                paddingBottom: '0.50rem',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center',
                                gap: '0.5rem' 
                            }}>

                                🗓️ {editData.dayOfWeek}

                        </h2>

                        <div
                            className="service-box"
                            style={{ backgroundColor: '#ecfdf5', marginBottom: '1.5rem' }}
                        >

                            <div style={{ display: 'flex', justifyContent: 'center' , gap: '1.2rem', marginBottom: '1rem' }}>

                                <label className="info-line" style={{ fontSize: '18px' }}>

                                    <input
                                    type="radio"
                                    name="editStatus"
                                    value="working"
                                    checked={!editData.holiday}
                                    onChange={() =>
                                        setEditData((prev) => ({ ...prev, holiday: false }))
                                    }
                                    />

                                    Working Day
                                </label>

                                <label className="info-line" style={{ fontSize: '18px' }}>

                                    <input
                                    type="radio"
                                    name="editStatus"
                                    value="holiday"
                                    checked={editData.holiday}
                                    onChange={() =>
                                        setEditData((prev) => ({ ...prev, holiday: true }))
                                    }
                                    />

                                    Holiday
                                </label>

                            </div>

                            {!editData.holiday && (

                                <div className="delivery-summary" style={{ marginTop: '1rem', backgroundColor: '#FFFFFF' }}>

                                    <div>

                                        <strong>Start Time</strong>

                                        <input
                                            type="time"
                                            min="09:00"
                                            max="19:00"
                                            value={editData.startTime}
                                            onChange={(e) =>
                                                setEditData((prev) => ({
                                                    ...prev,
                                                    startTime: e.target.value,
                                                }))
                                            }
                                            style={{ padding:'10px', borderRadius: '5px', border: '2px solid #d1fae5' }}
                                        />

                                    </div>

                                    <div>

                                        <strong>End Time</strong>

                                        <input
                                            type="time"
                                            min="09:00"
                                            max="19:00"
                                            value={editData.endTime}
                                            onChange={(e) =>
                                                setEditData((prev) => ({
                                                    ...prev,
                                                    endTime: e.target.value,
                                                }))
                                            }
                                            style={{ padding:'10px', borderRadius: '5px', border: '2px solid #d1fae5' }}
                                        />

                                    </div>

                                </div>

                            )}

                        </div>

                        <div
                            style={{
                                display: 'flex',
                                justifyContent: 'center',
                                marginTop: '1.5rem'
                            }}
                        >

                            <button 
                                className="agent-btn accept-btn" 
                                onClick={handleEditSave}
                                style={{ marginRight: '10px', width: '120px' }}>
                                SAVE
                            </button>

                            <button
                                className="agent-btn reject-btn"
                                onClick={() => setEditModalOpen(false)}
                                style={{ width: '120px' }}
                            >
                                CANCEL
                            </button>

                        </div>

                    </div>
                

                </div> 
            
            )}

            {toast.visible && <div className={`custom-toast ${toast.type}`}>{toast.message}</div>}

        </>

    );
};

export default SavedAvailability;
