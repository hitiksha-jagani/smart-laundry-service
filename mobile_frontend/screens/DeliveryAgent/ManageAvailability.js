import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    StyleSheet,
    Dimensions,
    TouchableOpacity,
    Alert,
    FlatList
} from 'react-native'; 
import DateTimePicker from '@react-native-community/datetimepicker';
import axiosInstance from '../../utils/axiosInstance';
import RNDateTimePicker from '@react-native-community/datetimepicker';
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const screenWidth = Dimensions.get('window').width;

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
  const [showStartPicker, setShowStartPicker] = useState(false);
  const [showEndPicker, setShowEndPicker] = useState(false);
  const [availableDays, setAvailableDays] = useState([]);
  const [selectedDays, setSelectedDays] = useState([]);
  const [isHoliday, setIsHoliday] = useState(null);
  const [startTime, setStartTime] = useState(new Date());
  const [endTime, setEndTime] = useState(new Date());
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

  const handleStartTimeChange = (event, selectedDate) => {
    setShowStartPicker(false);
    if (selectedDate) setStartTime(selectedDate);
  };

  const handleEndTimeChange = (event, selectedDate) => {
    setShowEndPicker(false);
    if (selectedDate) setEndTime(selectedDate);
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
    
    <View style={styles.card}>

      <Text style={styles.title}>Select Days</Text>

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

      <Text style={styles.title}>Status</Text>
      <View style={styles.radioGroup}>
        <TouchableOpacity style={styles.radioOption} onPress={() => setIsHoliday(false)}>
          <Text>{isHoliday === false ? '🔘' : '⚪'} Working Day</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.radioOption} onPress={() => setIsHoliday(true)}>
          <Text>{isHoliday === true ? '🔘' : '⚪'} Holiday</Text>
        </TouchableOpacity>
      </View>

    {selectedDays.length > 0 && isHoliday === false && (
      <View style={styles.row}>
        <View style={styles.field}>
          <Text style={styles.label}>Start Time</Text>
          <TouchableOpacity onPress={() => setShowStartPicker(true)}>
            <Text>{startTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.field}>
          <Text style={styles.label}>End Time</Text>
          <TouchableOpacity onPress={() => setShowEndPicker(true)}>
            <Text>{endTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</Text>
          </TouchableOpacity>
        </View>
      </View>
    )}

      {showStartPicker && (
        <DateTimePicker
          value={startTime}
          mode="time"
          is24Hour={true}
          display="default"
          onChange={handleStartTimeChange}
        />
      )}

      {showEndPicker && (
        <DateTimePicker
          value={endTime}
          mode="time"
          is24Hour={true}
          display="default"
          onChange={handleEndTimeChange}
        />
      )}

      <TouchableOpacity style={styles.addBtn} onPress={handleAddOrUpdate}>
        <Text style={{ color: 'white', textAlign: 'center' }}>{editIndex !== null ? 'Update' : 'Add'}</Text>
      </TouchableOpacity>

      <Text style={styles.title}>Saved Availabilities</Text>
      {availabilities.length === 0 ? (
        <Text>No availabilities added.</Text>
      ) : (
        availabilities.map((entry, idx) => (
          <View key={idx} style={styles.contactBox}>
  <Text style={styles.contactTitle}>{entry.day}</Text>

  <View style={styles.itemCardRow}>
    <Text style={styles.itemLabel}>Date:</Text>
    <Text style={styles.itemValue}>{entry.date}</Text>
  </View>

  {entry.isHoliday ? (
    <View style={styles.itemCardRow}>
      <Text style={styles.itemLabel}>Status:</Text>
      <Text style={styles.itemValue}>Holiday</Text>
    </View>
  ) : (
    <>
      <View style={styles.itemCardRow}>
        <Text style={styles.itemLabel}>Start Time:</Text>
        <Text style={styles.itemValue}>
          {new Date(entry.startTime).toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit',
          })}
        </Text>
      </View>
      <View style={styles.itemCardRow}>
        <Text style={styles.itemLabel}>End Time:</Text>
        <Text style={styles.itemValue}>
          {new Date(entry.endTime).toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit',
          })}
        </Text>
      </View>
    </>
  )}

  <View style={styles.actions}>
    <TouchableOpacity style={styles.acceptBtn} onPress={() => handleEdit(idx)}>
      <Text style={styles.actionText}>Edit</Text>
    </TouchableOpacity>

    <TouchableOpacity style={styles.rejectBtn} onPress={() => handleDelete(idx)}>
      <Text style={styles.actionText}>Delete</Text>
    </TouchableOpacity>
  </View>
</View>

        ))
      )}

      {availabilities.length > 0 && (

        <View style={styles.actions}>
        
          <TouchableOpacity style={styles.acceptBtn} onPress={handleSaveToBackend}>
            <Text style={styles.actionText}>Save</Text>
          </TouchableOpacity>
        
          <TouchableOpacity style={styles.rejectBtn} onPress={handleResetAll}>
            <Text style={styles.actionText}>Reset</Text>
          </TouchableOpacity>
        
        </View>

      )}
    </View>
  );
};

const styles = StyleSheet.create({

  itemCard: {
        backgroundColor: '#ecfdf5',
        borderColor: '#bbf7d0',
        borderWidth: 1,
        padding: 10,
        borderRadius: 8,
        marginBottom: 10,
        width: '90%', 
    },

  card: {
        backgroundColor: '#f0fdf4',
        padding: 20,
        borderColor: '#4ADE80',
        borderWidth: 1,
        borderRadius: 12,
        width: screenWidth * 0.9, 
        alignSelf: 'center', 
        marginVertical: '25',
    },

    title: {
        fontSize: 18,
        fontWeight: '700',
        color: '#388E3C',
        backgroundColor: '#ecfdf5',
        padding: 10,
        borderLeftWidth: 5,
        borderLeftColor: '#10b981',
        borderRadius: 8,
        marginVertical: 10,
    },

    row: {
    flexDirection: 'row',
    gap: 8,
  },
  field: {
    flex: 1,
    fontSize: '1px',
    backgroundColor: '#E8F5E9',
    padding: 10,
    borderRadius: 10,
    borderColor: '#ccc',
    borderWidth: 1,
    marginBottom: 10,
  },
  label: {
    color: '#388E3C',
    fontWeight: '600',
    fontSize: 10,
    marginBottom: 4,
  },
  value: {
    color: '#555',
    fontSize: 13,
  },

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
  contactBox: {
        backgroundColor: '#fff',
        borderColor: '#d1fae5',
        borderWidth: 1,
        borderRadius: 10,
        padding: 15,
        marginVertical: 10,
    },

    contactTitle: {
        fontSize: 16,
        fontWeight: '600',
        color: '#10b981',
        borderBottomColor: '#d1fae5',
        borderBottomWidth: 2,
        marginBottom: 10,
    },

    contactDetails: {
        gap: 10,
    },
    summaryValue: {
        fontWeight: '600',
        color: '#065f46',
        fontSize: 15,
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
  actions: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        marginTop: 20,
    },

    acceptBtn: {
        backgroundColor: '#4ADE80',
        padding: 12,
        borderRadius: 10,
        width: '40%',
        alignItems: 'center',
    },

    rejectBtn: {
        backgroundColor: '#ef4444',
        padding: 12,
        borderRadius: 10,
        width: '40%',
        alignItems: 'center',
    },

    actionText: {
        color: '#fff',
        fontWeight: '700',
        fontSize: 16,
    },
    itemCardRow: {
  flexDirection: 'row',
  justifyContent: 'space-between',
  alignItems: 'center',
  backgroundColor: '#ecfdf5',
  borderColor: '#bbf7d0',
  borderWidth: 1,
  paddingVertical: 8,
  paddingHorizontal: 12,
  borderRadius: 8,
  marginBottom: 8,
},

itemLabel: {
  fontWeight: '600',
  color: '#065f46',
  fontSize: 14,
},

itemValue: {
  fontSize: 14,
  color: '#333',
},

});

export default ManageAvailability;
