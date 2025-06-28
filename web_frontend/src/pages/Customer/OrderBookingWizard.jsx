import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

import Navbar from "../../components/Navbar"; // ✅ Added
import Footer from "../../components/Footer"; // ✅ Added

import InitialOrder from "./InitialOrder";
import SchedulePlan from "./SchedulePlan";
import ContactInfo from "./ContactInfo";
import ReviewAndConfirm from "./ReviewAndConfirm";

export default function OrderBookingWizard() {
  const [step, setStep] = useState(1);
  const [dummyOrderId, setDummyOrderId] = useState("");

  const [initialOrderData, setInitialOrderData] = useState({
    items: [{ itemId: "", quantity: 1 }],
    pickupDate: "",
    pickupTime: "",
    goWithSchedulePlan: false,
    serviceProviderId: "",

    schedulePlan: "",
    paymentOption: "",
    contactName: "",
    contactPhone: "",
    contactAddress: "",
  });

  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userId = token ? jwtDecode(token).userId : null;

  const nextStep = (useSchedulePlan = null) => {
    if (step === 1 && useSchedulePlan !== null) {
      setInitialOrderData((prev) => ({
        ...prev,
        goWithSchedulePlan: useSchedulePlan,
      }));
      setStep(useSchedulePlan ? 2 : 3);
    } else {
      setStep((prev) => prev + 1);
    }
  };

  const prevStep = () => {
    setStep((prev) => prev - 1);
  };

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, [step]);

  const steps = [
    "Initial Order",
    "Schedule Plan",
    "Contact Info",
    "Review & Confirm",
  ];

  return (
    <div className="flex flex-col min-h-screen bg-white text-gray-900">
      {/* Header */}
      <Navbar />

      {/* Main Content */}
      <main className="flex-grow max-w-4xl mx-auto px-4">
        {/* Step Tracker */}
        <div className="flex justify-center space-x-6 my-6">
          {steps.map((label, index) => (
            <div key={index} className="text-center">
              <div
                className={`w-8 h-8 mx-auto rounded-full flex items-center justify-center text-white font-semibold transition-all ${
                  step === index + 1
                    ? "bg-accent4"
                    : step > index + 1
                    ? "bg-green-500"
                    : "bg-border"
                }`}
              >
                {index + 1}
              </div>
              <div className="text-xs mt-1 text-text font-medium">{label}</div>
            </div>
          ))}
        </div>

        {/* Step Components */}
        {step === 1 && (
          <InitialOrder
            onNext={nextStep}
            setDummyOrderId={setDummyOrderId}
            userId={userId}
            initialOrderData={initialOrderData}
            setInitialOrderData={setInitialOrderData}
          />
        )}

        {step === 2 && (
          <SchedulePlan
            dummyOrderId={dummyOrderId}
            userId={userId}
            providerId={initialOrderData.serviceProviderId}
            onNext={nextStep}
            onPrev={prevStep}
            initialOrderData={initialOrderData}
            setInitialOrderData={setInitialOrderData}
          />
        )}

        {step === 3 && (
          <ContactInfo
            dummyOrderId={dummyOrderId}
            userId={userId}
            initialOrderData={initialOrderData}
            setInitialOrderData={setInitialOrderData}
            onNext={nextStep}
            onPrev={() => setStep(initialOrderData.goWithSchedulePlan ? 2 : 1)}
          />
        )}

        {step === 4 && (
          <ReviewAndConfirm
            dummyOrderId={dummyOrderId}
            userId={userId}
            onPrev={() => setStep(3)}
            onOrderCreated={(order) => {
              console.log("Order placed:", order);
              navigate("/order/success", { state: order });
            }}
          />
        )}
      </main>

      {/* Footer */}
      <Footer />
    </div>
  );
}
