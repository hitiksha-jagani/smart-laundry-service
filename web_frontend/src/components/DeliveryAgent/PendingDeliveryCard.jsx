import React from 'react';
import '../../styles/DeliveryAgent/PendingDeliveryCard.css'; // optional for styles

const PendingDeliveryCard = ({ delivery, onAccept, onReject }) => {
  const {
    orderId, deliveryType, deliveryEarning, km,
    customerName, customerPhone, customerAddress,
    providerName, providerPhone, providerAddress,
    bookingItemDTOList, totalQuantity
  } = delivery;

  const mapRouteLink = `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(providerAddress)}&destination=${encodeURIComponent(customerAddress)}`;

  return (
    <div className="delivery-card">
      <h3>Delivery Type: {deliveryType}</h3>

      <div className="delivery-section">
        <h4>Pickup Contact (Provider)</h4>
        <p>{providerName}</p>
        <p>{providerPhone}</p>
        <p>{providerAddress}</p>
      </div>

      <div className="delivery-section">
        <h4>Delivery Contact (Customer)</h4>
        <p>{customerName}</p>
        <p>{customerPhone}</p>
        <p>{customerAddress}</p>
      </div>

      <div className="delivery-section">
        <h4>Item List</h4>
        <ul>
          {bookingItemDTOList.map((item, idx) => (
            <li key={idx}>
              {item.itemName} ({item.serviceName}) - Qty: {item.quantity}
            </li>
          ))}
        </ul>
      </div>

      <div className="delivery-summary">
        <p><strong>Total Items:</strong> {totalQuantity}</p>
        <p><strong>Total KM:</strong> {km} km</p>
        <p><strong>Earnings:</strong> ₹{deliveryEarning}</p>
        <a href={mapRouteLink} target="_blank" rel="noreferrer" className="route-link">View Route</a>
      </div>

      <div className="delivery-actions">
        <button className="accept-btn" onClick={() => onAccept(orderId)}>Accept</button>
        <button className="reject-btn" onClick={() => onReject(orderId)}>Reject</button>
      </div>
    </div>
  );
};

export default PendingDeliveryCard;
