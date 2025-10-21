from fastapi import FastAPI, HTTPException
from fastapi.responses import HTMLResponse
from pymongo import MongoClient
from bson.objectid import ObjectId
from models import Order
import uvicorn
import config

app = FastAPI(title="FastAPI")


mongo_uri = f"mongodb+srv://{config.MONGO_USER}:{config.MONGO_PASSWORD}@{config.MONGO_CLUSTER}/{config.MONGO_DATABASE}?retryWrites=true&w=majority"
client = MongoClient(mongo_uri)
db = client[config.MONGO_DATABASE]
collection = db["orders"]

@app.get("/orders")
def get_orders():
    orders = list(collection.find())
    for order in orders:
        order["_id"] = str(order["_id"])
    return orders

from bson.errors import InvalidId

@app.get("/orders/filter")
def filter_orders(min_amount: float = 0):
    filtered = list(collection.find({"order_amount": {"$gte": min_amount}}))
    for order in filtered:
        order["_id"] = str(order["_id"])
    return filtered

@app.get("/orders/{order_id}", response_model=Order)
def get_order_by_id(order_id: str):
    try:
        object_id = ObjectId(order_id)
    except InvalidId:
        raise HTTPException(status_code=400, detail="Invalid order ID format")

    result = collection.find_one({"_id": object_id})
    if result is None:
        raise HTTPException(status_code=404, detail="Order not found")

    result["_id"] = str(result["_id"])
    return result

@app.post("/orders")
def add_order(order: Order):
    result = collection.insert_one(order.model_dump())
    return {"message": "Order added successfully", "id": str(result.inserted_id)}

@app.put("/orders/{order_id}")
def update_order(order_id: str, updated_order: Order):
    result = collection.update_one(
        {"_id": ObjectId(order_id)},
        {"$set": updated_order.model_dump()}
    )
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Order not found")
    return {"message": "Order updated successfully"}

@app.delete("/orders/{order_id}")
def delete_order(order_id: str):
    result = collection.delete_one({"_id": ObjectId(order_id)})
    if result.deleted_count == 0:
        raise HTTPException(status_code=404, detail="Order not found")
    return {"message": "Order deleted successfully"}

if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)