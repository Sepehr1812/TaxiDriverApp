# Taxi Driver App
A simple Android app that simulates ridesharing offers for drivers and lets them decide whether to accept the offers or not (similar to Uber's driver app).

## Pages
In this app, we have two pages as described below:
1. The first page is a map (powered by Google Map API) that shows the user's location.
2. The second page is an **offer** page. This page launches whenever an FCM push notification is received. This push notification contains offer data from a passenger user.

### Offer Page
In the **Offer Page**, there is a map that shows some points as terminals (origin and destinations) of the offer. The map zoom level is bounded to all the terminals.

The app shows terminals in a scrollable view in the middle of the page. Each terminal is clickable to move and zoom the map camera to the terminal location.

At the bottom of the page, a custom button starts filling progress from the left side as a timer indicator, which is the offer timeout (10 seconds). The user should hold the button for 3 seconds to call the button's functionality (accepting the offer).

As the user holds the button, a dark circle starts to fill it from the center until it covers the whole view (which is captured around 1.5 seconds of holding the button). If the user holds the button for 3 seconds, the offer is accepted by the driver, and the app will be closed.

The application can launch either in the foreground or background.

## Technologies and Tools
In this project, I benefited from several Android concepts and tools, including:
* Google Map API
* Location Services
* Firebase Cloud Messaging (FCM)
* FCM Rest API
