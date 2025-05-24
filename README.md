#Smart Laundry Service

API DOCUMENTATION


#CUSTOMER

openapi: 3.0.0
info:
  title: Smart Laundry Service API
  version: 1.0.0
  description: API for the Smart Laundry Service web and mobile applications, including user registration and homepage content.

paths:
  /customer/home:
    get:
      summary: Get homepage content for customers
      description: Returns homepage data including headline text, call-to-action, about us section, and featured service providers.
      tags:
        - Customer
      responses:
        '200':
          description: Homepage content retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  headline:
                    type: string
                    example: "Smart Laundry. Delivered to Your Doorstep"
                  callToAction:
                    type: object
                    properties:
                      label:
                        type: string
                        example: "Book Now"
                      link:
                        type: string
                        example: "/customer/book-service"
                  aboutUs:
                    type: string
                    example: >
                      We are revolutionizing laundry with our smart, on-demand service that brings convenience, cleanliness, and care right to your doorstep.
                  featuredServiceProviders:
                    type: array
                    items:
                      type: object
                      properties:
                        id:
                          type: integer
                          example: 101
                        name:
                          type: string
                          example: "Sparkle Cleaners"
                        imageUrl:
                          type: string
                          format: uri
                          example: "https://cdn.smartlaundry.com/images/providers/101.jpg"
                  moreProvidersLink:
                    type: string
                    example: "/customer/service-providers"
        '500':
          description: Internal server error
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Unable to load homepage content. Please try again later."
 
  /customer/register:
    post:
      summary: Register a new user with role selection
      description: >
        Register a new user as a customer, service provider, or admin with
        personal details, address, and agreement to terms and conditions.
      tags:
        - Customer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - role
                - firstname
                - lastname
                - phoneNo
                - email
                - address
                - agreeTerms
              properties:
                role:
                  type: string
                  description: User role selection
                  enum:
                    - customer
                    - service provider
                    - admin
                  example: customer
                firstname:
                  type: string
                  example: Rohan
                lastname:
                  type: string
                  example: Patel
                phoneNo:
                  type: integer
                  example: 9876543210
                email:
                  type: string
                  format: email
                  example: abc123@gmail.com
                address:
                  type: object
                  required:
                    - No
                    - name
                    - areaName
                    - city
                    - pincode
                    - state
                  properties:
                    no:
                      type: string
                      description: House or building number
                      example: "123A"
                    name:
                      type: string
                      description: Street name or locality
                      example: "MG Road"
                    areaName:
                      type: string
                      example: "Sector 21"
                    city:
                      type: string
                      example: "Ahmedabad"
                    pincode:
                      type: integer
                      example: 380015
                    state:
                      type: string
                      example: "Gujarat"
                agreeTerms:
                  type: boolean
                  description: Checkbox for agreeing to terms and conditions
                  example: true
      responses:
        '201':
          description: User registered successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Registration successful. Please login."
        '400':
          description: Bad Request - Missing or invalid fields
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Invalid input or missing required fields."
                    
  /customer/book-service:
    post:
      summary: Book a laundry service
      description: >
        Allows a customer to book laundry services by selecting service type, sub-service type (if applicable),
        clothes, pickup details, service plan option, address, and contact info.
      tags:
        - Customer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - serviceType
                - clothes
                - pickupDate
                - pickupTime
                - pickupAddress
                - contactInfo
              properties:
                serviceType:
                  type: string
                  description: Main service type
                  enum:
                    - Undergarments
                    - Wash+Iron
                    - Drycleaning
                    - Specialized
                    - Wash
                    - Iron
                  example: Specialized
                subServiceType:
                  type: string
                  description: Required if serviceType is Specialized
                  enum:
                    - Curtain(Wash)
                    - Curtain(Wash+Iron)
                    - Woolen(Wash)
                    - Woolen(Wash+Iron)
                  example: Curtain(Wash)
                clothes:
                  type: array
                  description: List of clothes to be serviced
                  items:
                    type: string
                    enum:
                      - cotton
                      - pair
                      - remove
                  example:
                    - cotton
                pickupDate:
                  type: string
                  format: date
                  example: 2025-05-28
                pickupTime:
                  type: string
                  format: time
                  example: "10:30"
                useServicePlanSelector:
                  type: boolean
                  description: Whether the user opted for a service plan
                  example: true
                pickupAddress:
                  type: object
                  required:
                    - No
                    - name
                    - area
                    - city
                    - pincode
                    - state
                  properties:
                    chooseThisAddress:
                      type: boolean
                      description: Checkbox to confirm this address
                      example: true
                    no:
                      type: string
                      example: "12B"
                    name:
                      type: string
                      example: "Green Heights"
                    area:
                      type: string
                      example: "Satellite"
                    city:
                      type: string
                      example: "Ahmedabad"
                    pincode:
                      type: integer
                      example: 380015
                    state:
                      type: string
                      example: "Gujarat"
                contactInfo:
                  type: object
                  required:
                    - mobile
                    - name
                  properties:
                    mobile:
                      type: string
                      example: "9876543210"
                    name:
                      type: string
                      example: "Rohan"
      responses:
        '201':
          description: Booking successful
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Service booked successfully."
                  bookingId:
                    type: integer
                    example: 456
                  status:
                    type: string
                    example: "PENDING"
                  estimatedDelivery:
                    type: string
                    format: date
                    example: 2025-05-31
        '400':
          description: Invalid request data
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Missing required fields or invalid input."
        '401':
          description: Unauthorized - Login required
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Authentication token required."
  /customer/referral:
    get:
      summary: Get referral and coupon information
      description: >
        Returns the user's referral code, sharing instructions, generated coupon list,
        and coupon usage history.
      tags:
        - Customer
      responses:
        '200':
          description: Referral and coupon data retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  referralCode:
                    type: string
                    example: REF500201
                  inviteText:
                    type: string
                    example: >
                      Invite your friends and family to use our service. For every new user, you will be rewarded with a unique personalized coupon sent via EMAIL and SMS.
                  shareOptions:
                    type: object
                    properties:
                      shareViaWhatsApp:
                        type: string
                        example: "https://wa.me/?text=Use%20my%20coupon%20REF500201%20to%20get%20a%20discount%20on%20Smart%20Laundry%20services!"
                      canShareOffline:
                        type: boolean
                        example: true
                  termsNote:
                    type: string
                    example: "*After discount, minimum payable amount must be Rs.100/-"
                  couponsForMe:
                    type: array
                    items:
                      type: object
                      properties:
                        couponCode:
                          type: string
                          example: APP30
                        get:
                          type: string
                          example: "30% Cashback"
                        maxCashback:
                          type: string
                          example: "Max cashback Rs. 150"
                        validity:
                          type: string
                          format: date
                          example: "2025-12-30"
                        service:
                          type: string
                          example: "All Garment Cleaning"
                        minOrder:
                          type: string
                          example: "Rs. 219 + TAX"
                        description:
                          type: string
                          example: ""
                  couponUsageHistory:
                    type: array
                    items:
                      type: object
                      properties:
                        couponCode:
                          type: string
                          example: "4CL"
                        orderId:
                          type: string
                          example: "2024010001"
        '401':
          description: Unauthorized - Login required
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Authentication required"
        '500':
          description: Internal server error
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Unable to fetch referral data at this time."
  /customer/faq:
    get:
      summary: Get FAQ and support options
      description: Returns a list of frequently asked questions with answers and available support actions.
      tags:
        - Customer
      responses:
        '200':
          description: FAQ and support information retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  faqs:
                    type: array
                    items:
                      type: object
                      properties:
                        question:
                          type: string
                        answer:
                          type: string
                    example:
                      - question: "Do you give discount in Shoe drycleaning?"
                        answer: "NO"
                      - question: "How do you calculate curtain rates?"
                        answer: "We charge curtains on per panel basis. 1 panel is 3 ft in width and maximum of 10ft in length."
                  supportOptions:
                    type: object
                    properties:
                      liveChat:
                        type: object
                        properties:
                          description:
                            type: string
                            example: "Get instant help from our support agents"
                          actionLabel:
                            type: string
                            example: "Start Chat Now"
                          link:
                            type: string
                            example: "/customer/support/live-chat"
                      supportTicket:
                        type: object
                        properties:
                          description:
                            type: string
                            example: "Submit a detailed request and we'll investigate"
                          actionLabel:
                            type: string
                            example: "Create New Ticket"
                          link:
                            type: string
                            example: "/customer/support/ticket"
        '500':
          description: Internal server error
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Unable to fetch FAQ data. Please try again later."
  /customer/bill/{orderId}:
    get:
      summary: Get billing details for a customer order
      description: Returns the bill for a completed order including item breakdown, taxes, promo discounts, and total amount.
      parameters:
        - name: orderId
          in: path
          required: true
          description: Unique identifier for the customer order
          schema:
            type: string
      responses:
        '200':
          description: Successfully retrieved billing details
          content:
            application/json:
              schema:
                type: object
                properties:
                  billedTo:
                    type: string
                    description: Name of the customer
                    example: John Doe
                  invoiceNumber:
                    type: string
                    description: Unique invoice number
                    example: INV-2025-00342
                  dateIssued:
                    type: string
                    format: date
                    description: Date when the invoice was generated
                    example: 2025-05-24
                  items:
                    type: array
                    description: List of items included in the bill
                    items:
                      type: object
                      properties:
                        item:
                          type: string
                          description: Name of the service/item
                          example: "Dry Cleaning - Shirt"
                        quantity:
                          type: integer
                          example: 4
                        perItemPrice:
                          type: number
                          format: float
                          example: 40.00
                        amount:
                          type: number
                          format: float
                          description: quantity * perItemPrice
                          example: 160.00
                  subTotal:
                    type: number
                    format: float
                    description: Total before GST and additional charges
                    example: 160.00
                  gst:
                    type: number
                    format: float
                    description: GST (Goods and Services Tax)
                    example: 28.80
                  pickupAndDeliveryCharge:
                    type: number
                    format: float
                    example: 25.00
                  promoCode:
                    type: string
                    nullable: true
                    description: Promo code applied, if any
                    example: "SAVE10"
                  promoDiscount:
                    type: number
                    format: float
                    example: 10.00
                  total:
                    type: number
                    format: float
                    description: Final amount after taxes and discounts
                    example: 203.80
        '404':
          description: Order not found
        '500':
          description: Server error
  /customer/service-providers:
    get:
      summary: Get list of service providers
      description: Returns a list of all or nearby service providers based on the filter type and search keyword.
      parameters:
        - name: filter
          in: query
          required: false
          description: Filter for listing providers - 'all' or 'nearby'
          schema:
            type: string
            enum: [all, nearby]
            default: all
            example: nearby
        - name: keyword
          in: query
          required: false
          description: Search by laundry name, address, or service
          schema:
            type: string
            example: "Express Wash"
        - name: customerLat
          in: query
          required: false
          description: Customer latitude (required for nearby filter)
          schema:
            type: number
            format: float
            example: 23.0205
        - name: customerLng
          in: query
          required: false
          description: Customer longitude (required for nearby filter)
          schema:
            type: number
            format: float
            example: 72.5797
      responses:
        '200':
          description: Successfully retrieved list of service providers
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    providerId:
                      type: string
                      description: Unique ID of the service provider
                      example: "sp1234"
                    photoUrl:
                      type: string
                      description: URL to the provider's photo
                      example: "https://example.com/photos/provider1.jpg"
                    laundryName:
                      type: string
                      example: "Sparkle Laundry"
                    rating:
                      type: number
                      format: float
                      example: 4.6
                    address:
                      type: string
                      example: "15 Ring Road, Navrangpura, Ahmedabad"
                    daysAvailable:
                      type: array
                      items:
                        type: string
                      example: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
                    timeSlots:
                      type: array
                      items:
                        type: string
                      example: ["09:00 AM - 01:00 PM", "03:00 PM - 08:00 PM"]
                    distance:
                      type: string
                      description: Distance from customer (optional, shown if nearby filter is used)
                      example: "1.2 km"
                    isNearby:
                      type: boolean
                      description: Indicates whether the provider is nearby (based on location)
                      example: true
        '400':
          description: Bad request (e.g., missing lat/lng for nearby filter)
        '500':
          description: Internal server error
  /customer/orders:
    get:
      summary: Get customer orders
      description: |
        Fetches all customer orders grouped by status — Incomplete, Completed, and Canceled.
        Includes service provider, slots, prices, status, delivery info, etc.
      parameters:
        - name: customerId
          in: query
          required: true
          description: Unique identifier for the customer
          schema:
            type: string
            example: "cust1234"
      responses:
        '200':
          description: List of categorized orders
          content:
            application/json:
              schema:
                type: object
                properties:
                  incompleteOrders:
                    type: array
                    items:
                      type: object
                      properties:
                        orderId:
                          type: string
                          example: "ORD123456"
                        pickupDate:
                          type: string
                          format: date
                          example: "2025-05-20"
                        providerName:
                          type: string
                          example: "Sparkle Clean Laundry"
                        pickupSlot:
                          type: string
                          example: "10:00 AM - 12:00 PM"
                        couponCode:
                          type: string
                          example: "SAVE20"
                        comment:
                          type: string
                          example: "Handle with care"
                        price:
                          type: number
                          format: float
                          example: 300.00
                        cloths:
                          type: integer
                          example: 5
                        status:
                          type: string
                          example: "Pickup Scheduled"
                  completedOrders:
                    type: array
                    items:
                      type: object
                      properties:
                        orderId:
                          type: string
                          example: "ORD654321"
                        pickupDate:
                          type: string
                          format: date
                          example: "2025-05-15"
                        providerName:
                          type: string
                          example: "Fresh & Clean Laundry"
                        pickupSlot:
                          type: string
                          example: "09:00 AM - 11:00 AM"
                        comment:
                          type: string
                          example: "Express wash"
                        couponCode:
                          type: string
                          example: "FIRSTORDER"
                        cloths:
                          type: integer
                          example: 7
                        price:
                          type: number
                          format: float
                          example: 420.00
                        pickupAgentName:
                          type: string
                          example: "Raj Mehta"
                        pickupAgentPhone:
                          type: string
                          example: "+91 9876543210"
                        delivered:
                          type: boolean
                          example: true
                        deliveryAgentName:
                          type: string
                          example: "Neha Patel"
                        deliveryAgentPhone:
                          type: string
                          example: "+91 9876543200"
                        pickupTime:
                          type: string
                          example: "10:00 AM"
                        deliveryDateTime:
                          type: string
                          example: "2025-05-17 05:00 PM"
                  canceledOrders:
                    type: array
                    items:
                      type: object
                      properties:
                        orderId:
                          type: string
                          example: "ORD111222"
                        providerName:
                          type: string
                          example: "White Laundry"
                        price:
                          type: number
                          format: float
                          example: 150.00
                        cloths:
                          type: integer
                          example: 3
                        reason:
                          type: string
                          example: "Customer canceled before pickup"
        '404':
          description: Customer not found or no orders available
        '500':
          description: Internal server error
  /customer/payment/success:
    get:
      summary: Display payment success details
      description: |
        After successful payment, this API returns a confirmation message with order and transaction details.
        Used for rendering the "Payment Successful" screen to the customer.
      parameters:
        - name: orderId
          in: query
          required: true
          description: Unique identifier for the order
          schema:
            type: string
            example: "123455"
      responses:
        '200':
          description: Payment confirmation details
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                    example: true
                  message:
                    type: string
                    example: "Payment Successful! Thank you for your payment."
                  amount:
                    type: number
                    format: float
                    example: 3000.00
                  orderId:
                    type: string
                    example: "123455"
                  transactionId:
                    type: string
                    example: "30234"
                  paymentMethod:
                    type: string
                    example: "Visa ***** 4242"
                  timestamp:
                    type: string
                    format: date-time
                    example: "2025-05-24T15:37:00Z"
        '400':
          description: Missing or invalid order ID
        '404':
          description: Payment record not found
        '500':
          description: Internal server error
  /customer/order-summary:
    get:
      summary: Get Order Summary
      description: Retrieve detailed order summary including service types, item details, charges, promo code, and total cost.
      parameters:
        - name: customer_id
          in: query
          required: true
          schema:
            type: string
          description: Unique identifier for the customer
      responses:
        '200':
          description: Successfully retrieved order summary
          content:
            application/json:
              schema:
                type: object
                properties:
                  serviceType:
                    type: string
                    example: Dry Cleaning
                  subServiceType:
                    type: string
                    example: Daily Wear
                  items:
                    type: array
                    items:
                      type: object
                      properties:
                        itemName:
                          type: string
                          example: Shirt
                        quantity:
                          type: integer
                          example: 2
                        pricePerItem:
                          type: integer
                          example: 50
                        amount:
                          type: integer
                          example: 100
                  subTotal:
                    type: integer
                    example: 100
                  gst:
                    type: string
                    example: "5%"
                  pickupAndDeliveryCharge:
                    type: integer
                    example: 20
                  promoCode:
                    type: string
                    example: LAUNDRY20
                  total:
                    type: integer
                    example: 105
        '400':
          description: Missing or invalid parameters
        '500':
          description: Server error
  /customer/support/ticket:
    post:
      summary: Raise a New Support Ticket
      description: Allows a customer to raise a support ticket with details like title, description, category, and optional photo.
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                ticketTitle:
                  type: string
                  example: Not Delivered yet
                description:
                  type: string
                  example: My Delivery Date was yesterday, but I still did not get my delivery today.
                photo:
                  type: string
                  format: binary
                category:
                  type: string
                  example: No Timely Delivery
      responses:
        '201':
          description: Ticket successfully created
          content:
            application/json:
              schema:
                type: object
                properties:
                  ticketId:
                    type: string
                    example: TCKT123456
                  message:
                    type: string
                    example: Support ticket submitted successfully.
        '400':
          description: Invalid input or missing required fields
        '500':
          description: Internal server error

  /customer/support/tickets:
    get:
      summary: Get All Support Tickets
      description: Retrieve all submitted support tickets for a customer.
      parameters:
        - name: customer_id
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: List of support tickets
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    submittedAt:
                      type: string
                      format: date-time
                      example: 2025-05-15T21:23:00
                    ticketTitle:
                      type: string
                      example: Not Delivered yet
                    description:
                      type: string
                      example: My Delivery Date was yesterday, but I still did not get my delivery today.
                    photo:
                      type: string
                      example: photo1.jpg
                    category:
                      type: string
                      example: No Timely Delivery
                    respondedAt:
                      type: string
                      format: date-time
                      example: 2025-05-16T13:50:00
                    response:
                      type: string
                      example: Thank You For Your Response.
                    status:
                      type: string
                      enum: [Pending, Resolved, In Progress]
                      example: Resolved
        '404':
          description: No tickets found
        '500':
          description: Internal server error
  /customer/wallet:
    get:
      summary: Get Wallet Balance and Transaction History
      description: Returns the current wallet balance and a list of past wallet transactions for the customer.
      parameters:
        - name: customer_id
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Wallet information retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  currentBalance:
                    type: number
                    example: 1500
                  transactions:
                    type: array
                    items:
                      type: object
                      properties:
                        type:
                          type: string
                          example: Order Deduction
                        date:
                          type: string
                          format: date
                          example: 2025-04-22
                        amount:
                          type: number
                          example: -150
        '404':
          description: Wallet not found for the customer
        '500':
          description: Internal server error

  /customer/wallet/topup:
    post:
      summary: Top Up Wallet Balance
      description: Add funds to the customer's wallet balance.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                customer_id:
                  type: string
                  example: CUST12345
                amount:
                  type: number
                  example: 500
      responses:
        '200':
          description: Wallet successfully topped up
          content:
            application/json:
              schema:
                type: object
                properties:
                  newBalance:
                    type: number
                    example: 2000
                  message:
                    type: string
                    example: Wallet topped up successfully.
        '400':
          description: Invalid request payload
        '500':
          description: Failed to top up the wallet
  /customer/profile:
    get:
      summary: Get Customer Profile
      description: Retrieve the profile information of a customer.
      parameters:
        - name: customer_id
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Customer profile retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  firstName:
                    type: string
                    example: John
                  lastName:
                    type: string
                    example: Doe
                  phone:
                    type: string
                    example: +91 9567839424
                  email:
                    type: string
                    example: johndoe1234@gmail.com
                  address:
                    type: object
                    properties:
                      houseNo:
                        type: string
                        example: 101, Maple Residency
                      area:
                        type: string
                        example: Lakeview
                      city:
                        type: string
                        example: Mumbai
                      state:
                        type: string
                        example: Maharashtra
                      pincode:
                        type: string
                        example: 400001
                  preferences:
                    type: object
                    properties:
                      notifications:
                        type: boolean
                        example: true
                      language:
                        type: string
                        example: English
        '404':
          description: Customer not found
        '500':
          description: Internal server error

    put:
      summary: Update Customer Profile
      description: Update customer profile and address details.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                customer_id:
                  type: string
                  example: CUST12345
                firstName:
                  type: string
                  example: John
                lastName:
                  type: string
                  example: Doe
                phone:
                  type: string
                  example: +91 9567839424
                email:
                  type: string
                  example: johndoe1234@gmail.com
                address:
                  type: object
                  properties:
                    houseNo:
                      type: string
                      example: 101, Maple Residency
                    area:
                      type: string
                      example: Lakeview
                    city:
                      type: string
                      example: Mumbai
                    state:
                      type: string
                      example: Maharashtra
                    pincode:
                      type: string
                      example: 400001
      responses:
        '200':
          description: Profile updated successfully
        '400':
          description: Invalid input data
        '500':
          description: Error updating profile

  /customer/profile/statistics:
    get:
      summary: Get Order Statistics
      description: Returns number of completed and cancelled orders for the customer.
      parameters:
        - name: customer_id
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Statistics retrieved
          content:
            application/json:
              schema:
                type: object
                properties:
                  ordersCompleted:
                    type: integer
                    example: 21
                  ordersCancelled:
                    type: integer
                    example: 1
        '404':
          description: Customer not found
        '500':
          description: Internal server error
  /customer/profile/preferences:
    put:
      summary: Update Notification and Language Preferences
      description: Update notification settings and preferred language for the customer.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                customer_id:
                  type: string
                  example: CUST12345
                notifications:
                  type: boolean
                  example: true
                language:
                  type: string
                  example: English
      responses:
        '200':
          description: Preferences updated
        '400':
          description: Invalid input
        '500':
          description: Update failed
  /customer/schedule:
    post:
      summary: Schedule Laundry Pickup
      description: Allows customers to schedule a laundry pickup with preferred time slots and payment preferences.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - customer_id
                - schedule_date
                - selected_slots
              properties:
                customer_id:
                  type: string
                  example: CUST12345
                schedule_date:
                  type: string
                  format: date
                  example: 2025-05-25
                selected_slots:
                  type: array
                  items:
                    type: string
                    enum:
                      - "10:00AM - 12:00PM"
                      - "2:00PM - 4:00PM"
                      - "6:00PM - 8:00PM"
                      - "9:00PM - 10:00PM"
                  example: ["10:00AM - 12:00PM", "6:00PM - 8:00PM"]
                pay_with_every_delivery:
                  type: boolean
                  example: true
                pay_with_last_delivery:
                  type: boolean
                  example: false
      responses:
        '201':
          description: Schedule created successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  schedule_id:
                    type: string
                    example: SCH45678
                  message:
                    type: string
                    example: "Pickup schedule confirmed successfully."
        '400':
          description: Invalid input data
        '500':
          description: Internal server error

  /customer/schedule/{schedule_id}:
    get:
      summary: Get Scheduled Pickup Details
      description: Retrieves scheduled pickup information for a given schedule ID.
      parameters:
        - name: schedule_id
          in: path
          required: true
          schema:
            type: string
            example: SCH45678
      responses:
        '200':
          description: Schedule details retrieved
          content:
            application/json:
              schema:
                type: object
                properties:
                  customer_id:
                    type: string
                    example: CUST12345
                  schedule_date:
                    type: string
                    format: date
                    example: 2025-05-25
                  selected_slots:
                    type: array
                    items:
                      type: string
                    example: ["10:00AM - 12:00PM"]
                  pay_with_every_delivery:
                    type: boolean
                    example: true
                  pay_with_last_delivery:
                    type: boolean
                    example: false
        '404':
          description: Schedule not found
        '500':
          description: Internal server error
  /customer/order/reschedule:
    put:
      summary: Reschedule an existing laundry pickup
      description: Allows a customer to reschedule the pickup date and slot of an existing laundry order.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - order_id
                - new_pickup_date
                - new_pickup_slot
              properties:
                order_id:
                  type: string
                  example: ORD123456
                new_pickup_date:
                  type: string
                  format: date
                  example: 2025-05-10
                new_pickup_slot:
                  type: string
                  enum:
                    - "08:00 AM - 10:00 AM"
                    - "10:00 AM - 11:00 AM"
                    - "11:00 AM - 12:00 PM"
                  example: "10:00 AM - 11:00 AM"
      responses:
        '200':
          description: Order rescheduled successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Your order has been rescheduled successfully."
                  order_id:
                    type: string
                    example: ORD123456
                  updated_schedule:
                    type: object
                    properties:
                      pickup_date:
                        type: string
                        format: date
                        example: 2025-05-10
                      pickup_slot:
                        type: string
                        example: "10:00 AM - 11:00 AM"
        '400':
          description: Invalid input (e.g. slot not available or past date)
        '404':
          description: Order not found
        '500':
          description: Internal server error
  /customer/feedback:
    post:
      summary: Submit service feedback
      description: Allows customers to rate the quality of the laundry service and optionally leave a comment.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - customer_id
                - rating
              properties:
                customer_id:
                  type: string
                  example: CUST12345
                rating:
                  type: integer
                  minimum: 1
                  maximum: 5
                  example: 4
                comment:
                  type: string
                  example: "Excellent service, very timely and clean laundry."
      responses:
        '201':
          description: Feedback submitted successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Thank you for your feedback!"
        '400':
          description: Invalid input (e.g. missing rating or customer ID)
        '500':
          description: Internal server error
  /customer/order-tracking:
    get:
      summary: Track customer order status
      description: Returns the current status and timeline of a specific order, along with a list of recent orders for a customer.
      parameters:
        - in: query
          name: customer_id
          schema:
            type: string
          required: true
          example: CUST12345
        - in: query
          name: order_id
          schema:
            type: string
          required: false
          example: ORD1002
      responses:
        '200':
          description: Order tracking data retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  tracking:
                    type: object
                    nullable: true
                    description: Detailed tracking status if an order_id is provided
                    properties:
                      order_id:
                        type: string
                        example: ORD1002
                      stages:
                        type: array
                        items:
                          type: object
                          properties:
                            stage:
                              type: string
                              enum:
                                - Placed
                                - Picked Up
                                - In Cleaning
                                - Ready for Delivery
                                - Out for Delivery
                                - Delivered
                              example: Picked Up
                            timestamp:
                              type: string
                              format: date-time
                              example: 2025-05-15T10:00:00Z
                  recent_orders:
                    type: array
                    description: List of recent orders for the customer
                    items:
                      type: object
                      properties:
                        order_id:
                          type: string
                          example: ORD1001
                        status:
                          type: string
                          example: Delivered
                        date:
                          type: string
                          format: date-time
                          example: 2025-05-20T10:30:00Z
        '400':
          description: Missing or invalid customer ID
        '404':
          description: Order not found
        '500':
          description: Internal server error
  /customer/rewards:
    get:
      summary: Get customer reward information
      description: >
        Returns reward points summary, earning methods, redemption options, and points history for the authenticated customer.
      tags:
        - Customer
      responses:
        '200':
          description: Reward data retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  totalPoints:
                    type: integer
                    example: 1350
                  redeemableValue:
                    type: string
                    example: "₹135 (1pt = ₹0.10)"
                  earningMethods:
                    type: array
                    description: How customers can earn reward points
                    items:
                      type: object
                      properties:
                        activity:
                          type: string
                          example: "Book A Service"
                        points:
                          type: string
                          example: "+10 pts per ₹200"
                  redemptionOptions:
                    type: array
                    description: Reward redemption options
                    items:
                      type: object
                      properties:
                        title:
                          type: string
                          example: "₹50 off next order"
                        requiredPoints:
                          type: integer
                          example: 500
                        rewardValue:
                          type: string
                          example: "₹50 off"
                        conditions:
                          type: string
                          example: "Next order"
                        isRedeemable:
                          type: boolean
                          example: true
                  pointsHistory:
                    type: array
                    description: History of points earned and used
                    items:
                      type: object
                      properties:
                        date:
                          type: string
                          format: date
                          example: "2025-05-20"
                        activity:
                          type: string
                          example: "Order ID 123455 (₹1200)"
                        pointsChange:
                          type: string
                          example: "+60 pts"
        '401':
          description: Unauthorized - Login required
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Authentication required"
        '500':
          description: Internal server error
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Unable to fetch reward data at this time."

  /customer/rewards/redeem:
    post:
      summary: Redeem a reward
      description: >
        Allows a customer to redeem points for available rewards.
      tags:
        - Customer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - rewardTitle
              properties:
                rewardTitle:
                  type: string
                  example: "₹100 off above ₹500"
      responses:
        '200':
          description: Reward redeemed successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Reward redeemed. ₹100 off applied to your next order."
        '400':
          description: Not enough points or invalid reward
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Insufficient points for redemption."
        '401':
          description: Unauthorized
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "User must be logged in to redeem rewards."
  /customer/login:
    post:
      summary: User login with phone number or email and OTP
      description: Allows a customer to login by providing either phone number or email along with an OTP code.
      tags:
        - Customer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - otp
              properties:
                phoneNo:
                  type: string
                  description: Phone number for login (optional if email provided)
                  example: "9876543210"
                email:
                  type: string
                  format: email
                  description: Email address for login (optional if phoneNo provided)
                  example: "abc123@gmail.com"
                otp:
                  type: string
                  description: One Time Password sent to phone or email
                  example: "123456"
              oneOf:
                - required: ["phoneNo", "otp"]
                - required: ["email", "otp"]
      responses:
        '200':
          description: Login successful
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: "Login successful."
                  token:
                    type: string
                    description: JWT or session token to authenticate further requests
                    example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        '400':
          description: Bad request - Missing or invalid credentials
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Please provide phone number or email and OTP."
        '401':
          description: Unauthorized - Incorrect OTP or user not found
          content:
            application/json:
              schema:
                type: object
                properties:
                  error:
                    type: string
                    example: "Invalid phone number/email or OTP."


#Service-Provider


 openapi: 3.0.3
info:
  title: Smart Laundry Service - Complete Profile API
  version: 1.0.0
  description: API for service providers to complete or update their business profile.


paths:
  /service-provider/workload:
    get:
      summary: Get workload dashboard data
      tags:
        - Service Provider
      responses:
        '200':
          description: Dashboard data retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  providerInfo:
                    type: object
                    properties:
                      providerId:
                        type: string
                        example: PDT1001
                      phone:
                        type: string
                        example: '+91 9876543218'
                      email:
                        type: string
                        example: johndoe1234@gmail.com
                      status:
                        type: string
                        example: Active
                  incomingJobRequest:
                    type: object
                    properties:
                      orderId:
                        type: string
                        example: '#123455'
                      serviceType:
                        type: string
                        example: Dry Cleaning
                      subServiceType:
                        type: string
                        example: Daily Wear
                      item:
                        type: string
                        example: Shirt
                      quantity:
                        type: integer
                        example: 5
                      requestedDeliveryDate:
                        type: string
                        format: date
                        example: '2025-05-05'
                      requestedDeliveryTimeSlot:
                        type: string
                        example: '2:00PM - 4:00PM'
                  activeJobs:
                    type: array
                    items:
                      type: object
                      properties:
                        orderId:
                          type: string
                          example: '#123452'
                        inProgress:
                          type: string
                          example: 'Collected'
                        ready:
                          type: string
                          example: null
                  dailyCapacity:
                    type: object
                    properties:
                      acceptedToday:
                        type: string
                        example: '5/8'

    post:
      summary: Accept incoming job request
      tags:
        - Service Provider
      requestBody:
        description: Order to be accepted
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                orderId:
                  type: string
                  example: '#123455'
      responses:
        '200':
          description: Job accepted successfully

  /service-provider/job/update-status:
    post:
      summary: Update job status
      tags:
        - Service Provider
      requestBody:
        description: Update status for an in-progress job
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                orderId:
                  type: string
                  example: '#123453'
                newStatus:
                  type: string
                  example: 'Washed'
      responses:
        '200':
          description: Job status updated

  /service-provider/job/mark-complete:
    post:
      summary: Mark job as completed
      tags:
        - Service Provider
      requestBody:
        description: Mark job as ready/completed
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                orderId:
                  type: string
                  example: '#123454'
      responses:
        '200':
          description: Job marked as completed
  /service-provider/payments/summary:
    get:
      summary: Get payment summary for service provider
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
          example: PDT1001
      responses:
        '200':
          description: Payment summary retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  totalEarnings:
                    type: number
                    format: float
                    example: 25000.00
                  pendingPayouts:
                    type: number
                    format: float
                    example: 5000.00
                  bonuses:
                    type: number
                    format: float
                    example: 2000.00
                  deductions:
                    type: number
                    format: float
                    example: 1000.00

  /service-provider/payments/transactions:
    get:
      summary: Get transaction history
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
          example: PDT1001
        - name: filter
          in: query
          required: false
          description: Time filter for transactions
          schema:
            type: string
            enum: [today, this_week, this_month, overall, custom]
            example: this_month
        - name: search
          in: query
          required: false
          description: Search by Payment ID, type or date
          schema:
            type: string
      responses:
        '200':
          description: Transaction history retrieved successfully
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    paymentId:
                      type: string
                      example: PMT1001
                    bookingId:
                      type: string
                      example: BK1001
                    date:
                      type: string
                      format: date
                      example: 2025-05-22
                    amount:
                      type: number
                      format: float
                      example: 70.00
                    type:
                      type: string
                      enum: [PAYOUT, BONUS, DEDUCTION]
                      example: PAYOUT
  /service-provider/myprofile:
    get:
      summary: Get profile information
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
          example: PDT1001
      responses:
        '200':
          description: Profile data retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  name:
                    type: string
                    example: John Doe
                  phone:
                    type: string
                    example: +91 9567839424
                  email:
                    type: string
                    example: johndoe1234@gmail.com
                  providerId:
                    type: string
                    example: PDT1001

    put:
      summary: Update profile information
      tags:
        - Service Provider
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                name:
                  type: string
                phone:
                  type: string
                email:
                  type: string
      responses:
        '200':
          description: Profile updated successfully

  /service-provider/services:
    get:
      summary: Get list of provided services and pricing
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
          example: PDT1001
      responses:
        '200':
          description: Services retrieved
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    serviceType:
                      type: string
                      example: Dry Cleaning
                    subServiceType:
                      type: string
                      example: Daily Wear
                    item:
                      type: string
                      example: T-Shirt
                    price:
                      type: number
                      example: 50

    post:
      summary: Add a new service
      tags:
        - Service Provider
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [serviceType, subServiceType, item, price]
              properties:
                serviceType:
                  type: string
                subServiceType:
                  type: string
                item:
                  type: string
                price:
                  type: number
      responses:
        '201':
          description: Service added successfully

    put:
      summary: Update service pricing
      tags:
        - Service Provider
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                item:
                  type: string
                newPrice:
                  type: number
      responses:
        '200':
          description: Service price updated
  /service-provider/order-history:
    get:
      summary: Get order history for a service provider
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
          example: PDT1001
      responses:
        '200':
          description: Order history fetched successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  providerId:
                    type: string
                    example: PDT1001
                  orders:
                    type: array
                    items:
                      type: object
                      properties:
                        orderId:
                          type: string
                          example: ORD102
                        serviceType:
                          type: string
                          example: Dry Cleaning
                        subServiceType:
                          type: string
                          example: Daily Wear
                        item:
                          type: string
                          example: Shirt
                        quantity:
                          type: integer
                          example: 5
                        status:
                          type: string
                          example: Completed
  /service-provider/schedule:
    get:
      summary: Get schedule and pickup plan
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Schedule retrieved
          content:
            application/json:
              schema:
                type: object
                properties:
                  schedulePlan:
                    type: string
                    example: Mon-Wed-Fri
                  pickupDays:
                    type: string
                    example: Tue-Thu-Sat

    put:
      summary: Update schedule and pickup plan
      tags:
        - Service Provider
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                schedulePlan:
                  type: string
                pickupDays:
                  type: string
      responses:
        '200':
          description: Schedule updated successfully

  /service-provider/availability:
    get:
      summary: Get unavailable (block-off) days
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Block-off days fetched
          content:
            application/json:
              schema:
                type: array
                items:
                  type: string
                  format: date
                  example: 2025-01-20

    post:
      summary: Mark unavailable (block-off) days
      tags:
        - Service Provider
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                blockOffDates:
                  type: array
                  items:
                    type: string
                    format: date
      responses:
        '200':
          description: Block-off days updated
  /service-provider/home:
    get:
      summary: Get service provider home page data
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
          example: PDT1001
      responses:
        '200':
          description: Home page data fetched successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  providerId:
                    type: string
                    example: PDT1001
                  name:
                    type: string
                    example: John Doe
                  phone:
                    type: string
                    example: +91 9567438924
                  email:
                    type: string
                    example: johndoe1234@gmail.com
                  status:
                    type: string
                    enum: [Active, Inactive]
                    example: Active
                  todayOrders:
                    type: integer
                    example: 1
                  upcomingOrders:
                    type: integer
                    example: 7
                  pendingRequests:
                    type: integer
                    example: 2
  /service-provider/feedback:
    get:
      summary: Get feedbacks from customers for a service provider
      tags:
        - Service Provider
      parameters:
        - name: providerId
          in: query
          required: true
          schema:
            type: string
          example: PDT1001
      responses:
        '200':
          description: Feedback list retrieved successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  providerId:
                    type: string
                    example: PDT1001
                  feedbacks:
                    type: array
                    items:
                      type: object
                      properties:
                        customerName:
                          type: string
                          example: John D.
                        rating:
                          type: integer
                          format: int32
                          example: 5
                        description:
                          type: string
                          example: Excellent service—clothes came back spotless, fresh, and perfectly folded.
                        response:
                          type: string
                          example: Thank you for your valuable feedback.
  /service-provider/edit-profile:
    put:
      summary: Update profile of a service provider
      tags:
        - Service Provider
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                providerId:
                  type: string
                  example: PDT1001
                firstName:
                  type: string
                  example: John
                lastName:
                  type: string
                  example: Doe
                phoneNo:
                  type: string
                  example: +91 9876543210
                email:
                  type: string
                  format: email
                  example: john.doe@example.com
                address:
                  type: string
                  example: 123 Laundry Street
                noName:
                  type: string
                  example: No. 5
                areaName:
                  type: string
                  example: Satellite
                city:
                  type: string
                  example: Ahmedabad
                pincode:
                  type: string
                  example: 380015
                state:
                  type: string
                  example: Gujarat
                businessName:
                  type: string
                  example: FreshClean Laundry Services
                gstNumber:
                  type: string
                  example: 24ABCDE1234F1Z5
                businessLicenseNumber:
                  type: string
                  example: LIC123456789
                services:
                  type: array
                  items:
                    type: object
                    properties:
                      serviceType:
                        type: string
                        example: Dry Cleaning
                      subServiceType:
                        type: string
                        example: Daily Wear
                      item:
                        type: string
                        example: Shirt
                      price:
                        type: string
                        example: 50₹
                schedulePlan:
                  type: string
                  example: Weekly
                bankAccountNumber:
                  type: string
                  example: 123456789012
                bankName:
                  type: string
                  example: HDFC Bank
                bankAccountHolderName:
                  type: string
                  example: John Doe
                ifscCode:
                  type: string
                  example: HDFC0001234
                upiId:
                  type: string
                  example: john.doe@hdfcbank
                panCard:
                  type: string
                  format: binary
                aadharCard:
                  type: string
                  format: binary
                businessProof:
                  type: string
                  format: binary
                photo:
                  type: string
                  format: binary
                wantsDeliveryAgent:
                  type: boolean
                  example: true
      responses:
        '200':
          description: Profile updated successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
                    example: Profile updated successfully.
        '400':
          description: Invalid request payload
        '500':
          description: Server error

  /service-provider/profile:
    post:
      summary: Submit or update complete profile
      description: Allows service providers to submit or update their complete business profile including services offered, bank details, pickup schedule, and document uploads.
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                business_name:
                  type: string
                  example: Sparkle Cleaners
                business_license_number:
                  type: string
                  example: LIC123456
                gst_number:
                  type: string
                  example: 27ABCDE1234F1Z5
                services:
                  type: array
                  items:
                    type: object
                    properties:
                      service_type:
                        type: string
                        example: Dry Cleaning
                      sub_service_type:
                        type: string
                        example: Daily Wear
                      item:
                        type: string
                        example: T-Shirt
                      price:
                        type: number
                        example: 50
                pickup_schedule:
                  type: string
                  example: Morning
                schedule_plan:
                  type: string
                  example: Weekly
                slots:
                  type: array
                  items:
                    type: string
                  example: ["9:00 AM - 11:00 AM", "5:00 PM - 7:00 PM"]
                bank_account_holder_name:
                  type: string
                  example: John Doe
                bank_account_number:
                  type: string
                  example: 123456789012
                bank_name:
                  type: string
                  example: HDFC Bank
                ifsc_code:
                  type: string
                  example: HDFC0001234
                upi_id:
                  type: string
                  example: johndoe@upi
                pan_card:
                  type: string
                  format: binary
                aadhar_card:
                  type: string
                  format: binary
                business_utility:
                  type: string
                  format: binary
                photo:
                  type: string
                  format: binary
                wants_delivery_agent:
                  type: boolean
                  example: true
      responses:
        '200':
          description: Profile submitted successfully
        '400':
          description: Invalid or missing data
        '500':
          description: Internal server error





