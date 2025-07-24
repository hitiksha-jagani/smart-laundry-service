import React from "react";

const SelectField = ({ name, value, onChange, options, placeholder }) => {
  return (
    <select
      name={name}
      value={value}
      onChange={onChange}
      className="w-full border border-border rounded-lg px-3 py-2 text-foreground bg-background focus:outline-none focus:ring-2 focus:ring-primary"
    >
      <option value="" disabled>
        {placeholder}
      </option>
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </select>
  );
};

export default SelectField;
