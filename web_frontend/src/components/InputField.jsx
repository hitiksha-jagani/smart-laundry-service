
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

  const isFloating = isFocused || value;

  return (
    <div className="relative w-full mb-4">
      <label
        htmlFor={name}
        className={`absolute left-4 transition-all duration-300 ease-in-out bg-background dark:bg-background px-1 pointer-events-none
        ${isFloating ? "top-1 text-xs text-primary" : "top-3.5 text-sm text-muted-foreground"}`}
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
