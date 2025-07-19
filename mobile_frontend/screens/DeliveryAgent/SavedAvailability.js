// Converted: SavedAvailability.js (React Native)

import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Modal,
  Pressable,
  Alert,
  TextInput,
  Switch,
} from 'react-native';
// import Toast from 'react-native-toast-message';
import { MaterialIcons } from '@expo/vector-icons';

const SavedAvailability = ({ availabilities }) => {
  const [repeatNextWeek, setRepeatNextWeek] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [toast, setToast] = useState({ message: '', type: '', visible: false });

  const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        setTimeout(() => {
        setToast({ message: '', type: '', visible: false });
        }, 3000);
    };

  const handleEditClick = (entry) => {
    setEditData({
      id: entry.availabilityId,
      dayOfWeek: entry.dayOfWeek,
      startTime: entry.startTime || '09:00',
      endTime: entry.endTime || '17:00',
      holiday: entry.holiday,
    });
    setEditModalOpen(true);
  };

  const handleEditSave = () => {
    // replace with actual API call
    setEditModalOpen(false);
    showToast('Availability saved successfully!');
  };

  const handleDelete = (id) => {
    Alert.alert('Confirm Delete', 'Are you sure?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: () => {
          // replace with actual API call
          showToast('Deleted successfully');
        },
      },
    ]);
  };

  const renderAvailabilityCard = ({ item, index }) => (
    <View style={styles.card}>
      <Text style={styles.day}>{item.dayOfWeek}</Text>
      <Text>Date: {item.date}</Text>
      <Text>Holiday: {item.holiday ? 'Yes' : 'No'}</Text>
      <Text>Start: {item.startTime || '-'}</Text>
      <Text>End: {item.endTime || '-'}</Text>
      <View style={styles.actionRow}>
        <TouchableOpacity
          onPress={() => handleEditClick(item)}
          style={styles.editBtn}
        >
          <Text style={styles.btnText}>Edit</Text>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() => handleDelete(item.availabilityId)}
          style={styles.deleteBtn}
        >
          <Text style={styles.btnText}>Delete</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      {availabilities?.length ? (
        <FlatList
          data={availabilities}
          renderItem={renderAvailabilityCard}
          keyExtractor={(item, i) => item.availabilityId.toString() || i.toString()}
        />
      ) : (
        <View style={styles.emptyBox}>
          <MaterialIcons name="inbox" size={64} color="#ccc" />
          <Text style={styles.emptyText}>No Availabilities</Text>
          <Text>Your schedule will appear here once added.</Text>
        </View>
      )}

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
        style={[styles.confirmBtn, (!repeatNextWeek || submitting) && { opacity: 0.5 }]}
      >
        <Text style={styles.confirmText}>{submitting ? 'Applying...' : 'Confirm'}</Text>
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
            <Text style={styles.modalTitle}>Edit: {editData?.dayOfWeek}</Text>
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
              <View>
                <Text>Start Time</Text>
                <TextInput
                  value={editData?.startTime}
                  onChangeText={(val) =>
                    setEditData((prev) => ({ ...prev, startTime: val }))
                  }
                  style={styles.input}
                  placeholder="09:00"
                />
                <Text>End Time</Text>
                <TextInput
                  value={editData?.endTime}
                  onChangeText={(val) =>
                    setEditData((prev) => ({ ...prev, endTime: val }))
                  }
                  style={styles.input}
                  placeholder="17:00"
                />
              </View>
            )}
            <View style={styles.modalBtns}>
              <Pressable style={styles.saveBtn} onPress={handleEditSave}>
                <Text style={styles.btnText}>Save</Text>
              </Pressable>
              <Pressable
                style={[styles.saveBtn, { backgroundColor: '#ccc' }]}
                onPress={() => setEditModalOpen(false)}
              >
                <Text style={styles.btnText}>Cancel</Text>
              </Pressable>
            </View>
          </View>
        </View>
      </Modal>
      {toast.visible && (
      
                              <View style={[styles.toast, toast.type === 'error' ? styles.toastError : styles.toastSuccess]}>
                                  <Text style={styles.toastText}>{toast.message}</Text>
                              </View>
      
                          )}

      {/* <Toast /> */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#f0fdf4',
    flex: 1,
  },
  card: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
    elevation: 2,
  },
  day: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  actionRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 12,
  },
  editBtn: {
    backgroundColor: '#4ADE80',
    padding: 10,
    borderRadius: 8,
  },
  deleteBtn: {
    backgroundColor: '#dc3545',
    padding: 10,
    borderRadius: 8,
  },
  btnText: {
    color: 'white',
    fontWeight: 'bold',
  },
  emptyBox: {
    alignItems: 'center',
    padding: 24,
    backgroundColor: '#fff',
    borderRadius: 12,
    marginBottom: 20,
  },
  emptyText: {
    fontSize: 18,
    fontWeight: 'bold',
    marginVertical: 10,
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
    backgroundColor: 'rgba(0,0,0,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContent: {
    backgroundColor: '#fff',
    padding: 24,
    borderRadius: 12,
    width: '90%',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 16,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 8,
    borderRadius: 6,
    marginBottom: 12,
  },
  switchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  modalBtns: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  saveBtn: {
    backgroundColor: '#4ADE80',
    padding: 12,
    borderRadius: 8,
    flex: 1,
    marginHorizontal: 6,
    alignItems: 'center',
  },
});

export default SavedAvailability;
