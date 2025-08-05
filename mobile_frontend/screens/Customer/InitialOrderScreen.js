// import React, { useEffect, useState } from "react";
// import {
//   View,
//   Text,
//   TextInput,
//   Button,
//   TouchableOpacity,
//   StyleSheet,
//   ScrollView,
//   Alert,
//   Platform,
// } from "react-native";
// import { useRoute } from "@react-navigation/native";
// import { Picker } from "@react-native-picker/picker";
// import axios from "../../utils/axiosInstance";

// export default function InitialOrderScreen({
//   onNext,
//   setDummyOrderId,
//   initialOrderData,
//   setInitialOrderData,
// }) {
//   const route = useRoute();
//   let providerId = null;

//   try {
//     providerId = route?.params?.providerId;
//   } catch (e) {
//     if (Platform.OS === "web") {
//       const query = new URLSearchParams(window?.location?.search || "");
//       providerId = query.get("providerId");
//     }
//   }

//   const [providerItems, setProviderItems] = useState([]);
//   const [error, setError] = useState("");
//   const [loading, setLoading] = useState(false);

//   const { pickupDate, pickupTime, items, goWithSchedulePlan } = initialOrderData;

//   useEffect(() => {
//     if (providerId) {
//       setInitialOrderData((prev) => ({ ...prev, serviceProviderId: providerId }));
//     }
//   }, [providerId]);

//   useEffect(() => {
//     const fetchItems = async () => {
//       try {
//         const res = await axios.get(`/customer/serviceProviders/${providerId}`);
//         const prices = Array.isArray(res.data?.prices) ? res.data.prices : [];
//         const items = prices.map((p) => ({
//           itemId: p.item?.itemId,
//           itemName: p.item?.itemName,
//           serviceId: p.item?.serviceId,
//           serviceName: p.item?.serviceName,
//           subServiceId: p.item?.subServiceId,
//           subServiceName: p.item?.subServiceName,
//           price: p.price,
//         }));
//         setProviderItems(items);
//       } catch (e) {
//         console.error("❌ Failed to fetch provider items:", e);
//         Alert.alert("Error", "Could not load provider details.");
//         setError("Failed to fetch provider details.");
//       }
//     };

//     if (providerId) fetchItems();
//   }, [providerId]);

//   const setField = (field, value) => {
//     setInitialOrderData((prev) => ({ ...prev, [field]: value }));
//   };

//   const updateItem = (idx, field, value) => {
//     const updated = [...items];
//     updated[idx][field] = value;

//     if (field === "itemId") {
//       const selectedItem = providerItems.find((item) => item.itemId === value);
//       if (selectedItem) {
//         updated[idx].serviceId = selectedItem.serviceId || "";
//         updated[idx].subServiceId = selectedItem.subServiceId || "";
//       } else {
//         updated[idx].serviceId = "";
//         updated[idx].subServiceId = "";
//       }
//     }

//     setField("items", updated);
//   };

//   const addLine = () =>
//     setField("items", [...items, { serviceId: "", subServiceId: "", itemId: "", quantity: 1 }]);

//   const removeLine = (idx) => setField("items", items.filter((_, i) => i !== idx));

//   const validate = () => {
//     if (!providerId || !pickupDate || !pickupTime) return "Fill all required fields";
//     for (let it of items) {
//       if (!it.itemId || it.quantity < 1) return "Please select item & quantity ≥1";
//     }
//     return null;
//   };

//   const submit = async () => {
//     const v = validate();
//     if (v) return setError(v);

//     setLoading(true);
//     try {
//       const cleanedItems = items.map(({ itemId, quantity }) => ({ itemId, quantity }));
//       const res = await axios.post("/orders/initial", {
//         serviceProviderId: providerId,
//         pickupDate,
//         pickupTime,
//         items: cleanedItems,
//         goWithSchedulePlan,
//       });

//       const dummyOrderId = res.data;
//       setDummyOrderId(dummyOrderId);
//       onNext(goWithSchedulePlan);
//     } catch (e) {
//       setError(e.response?.data?.message || "Could not submit order");
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <ScrollView contentContainerStyle={styles.container}>
//       <Text style={styles.title}>Step 1: Start Your Laundry Order</Text>

//       <Text style={styles.label}>Pickup Date</Text>
//       <TextInput
//         style={styles.input}
//         placeholder="YYYY-MM-DD"
//         value={pickupDate}
//         onChangeText={(text) => setField("pickupDate", text)}
//       />

//       <Text style={styles.label}>Pickup Time</Text>
//       <TextInput
//         style={styles.input}
//         placeholder="HH:MM"
//         value={pickupTime}
//         onChangeText={(text) => setField("pickupTime", text)}
//       />

//       <Text style={styles.label}>Select Items</Text>
//       {items.map((it, idx) => {
//         const filteredItems = providerItems;
//         const filteredServices = providerItems
//           .filter((i) => i.itemId === it.itemId && i.serviceId)
//           .map((i) => ({ serviceId: i.serviceId, serviceName: i.serviceName }));
//         const filteredSubServices = providerItems
//           .filter((i) => i.itemId === it.itemId && i.subServiceId)
//           .map((i) => ({ subServiceId: i.subServiceId, subServiceName: i.subServiceName }));

//         return (
//           <View key={idx} style={styles.itemRow}>
//             <View style={styles.pickerContainer}>
//               <Text style={styles.labelSmall}>Item</Text>
//               <Picker
//                 selectedValue={it.itemId}
//                 onValueChange={(value) => updateItem(idx, "itemId", value)}
//               >
//                 <Picker.Item label="-- select item --" value="" />
//                 {filteredItems.map((item, i) => (
//                   <Picker.Item
//                     key={`${item.itemId}-${i}`}
//                     label={`${item.itemName} - ₹${item.price}`}
//                     value={item.itemId}
//                   />
//                 ))}
//               </Picker>
//             </View>

//             <View style={styles.pickerContainer}>
//               <Text style={styles.labelSmall}>Service</Text>
//               <Picker
//                 selectedValue={it.serviceId}
//                 onValueChange={(value) => updateItem(idx, "serviceId", value)}
//                 enabled={filteredServices.length > 0}
//               >
//                 <Picker.Item label="-- select service --" value="" />
//                 {filteredServices.map((s, i) => (
//                   <Picker.Item key={i} label={s.serviceName} value={s.serviceId} />
//                 ))}
//               </Picker>
//             </View>

//             <View style={styles.pickerContainer}>
//               <Text style={styles.labelSmall}>Sub-Service</Text>
//               <Picker
//                 selectedValue={it.subServiceId}
//                 onValueChange={(value) => updateItem(idx, "subServiceId", value)}
//                 enabled={filteredSubServices.length > 0}
//               >
//                 <Picker.Item label="-- select subservice --" value="" />
//                 {filteredSubServices.map((s, i) => (
//                   <Picker.Item key={i} label={s.subServiceName} value={s.subServiceId} />
//                 ))}
//               </Picker>
//             </View>

//             <View style={styles.quantityContainer}>
//               <Text style={styles.labelSmall}>Qty</Text>
//               <TextInput
//                 placeholder="Qty"
//                 keyboardType="numeric"
//                 style={styles.input}
//                 value={String(it.quantity)}
//                 onChangeText={(value) => updateItem(idx, "quantity", parseInt(value) || 1)}
//               />
//             </View>

//             {items.length > 1 && (
//               <TouchableOpacity onPress={() => removeLine(idx)}>
//                 <Text style={styles.removeBtn}>✕</Text>
//               </TouchableOpacity>
//             )}
//           </View>
//         );
//       })}

//       <TouchableOpacity onPress={addLine}>
//         <Text style={styles.addLink}>+ Add another item</Text>
//       </TouchableOpacity>

//       <View style={styles.checkboxRow}>
//         <Text>Enable Schedule Plan</Text>
//         <TouchableOpacity onPress={() => setField("goWithSchedulePlan", !goWithSchedulePlan)}>
//           <Text style={styles.checkbox}>{goWithSchedulePlan ? "☑" : "☐"}</Text>
//         </TouchableOpacity>
//       </View>

//       {error !== "" && <Text style={styles.error}>{error}</Text>}

//       <Button title={loading ? "Submitting..." : "Next"} onPress={submit} disabled={loading} />
//     </ScrollView>
//   );
// }

// const styles = StyleSheet.create({
//   container: {
//     padding: 16,
//     gap: 10,
//   },
//   title: {
//     fontSize: 18,
//     fontWeight: "bold",
//     marginBottom: 8,
//   },
//   label: {
//     fontWeight: "600",
//     marginBottom: 4,
//   },
//   labelSmall: {
//     fontSize: 12,
//     fontWeight: "500",
//     marginBottom: 2,
//   },
//   input: {
//     borderWidth: 1,
//     borderColor: "#ccc",
//     padding: 8,
//     borderRadius: 6,
//     marginBottom: 6,
//   },
//   itemRow: {
//     borderWidth: 1,
//     borderColor: "#ddd",
//     padding: 10,
//     borderRadius: 8,
//     marginBottom: 12,
//     backgroundColor: "#fafafa",
//   },
//   pickerContainer: {
//     marginBottom: 6,
//   },
//   quantityContainer: {
//     marginBottom: 6,
//   },
//   checkboxRow: {
//     flexDirection: "row",
//     alignItems: "center",
//     gap: 10,
//     marginVertical: 10,
//   },
//   checkbox: {
//     fontSize: 18,
//   },
//   addLink: {
//     color: "#4B00B5",
//     fontWeight: "500",
//     marginBottom: 12,
//   },
//   removeBtn: {
//     color: "red",
//     fontWeight: "bold",
//     fontSize: 18,
//     marginTop: 4,
//   },
//   error: {
//     color: "red",
//     fontWeight: "500",
//     marginBottom: 8,
//   },
// });

import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TextInput,
  Button,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
  Platform,
} from "react-native";
import { useRoute } from "@react-navigation/native";
import { Picker } from "@react-native-picker/picker";
import DateTimePicker from "@react-native-community/datetimepicker";
import axios from "../../utils/axiosInstance";

export default function InitialOrderScreen({
  onNext,
  setDummyOrderId,
  initialOrderData,
  setInitialOrderData,
}) {
  const route = useRoute();
  let providerId = null;

  try {
    providerId = route?.params?.providerId;
  } catch (e) {
    if (Platform.OS === "web") {
      const query = new URLSearchParams(window?.location?.search || "");
      providerId = query.get("providerId");
    }
  }

  const [providerItems, setProviderItems] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [showTimePicker, setShowTimePicker] = useState(false);

  const { pickupDate, pickupTime, items, goWithSchedulePlan } = initialOrderData;

  useEffect(() => {
    if (providerId) {
      setInitialOrderData((prev) => ({ ...prev, serviceProviderId: providerId }));
    }
  }, [providerId]);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const res = await axios.get(`/customer/serviceProviders/${providerId}`);
        const prices = Array.isArray(res.data?.prices) ? res.data.prices : [];
        const items = prices.map((p) => ({
          itemId: p.item?.itemId,
          itemName: p.item?.itemName,
          serviceId: p.item?.serviceId,
          serviceName: p.item?.serviceName,
          subServiceId: p.item?.subServiceId,
          subServiceName: p.item?.subServiceName,
          price: p.price,
        }));
        setProviderItems(items);
      } catch (e) {
        console.error("❌ Failed to fetch provider items:", e);
        Alert.alert("Error", "Could not load provider details.");
        setError("Failed to fetch provider details.");
      }
    };

    if (providerId) fetchItems();
  }, [providerId]);

  const setField = (field, value) => {
    setInitialOrderData((prev) => ({ ...prev, [field]: value }));
  };

  const updateItem = (idx, field, value) => {
    const updated = [...items];
    updated[idx][field] = value;

    if (field === "itemId") {
      const selectedItem = providerItems.find((item) => item.itemId === value);
      if (selectedItem) {
        updated[idx].serviceId = selectedItem.serviceId || "";
        updated[idx].subServiceId = selectedItem.subServiceId || "";
      } else {
        updated[idx].serviceId = "";
        updated[idx].subServiceId = "";
      }
    }

    setField("items", updated);
  };

  const addLine = () =>
    setField("items", [...items, { serviceId: "", subServiceId: "", itemId: "", quantity: "1" }]);

  const removeLine = (idx) => setField("items", items.filter((_, i) => i !== idx));

  // ✅ Updated validation logic
  const validate = () => {
    if (!providerId || !pickupDate || !pickupTime) return "Fill all required fields";

    for (let it of items) {
      const qty = it.quantity?.trim();
      const parsedQty = parseInt(qty);

      if (!it.itemId || !qty || isNaN(parsedQty) || parsedQty < 1) {
        return "Please select item and enter valid quantity (≥1)";
      }
    }

    return null;
  };

  const submit = async () => {
    const v = validate();
    if (v) return setError(v);

    setLoading(true);
    try {
      const cleanedItems = items.map(({ itemId, quantity }) => ({
        itemId,
        quantity: parseInt(quantity) || 1,
      }));
      const res = await axios.post("/orders/initial", {
        serviceProviderId: providerId,
        pickupDate,
        pickupTime,
        items: cleanedItems,
        goWithSchedulePlan,
      });

      const dummyOrderId = res.data;
      setDummyOrderId(dummyOrderId);
      onNext(goWithSchedulePlan);
    } catch (e) {
      setError(e.response?.data?.message || "Could not submit order");
    } finally {
      setLoading(false);
    }
  };

  const handleDateChange = (event, selectedDate) => {
    setShowDatePicker(false);
    if (selectedDate) {
      const formattedDate = selectedDate.toISOString().split("T")[0];
      setField("pickupDate", formattedDate);
    }
  };

  const handleTimeChange = (event, selectedTime) => {
    setShowTimePicker(false);
    if (selectedTime) {
      const formattedTime = selectedTime.toTimeString().slice(0, 5);
      setField("pickupTime", formattedTime);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Step 1: Start Your Laundry Order</Text>

      <Text style={styles.label}>Pickup Date</Text>
      <TouchableOpacity onPress={() => setShowDatePicker(true)} style={styles.input}>
        <Text>{pickupDate || "Select date"}</Text>
      </TouchableOpacity>
      {showDatePicker && (
        <DateTimePicker
          mode="date"
          value={pickupDate ? new Date(pickupDate) : new Date()}
          display="default"
          onChange={handleDateChange}
        />
      )}

      <Text style={styles.label}>Pickup Time</Text>
      <TouchableOpacity onPress={() => setShowTimePicker(true)} style={styles.input}>
        <Text>{pickupTime || "Select time"}</Text>
      </TouchableOpacity>
      {showTimePicker && (
        <DateTimePicker
          mode="time"
          value={new Date()}
          is24Hour={true}
          display="default"
          onChange={handleTimeChange}
        />
      )}

      <Text style={styles.label}>Select Items</Text>
      {items.map((it, idx) => {
        const filteredItems = providerItems;
        const filteredServices = providerItems
          .filter((i) => i.itemId === it.itemId && i.serviceId)
          .map((i) => ({ serviceId: i.serviceId, serviceName: i.serviceName }));
        const filteredSubServices = providerItems
          .filter((i) => i.itemId === it.itemId && i.subServiceId)
          .map((i) => ({ subServiceId: i.subServiceId, subServiceName: i.subServiceName }));

        return (
          <View key={idx} style={styles.itemRow}>
            <View style={styles.pickerContainer}>
              <Text style={styles.labelSmall}>Item</Text>
              <Picker
                selectedValue={it.itemId}
                onValueChange={(value) => updateItem(idx, "itemId", value)}
              >
                <Picker.Item label="-- select item --" value="" />
                {filteredItems.map((item, i) => (
                  <Picker.Item
                    key={`${item.itemId}-${i}`}
                    label={`${item.itemName} - ₹${item.price}`}
                    value={item.itemId}
                  />
                ))}
              </Picker>
            </View>

            <View style={styles.pickerContainer}>
              <Text style={styles.labelSmall}>Service</Text>
              <Picker
                selectedValue={it.serviceId}
                onValueChange={(value) => updateItem(idx, "serviceId", value)}
                enabled={filteredServices.length > 0}
              >
                <Picker.Item label="-- select service --" value="" />
                {filteredServices.map((s, i) => (
                  <Picker.Item key={i} label={s.serviceName} value={s.serviceId} />
                ))}
              </Picker>
            </View>

            <View style={styles.pickerContainer}>
              <Text style={styles.labelSmall}>Sub-Service</Text>
              <Picker
                selectedValue={it.subServiceId}
                onValueChange={(value) => updateItem(idx, "subServiceId", value)}
                enabled={filteredSubServices.length > 0}
              >
                <Picker.Item label="-- select subservice --" value="" />
                {filteredSubServices.map((s, i) => (
                  <Picker.Item key={i} label={s.subServiceName} value={s.subServiceId} />
                ))}
              </Picker>
            </View>

            <View style={styles.quantityContainer}>
              <Text style={styles.labelSmall}>Qty</Text>
              <TextInput
                placeholder="Qty"
                keyboardType="numeric"
                style={styles.input}
                value={it.quantity}
                onChangeText={(value) => {
                  const numeric = value.replace(/[^0-9]/g, "");
                  updateItem(idx, "quantity", numeric);
                }}
              />
            </View>

            {items.length > 1 && (
              <TouchableOpacity onPress={() => removeLine(idx)}>
                <Text style={styles.removeBtn}>✕</Text>
              </TouchableOpacity>
            )}
          </View>
        );
      })}

      <TouchableOpacity onPress={addLine}>
        <Text style={styles.addLink}>+ Add another item</Text>
      </TouchableOpacity>

      <View style={styles.checkboxRow}>
        <Text>Enable Schedule Plan</Text>
        <TouchableOpacity onPress={() => setField("goWithSchedulePlan", !goWithSchedulePlan)}>
          <Text style={styles.checkbox}>{goWithSchedulePlan ? "☑" : "☐"}</Text>
        </TouchableOpacity>
      </View>

      {error !== "" && <Text style={styles.error}>{error}</Text>}

      <Button title={loading ? "Submitting..." : "Next"} onPress={submit} disabled={loading} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    gap: 10,
  },
  title: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 8,
  },
  label: {
    fontWeight: "600",
    marginBottom: 4,
  },
  labelSmall: {
    fontSize: 12,
    fontWeight: "500",
    marginBottom: 2,
  },
  input: {
    borderWidth: 1,
    borderColor: "#ccc",
    padding: 10,
    borderRadius: 6,
    marginBottom: 6,
    backgroundColor: "#fff",
  },
  itemRow: {
    borderWidth: 1,
    borderColor: "#ddd",
    padding: 10,
    borderRadius: 8,
    marginBottom: 12,
    backgroundColor: "#fafafa",
  },
  pickerContainer: {
    marginBottom: 6,
  },
  quantityContainer: {
    marginBottom: 6,
  },
  checkboxRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    marginVertical: 10,
  },
  checkbox: {
    fontSize: 18,
  },
  addLink: {
    color: "#4B00B5",
    fontWeight: "500",
    marginBottom: 12,
  },
  removeBtn: {
    color: "red",
    fontWeight: "bold",
    fontSize: 18,
    marginTop: 4,
  },
  error: {
    color: "red",
    fontWeight: "500",
    marginBottom: 8,
  },
});


