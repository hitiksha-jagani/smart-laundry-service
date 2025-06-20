/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        // 🌸 Customer Dashboard Theme (default)
        primary: "#8E6FB7",        // Dusty Purple
        hover: "#6C4D9F",          // Royal Plum
        background: "#FAF6FF",     // Lavender Mist
        card: "#FFFFFF",           // White
        border: "#DDD6E0",         // Mist Gray
        text: "#2F2F3A",           // Deep Slate
        muted: "#6E6E73",          // Soft Gray

        success: "#BAA48F",
        error: "#9A3B3B",
        light: "#F4F0FA",
        highlight: "#ECE2F9",

        accent1: "#FFD200",        // Yellow (footer section titles)
        accent2: "#FF9F40",        // Orange hover (footer links)
        accent3: "#FF4774",        // Pinkish-red (navbar gradient)
        accent4: "#A566FF",        // Purple-pink (hero button text)

        // 🟧 Service Provider Dashboard Theme (namespaced under `provider`)
        provider: {
          primary: "#FFF3E0",     // Light Cream
          accent: "#FF9800",      // Vibrant Orange
          background: "#FAFAFA",  // Subtle Light
          text: "#263238",        // Dark Gray
          button: "#FB923C",      // Modern Orange
          orangeAccent: '#FB923C',
          orangeHover: '#F97316',
        },
      },
    },
  },
  plugins: [],
};
