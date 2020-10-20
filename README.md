# SwiftStaff Serer

Kotlin Application to handle backend of App

### Testing locally

Download and install mongodb.

Create a folder /data/db and make sure to give permissions so mongod can access it.
(Eg if you start mongod from terminal give your user rwx permissions on linux)

```
sudo chmod 774 /data/db
```

Start mongo daemond and then connect to mongo database

```
mongod
mongo
```

The second line output should be the local mongo server address, eg:

```
connecting to: mongodb://127.0.0.1:27017
```

Make sure in MongoDatabase.kt connectionString matches this address.

Start running main (Kotlin Ktor server)

In intellij can send test requests to ktor by creating a .http folder. (Or use a tool such as postman)
eg:

```
POST http://localhost:8080/api/v1/signup/restaurant
Content-Type: application/json

{
  "email": "test@test.com",
  "password": "pass",
  "name": "the jolly rodger",
  "address": "69, 69th street",
  "phone": "01234567891",
  "restaurantEmailAddress": "thejollyrodger@gmail.com"
}
```

You can query database with basic commands such as:

```
db.jobs.find( {} )
```

Which shows all jobs in jobs database.

You need to add a user:

https://docs.mongodb.com/manual/reference/method/db.createUser/

Make sure mongo daemon is running

```
mongod
mongo
use admin
db.createUser(
      {
          user: "mongoadmin",
          pwd: "mongoadmin",
          roles: [ "root" ]
      }
  )
```

To see current tables (eg: User table, Worker table):

```
show tables
```

And see whats in them with:

```
db.User.find({})
```

To log in to the real database, first ssh in (ip on trello, ssh keys should be set ip),  and then run:

```
mongo --username <username> --password <password> --authenticationDatabase "admin"
```

### Licence

GNU General Public License (See LICENCE)






