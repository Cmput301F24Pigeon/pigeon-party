## CRC Cards
----------

| **User**              |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Has a profile<br><br>**Role as Entrant:** <br> - Sign up for events <br> - View event details <br> - Confirm participation <br> <br> **Role as Organizer:** <br> - Create and publish events <br> - Upload event images <br> - Initiate lottery <br> <br> **Role as Admin:** <br> - Manage app data - Will also allow users to put in their names email and profile photo| - Collaborates with Event <br>  - Collaborates with Facility|


| **Event**             |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store details about a specific event <br> - Allow entrants to view details and register. Gives us a qr code for the event | - Collaborates with User <br> - Collaborates with Facility   |


| **Facility**          |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store the details of a Facility and allow organizers to host events at the facility.  | - Collaborates with User <br> |

| **UserArrayAdapter** |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view the lists of our users   | - Collaborates with User <br>  |


| **com.example.pigeon_party_app.EventArrayAdapter**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view the lists of our events  | - Collaborates with Events <br>  |

| **ImagesArrayAdapter**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view all the images on our app  |  |

| **HashDataArrayAdapter**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view the lists of our hashdate taken from our qr codes  | - Collaborates with Events <br>  |

| **RemoveEventFragment**|                   | 
|------------------------|-------------------|
| **Responsibilities**   | **Collaborators** |
| - Allows us to remove events as an admin  | -Collaborates with Events<br>  |

| **RemoveImagesFragment**|                   | 
|------------------------|-------------------|
| **Responsibilities**   | **Collaborators** |
| - Allows us to remove images as an admin  | -Collaborates with Events<br>  |

| **RemoveProfileFragment**|                   | 
|------------------------|-------------------|
| **Responsibilities**   | **Collaborators** |
| - Allows us to remove profiles as an admin  | -Collaborates with Events<br>  |

| **RemoveHashDataFragment**|                   | 
|---------------------------|-------------------|
| **Responsibilities**      | **Collaborators** |
| - Allows us to remove hashdata from the qr code as an admin  | -Collaborates with Events<br>  |

