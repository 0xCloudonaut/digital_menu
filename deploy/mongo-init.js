const dbName = process.env.MONGO_INITDB_DATABASE || 'restaurant_db';
const appUser = process.env.MONGO_APP_USERNAME || 'menu_app';
const appPassword = process.env.MONGO_APP_PASSWORD || 'menu_app_password';

db = db.getSiblingDB(dbName);

if (!db.getUser(appUser)) {
  db.createUser({
    user: appUser,
    pwd: appPassword,
    roles: [{ role: 'readWrite', db: dbName }]
  });
}
