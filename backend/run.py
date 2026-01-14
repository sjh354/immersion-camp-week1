from flask import Flask, request, jsonify, Response, g
import numpy as np
import jwt, time
from functools import wraps
from sqlalchemy.exc import IntegrityError

from google.oauth2 import id_token
from google.auth.transport import requests as grequests

from models import db, User, Menu, Favorite

app = Flask(__name__)

GOOGLE_WEB_CLIENT_ID = "1038876443378-f22m09vmivhhmphqiqqee9v0d1lp1qth.apps.googleusercontent.com"
APP_JWT_SECRET = "BiwqNhYJCoILMEEbfrExtgENZlUwgXwVQrZDzoPbL8eWNDqahAJwaQ6iN4Q3Zqkk"
APP_JWT_EXP_SEC = 60 * 60 * 24 * 7

# ✅ 환경변수로 바꾸고 싶으면 여기만 수정
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///app.db"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

db.init_app(app)

format_price = lambda x: f"{x:,}원"


@app.get("/")
def hello_world():
    return "Hello, World!"


# --------------------
# JWT
# --------------------
def issue_app_token(google_sub: str):
    now = int(time.time())
    payload = {"sub": google_sub, "iat": now, "exp": now + APP_JWT_EXP_SEC}
    return jwt.encode(payload, APP_JWT_SECRET, algorithm="HS256")


def get_google_sub_from_app_token() -> str:
    auth = request.headers.get("Authorization", "")
    if not auth.startswith("Bearer "):
        raise ValueError("missing bearer token")
    token = auth[len("Bearer "):].strip()
    if not token:
        raise ValueError("empty token")

    try:
        payload = jwt.decode(token, APP_JWT_SECRET, algorithms=["HS256"])
        google_sub = payload.get("sub")
        if not google_sub:
            raise ValueError("missing sub")
        return google_sub
    except jwt.ExpiredSignatureError:
        raise ValueError("token expired")
    except jwt.InvalidTokenError:
        raise ValueError("invalid token")


def require_auth(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        try:
            g.google_sub = get_google_sub_from_app_token()
        except ValueError as e:
            return jsonify({"error": str(e)}), 401
        return fn(*args, **kwargs)
    return wrapper


# --------------------
# DB helpers
# --------------------
def upsert_user(google_sub: str, email: str, name: str):
    u = User.query.get(google_sub)
    if u:
        u.email = email
        u.name = name
    else:
        u = User(
            google_sub=google_sub,
            email=email,
            name=name,
            created_at=int(time.time()),
        )
        db.session.add(u)
    db.session.commit()


def get_favorite_ids(google_sub: str) -> set[int]:
    rows = (
        db.session.query(Favorite.menu_id)
        .filter(Favorite.user_google_sub == google_sub)
        .all()
    )
    return set(r[0] for r in rows)


# --------------------
# Google Auth
# --------------------
@app.post("/api/auth/google")
def auth_google():
    body = request.get_json() or {}
    token = body.get("id_token")
    if not token:
        return jsonify({"error": "missing id_token"}), 400

    try:
        info = id_token.verify_oauth2_token(
            token,
            grequests.Request(),
            GOOGLE_WEB_CLIENT_ID
        )
        google_sub = info.get("sub")
        email = info.get("email")
        name = info.get("name")
        if not google_sub:
            return jsonify({"error": "no sub"}), 401
    except Exception:
        return jsonify({"error": "invalid token"}), 401

    upsert_user(google_sub, email, name)

    app_token = issue_app_token(google_sub)
    return jsonify({"app_token": app_token})


# --------------------
# Categories
# --------------------
@app.get("/api/categories")
@require_auth
def categories():
    categories = [c[0] for c in db.session.query(Menu.category).distinct().all()]
    if "기타" in categories:
        categories = [c for c in categories if c != "기타"] + ["기타"]
    return jsonify({"data": categories})


# --------------------
# Distance sort (same logic)
# --------------------
def haversine(lat1, lon1, lat2, lon2):
    R = 6371
    lat1 = np.radians(lat1); lon1 = np.radians(lon1)
    lat2 = np.radians(lat2); lon2 = np.radians(lon2)
    dlat = lat2 - lat1
    dlon = lon2 - lon1
    a = np.sin(dlat / 2)**2 + np.cos(lat1) * np.cos(lat2) * np.sin(dlon / 2)**2
    c = 2 * np.arctan2(np.sqrt(a), np.sqrt(1 - a))
    return R * c


# --------------------
# Menus
# --------------------
@app.route("/api/menus/<category>", methods=["GET", "POST"])
@require_auth
def menus(category):
    includes_no_image = request.args.get("noimage", False, type=bool)

    q = Menu.query.filter(Menu.category == category)
    if not includes_no_image:
        q = q.filter(Menu.img.isnot(None)).filter(Menu.img != "no-image")

    rows = q.all()

    fav_ids = get_favorite_ids(g.google_sub)

    req_data = request.get_json(silent=True) or {}
    sort_mode = req_data.get("sort") if request.method == "POST" else None
    user_lat = req_data.get("latitude")
    user_lon = req_data.get("longitude")

    data = []
    for m in rows:
        item = {
            "id": m.id,
            "name": m.name,
            "price": m.price,
            "img": m.img,
            "category": m.category,
            "store": m.store,
            "latitude": m.latitude,
            "longitude": m.longitude,
            "starpoint": m.starpoint,
            "deeplink": m.deeplink,
            "isFavorite": (m.id in fav_ids),
        }
        if sort_mode == "gps" and user_lat is not None and user_lon is not None:
            item["distance"] = float(haversine(user_lat, user_lon, m.latitude, m.longitude))
        data.append(item)

    # 즐겨찾기 먼저
    def fav_key(x):  # fav True가 먼저 오도록
        return 0 if x["isFavorite"] else 1

    if request.method == "GET":
        data.sort(key=lambda x: (fav_key(x), -(x["starpoint"] or 0)))
    else:
        if sort_mode == "gps":
            data.sort(key=lambda x: (fav_key(x), x.get("distance", 1e18)))
        elif sort_mode == "price_asc":
            data.sort(key=lambda x: (fav_key(x), x["price"] if x["price"] is not None else 10**18))
        elif sort_mode == "price_desc":
            data.sort(key=lambda x: (fav_key(x), -(x["price"] or 0)))
        elif sort_mode == "starpoint_desc":
            data.sort(key=lambda x: (fav_key(x), -(x["starpoint"] or 0)))
        else:
            return Response("정렬 기준이 이상함", mimetype="application/json", status=400)

    for x in data:
        x["price"] = format_price(x["price"] or 0)
        x.pop("distance", None)

    return jsonify({"data": data})


# --------------------
# Favorites toggle
# --------------------
@app.post("/api/menus/favorite")
@require_auth
def set_favorite():
    gsub = g.google_sub
    req_data = request.get_json() or {}
    menu_id = req_data.get("id")
    changeto = req_data.get("changeto")

    if menu_id is None or changeto is None:
        return Response("요청 값 부족", mimetype="application/json", status=400)

    menu_id = int(menu_id)

    if changeto is True:
        db.session.add(Favorite(user_google_sub=gsub, menu_id=menu_id))
        try:
            db.session.commit()
            return Response("수정 성공!", mimetype="application/json", status=200)
        except IntegrityError:
            db.session.rollback()
            return Response("이미 있는데?!", mimetype="application/json", status=409)
    else:
        fav = Favorite.query.get((gsub, menu_id))
        if not fav:
            return Response("이미 없는데?!!", mimetype="application/json", status=409)
        db.session.delete(fav)
        db.session.commit()
        return Response("수정 성공!", mimetype="application/json", status=200)


if __name__ == "__main__":
    with app.app_context():
        db.create_all()
    app.run("0.0.0.0", port=80, debug=False)
