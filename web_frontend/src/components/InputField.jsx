import React, { useState } from "react";

export default function InputField({
  label,
  type = "text",
  name,
  value,
  onChange,
  placeholder = "",
  error = "",
}) {
  const [isFocused, setIsFocused] = useState(false);

  return (
    <div className="relative w-full mb-4">
      <label
        htmlFor={name}
        className={`absolute left-4 top-1/2 transform -translate-y-1/2 pointer-events-none transition-all duration-300 ease-in-out px-1
        bg-background dark:bg-background
        ${isFocused || value
          ? "-top-2 text-sm text-primary"
          : "text-base text-muted-foreground"}`}
      >
        {label}
      </label>

      <input
        id={name}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
        autoComplete="off"
        className={`w-full px-4 pt-6 pb-2 rounded-lg transition-all duration-300 ease-in-out outline-none shadow-sm
        bg-background text-foreground border-2 border-border
        focus:border-primary focus:ring-4 focus:ring-primary/30
        hover:scale-[1.02] focus:scale-[1.05]
        ${error ? "border-destructive focus:border-destructive focus:ring-destructive/40" : ""}`}
      />

      {error && (
        <p className="text-destructive text-sm mt-1 ml-1 font-semibold animate-pulse">
          {error}
        </p>
      )}
    </div>
  );
}
