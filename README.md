# Easy Daily Review

This app is a simple Java project for writing easy daily productivity and well-being reviews. Use the template review or create your own. Check weekly rates, highs and lows, and compare answers from different days.

## Into the app

This is a final project for the Java Developer course of [+PraTI](https://maisprati.alfamidia.com.br/).

For data persistence, I decided to mock the database with text files, so I could learn how to read/write to files and move throw folders in Java. The project requirements did not include data persistency.

### Folder Structure / Software Layers

As you might see in the `src` folder, there is a division between the App layer, the CLI layer and the Database layer (in `mocks`).

The App layer includes entities, its use-cases, and repository contracts. The CLI implements the use-cases and connects to the Text-Database through the CliModule class (an improvisation, because I'm not sure how that should be done here).

The Auth class handles the login/logout, which is very simple and is not supposed to be actually secure.

Once reviews are created, they appear on the user's folder at `src/mocks/database/reviews/:user_id/`.

## Special Thanks

My special thanks goes to Alfamídia and all the tech companies that funds +PraTI. Thank you for the opportunity.
