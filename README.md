# ESS-Master

This is the master repository for all Senate Employee Self Service (ESS) applications.

## Setup

### Software Dependencies

- Java JDK 21
- Git
- IntelliJ IDEA
- Tomcat 11
  - ESS targets Jakarta EE (Servlet 6.1), so Tomcat 10.1+ is required. Tomcat 9 will not run this WAR.
- Postgresql
  - also libs (`postgresql-contrib` on ubuntu)
- Maven
- Node.js 20.12.0
  - Use nvm or Volta for versioning
- Bower + Grunt
  - `sudo npm install -g bower grunt`
  - Needed to build the legacy AngularJS assets; the React frontend builds through webpack via `npm`.

### Database Setup

Enter the postgres terminal:

```shell
sudo su postgres -c psql
```

In the postgres terminal:

```postgresql
CREATE USER essuser WITH LOGIN PASSWORD 'anything_but_this_password';
CREATE DATABASE ess;
GRANT CONNECT, CREATE ON DATABASE ess TO essuser;
```

### Data Directory

Create a data directory for ess and ensure it has proper ownership.
Set ownership to the user that will run the application (likely your personal user but maybe tomcat).

```shell
sudo mkdir -p /data/ess
sudo chown {youruser}:{yourusergroup} /data/ess
```

### Configuration

Create config files from all the example files:

```shell
for f in src/{main,test}/resources/*.example; do cp -- "$f" "${f%.example}"; done
touch src/test/resources/test.app.properties
cp src/main/resources/log4j2.xml src/test/resources/test.log4j2.xml
cp src/main/webapp/grunt.properties.example.json src/main/webapp/grunt.properties.json
```

Portions marked with 🧑‍💻 will require help from a dev or database admin.

There may be additional property config required for certain features.
This just covers essential properties required to run the app + tests.

#### app.properties

- If you used something other than `/data/ess` for the data dir, record that path in `data.dir`
- If you are setting up for dev use, set `auth.enabled = false` to login as any user.
  Also set `auth.master.pass` to your desired master password.
- 🧑‍💻 Obtain ldap url, base, as well as the user dn and password for the developer account.
  Use this to fill out the `ldap` fields in the ldap config section
- Fill out the `db.local` user/pass based on the postgres config from earlier.
- 🧑‍💻 Fill out the `db.remote` user/pass fields with your SFMS credentials. Ask the db admin if not known.
- 🧑‍💻 Fill out `master`, `ts`, and `base.sfms` schema values. Get these from a dev.
- 🧑‍💻 Fill out `mail.smtp` fields, obtain from a dev
- Set `mail.test.address` to your personal email address
- Each app chooses its frontend at runtime via `frontend.myinfo.framework`, `frontend.time.framework`,
  `frontend.supply.framework`, and `frontend.travel.framework`. Valid values are `angularjs` and `react`;
  anything else logs a warning and falls back to `angularjs`. Set the app you're working on to `react`
  if you want the React frontend rather than the legacy JSP/AngularJS one.

#### flyway.conf

Fill out `flyway.user` and `flyway.password` properties with values from the postgres config

#### test.data.properties

🧑‍💻 Fill out the ldap test fields. You can use the same credentials from `app.properties`.
For the `dn` field, just use the `cn` portion of `ldap.user.dn` from `app.properties`.

### Compile and Test

This command does the following:
- builds the Java backend
- builds the frontend (runs `npm ci` in `src/main/webapp`, whose `postinstall` runs the webpack build,
  `bower install`, and `grunt compile`)
- packages the whole application
- runs unit tests
- applies Flyway migrations to your local postgres database, at the `pre-integration-test` phase
- runs integration tests

```shell
mvn verify
```

Because migrations run as part of `verify`, `flyway.conf` must be filled out before this will succeed.
To apply migrations on their own:

```shell
mvn flyway:migrate
```

### Frontend Development

The frontend lives in `src/main/webapp`. Two stacks coexist: the React app in `WEB-INF/app`, and the
legacy AngularJS app in `assets/js/src` with its JSPs in `WEB-INF/view`.

```shell
cd src/main/webapp

npm run dev      # webpack dev server on :3000, proxying /api and /assets to :8080
npm run build    # production React bundle into assets/dist
npm test         # vitest
npm run lint     # eslint over WEB-INF/app
grunt compile    # legacy AngularJS/LESS assets only
```

`npm run dev` expects the Java backend to already be running on `localhost:8080` (see the run
configuration below), since it proxies API requests there.

### Intellij Run Configuration

Create a run configuration in IntelliJ that can be used launch ESS on Tomcat with debugging capability.
Recommended for development. 

1. In the IntelliJ top menu, go to Run -> Edit Configurations.
2. Above the list of configurations on the left, click "+" to create a new config.
3. Scroll down until you find Tomcat Server. Select the Local option.
4. In the Server tab, check the Application Server setting to ensure it's referencing your tomcat install.
   - If not, click "Configure" and enter your install dir as Tomcat Home. Tomcat 11 usually isn't
     available as a distro package, so this is wherever you unpacked the Apache tarball (e.g. `/opt/tomcat`).
5. Ensure that the JRE is set to Java 21.
6. In the Deployment tab, click "+" to add a new deployment artifact. Select `ess:war exploded`
7. Scroll down to Application Context and make sure that is set to "/"
8. Click "OK" to save the configuration
9. Open the Services tab. (Alt + 8 or look for a "play" symbol in a hexagon)
10. Select ESS under Tomcat Server and click the Run button (play symbol icon).
    - This should start the ESS server on localhost:8080.
    - Rerun using the Debug button for debug capability.

