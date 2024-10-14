## CRC Cards
----------

| **User**              |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Has a profile<br><br>**Role as Entrant:** <br> - Sign up for events <br> - View event details <br> - Confirm participation <br> <br> **Role as Organizer:** <br> - Create and publish events <br> - Upload event images <br> - Initiate lottery <br> <br> **Role as Admin:** <br> - Manage app data | - Collaborates with Event <br> - Collaborates with Firebase <br>  |


| **Event**             |                   | 
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store details about a specific event <br> - Allow entrants to view details and register.  | - Collaborates with User <br> - Collaborates with Firebase   |

| Firebase             |                   |
|-----------------------|-------------------|
| **Responsibilities**  | **Collaborators** |
| - Store and manage event data, roles and lottery results <br> - Provide real-time updates for registration statuses and event changes.  | - Collaborates with User   |
