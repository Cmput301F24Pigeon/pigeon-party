## CRC Cards
----------

| **User**              |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Has a profile<br><br>**Role as Entrant:** <br> - Sign up for events <br> - View event details <br> - Confirm participation <br> <br> **Role as Organizer:** <br> - Create and publish events <br> - Upload event images <br> - Initiate lottery <br> <br>**Role as Admin:** <br> - Browse events, profile and images <br> - Remove images, profiles and events <br> |


| **Event**             |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store details about a specific event <br> - Allow Entrants to view details and register <br> - Gives us a qr code for the event <br> - Stores list of entrants to send notifications <br> - Runs a lottery to determine entrants in the event | - Collaborates with User <br> - Collaborates with Facility <br> - Collaborates with NotificationHelper  |


| **Facility**          |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store the details of a Facility and allow organizers to host events at the facility.  | - Collaborates with User <br> |

| **UserArrayAdapter** |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view the lists of our users   | - Collaborates with User <br>  |


| **EventArrayAdapter**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view the lists of our events  | - Collaborates with Events <br> - Collaborates with User  |

| **NotificationHelper**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - crates and sends notifications  | - Collaborates with Events <br> - Collaborates with User  |

| **ImagesArrayAdapter**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view all the images on our app  |  |

| **AdminEventFragment**|                   | 
|------------------------|-------------------|
| **Responsibilities**   | **Collaborators** |
| - Allows us to remove events as an admin  | -Collaborates with Events<br>  |

| **AdminImagesFragment**|                   | 
|------------------------|-------------------|
| **Responsibilities**   | **Collaborators** |
| - Allows us to remove images as an admin  | -Collaborates with Events<br>  |

| **AdminProfileFragment**|                   | 
|------------------------|-------------------|
| **Responsibilities**   | **Collaborators** |
| - Allows us to remove profiles as an admin  | -Collaborates with Events<br>  |

| **Validator**|                   | 
|---------------------------|-------------------|
| **Responsibilities**      | **Collaborators** |
| - Validates input from user | -Collaborates with User and Event<br>  |
