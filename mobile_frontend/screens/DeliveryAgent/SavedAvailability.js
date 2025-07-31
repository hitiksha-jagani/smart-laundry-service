// Converted: SavedAvailability.js (React Native)

import React, { useState } from 'react';
import { useNavigation } from '@react-navigation/native';
import { Platform } from 'react-native';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Modal,
  Pressable,
  Alert,
  TextInput,
  Switch,
  Dimensions
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import axiosInstance from '../../utils/axiosInstance';
import DateTimePicker from '@react-native-community/datetimepicker';

const screenWidth = Dimensions.get('window').width;

const SavedAvailability = ({ availabilities }) => {
  const [showStartPicker, setShowStartPicker] = useState(false);
  const [showEndPicker, setShowEndPicker] = useState(false);
  const [repeatNextWeek, setRepeatNextWeek] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [toast, setToast] = useState({ message: '', type: '', visible: false });
  const navigation = useNavigation();

  const showToast = (message, type = 'success') => {
    setToast({ message, type, visible: true });
    setTimeout(() => {
      setToast({ message: '', type: '', visible: false });
    }, 3000);
  };
 
  const handleEditClick = (entry) => {
    setShowStartPicker(false);
    setShowEndPicker(false);
    setEditData({
      id: entry.availabilityId,
      dayOfWeek: entry.dayOfWeek,
      startTime: entry.startTime || '09:00',
      endTime: entry.endTime || '17:00',
      holiday: entry.holiday,
    });
    setEditModalOpen(true);
  };

  const handleEditSave = async () => {
    const dto = {
      startTime: editData.holiday ? null : editData.startTime,
      endTime: editData.holiday ? null : editData.endTime,
      holiday: editData.holiday,
    };

    try {
      await axiosInstance.put(`/availability/manage/edit/${editData.id}`, dto);
      showToast('Availability saved successfully!','success')
     
      setEditModalOpen(false);

      navigation.goBack();
    } catch (err) {
      console.error('Update failed', err);
      showToast('Failed to update availability.','error');
    }
  };

  const handleDelete = (id) => {
    Alert.alert('Confirm Delete', 'Are you sure?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: async () => {
          try { 
            const response = await axiosInstance.delete(`/availability/manage/delete/${id}`);
            
            if (response.status === 200) {
              showToast('Deleted successfully');
              navigation.goBack();
            } else {
              const errorText = await response.text();
              showToast(`Error: ${errorText}`, 'error');
            }
          } catch (err) {
            console.error('Delete error:', err);
            showToast('Failed to delete. Please try again.', 'error');
          }
        },
      },
    ]);
  };

  const parseTime = (timeStr) => {
    const [hour, minute] = timeStr.split(':');
    return new Date(2020, 0, 1, parseInt(hour), parseInt(minute));
  };

  const formatTime = (date) => {
    return date.toTimeString().slice(0, 5); // HH:mm
  };

  return (
    <>

    <View style={styles.card}>

      {availabilities.length === 0 ? (

          <View style={styles.emptyBox}>

            <MaterialIcons name="inbox" size={64} color="#ccc" />
            <Text style={styles.emptyText}>No Availabilities</Text>
            <Text>Your schedule will appear here once added.</Text>

          </View>

        ) : (
            
          availabilities.map((entry, idx) => (

            <View key={idx} style={styles.contactBox}>

              <Text style={styles.contactTitle}>{entry.dayOfWeek}</Text>
          
              <View style={styles.itemCardRow}>

                <Text style={styles.itemLabel}>Date:</Text>
                <Text style={styles.itemValue}>{entry.date}</Text>

              </View>
          
              {entry.holiday ? (

                <View style={styles.itemCardRow}>

                  <Text style={styles.itemLabel}>Status:</Text>
                  <Text style={styles.itemValue}>Holiday</Text>

                </View>

                ) : (

                  <>

                    <View style={styles.itemCardRow}>

                      <Text style={styles.itemLabel}>Start Time:</Text>
                      <Text>
                        {new Date(`${entry.date}T${entry.startTime}`).toLocaleTimeString([], {
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </Text>


                    </View>

                    <View style={styles.itemCardRow}>
                      <Text style={styles.itemLabel}>End Time:</Text>
                      <Text>
                        {new Date(`${entry.date}T${entry.endTime}`).toLocaleTimeString([], {
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </Text>

                    </View>

                  </>

                )
              }
          
              <View style={styles.actions}>

                <TouchableOpacity style={styles.acceptBtn} onPress={() => handleEditClick(entry)}>
                  <Text style={styles.actionText}>Edit</Text>
                </TouchableOpacity>
          
                <TouchableOpacity style={styles.rejectBtn} onPress={() => handleDelete(entry.availabilityId)}>
                  <Text style={styles.actionText}>Delete</Text>
                </TouchableOpacity>

              </View>

            </View>
                
          )))
      }
        
      {/* Repeat Next Week Toggle */}
      <View style={styles.repeatRow}>

        <Switch value={repeatNextWeek} onValueChange={setRepeatNextWeek} />
        <Text style={{ marginLeft: 10 }}>Repeat this schedule next week</Text>

      </View>

      <TouchableOpacity
        onPress={() => {
          setSubmitting(true);
          setTimeout(() => {
            setSubmitting(false);
            showToast('Repeated for next week');
          }, 1000);
        }}
        disabled={!repeatNextWeek || submitting}
        style={styles.addBtn}
      >
        <Text style={{ color: 'white', textAlign: 'center' }}>{submitting ? 'Applying...' : 'Confirm'}</Text>
      </TouchableOpacity>

      {/* Edit Modal */}
      <Modal
        visible={editModalOpen}
        transparent
        animationType="slide"
        onRequestClose={() => setEditModalOpen(false)}
      >

        <View style={styles.modalOverlay}>

          <View style={styles.modalContent}>

            <Text style={styles.modalTitle}>{editData?.dayOfWeek}</Text>

            <View style={styles.switchRow}>

              <Text>Holiday:</Text>
              <Switch
                value={editData?.holiday}
                onValueChange={(val) =>
                  setEditData((prev) => ({ ...prev, holiday: val }))
                }
              />

            </View>

            {!editData?.holiday && (

              <View style={styles.row}>

                <View style={styles.field}>

                  <Text style={styles.label}>Start Time</Text>
                  <TouchableOpacity
                    onPress={() => setShowStartPicker(true)}
                  >
                    <Text>{editData?.startTime ? editData.startTime : '00:00'}</Text>
                  </TouchableOpacity>
                  {showStartPicker && (
<DateTimePicker
            mode="time"
            value={editData?.startTime ? parseTime(editData.startTime) : parseTime('09:00')}
            is24Hour={true}
            display={Platform.OS === 'ios' ? 'spinner' : 'default'}
            minimumDate={parseTime('09:00')}
            maximumDate={parseTime('19:00')}
            onChange={(event, selectedDate) => {
              setShowStartPicker(false);
              if (selectedDate) {
                const time = formatTime(selectedDate);
                setEditData((prev) => ({ ...prev, startTime: time }));
              }
            }}
          />

                  )}

                </View>

                <View style={styles.field}>

                  <Text style={styles.label}>End Time</Text>
                  <TouchableOpacity
                    onPress={() => setShowEndPicker(true)}
                  >
                    <Text>{editData?.endTime ? editData.endTime : '00'}</Text>
                  </TouchableOpacity>

                  {showEndPicker && (

 <DateTimePicker
            mode="time"
            value={editData?.endTime ? parseTime(editData.endTime) : parseTime('17:00')}
            is24Hour={true}
            display={Platform.OS === 'ios' ? 'spinner' : 'default'}
            minimumDate={parseTime('09:00')}
            maximumDate={parseTime('19:00')}
            onChange={(event, selectedDate) => {
              setShowEndPicker(false);
              if (selectedDate) {
                const time = formatTime(selectedDate);
                setEditData((prev) => ({ ...prev, endTime: time }));
              }
            }}
          />

                  )}

                </View>

              </View>

            )}

            <View style={styles.actions}>

              <Pressable style={styles.acceptBtn} onPress={handleEditSave}>
                <Text style={styles.btnText}>Save</Text>
              </Pressable>

              <Pressable
                style={styles.rejectBtn}
                onPress={() => setEditModalOpen(false)}
              >
                <Text style={styles.btnText}>Cancel</Text>
              </Pressable>

            </View>

          </View>

        </View>

      </Modal>

    </View>

    {toast.visible && (
      
        <View style={[styles.toast, toast.type === 'error' ? styles.toastError : styles.toastSuccess]}>
          <Text style={styles.toastText}>{toast.message}</Text>
        </View>
      
    )}
    </>
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

  repeatRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 20,
  },

  confirmBtn: {
    backgroundColor: '#4ADE80',
    padding: 12,
    borderRadius: 8,
    marginTop: 16,
    alignItems: 'center',
  },
  confirmText: {
    color: 'white',
    fontWeight: 'bold',
  },

  modalOverlay: {
  flex: 1,
  backgroundColor: 'rgba(0,0,0,0.5)',
  justifyContent: 'center',
  alignItems: 'center',
},
modalContent: {
  backgroundColor: '#fff',
  borderColor: '#d1fae5',
  borderWidth: 1,
  borderRadius: 10,
  padding: 20,
  width: '90%',
  shadowColor: '#000',
  shadowOffset: { width: 0, height: 2 },
  shadowOpacity: 0.25,
  shadowRadius: 3.84,
  elevation: 5,
},
modalTitle: {
  fontSize: 18,
  fontWeight: '700',
  color: '#10b981',
  marginBottom: 15,
  textAlign: 'center',
},
modalBtns: {
  flexDirection: 'row',
  justifyContent: 'space-between',
  marginTop: 20,
},
input: {
  borderWidth: 1,
  borderColor: '#d1fae5',
  backgroundColor: '#f0fdf4',
  borderRadius: 8,
  padding: 10,
  marginBottom: 10,
},
switchRow: {
  flexDirection: 'row',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: 15,
},
btnText: {
  color: '#fff',
  fontWeight: '600',
  textAlign: 'center',
},


});

export default SavedAvailability;
