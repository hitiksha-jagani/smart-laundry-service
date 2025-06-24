const DeliverySummary = ({ summary }) => {
  if (!summary) return null;

  return (
    <div className="p-4 bg-white rounded-lg shadow-md mb-4">
      <h2 className="text-xl font-bold mb-2">Delivery Summary</h2>
      <div className="flex flex-col gap-2">
        <div>Pending Deliveries: <strong>{summary.pendingDeliveries}</strong></div>
        <div>Today's Deliveries: <strong>{summary.todayDeliveries}</strong></div>
      </div>
    </div>
  );
};

export default DeliverySummary;
