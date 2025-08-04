import { Dimensions, StyleSheet,  View, Text } from 'react-native';
import { useRoute } from '@react-navigation/native';
import { MaterialIcons } from '@expo/vector-icons';
import DeliveryAgentLayout from '../../components/DeliveryAgent/Layout'; 
import { deliveryAgentStyles } from '../../styles/DeliveryAgent/deliveryAgentStyles';

const screenWidth = Dimensions.get('window').width;

const PaidPayouts = () => {
    const route = useRoute();
    const { user, data = [] } = route.params || {};

    return (
 
        <DeliveryAgentLayout> 

          <View style={[styles.container, deliveryAgentStyles.deliveryAgentBody]}>
          
            <View style={deliveryAgentStyles.container}>

              <Text style={[deliveryAgentStyles.h1Agent, styles.heading]}>Paid Payouts</Text>
                  
              {data.length === 0 ? (
          
                <View style={styles.emptyBox}>
          
                  <MaterialIcons name="inbox" size={64} color="#ccc" />

                    <Text style={styles.emptyTitle}>No Payouts Available</Text>
                    <Text style={styles.emptySubtitle}>
                      Once payouts arrived, they’ll appear here.
                    </Text>
          
                </View>
          
              ) : (

                data.map((entry, idx) => (

                      <View key={idx} style={styles.contactBox}>

                        <Text style={styles.contactTitle}>{entry.orderId}</Text>
                                  
                        <View style={styles.itemCardRow}>
                        
                          <Text style={styles.itemLabel}>Delivery Earnings:</Text>
                          <Text style={styles.itemValue}>{entry.deliveryEarning ? entry.deliveryEarning.toFixed(2) : '-'}</Text>
                        
                        </View>

                        <View style={styles.itemCardRow}>
                        
                          <Text style={styles.itemLabel}>Charge:</Text>
                          <Text style={styles.itemValue}>{entry.charge ? entry.charge.toFixed(2) : '-'}</Text>
                        
                        </View>

                        <View style={styles.itemCardRow}>
                        
                          <Text style={styles.itemLabel}>Final Amount:</Text>
                          <Text style={styles.itemValue}>{entry.finalAmount?.toFixed(2)}</Text>
                        
                        </View>

                        <View style={styles.itemCardRow}>
                        
                          <Text style={styles.itemLabel}>Date Time:</Text>
                          <Text style={styles.itemValue}>{entry.dateTime ? new Date(entry.dateTime).toLocaleString() : '-'}</Text>
                        
                        </View>

                  </View>

                ))
              )}

            </View>

          </View>

        </DeliveryAgentLayout>
    )

}

const styles = StyleSheet.create({

  container: {
    padding: 20,
    backgroundColor: '#ecfdf5',
    flexGrow: 1,
  },

  heading: {
    marginTop: '30px'
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

  emptyBox: {
    alignItems: 'center',
    padding: 30,
    backgroundColor: '#fff',
    borderStyle: 'dashed',
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 16,
    marginVertical: 30,
  },

  emptyTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginTop: 10,
    color: '#555',
  },

  emptySubtitle: {
    marginTop: 8,
    color: '#777',
    textAlign: 'center',
  },


  contactBox: {
    width: screenWidth * 0.9, 
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

export default PaidPayouts;