from DTO import Hat, Order, Supplier, Hat_more_details


class Hats:
    def __init__(self, con):
        self.conn = con

    def insert(self, hat):
        self.conn.execute("""INSERT OR REPLACE INTO hats
                        VALUES(?,?,?,?)""", [hat.id, hat.topping, hat.supplier, hat.quantity])

    def update_quantity(self, hat):
        if hat.quantity != 1:
            self.conn.execute("""
                            UPDATE hats SET quantity=(?) WHERE id=(?)
                            """, [hat.quantity - 1, hat.id])
        else:
            self.conn.execute("""
                        DELETE FROM hats WHERE id=(?)
                        """, [hat.id])

    def find(self, hat_id):
        c = self.conn.cursor()
        c.execute(""" SELECT * FROM hats WHERE id=?""", [hat_id])
        return Hat(*c.fetchone())


class Orders:
    def __init__(self, con):
        self.conn = con

    def insert(self, order):

        self.conn.execute("""INSERT OR REPLACE INTO orders (id, location, hat) 
                        VALUES(?,?,?)""", [order.id, str(order.location), int(order.hat)])

    # returns the supplier with the min ID which has this topping
    def find_supplier(self, _topping):
        c = self.conn.cursor()
        c.execute("""
                SELECT MIN(supplier) FROM hats WHERE topping=(?) """, [_topping])
        return c.fetchone()


class Suppliers:
    def __init__(self, con):
        self.conn = con

    def insert(self, supplier):
        self.conn.execute("""INSERT OR REPLACE INTO suppliers
                        VALUES('{}','{}')""".format(supplier.id, supplier.name))

    def find_supplier(self, supplier_id):
        c = self.conn.cursor()
        c.execute("""
                SELECT name FROM Suppliers WHERE id=(?) """, [str(supplier_id)])
        return c.fetchone()

    def find_supplier1(self, topping):
        c = self.conn.cursor()
        all = c.execute("""
        SELECT hats.id, hats.quantity,suppliers.id, suppliers.name
        FROM hats 
        JOIN suppliers ON suppliers.id = hats.supplier
        WHERE topping = ?
        """, [topping]).fetchall()
        return [Hat_more_details(*row) for row in all]
