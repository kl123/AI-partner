import pymysql

HOST = "rm-bp1bvg3o0jrb012ep5o.mysql.rds.aliyuncs.com"
PORT = 3306
DB = "db_aipartner"
USER = "root"
PASSWORD = "@117921633Yy"

def connect() -> pymysql.connections.Connection:
    return pymysql.connect(host=HOST, port=PORT, user=USER, password=PASSWORD, database=DB, charset="utf8mb4", autocommit=True)

def get_db():
    conn = connect()
    try:
        yield conn
    finally:
        try:
            conn.close()
        except Exception:
            pass