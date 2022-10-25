import os
from Repository import *
import sys

from Repository import *
from DTO import Hat, Supplier, Order

repo = create_repository()
repo.create_tables()

config = sys.argv[1]
with open(config) as f:
    lines = f.readlines()

# reads the first line
first_line = lines[0].split(',')
num_of_hats = int(first_line[0])
num_of_suppliers = int(first_line[1])

# starts to parse the configFile
for i in range(1, num_of_hats + 1):
    split_line = lines[i].split(',')
    # creates the Hat
    hati = Hat(int(split_line[0]), split_line[1], int(split_line[2]), int(split_line[3][:-1]))
    repo.hats.insert(hati)

for i in range(num_of_hats + 1, len(lines)):
    split_line = lines[i].split(',')
    if i != len(lines) - 1:
        # creates the supplier
        my_supplier = Supplier(int(split_line[0]), split_line[1][:-1])
    else:
        my_supplier = Supplier(int(split_line[0]), split_line[1])
    repo.suppliers.insert(my_supplier)

# reads the orders file
orders = sys.argv[2]
with open(orders) as f1:
    order_lines = f1.readlines()

outputFile = open(sys.argv[3], "w")

orders_counter = 0
for i in range(0, len(order_lines)):
    orderi = order_lines[i].split(",")
    location = orderi[0]
    if i != len(order_lines) - 1:
        topping = orderi[1][:-1]
    else:
        topping = orderi[1]

    supply_options = repo.suppliers.find_supplier1(topping)
    if len(supply_options) > 0:
        min_id = supply_options[0].supplier_id
        best_supplier = supply_options[0]
        for s in supply_options:
            if s.supplier_id < min_id:
                min_id = s.supplier_id
                best_supplier = s

    # add order to the table
    orders_counter += 1
    my_order = Order(orders_counter, location, best_supplier.id)
    repo.orders.insert(my_order)

    # updates the dataBase
    repo.hats.update_quantity(repo.hats.find(my_order.hat))

    # updates output file
    my_line = ""
    my_line = my_line + topping + ","
    my_line = my_line + best_supplier.supplier_name + ","
    my_line = my_line + location + "\n"
    outputFile.write(my_line)

outputFile.close()
