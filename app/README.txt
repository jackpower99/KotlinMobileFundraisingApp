The film collective is an app for users to share information on their favourite movies.
It uses firebase authentication with email and password and google sign-in and stores movie information on a firebase realtime database.
Users can create update and delete their own movies as well as
View there's and all the other users on the apps movies.

The app has two activities. Login -which handles all the login functionality in terms of firebase register and login with email and password as well as with google sign-in using FirebaseAuth and GoogleAuth.
Once the user is validated they are directed to the contribute page of the app of the movie fragment. The movie fragment is one of four fragments all managed by the home activity. They are all accessible from the nav drawer.
These include - ViewFragment: Where the user can view their movies.
AllMoviesFragment: Where the user can view all  movies on the app.
UserFavouritesFragment: Where the user can view movies they've favourited.

Users can swipe to delete movies which is done using ItemTouchHelper as well as a AlertDialog to confirm or cancel.

Users can also change their profile images I implemented this using the same helper methods we done in the donations labs.

For my Git approach I committed onto one main branch after adding each additional step of functionality as well as added a comment on what was achieved since last commit.

Personal Statement:
I found it very hard to watch the lectures on their scheduled time slot a well as do the labs and ended up cramming a lot of work into the last two/three weeks of the semester with alot of other modules work.
If we were out in the college and not at home i think i could of got a much better result in the module overall as my first assignment was quite good. Hopefully we'll be back in September!
The module was thought very well and dave was extremely helpful whenever we had any questions or concerns.