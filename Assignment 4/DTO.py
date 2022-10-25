class Hat:
    def __init__(self, id, topping, supplier, quantity):
        self.id = id
        self.topping = topping
        self.supplier = supplier
        self.quantity = quantity


class Supplier:
    def __init__(self, id, name):
        self.id = id
        self.name = name


class Order:
    def __init__(self, id, location, hat):
        self.id = id
        self.location = location
        self.hat = hat


class Hat_more_details:
    def __init__(self, id, quantity, supplier_id, supplier_name):
        self.id = id
        self.quantity = quantity
        self.supplier_id = supplier_id
        self.supplier_name = supplier_name
