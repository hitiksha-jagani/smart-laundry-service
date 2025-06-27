import React from "react";
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";

export default function TermsAndConditions() {
  return (
    <div className="flex flex-col min-h-screen bg-white text-gray-800">
      <Navbar />

      <main className="flex-grow max-w-4xl mx-auto px-6 py-12">
        <h1 className="text-4xl font-bold mb-8 text-center text-[#4B00B5]">
          Terms and Conditions for Customers
        </h1>

        <section className="space-y-6 text-justify">
          <p className="text-sm text-gray-500">
            Smart Laundry Service <br />
            <strong>Effective Date:</strong> 15/5/2025 <br />
            <strong>Last Updated:</strong> 23/5/2025
          </p>

          <p>
            Welcome to Smart Laundry Service! These Terms and Conditions (“Terms”) govern your use of our laundry services as a registered customer (“you” or “Customer”) on our platform (“Smart Laundry Service”, “we”, “our”, or “the Company”). By using our services, you agree to these Terms in full.
          </p>

          <ol className="list-decimal list-inside space-y-4">
            <li>
              <strong>Account Registration</strong>
              <ul className="list-disc ml-6">
                <li>You must be at least 18 years of age to register.</li>
                <li>You agree to provide accurate, current, and complete information.</li>
                <li>You are responsible for maintaining the confidentiality of your login credentials.</li>
              </ul>
            </li>

            <li>
              <strong>Laundry Services</strong>
              <ul className="list-disc ml-6">
                <li>You may request pickup, laundering, and delivery of your items through our platform.</li>
                <li>Service availability may vary based on your location.</li>
                <li>We reserve the right to reject or reschedule orders due to capacity or logistical constraints.</li>
              </ul>
            </li>

            <li>
              <strong>Pickups and Deliveries</strong>
              <ul className="list-disc ml-6">
                <li>You must ensure items are ready for pickup at the scheduled time.</li>
                <li>If you are unavailable, you may authorize someone else to hand over or receive items.</li>
                <li>Missed pickups or deliveries may incur a rescheduling fee.</li>
              </ul>
            </li>

            <li>
              <strong>Garment Care and Liability</strong>
              <ul className="list-disc ml-6">
                <li>We follow standard industry practices for cleaning and handling garments.</li>
                <li>We are not responsible for items with missing or unclear care labels.</li>
                <li>We are not responsible for damage from normal wear and tear or inherent fabric weaknesses.</li>
                <li>We are not responsible for pre-existing damages or color bleeding.</li>
                <li>You must notify us of any damage or missing items within 24 hours of delivery.</li>
              </ul>
            </li>

            <li>
              <strong>Pricing and Payments</strong>
              <ul className="list-disc ml-6">
                <li>Prices are listed on the platform and are subject to change.</li>
                <li>All payments must be made online through our secure payment gateway.</li>
                <li>You agree to pay all charges incurred under your account.</li>
              </ul>
            </li>

            <li>
              <strong>Cancellations and Refunds</strong>
              <ul className="list-disc ml-6">
                <li>Cancellations are allowed up to 1 hour before scheduled pickup without penalty.</li>
                <li>Late cancellations may incur a fee.</li>
              </ul>
            </li>

            <li>
              <strong>Customer Conduct</strong>
              <ul className="list-disc ml-6">
                <li>You agree to use the platform for lawful purposes only.</li>
                <li>You will not misuse the service or harass staff, delivery agents, or service providers.</li>
                <li>You will not attempt to reverse engineer, damage, or hack the system.</li>
              </ul>
            </li>

            <li>
              <strong>Account Termination</strong>
              <ul className="list-disc ml-6">
                <li>We may suspend or terminate your account if you breach any of these Terms.</li>
                <li>We may suspend or terminate your account for misuse of service or inappropriate behavior.</li>
                <li>We may suspend or terminate your account for fraudulent activity.</li>
              </ul>
            </li>

            <li>
              <strong>Limitation of Liability</strong>
              <ul className="list-disc ml-6">
                <li>Our liability is limited to the value of the affected laundry order.</li>
                <li>We are not liable for indirect or consequential damages, including emotional distress, lost profits, or missed events.</li>
              </ul>
            </li>

            <li>
              <strong>Privacy Policy</strong>
              <p>We value your privacy. Your personal and payment information is handled according to our Privacy Policy.</p>
            </li>

            <li>
              <strong>Changes to These Terms</strong>
              <p>We may update these Terms from time to time. Continued use of the platform indicates your acceptance of the revised Terms.</p>
            </li>

            <li>
              <strong>Contact Us</strong>
              <p>
                If you have any questions or concerns, please reach out to us at: <br />
                <strong>Email:</strong> support@smartlaundryservice.com <br />
                <strong>Phone:</strong> +91 1234567890
              </p>
            </li>
          </ol>
        </section>
      </main>

      <Footer />
    </div>
  );
} 
