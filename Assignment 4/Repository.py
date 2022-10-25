# The Repository

import atexit
import os
import sqlite3
import sys

from DAO import Hats, Suppliers, Orders


def create_repository():
    repo = Repository()
    atexit.register(repo.close)
    return repo




class Repository:
    def __init__(self):
        self._conn = sqlite3.connect(sys.argv[4])
        self.hats = Hats(self._conn)
        self.suppliers = Suppliers(self._conn)
        self.orders = Orders(self._conn)


    def close(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        cursor = self._conn.cursor()
        cursor.executescript("""
        CREATE TABLE IF NOT EXISTS Hats (
            id INTEGER,
            topping TEXT NOT NULL,
            supplier INTEGER,
            quantity INTEGER NOT NULL,
            FOREIGN KEY(supplier) REFERENCES Suppliers(id),
            PRIMARY KEY(id)
        );

        CREATE TABLE IF NOT EXISTS Suppliers (
            id INTEGER,
            name TEXT NOT NULL,
            PRIMARY KEY(id)
        );
        
        CREATE TABLE IF NOT EXISTS Orders (
            id INTEGER,
            location TEXT NOT NULL,
            hat INTEGER,
            FOREIGN KEY(hat) REFERENCES Hats(id)
            PRIMARY KEY(id)
        );
        """)


