# I-TTrack

It is a mobile app developed in Android Studio for tracking products in the distribution

First it scans a set of barcodes using the camera with GmsBarcodeScanning, then it finds the position of the user and shows it on a map

All the data are finally sent to a Firestore database (this functionality is commented since no database is not connected)

The app is organized in fragments managed by a single activity and a navigation graph. All the information are managed inside the app through a ViewModel
