import React from "react";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-[#1A1B41] text-[#E0E0E0] py-10 px-4 sm:px-8 md:px-16 mt-10 border-t border-[#EDEDED]">
      <div className="max-w-7xl mx-auto grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-6 text-sm">
        {/* Company */}
        <div>
          <h4 className="font-semibold mb-3 text-[#FFD200]">Company</h4>
          <ul className="space-y-2">
            <li><Link to="/" className="text-white hover:text-[#FF9F40] transition">Home</Link></li>
            {/* <li><Link to="/about" className="text-white hover:text-[#FF9F40] transition">About Us</Link></li> */}
            {/* <li><Link to="/services" className="text-white hover:text-[#FF9F40] transition">Services</Link></li>
            <li><Link to="/pricing" className="text-white hover:text-[#FF9F40] transition">Pricing</Link></li> */}
            <li><Link to="/contact" className="text-white hover:text-[#FF9F40] transition">Contact</Link></li>
            <li><Link to="/customer/Orderhistory" className="text-white hover:text-[#FF9F40] transition">Order History</Link></li>
          </ul>
        </div>

        {/* Support */}
        <div>
          <h4 className="font-semibold mb-3 text-[#FFD200]">Support</h4>
          <ul className="space-y-2">
            {/* <li><Link to="/faq" className="text-white hover:text-[#FF9F40] transition">FAQs</Link></li> */}
            <li><Link to="/customer/Orderhistory" className="text-white hover:text-[#FF9F40] transition">Track Order</Link></li>
            <li><Link to="/ticket/raise" className="text-white hover:text-[#FF9F40] transition">Raise Ticket</Link></li>
            <li><Link to="/login" className="text-white hover:text-[#FF9F40] transition">Login</Link></li>
            <li><Link to="/register" className="text-white hover:text-[#FF9F40] transition">Sign Up</Link></li>
          </ul>
        </div>

        {/* Services
        <div>
          <h4 className="font-semibold mb-3 text-[#FFD200]">Services</h4>
          <ul className="space-y-2">
            <li><Link to="/dry-cleaning" className="text-white hover:text-[#FF9F40] transition">Dry Cleaning</Link></li>
            <li><Link to="/washing" className="text-white hover:text-[#FF9F40] transition">Washing & Folding</Link></li>
            <li><Link to="/ironing" className="text-white hover:text-[#FF9F40] transition">Ironing</Link></li>
            <li><Link to="/home-pickup" className="text-white hover:text-[#FF9F40] transition">Home Pickup</Link></li>
            <li><Link to="/express-delivery" className="text-white hover:text-[#FF9F40] transition">Express Delivery</Link></li>
          </ul>
        </div> */}

        {/* Contact */}
        <div>
          <h4 className="font-semibold mb-3 text-[#FFD200]">Contact Us</h4>
          <ul className="space-y-2 text-[#F8F9FA]">
            <li>📍 123 Laundry Street, Mumbai</li>
            <li>📞 <a href="tel:+911234567890" className="text-white hover:text-[#FF9F40] transition">+91 1234567890</a></li>
            <li>📧 <a href="mailto:info@smartlaundry.com" className="text-white hover:text-[#FF9F40] transition">info@smartlaundry.com</a></li>
          </ul>
        </div>

        {/* Legal */}
        <div>
          <h4 className="font-semibold mb-3 text-[#FFD200]">Legal</h4>
          <ul className="space-y-2">
            <li><Link to="/terms" className="text-white hover:text-[#FF9F40] transition">Terms of Service</Link></li>
            <li><Link to="/terms" className="text-white hover:text-[#FF9F40] transition">Privacy Policy</Link></li>
            <li><Link to="/terms" className="text-white hover:text-[#FF9F40] transition">Refund Policy</Link></li>
          </ul>
        </div>
      </div>

      <div className="text-center text-xs text-[#F8F9FA] mt-10">
        © {new Date().getFullYear()} Smart Laundry Service | All Rights Reserved
      </div>
    </footer>
  );
};

export default Footer;
