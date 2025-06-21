const TodayDeliveries = ({ deliveries }) => {
  return (
    <div className="mb-6">
      <h2 className="text-xl font-semibold">Today’s Deliveries</h2>
      <ul>
        {deliveries.map((delivery) => (
          <li key={delivery.orderId}>
            {delivery.customerName} - {delivery.status}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TodayDeliveries;
