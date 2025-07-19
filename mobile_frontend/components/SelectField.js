import React from 'react';
import RNPickerSelect from 'react-native-picker-select';
import { View, StyleSheet } from 'react-native';

const SelectField = ({ name, value, onChange, options, placeholder }) => {
  return (
    <View style={styles.container}>
      <RNPickerSelect
        onValueChange={(val) => onChange({ target: { name, value: val } })}
        items={options}
        value={value}
        placeholder={{ label: placeholder || 'Select an option', value: null }}
        style={pickerSelectStyles}
      />
    </View>
  );
};

export default SelectField;

const styles = StyleSheet.create({
  container: {
    borderWidth: 1,
    borderColor: '#DDD6E0',
    borderRadius: 10,
    paddingHorizontal: 10,
    backgroundColor: '#FAF6FF',
  },
});

const pickerSelectStyles = {
  inputIOS: {
    fontSize: 16,
    paddingVertical: 10,
    color: '#2F2F3A',
  },
  inputAndroid: {
    fontSize: 16,
    paddingVertical: 10,
    color: '#2F2F3A',
  },
};
