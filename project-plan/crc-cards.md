## CRC Cards
----------

| **User**              |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Has a profile<br><br>**Role as Entrant:** <br> - Sign up for events <br> - View event details <br> - Confirm participation <br> <br> **Role as Organizer:** <br> - Create and publish events <br> - Upload event images <br> - Initiate lottery <br> <br> **Role as Admin:** <br> - Manage app data - Will also allow users to put in their names email and profile photo| - Collaborates with Event <br>  - Collaborates with Facility|


| **Event**             |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store details about a specific event <br> - Allow entrants to view details and register.  | - Collaborates with User <br> - Collaborates with Facility   |


| **Facility**          |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store the details of a Facility and allow organizers to host events at the facility.  | - Collaborates with User <br> |

| **User ArrayAdapter** |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view the lists of our users   | - Collaborates with User <br>  |


| **Event ArrayAdapter**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view the lists of our events  | - Collaborates with Events <br>  |

| **Images ArrayAdapter**|                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Allows us to view all the images on our app  |  |

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
