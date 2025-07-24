import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TextInput,
  Button,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
} from "react-native";
import { useRoute } from "@react-navigation/native";
import { Picker } from "@react-native-picker/picker";
import axios from "../../utils/axiosInstance";

export default function InitialOrderScreen({
  onNext,
  setDummyOrderId,
  initialOrderData,
  setInitialOrderData,
}) {
  let providerId = null;
  try {
    const route = useRoute();
    providerId = route?.params?.providerId;
  } catch (e) {
    const query = new URLSearchParams(window?.location?.search || "");
    providerId = query.get("providerId");
  }

  const [providerItems, setProviderItems] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { pickupDate, pickupTime, items, goWithSchedulePlan } = initialOrderData;

  useEffect(() => {
    if (providerId) {
      setInitialOrderData((prev) => ({ ...prev, serviceProviderId: providerId }));
    }
  }, [providerId]);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const res = await axios.get(`/customer/serviceProviders/${providerId}`, {
          headers: { Accept: "application/json" },
        });

        console.log("📦 Provider response:", res.data);

        const prices = Array.isArray(res.data?.prices) ? res.data.prices : [];

        const items = prices.map((p) => ({
          itemId: p.item.itemId,
          itemName: p.item.itemName,
          serviceId: p.item.serviceId || "",
          serviceName: p.item.serviceName || "",
          subServiceId: p.item.subServiceId || "",
          subServiceName: p.item.subServiceName || "",
          price: p.price,
        }));

        setProviderItems(items);
      } catch (e) {
        console.error("❌ Failed to fetch provider items:", e);
        setError("Failed to fetch items.");
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
    setField("items", [...items, { serviceId: "", subServiceId: "", itemId: "", quantity: 1 }]);

  const removeLine = (idx) => setField("items", items.filter((_, i) => i !== idx));

  const validate = () => {
    if (!providerId || !pickupDate || !pickupTime) return "Fill all required fields";
    for (let it of items) {
      if (!it.itemId || it.quantity < 1) return "Please select item & quantity ≥1";
    }
    return null;
  };

const submit = async () => {
  const v = validate();
  if (v) return setError(v);

  setLoading(true);
  try {
    const cleanedItems = items.map(({ itemId, quantity }) => ({ itemId, quantity }));
    const res = await axios.post("/orders/initial", {
      serviceProviderId: providerId,
      pickupDate,
      pickupTime,
      items: cleanedItems,
      goWithSchedulePlan,
    });

    const dummyOrderId = res.data;
    console.log("✅ Dummy Order ID created:", dummyOrderId);

    setDummyOrderId(dummyOrderId);

    // ✅ Move to next step
    onNext(goWithSchedulePlan); // <-- this is what triggers step change
  } catch (e) {
    console.error("❌ Order submit error:", e);
    setError(e.response?.data?.message || "Could not submit order");
  } finally {
    setLoading(false);
  }
};


  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Step 1: Start Your Laundry Order</Text>

      <Text style={styles.label}>Pickup Date</Text>
      <TextInput
        style={styles.input}
        placeholder="YYYY-MM-DD"
        value={pickupDate}
        onChangeText={(text) => setField("pickupDate", text)}
      />

      <Text style={styles.label}>Pickup Time</Text>
      <TextInput
        style={styles.input}
        placeholder="HH:MM"
        value={pickupTime}
        onChangeText={(text) => setField("pickupTime", text)}
      />

      <Text style={styles.label}>Select Items</Text>
      {items.map((it, idx) => {
        const filteredServices = providerItems
          .filter((i) => i.itemId === it.itemId && i.serviceId)
          .map((i) => ({ serviceId: i.serviceId, serviceName: i.serviceName }));

        const filteredSubServices = providerItems
          .filter((i) => i.itemId === it.itemId && i.subServiceId)
          .map((i) => ({ subServiceId: i.subServiceId, subServiceName: i.subServiceName }));

        return (
          <View key={idx} style={styles.itemRow}>
            <View style={{ flex: 1 }}>
              <Text style={styles.labelSmall}>Item</Text>
              <Picker
                selectedValue={it.itemId}
                onValueChange={(value) => updateItem(idx, "itemId", value)}
              >
                <Picker.Item label="-- select item --" value="" />
                {providerItems.map((item) => (
                  <Picker.Item
                    key={item.itemId}
                    label={`${item.itemName} - ₹${item.price}`}
                    value={item.itemId}
                  />
                ))}
              </Picker>
            </View>

            <View style={{ flex: 1 }}>
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

            <View style={{ flex: 1 }}>
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

            <View style={{ flex: 1 }}>
              <Text style={styles.labelSmall}>Qty</Text>
              <TextInput
                placeholder="Qty"
                keyboardType="number-pad"
                style={styles.input}
                value={String(it.quantity)}
                onChangeText={(value) => updateItem(idx, "quantity", parseInt(value) || 1)}
              />
            </View>

            {items.length > 1 && (
              <TouchableOpacity onPress={() => removeLine(idx)}>
                <Text style={{ color: "red", fontWeight: "bold", marginTop: 28 }}>✕</Text>
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
    marginTop: 10,
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
    padding: 8,
    borderRadius: 6,
    marginBottom: 6,
  },
  itemRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10,
    marginBottom: 16,
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
  error: {
    color: "red",
    fontWeight: "500",
    marginBottom: 8,
  },
});
