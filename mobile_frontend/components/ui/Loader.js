
import React from 'react';
import { View, ActivityIndicator, StyleSheet } from 'react-native';

const Loader = () => {
  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color="#8E6FB7" /> {/* purple-500 equivalent */}
    </View>
  );
};

export default Loader;

const styles = StyleSheet.create({
  container: {
    paddingVertical: 24,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
