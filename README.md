# 📱 Convy — Android Lending & Renting App

An Android application built for the Programming Techniques course at KU Leuven.  
**Convy** allows users to lend, browse, and rent items from others via an intuitive, category-based mobile platform. 
It features real-time item listings, detailed offer/rental history, and a smooth user experience using fragments and RecyclerViews.

---

## 🚀 Features

- **User Authentication**  
  Secure login and registration with input validation and error handling.

- **Lending Interface**  
  Add items to lend with categories, titles, descriptions, prices, and availability.

- **Renting System**  
  Browse available items, filter by category, and view detailed offer pages.

- **Rental History**  
  Tracks both items (rented from others) and (lent to others) in organized fragments.

- **Profile Management**  
  Edit account info, switch between tabs, and manage personal data.

- **Clean Navigation**  
  Bottom navigation with fragment swapping — no full reloads, no jank.

- **Backend Communication**  
  All data operations (CRUD) handled via **Volley** HTTP requests to a backend server.

---

## 🧱 Tech Stack

| Layer        | Technology                  |
|--------------|-----------------------------|
| Language     | Java                        |
| UI           | Android XML, Fragments      |
| Networking   | Volley HTTP Library         |
| Architecture | MVVM-lite (Fragment-based)  |
| IDE          | Android Studio              |
| Compatibility| API 28+                     |

## 📸  Screenshots of the App:

<table>
  <tr>
    <td><strong>Sign Up Page</strong></td>
    <td><strong>Login Page</strong></td
    <td><strong>Loading Screen</strong></td>
  </tr>
  <tr>
    <td><img src="screenshots/signup_page.jpg" width="300"/></td>
    <td><img src="screenshots/login_page.jpg" width="300"/></td>
    <td><img src="screenshots/loading_page.jpg" width="300"/></td>
  </tr>
  <tr>
    <td><strong>Renting Page</strong></td>
    <td><strong>Lending Page</strong></td>
    <td><strong>Account Page</strong></td>
  </tr>
  <tr>
    <td><img src="screenshots/rent_page.jpg" width="300"/></td>
    <td><img src="screenshots/lending_page.jpg" width="300"/></td>
    <td><img src="screenshots/account_page.jpg" width="300"/></td>
  </tr>
  <tr>
    <td><strong>My Items Fragment</strong></td>
    <td><strong>Renting Fragment</strong></td>
    <td><strong>Rented Fragment</strong></td>
  </tr>
  <tr>
    <td><img src="screenshots/my_items_fragment.jpg" width="300"/></td>
    <td><img src="screenshots/renting_fragment.jpg" width="300"/></td>
    <td><img src="screenshots/rented_fragment.jpg" width="300"/></td>
  </tr>
</table>


## 🛠️ Running the App Locally

To run this app from source, follow these steps:

1. **Clone the repository**
   ```bash
   git clone https://github.com/Cloudware727/Convy-Android-App.git
   cd Convy-Android-App
2. **Open in Android Studio**
   
  - Open the project folder (ap211 or root depending on your structure)
   
  - Let Gradle sync and resolve dependencies
   
3.**Set up emulator or connect a physical device**

  - API level 28+ recommended
  
  - Enable developer mode if using a phone

4.**Run the app**

  - Select the target device
  
  - Click the green play button or use Shift + F10

## 🧠 Project Motivation

The goal of Convy was to build a fully functional Android app that replicates core features of modern marketplace platforms, such as item listings, user-based interactions, and persistent data. The app was designed to reinforce Android development fundamentals while applying backend integration and user interface best practices.

---

## 📚 How to Use

1. **Sign Up or Login**
  - Create a new account or log in with existing credentials.

2. **Lend an Item**
   - Navigate to the “Lend” tab, fill in item details, and submit.

3. **Rent an Item**
   - Browse items under the “Rent” tab, view item details, and request to rent.

4. **Track History**
   - Use the history tabs to monitor items you've rented or lent.

5. **Edit Profile**
   - Manage your personal info via the “Account” tab.

---

## 🧪 Testing & Debugging

- All screens were manually tested on Android Emulator (API 30) and a real device.
- Edge cases like empty forms, invalid login, and missing images were handled.
- Volley error handling includes toast messages and fallbacks on failure.

---

## 🔮 Future Improvements

- Firebase integration for auth and storage
- Admin panel for managing reported items
- Push notifications for status changes
- In-app messaging system between users
- Dark mode and enhanced animations


