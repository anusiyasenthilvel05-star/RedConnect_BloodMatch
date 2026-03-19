❤️ RedConnect – Blood Donation & Request App

📌 Overview

RedConnect is an Android application designed to connect blood donors and recipients quickly during emergencies.
The app allows users to request blood, find nearby donors, and communicate via SMS in real-time.

---

🚀 Features

🩸 Blood Request System

- Users can submit a blood request with:
  - Patient Name
  - Blood Group
  - Required Units
  - City & District
- Automatically finds matching donors

📍 Location-Based Matching

- Uses device location (Latitude & Longitude)
- Displays nearest donors first using distance calculation

📲 SMS Notification

- Sends emergency SMS to matching donors
- Message includes:
  - Patient Name
  - Blood Group
  - Units Required
  - Location

🔔 Donor Response System

- Donors receive SMS and can respond:
  - YES → Accept request
  - NO → Reject request

✅ Willing / Not Willing Actions

- Willing Button
  - Sends reminder SMS up to 5 times
- Not Willing Button
  - Removes donor from list or fades card

🧾 Donor History

- Stores donation history in Firebase
- Displays past donations when clicking history icon

🔐 Email Verification

- Firebase Authentication with email verification
- Only verified users can access app features

---

🛠️ Technologies Used

- Java (Android)
- Firebase Authentication
- Cloud Firestore
- Firebase Realtime Database
- RecyclerView
- SMS Manager API
- Google Maps Location

---

🏗️ Project Structure

com.example.redconnectlogo
│
├── Activities
│   ├── LoginActivity
│   ├── RegisterActivity
│   ├── BloodRequestFormActivity
│   ├── MatchingListActivity
│   ├── DonorHistoryActivity
│
├── Adapters
│   └── DonorAdapter
│
├── Models
│   └── Donor.java
│
├── Firebase
│   ├── Firestore (Donors, Requests, History)
│   └── Realtime Database (Live Location)

---

🔥 Firebase Database Structure

📂 Firestore Collections

1. Donors

name
phone
bloodGroup
city
district
latitude
longitude

2. Requests

patientName
bloodGroup
phone
units
city
district
status
timestamp

3. Donation History

donorId
patientName
bloodGroup
date

---

📲 App Flow

User Registration
      ↓
Email Verification
      ↓
Login
      ↓
Submit Blood Request
      ↓
Matching Donor List Displayed
      ↓
Send Request via SMS
      ↓
Donor Responds (YES / NO)
      ↓
Track & Store Donation History

---

⚙️ Setup Instructions

1. Clone the repository:

git clone https://github.com/your-username/redconnect.git

2. Open in Android Studio

3. Connect Firebase:
   
   - Add "google-services.json"
   - Enable Authentication (Email/Password)
   - Enable Firestore Database

4. Add SMS Permission in "AndroidManifest.xml":

<uses-permission android:name="android.permission.SEND_SMS"/>

5. Run the app on a real device (SMS requires SIM)

---

⚠️ Known Issues & Fixes

❌ Null Data in SMS / Firebase

✔ Fixed by passing data via Intent between activities

❌ Same Location for All Donors

✔ Fixed by using individual donor latitude & longitude

❌ Email Verification Error

✔ Fixed by enabling Firebase Authentication and checking quota

---

📌 Future Enhancements

- Push notifications instead of SMS
- Donor availability toggle
- Real-time donor tracking on map
- Admin dashboard
- Blood bank integration

---

👩‍💻 Developed By

Anusiya S , Pavithra R, Manjusri K, Keerthana S

---

💡 Note

This project is built for educational  purposes to demonstrate real-time emergency response using mobile technology.

---
