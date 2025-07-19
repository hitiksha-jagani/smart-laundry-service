/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./App.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#8E6FB7",
        hover: "#6C4D9F",
        background: "#FAF6FF",
        card: "#FFFFFF",
        border: "#DDD6E0",
        text: "#2F2F3A",
        muted: "#6E6E73",
        accent4: "#A566FF",
        accent4Hover: "#914be3",
        success: "#BAA48F",
        error: "#9A3B3B",
        light: "#F4F0FA",
        highlight: "#ECE2F9",

        accent1: "#FFD200",
        accent2: "#FF9F40",
        accent3: "#FF4774",

        provider: {
          primary: "#FFF3E0",
          accent: "#FF9800",
          background: "#FAFAFA",
          text: "#263238",
          button: "#FB923C",
          orangeAccent: '#FB923C',
          orangeHover: '#F97316',
        },
      },
    },
  },
  plugins: [],
};
