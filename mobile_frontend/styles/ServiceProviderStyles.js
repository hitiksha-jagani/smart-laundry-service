import { StyleSheet } from 'react-native';

export const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#FFF7ED',
  },
  header: {
    fontSize: 26,
    fontWeight: 'bold',
    color: '#EA580C',
    textAlign: 'center',
    marginBottom: 20,
  },
  subHeader: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 20,
    marginBottom: 10,
  },
  input: {
    borderWidth: 1,
    borderColor: '#E5E7EB',
    padding: 10,
    borderRadius: 8,
    marginBottom: 12,
    backgroundColor: '#FFF',
  },
  checkboxContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  checkbox: {
    backgroundColor: '#fff',
    padding: 8,
    borderRadius: 20,
    borderColor: '#ddd',
    borderWidth: 1,
    marginBottom: 10,
  },
  button: {
    backgroundColor: '#FB923C',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginVertical: 10,
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  itemRow: {
    backgroundColor: '#fff',
    padding: 10,
    marginVertical: 5,
    borderRadius: 8,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  uploadButton: {
    backgroundColor: '#fff',
    padding: 12,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    marginBottom: 10,
  },
  submitButton: {
    backgroundColor: '#EA580C',
    padding: 16,
    borderRadius: 8,
    marginTop: 20,
  },
  submitButtonText: {
    color: '#fff',
    fontWeight: 'bold',
    textAlign: 'center',
  },
});
