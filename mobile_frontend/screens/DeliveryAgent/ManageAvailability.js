import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  FlatList
} from 'react-native';
import DateTimePicker from '@react-native-community/datetimepicker';
import axiosInstance from '../../utils/axiosInstance';
import RNDateTimePicker from '@react-native-community/datetimepicker';

const fullDaysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

const getTodayAndFutureDays = () => {
  const today = new Date();
  const currentDayIndex = today.getDay();
  const adjustedIndex = currentDayIndex === 0 ? 6 : currentDayIndex - 1;
  return fullDaysOfWeek.slice(adjustedIndex);
};

const getDateForWeekday = (weekday) => {
  const today = new Date();
  const currentDay = today.getDay();
  const targetDay = fullDaysOfWeek.indexOf(weekday);
  const diff = targetDay - (currentDay === 0 ? 6 : currentDay - 1);
  const targetDate = new Date(today);
  targetDate.setDate(today.getDate() + diff);
  return targetDate.toISOString().split('T')[0];
};

const ManageAvailability = () => {
  const [availableDays, setAvailableDays] = useState([]);
  const [selectedDays, setSelectedDays] = useState([]);
  const [isHoliday, setIsHoliday] = useState(null);
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('17:00');
  const [availabilities, setAvailabilities] = useState([]);
  const [editIndex, setEditIndex] = useState(null);

  useEffect(() => {
    setAvailableDays(getTodayAndFutureDays());
  }, []);

  const handleCheckboxChange = (day) => {
    setSelectedDays((prev) =>
      prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day]
    );
  };

  const handleAddOrUpdate = () => {
    if (selectedDays.length === 0 || isHoliday === null) return;

    const entries = selectedDays.map((day) => ({
      day,
      date: getDateForWeekday(day),
      isHoliday,
      startTime: isHoliday ? null : startTime,
      endTime: isHoliday ? null : endTime,
    }));

    if (editIndex !== null) {
      const updated = [...availabilities];
      updated[editIndex] = entries[0];
      setAvailabilities(updated);
      setEditIndex(null);
    } else {
      const updated = [...availabilities];
      entries.forEach((entry) => {
        if (!updated.some((a) => a.day === entry.day)) {
          updated.push(entry);
        }
      });
      setAvailabilities(updated);
    }

    setSelectedDays([]);
    setIsHoliday(null);
    setStartTime('09:00');
    setEndTime('17:00');
  };

  const handleEdit = (index) => {
    const entry = availabilities[index];
    setSelectedDays([entry.day]);
    setIsHoliday(entry.isHoliday);
    setStartTime(entry.startTime || '09:00');
    setEndTime(entry.endTime || '17:00');
    setEditIndex(index);
  };

  const handleDelete = (index) => {
    const updated = availabilities.filter((_, i) => i !== index);
    setAvailabilities(updated);
  };

  const handleSaveToBackend = async () => {
    const payload = availabilities.map((entry) => ({
      dayOfWeek: entry.day.toUpperCase(),
      startTime: entry.startTime ? `${entry.startTime}:00` : null,
      endTime: entry.endTime ? `${entry.endTime}:00` : null,
      holiday: entry.isHoliday,
    }));

    try {
      await axiosInstance.post('/availability/manage', payload);
      Alert.alert('Success', 'Availability saved successfully!');
      handleResetAll();
    } catch (error) {
      console.error('Error saving availability:', error);
      Alert.alert('Error', 'Failed to save availability.');
    }
  };

  const handleResetAll = () => {
    setAvailabilities([]);
    setSelectedDays([]);
    setIsHoliday(null);
    setStartTime('09:00');
    setEndTime('17:00');
    setEditIndex(null);
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.header}>Select Days</Text>
      <View style={styles.dayGrid}>
        {availableDays.map((day) => (
          <TouchableOpacity
            key={day}
            style={[styles.checkbox, selectedDays.includes(day) && styles.checkboxSelected]}
            onPress={() => handleCheckboxChange(day)}
            disabled={editIndex !== null}
          >
            <Text>{day}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={styles.header}>Status</Text>
      <View style={styles.radioGroup}>
        <TouchableOpacity style={styles.radioOption} onPress={() => setIsHoliday(false)}>
          <Text>{isHoliday === false ? '🔘' : '⚪'} Working Day</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.radioOption} onPress={() => setIsHoliday(true)}>
          <Text>{isHoliday === true ? '🔘' : '⚪'} Holiday</Text>
        </TouchableOpacity>
      </View>

      {!isHoliday && isHoliday !== null && (
        <View style={styles.timeGroup}>
          <View>
            <Text>Start Time:</Text>
            <Text>{startTime}</Text>
          </View>
          <View>
            <Text>End Time:</Text>
            <Text>{endTime}</Text>
          </View>
        </View>
      )}

      <TouchableOpacity style={styles.addBtn} onPress={handleAddOrUpdate}>
        <Text style={{ color: 'white', textAlign: 'center' }}>{editIndex !== null ? 'Update' : 'Add'}</Text>
      </TouchableOpacity>

      <Text style={styles.header}>Saved Availabilities</Text>
      {availabilities.length === 0 ? (
        <Text>No availabilities added.</Text>
      ) : (
        availabilities.map((entry, idx) => (
          <View key={idx} style={styles.entryRow}>
            <Text>{entry.day} - {entry.date}</Text>
            <Text>{entry.isHoliday ? 'Holiday' : `${entry.startTime} to ${entry.endTime}`}</Text>
            <View style={styles.entryActions}>
              <TouchableOpacity onPress={() => handleEdit(idx)}><Text>Edit</Text></TouchableOpacity>
              <TouchableOpacity onPress={() => handleDelete(idx)}><Text style={{ color: 'red' }}>Delete</Text></TouchableOpacity>
            </View>
          </View>
        ))
      )}

      {availabilities.length > 0 && (
        <View style={styles.actionButtons}>
          <TouchableOpacity style={styles.saveBtn} onPress={handleSaveToBackend}><Text style={{ color: 'white' }}>Save</Text></TouchableOpacity>
          <TouchableOpacity style={styles.resetBtn} onPress={handleResetAll}><Text style={{ color: 'white' }}>Reset</Text></TouchableOpacity>
        </View>
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#F0FDF4',
  },
  header: {
    fontSize: 18,
    fontWeight: '600',
    marginVertical: 10,
    textAlign: 'center',
  },
  dayGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  checkbox: {
    backgroundColor: '#fff',
    padding: 10,
    margin: 5,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ccc',
  },
  checkboxSelected: {
    backgroundColor: '#d1fae5',
    borderColor: '#10b981',
  },
  radioGroup: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 20,
    marginVertical: 10,
  },
  radioOption: {
    padding: 10,
  },
  timeGroup: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginVertical: 15,
  },
  addBtn: {
    backgroundColor: '#4ADE80',
    padding: 12,
    borderRadius: 8,
    marginVertical: 10,
  },
  entryRow: {
    backgroundColor: '#E8F5E9',
    padding: 10,
    borderRadius: 8,
    marginVertical: 5,
  },
  entryActions: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 5,
  },
  actionButtons: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 20,
  },
  saveBtn: {
    backgroundColor: '#4ADE80',
    padding: 12,
    borderRadius: 8,
  },
  resetBtn: {
    backgroundColor: '#dc3545',
    padding: 12,
    borderRadius: 8,
  },
});

export default ManageAvailability;
