from pydantic import BaseModel

class Order(BaseModel):
    employee_lastname: str
    order_amount: float
    product_name: str
    company_name: str
    client_lastname: str