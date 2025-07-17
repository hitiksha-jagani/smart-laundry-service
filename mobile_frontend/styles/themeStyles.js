// themeStyles.js
import { StyleSheet } from 'react-native';

export const theme = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: '#FAF6FF', // Custom theme background
    color: '#2F2F3A',
  },
  text: {
    color: '#2F2F3A',
    fontFamily: 'System',
  },
  code: {
    fontFamily: 'Courier New',
  },
  input: {
    padding: 12,
    borderWidth: 1,
    borderColor: '#D1D5DB', // Tailwind border-gray-300
    borderRadius: 8,
    width: '100%',
    backgroundColor: '#FFFFFF',
    color: '#2F2F3A',
  },
  inputFocused: {
    borderColor: '#6C4D9F',
    borderWidth: 2,
  },
  orangeButton: {
    backgroundColor: '#F97316', // Tailwind orange-500
    paddingVertical: 8,
    paddingHorizontal: 24,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 3,
    elevation: 3,
  },
  orangeButtonText: {
    color: '#FFFFFF',
    fontWeight: '600',
    textAlign: 'center',
  },
  fileInput: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderWidth: 1,
    borderColor: '#D1D5DB',
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 1 },
    shadowRadius: 2,
    elevation: 2,
  },
});
