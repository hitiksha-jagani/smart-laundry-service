// Author: Hitiksha Patel
// Description: Delivery page of delivery agent dashboard.

import React, { useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import SummaryCard from '../../components/DeliveryAgent/SummaryCard';
import DeliveryAgentDashboardLayout from '../../components/Layout/DeliveryAgentDashboardLayout';
import DeliverySummary from "../../components/DeliveryAgent/DeliverySummary";
import PendingDeliveries from "../../components/DeliveryAgent/PendingDeliveries";
import TodayDeliveries from "../../components/DeliveryAgent/TodayDeliveries";
import '../../styles/DeliveryAgent/DeliveryAgentSidebar.css';
import '../../styles/DeliveryAgent/SummaryCard.css';
import { BsController } from 'react-icons/bs';

const mockDeliveries = [
  {
    orderId: "ODR00001",
    deliveryType: "Customer -> Service Provider",
    deliveryEarning: 75,
    km: 4.2,
    pickupDate: "2025-06-23",
    pickupTime: "18:00:00",
    pickupName: "Customer A",
    pickupPhone: "9876543210",
    pickupAddress: "12, A-Block, CG Road, Ahmedabad, Gujarat, 380009",
    deliveryName: "Provider A",
    deliveryPhone: "9123456780",
    deliveryAddress: "18, B-Block, Maninagar, Ahmedabad, Gujarat, 380008",
    totalQuantity: 3,
    bookingItemDTOList: [
      {
        itemName: "Shirt",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      }
    ]
  },
  {
    orderId: "ODR00002",
    deliveryType: "Customer -> Service Provider",
    deliveryEarning: 75,
    km: 4.2,
    pickupDate: "2025-06-23",
    pickupTime: "18:00:00",
    pickupName: "Customer A",
    pickupPhone: "9876543210",
    pickupAddress: "12, A-Block, CG Road, Ahmedabad, Gujarat, 380009",
    deliveryName: "Provider A",
    deliveryPhone: "9123456780",
    deliveryAddress: "18, B-Block, Maninagar, Ahmedabad, Gujarat, 380008",
    totalQuantity: 3,
    bookingItemDTOList: [
      {
        itemName: "Shirt",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      }
    ]
  },
  {
    orderId: "ODR00003",
    deliveryType: "Customer -> Service Provider",
    deliveryEarning: 75,
    km: 4.2,
    pickupDate: "2025-06-23",
    pickupTime: "18:00:00",
    pickupName: "Customer A",
    pickupPhone: "9876543210",
    pickupAddress: "12, A-Block, CG Road, Ahmedabad, Gujarat, 380009",
    deliveryName: "Provider A",
    deliveryPhone: "9123456780",
    deliveryAddress: "18, B-Block, Maninagar, Ahmedabad, Gujarat, 380008",
    totalQuantity: 3,
    bookingItemDTOList: [
      {
        itemName: "Shirt",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Jeans",
        serviceName: "Wash & Iron",
        quantity: 2
      },
      {
        itemName: "Trousers",
        serviceName: "Dry Clean",
        quantity: 1
      }
    ]
  }
];

const DeliveryPage = () => {
    const [user, setUser] = useState(null);
    const [pending, setPending] = useState([]);
    const [today, setToday] = useState([]);
    const [loading, setLoading] = useState(true);

    const token = localStorage.getItem("token");
    console.log(token);

    const axiosInstance = axios.create({
        baseURL: "http://localhost:8080",
        headers: { Authorization: `Bearer ${token}` },
    });

    useEffect(() => {
        const fetchAllData = async () => {
            if (!token) return;

            let decoded;
            try {
                decoded = jwtDecode(token);
                // console.log(decoded)
                console.log("user Id : ", decoded.id)
            } catch (err) {
                console.error('Invalid token:', err);
                return;
            }

            const userId = decoded.userId || decoded.id;

            try {
                // Fetch all data in parallel
                const [userRes, pendingRes, todayRes] = await Promise.all([
                    
                    axiosInstance.get(`/user-detail/${userId}`).catch(err => {
                        console.error("User detail fetch failed", err);
                        return { data: null };
                    }),
                    axiosInstance.get("/deliveries/pending", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }).catch(err => {
                        console.error("Pending delivery data fetch failed", err);
                        return { data: null };
                    }),
                    axiosInstance.get("/deliveries/today", {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }).catch(err => {
                        console.error("Today delivery data fetch failed", err);
                        return { data: null };
                    }),

                ]);

                setUser(userRes.data);
                console.log("User data : " ,userRes.data);

                setPending(pendingRes.data);
                console.log("pending deliveries : ", pendingRes.data);

                setToday(todayRes.data);
                console.log("todays delivery : ", todayRes.data);

            } catch (error) {
                console.error("Failed to fetch one or more data:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchAllData();
    }, []);

    if (loading) return <p className="text-center">Loading...</p>;

    return (
        <>

            <DeliveryAgentDashboardLayout user={user}>
                <h1 className='heading inter-font'>DELIVERY DASHBOARD</h1>

                <div className="summary-container inter-font">
                    <SummaryCard title="TOTAL ORDERS" count={pending.length + today.length} />
                    <SummaryCard title="PENDING ORDERS" count={pending.length} />
                    <SummaryCard title="TODAY'S ORDERS" count={today.length}/>
                </div>

                <h2 className='pending-heading heading inter-font'>PENDING DELIVERIES</h2>
                {/* <PendingDeliveries deliveries={pending}/> */}
                <PendingDeliveries deliveries={mockDeliveries} token={token} />

                <h2 className='pending-heading heading inter-font' style={{marginTop: '50px'}}>TODAY'S DELIVERIES</h2>
                {/* <TodayDeliveries deliveries={today} /> */}
                <TodayDeliveries deliveries={mockDeliveries} token={token} />
                
            </DeliveryAgentDashboardLayout>
    
        </>
    );
};

export default DeliveryPage;
