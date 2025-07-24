import React from "react";
import { TouchableOpacity, Text, StyleSheet } from "react-native";

export default function PrimaryButton({ onPress, disabled, children }) {
  return (
    <TouchableOpacity
      style={[styles.button, disabled && styles.disabled]}
      onPress={onPress}
      disabled={disabled}
    >
      <Text style={styles.text}>{children}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  button: {
    backgroundColor: "#4B00B5",
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
  },
  text: {
    color: "#fff",
    fontWeight: "600",
    textAlign: "center",
  },
  disabled: {
    backgroundColor: "#a58cd6",
  },
});
