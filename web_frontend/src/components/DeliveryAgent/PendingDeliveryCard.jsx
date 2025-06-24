// Author : Hitiksha Jagani
// Description : Pending delivery cards in delivery agent dashboard.

import React from 'react';
import '../../styles/DeliveryAgent/PendingDeliveryCard.css';
import '../../styles/DeliveryAgent/DeliveryAgentCommon.css';
import { FaUser, FaPhoneAlt, FaMapMarkerAlt } from 'react-icons/fa';

const PendingDeliveryCard = ({ delivery, onAccept, onReject }) => {

    const {
      orderId, deliveryType, deliveryEarning, km,
      pickupDate, pickupTime, pickupName, pickupPhone, pickupAddress,
      deliveryName, deliveryPhone, deliveryAddress,
      bookingItemDTOList, totalQuantity
    } = delivery;

    const mapRouteLink = `https://www.google.com/maps/dir/?api=1&origin=START_LOCATION&destination=END_LOCATION`;

    return (

        <div className="delivery-card">

            <h4 className="item-list-title">Overview</h4>

            <div className="delivery-summary service-box">

                <div><strong>Delivery Type </strong> <span style={{fontSize: '17px'}}>{deliveryType}</span></div>
                <div><strong>Pikcup Date Time</strong> <span style={{fontSize: '17px'}}>{pickupDate} {pickupTime}</span></div>

            </div>

            <h4 className="item-list-title">Contact Info</h4>
            <div className="contact-grid">
                <div className="contact-box">

                    <h2 className="contact-title">📦 Pickup Contact</h2>

                    <div className="contact-info service-box" style={{backgroundColor:'#ecfdf5'}}>

                        <div className="info-line">
                            <FaUser className="info-icon" />
                            <span className="info-value">{pickupName}</span>
                        </div>

                        <div className="info-line">
                            <FaPhoneAlt className="info-icon" />
                            <span className="info-value">{pickupPhone}</span>
                        </div>

                        <div className="info-line">
                            <FaMapMarkerAlt className="info-icon" />
                            <span className="info-value">{pickupAddress}</span>
                        </div>

                    </div>

                    <button
                        className="route-btn agent-btn"
                        style={{width: '65%'}}
                        onClick={() =>
                          window.open(
                            `https://www.google.com/maps/dir/?api=1&origin=My+Location&destination=${encodeURIComponent(pickupAddress)}`,
                            '_blank'
                          )
                        }
                    >
                        View Route (You ➝ Pickup)
                    </button>

                  </div>

                  <div className="contact-box">

                      <h2 className="contact-title">🚚 Delivery Contact</h2>

                      <div className="contact-info service-box" style={{backgroundColor:'#ecfdf5'}}>

                          <div className="info-line">
                              <FaUser className="info-icon" />
                              <span className="info-value">{deliveryName}</span>
                          </div>

                          <div className="info-line">
                              <FaPhoneAlt className="info-icon" />
                              <span className="info-value">{deliveryPhone}</span>
                          </div>

                          <div className="info-line">
                              <FaMapMarkerAlt className="info-icon" />
                              <span className="info-value">{deliveryAddress}</span>
                          </div>

                      </div>

                      <button
                          className="route-btn agent-btn"
                          style={{width: '65%'}}
                          onClick={() =>
                            window.open(
                              `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(pickupAddress)}&destination=${encodeURIComponent(deliveryAddress)}`,
                              '_blank'
                            )
                          }
                      >
                            View Route (Pickup ➝ Delivery)
                      </button>

                  </div>

                </div>


            <h4 className="item-list-title">Item List</h4>

            <div className="service-group-container">

                {Object.entries(

                    bookingItemDTOList.reduce((grouped, item) => {
                        if (!grouped[item.serviceName]) grouped[item.serviceName] = [];
                        grouped[item.serviceName].push(item);
                        return grouped;
                    }, {})

                ).map(([serviceName, items], idx) => (

                    <div className="service-box" key={idx}>

                        <div className="service-header">
                            <h5>{serviceName}</h5>
                        </div>

                        <div className="item-grid">
                            {items.map((item, i) => (

                                <div className="item-card" key={i}>

                                    <div className="item-info">
                                        <span className="item-name">🧴 {item.itemName}</span>
                                        <span className="item-quantity">Qty: {item.quantity}</span>
                                    </div>

                                </div>

                            ))}
                        </div>

                    </div>

                ))}

            </div>

            <h4 className="item-list-title">Totals</h4>

            <div className="delivery-summary service-box">

                <div><strong>Total Items</strong> {totalQuantity}</div>
                <div><strong>Total KM</strong> {km} km</div>
                <div><strong>Earnings</strong> ₹{deliveryEarning}</div>

            </div>

            <div className="delivery-actions">

                <button className="accept-btn" onClick={() => onAccept(orderId)}>Accept</button>
                <button className="reject-btn" onClick={() => onReject(orderId)}>Reject</button>
            
            </div>

        </div>

    );
};

export default PendingDeliveryCard;