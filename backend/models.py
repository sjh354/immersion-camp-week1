from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class User(db.Model):
    __tablename__ = "users"
    google_sub = db.Column(db.String, primary_key=True)
    email = db.Column(db.String)
    name = db.Column(db.String)
    created_at = db.Column(db.Integer, nullable=False)

class Menu(db.Model):
    __tablename__ = "menus"
    id = db.Column(db.Integer, primary_key=True)  # test.json의 id 그대로
    name = db.Column(db.String)
    price = db.Column(db.Integer)
    img = db.Column(db.String)
    category = db.Column(db.String, index=True)
    store = db.Column(db.String)
    latitude = db.Column(db.Float)
    longitude = db.Column(db.Float)
    starpoint = db.Column(db.Float)
    deeplink = db.Column(db.String)

class Favorite(db.Model):
    __tablename__ = "favorites"
    user_google_sub = db.Column(
        db.String,
        db.ForeignKey("users.google_sub", ondelete="CASCADE"),
        primary_key=True,
    )
    menu_id = db.Column(
        db.Integer,
        db.ForeignKey("menus.id", ondelete="CASCADE"),
        primary_key=True,
    )
