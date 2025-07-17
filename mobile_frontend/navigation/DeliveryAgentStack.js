// navigation/DeliveryAgentStack.js

import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import DeliveryPage from '../screens/DeliveryAgent/DeliveryPage';
// import PendingDeliveries from '../screens/delivery/PendingDeliveries';
// import TodayDeliveries from '../screens/delivery/TodayDeliveries';

// import Availability from '../screens/availability/Availability';
// import SavedAvailability from '../screens/availability/SavedAvailability';

// import UpdateStatus from '../screens/status/UpdateStatus';

// import DeliveryAgentPayout from '../screens/payout/DeliveryAgentPayout';
// import AllPayouts from '../screens/payout/AllPayouts';
// import PaidPayouts from '../screens/payout/PaidPayouts';
// import PendingPayouts from '../screens/payout/PendingPayouts';

// import DeliveryAgentFeedback from '../screens/feedback/DeliveryAgentFeedback';
// import DeliveryAgentFeedbackList from '../screens/feedback/DeliveryAgentFeedbackList';

// import CompleteProfile from '../screens/profile/CompleteProfile';
// import ProfileDetail from '../screens/profile/ProfileDetail';
// import EditProfile from '../screens/profile/EditProfile';
// import ChangePassword from '../screens/profile/ChangePassword';

// import NotAvailablePage from '../screens/common/NotAvailablePage';

const Stack = createNativeStackNavigator();

const DeliveryAgentStack = () => {

    return (
        <Stack.Navigator initialRouteName="DeliveryPage" screenOptions={{ headerShown: false }}>
        
            <Stack.Screen name="DeliveryPage" component={DeliveryPage} />
            {/* <Stack.Screen name="PendingDeliveries" component={PendingDeliveries} />
            <Stack.Screen name="TodayDeliveries" component={TodayDeliveries} />

            <Stack.Screen name="ManageAvailability" component={Availability} />
            <Stack.Screen name="SavedAvailability" component={SavedAvailability} />
            <Stack.Screen name="EditAvailability" component={TodayDeliveries} />
            <Stack.Screen name="DeleteAvailability" component={TodayDeliveries} />

            <Stack.Screen name="UpdateStatus" component={UpdateStatus} />
            <Stack.Screen name="VerifyPickup" component={UpdateStatus} />
            <Stack.Screen name="VerifyHandover" component={UpdateStatus} />
            <Stack.Screen name="VerifyDelivery" component={UpdateStatus} />
            <Stack.Screen name="VerifyClothsConfirm" component={UpdateStatus} />

            <Stack.Screen name="PayoutsSummary" component={DeliveryAgentPayout} />
            <Stack.Screen name="AllPayouts" component={AllPayouts} />
            <Stack.Screen name="PaidPayouts" component={PaidPayouts} />
            <Stack.Screen name="PendingPayouts" component={PendingPayouts} />

            <Stack.Screen name="FeedbackSummary" component={DeliveryAgentFeedback} />
            <Stack.Screen name="FeedbackList" component={DeliveryAgentFeedbackList} />

            <Stack.Screen name="CompleteProfile" component={CompleteProfile} />
            <Stack.Screen name="ProfileDetail" component={ProfileDetail} />
            <Stack.Screen name="EditProfile" component={EditProfile} />
            <Stack.Screen name="ChangePassword" component={ChangePassword} />

            <Stack.Screen name="NotAvailable" component={NotAvailablePage} /> */}

        </Stack.Navigator>
    );

};

export default DeliveryAgentStack;
