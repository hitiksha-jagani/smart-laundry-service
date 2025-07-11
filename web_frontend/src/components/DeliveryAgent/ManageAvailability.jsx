// Author : Hitiksha Jagani
// Description : Manage Availability Form for delivery agent dashboard.

import React, { useState, useEffect } from "react";
import axiosInstance from "../../utils/axiosInstance";
import '../../styles/Toast.css';
import "../../styles/DeliveryAgent/ManageAvailability.css";

const fullDaysOfWeek = [
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday",
];

const getTodayAndFutureDays = () => {

    const today = new Date();
    const currentDayIndex = today.getDay();

    // Convert to match index of fullDaysOfWeek (starts with Monday)
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
    return targetDate.toISOString().split("T")[0];

};

const ManageAvailability = () => {
    const [availableDays, setAvailableDays] = useState([]);
    const [selectedDays, setSelectedDays] = useState([]);
    const [isHoliday, setIsHoliday] = useState(null);
    const [startTime, setStartTime] = useState("09:00");
    const [endTime, setEndTime] = useState("17:00");
    const [availabilities, setAvailabilities] = useState([]);
    const [editIndex, setEditIndex] = useState(null);

    const [toast, setToast] = useState({ message: '', type: '', visible: false });
        
    const showToast = (message, type = 'success') => {
        setToast({ message, type, visible: true });
        
        setTimeout(() => {
            setToast({ message: '', type: '', visible: false });
        }, 5000);
    };

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

        // Reset form
        setSelectedDays([]);
        setIsHoliday(null);
        setStartTime("09:00");
        setEndTime("17:00");
    };

    const handleEdit = (index) => {

        const entry = availabilities[index];
        setSelectedDays([entry.day]);
        setIsHoliday(entry.isHoliday);
        setStartTime(entry.startTime || "09:00");
        setEndTime(entry.endTime || "17:00");
        setEditIndex(index);

    };

    const handleDelete = (index) => {

        const updated = availabilities.filter((_, i) => i !== index);
        setAvailabilities(updated);

    };

    const handleSaveToBackend = async () => {

        const payload = availabilities.map((entry) => ({
            dayOfWeek: entry.day.toUpperCase(), // match enum like MONDAY
            startTime: entry.startTime ? `${entry.startTime}:00` : null,
            endTime: entry.endTime ? `${entry.endTime}:00` : null,
            holiday: entry.isHoliday,
        }));

        try {
            const response = await axiosInstance.post("/availability/manage", payload);
            showToast('Availability saved successfully!', "success");
        } catch (error) {
            console.error("Error saving availability:", error);
            showToast('Error saving availability.', "error");
        }

    };

    const handleResetAll = () => {
        setAvailabilities([]);
        setSelectedDays([]);
        setIsHoliday(null);
        setStartTime("09:00");
        setEndTime("17:00");
        setEditIndex(null);
    }; 

  return (
  <div className="availability-container">

    {/* Box for all checkboxes */}
    <div className="box checkbox-box">
      <h4>Select Days</h4>
      <div className="day-grid">
        {availableDays.map((day) => (
          <label key={day} className="day-checkbox">
            <input
              type="checkbox"
              checked={selectedDays.includes(day)}
              onChange={() => handleCheckboxChange(day)}
              disabled={editIndex !== null}
            />
            {day}
          </label>
        ))}
      </div>
    </div>

    {/* Box for radio buttons */}
    <div className="box radio-box">
      <h4>Status</h4>
      <div className="radio-group">
        <label>
          <input
            type="radio"
            name="status"
            value="working"
            checked={isHoliday === false}
            onChange={() => setIsHoliday(false)}
          />
          Working Day
        </label>
        <label>
          <input
            type="radio"
            name="status"
            value="holiday"
            checked={isHoliday === true}
            onChange={() => setIsHoliday(true)}
          />
          Holiday
        </label>
      </div>

      {/* Time input only if not holiday */}
      {!isHoliday && isHoliday !== null && (
        <div className="time-group">
          <div>
            <label>Start Time:</label>
            <input
              type="time"
              min="09:00"
              max="19:00"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
            />
          </div>
          <div>
            <label>End Time:</label>
            <input
              type="time"
              min="09:00"
              max="19:00"
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
            />
          </div>
        </div>
      )}
    </div>

    <button className="agent-btn" onClick={handleAddOrUpdate}>
      {editIndex !== null ? "Update" : "Add"}
    </button>

    <hr />

    {/* Box for availability list */}
    <div className="box list-box">
      <h3>Saved Availabilities</h3>
      {availabilities.length === 0 ? (
        <p>No availabilities added.</p>
      ) : (
        <table className="availability-table">
          <thead>
            <tr>
              <th>Day</th>
              <th>Date</th>
              <th>Holiday</th>
              <th>Start</th>
              <th>End</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {availabilities.map((entry, idx) => (
              <tr key={entry.day}>
                <td>{entry.day}</td>
                <td>{entry.date}</td>
                <td>{entry.isHoliday ? "Yes" : "No"}</td>
                <td>{entry.startTime || "-"}</td>
                <td>{entry.endTime || "-"}</td>
                <td>
                  <button className="agent-btn" onClick={() => handleEdit(idx)}>EDIT</button>
                  <button className="agent-btn" onClick={() => handleDelete(idx)}>DELETE</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {availabilities.length > 0 && (
        <div className="action-buttons">
          <button className="agent-btn save-btn" onClick={handleSaveToBackend}>
            SAVE
          </button>
          <button className="reset-btn" onClick={handleResetAll}>
            RESET ALL
          </button>
        </div>
      )}
    </div>
  </div>
);

};

export default ManageAvailability;
