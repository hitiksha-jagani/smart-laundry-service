import React from "react";
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";
import convenience from "../../assets/convenience.png";
import quality from "../../assets/quality.png";
import support from "../../assets/support.png";
import { FaArrowRight } from "react-icons/fa";
import { Link } from "react-router-dom";
import "../../style/Main.css";

export default function CustomerHomePage() {
  return (
    <div className="flex flex-col min-h-screen bg-gradient-to-br from-[#F9F1FF] via-[#FFFDFC] to-[#FFEFFF] text-text">
      {/* HEADER */}
      <Navbar />

      {/* HERO SECTION */}
      <section className="relative overflow-hidden bg-gradient-to-r from-[#A566FF] via-[#FF6AC2] to-[#FFD93D] text-white py-20 px-6 text-center">
        <div className="max-w-4xl mx-auto z-10 relative">
          <h1 className="text-5xl font-extrabold leading-tight mb-4 drop-shadow-md">
            Smart Laundry at Your Fingertips
          </h1>
          <p className="text-lg font-medium mb-8 opacity-90">
            Schedule pickups, track orders, and enjoy doorstep delivery.
          </p>
          <Link
            to="/service-providers"
            className="inline-flex items-center gap-2 bg-white text-[#A566FF] font-semibold px-6 py-3 rounded-full shadow-lg hover:bg-[#f6e4ff] transition-all duration-300 animate-pulse"
          >
            Book Now <FaArrowRight className="mt-[2px]" />
          </Link>

        </div>

        {/* Bottom curve */}
        <div className="absolute bottom-0 left-0 right-0 h-20 bg-white rounded-t-[50%]"></div>
      </section>

      {/* FEATURE CARDS */}
      <section className="py-16 px-6 bg-white relative z-10">
        <div className="max-w-6xl mx-auto text-center mb-12">
          <h2 className="text-3xl font-bold text-[#4B00B5] mb-2">Why Choose Us?</h2>
          <p className="text-muted text-sm">Experience next-gen laundry with these awesome features</p>
        </div>

        <div className="grid gap-10 sm:grid-cols-1 md:grid-cols-3">
          {[{
            icon: convenience,
            title: "Pickup & Delivery",
            desc: "We pick up your clothes and deliver them fresh & clean.",
          }, {
            icon: quality,
            title: "Professional Cleaning",
            desc: "Handled by experienced providers with top-grade equipment.",
          }, {
            icon: support,
            title: "Easy Support",
            desc: "Track orders, raise tickets, and get support anytime.",
          }].map(({ icon, title, desc }) => (
            <div
              key={title}
              className="group p-6 bg-white/80 backdrop-blur-md border border-[#e2d9ff] rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105 text-center"
            >
              <img src={icon} alt={title} className="h-16 mx-auto mb-4 transition-transform group-hover:rotate-3" />
              <h3 className="text-xl font-semibold text-[#4B00B5] mb-2 group-hover:text-[#FF6AC2] transition-colors">
                {title}
              </h3>
              <p className="text-sm text-gray-700">{desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* FOOTER */}
      <Footer />
    </div>
  );
}
