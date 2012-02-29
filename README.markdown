
# Barge Tracker #

**This Android application was developed as part of the course project for "Software Development for portable devices".**

**Description:** The app has 3 activities.

* **First** one displays all the Barges along with their status color on a MapView. Google Maps API was used for the map.
* The **second** activity is launched if the user taps on any Barge in the 1st activity. This second activity displays on half of the screen the details related to the tapped barge. Here the details include Originating port, Destination port, Status, Time of previous Loading and Unloading. The other half of the acitivity displays this Barge on a MapView.
* The **third** activity is launched by using the "Barge List" option from the options menu. This displays all the Barges in a list along with their status. Also, if a barge has the same status for 
more than a specified time, then the list highlights the entry for that barge.
* The app basically fetches coordinates alongwith other relevant detail pertaining to each Barge from a server. The server hosts this information as a *JSON file*.
* After fetching and pasring the JSON, the app plots this onto a 'MapView' alongwith their stauss colors.
* A major part of this project was to implement *Cloud to Device Messaging (C2DM)* for user notifications. The C2DM service was used to notify the user if any barge hasnt had its status changed for more than the specified time, or if the user desires to recieve notifications for every status change for particular barges.
