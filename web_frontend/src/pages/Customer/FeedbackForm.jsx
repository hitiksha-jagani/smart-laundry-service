import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "../../utils/axiosInstance";

export default function FeedbackForm() {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [providerId, setProviderId] = useState("");
  const [hasAgent, setHasAgent] = useState(false);

  const [providerRating, setProviderRating] = useState(0);
  const [providerReview, setProviderReview] = useState("");

  const [agentRating, setAgentRating] = useState(0);
  const [agentReview, setAgentReview] = useState("");

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axiosInstance.get(`/orders/${orderId}`)
      .then(res => {
        const order = res.data;
        setProviderId(order.serviceProviderId);
        setHasAgent(!!order.deliveryAgentId); // true if delivery agent exists
        setLoading(false);
      })
      .catch(err => {
        console.error("Failed to fetch order:", err);
        setLoading(false);
      });
  }, [orderId]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // 1. Submit provider feedback
      await axiosInstance.post(`/orders/provider-feedback/${orderId}`, {
        serviceProviderId: providerId,
        rating: providerRating,
        review: providerReview,
      });

      // 2. Submit agent feedback (if agent exists)
      if (hasAgent) {
        await axiosInstance.post(`/orders/agent-feedback/${orderId}`, {
          rating: agentRating,
          review: agentReview,
        });
      }

      alert("Thank you! Feedback submitted.");
      navigate("/customer/dashboard");
    } catch (err) {
      console.error("Feedback submission failed:", err);
      alert("Failed to submit feedback. Please try again.");
    }
  };

  if (loading) return <p className="text-center">Loading...</p>;

  return (
    <div className="max-w-2xl mx-auto mt-10 p-6 border rounded shadow bg-white">
      <h2 className="text-2xl font-semibold mb-6 text-center text-purple-700">
        Submit Your Feedback
      </h2>
      <form onSubmit={handleSubmit} className="space-y-6">

        {/* Provider Feedback */}
        <div>
          <h3 className="text-lg font-semibold mb-2">Service Provider</h3>
          <label className="block mb-1">Rating (1–5)</label>
          <input
            type="number"
            min="1" max="5" step="0.1"
            value={providerRating}
            onChange={(e) => setProviderRating(e.target.value)}
            className="w-full border rounded p-2 mb-3"
            required
          />
          <label className="block mb-1">Review</label>
          <textarea
            value={providerReview}
            onChange={(e) => setProviderReview(e.target.value)}
            className="w-full border rounded p-2"
            rows={3}
            required
          />
        </div>

        {/* Agent Feedback (optional) */}
        {hasAgent && (
          <div>
            <h3 className="text-lg font-semibold mt-6 mb-2">Delivery Agent</h3>
            <label className="block mb-1">Rating (1–5)</label>
            <input
              type="number"
              min="1" max="5" step="0.1"
              value={agentRating}
              onChange={(e) => setAgentRating(e.target.value)}
              className="w-full border rounded p-2 mb-3"
              required
            />
            <label className="block mb-1">Review</label>
            <textarea
              value={agentReview}
              onChange={(e) => setAgentReview(e.target.value)}
              className="w-full border rounded p-2"
              rows={3}
              required
            />
          </div>
        )}

        <button
          type="submit"
          className="w-full py-3 bg-purple-600 text-white font-semibold rounded hover:bg-purple-700"
        >
          Submit Feedback
        </button>
      </form>
    </div>
  );
}
