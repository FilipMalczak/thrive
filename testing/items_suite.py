import unittest
from requests import *
from json import *
from uuid import uuid4
from random import randint

randstr = lambda: str(uuid4())

#todo organize into given/when/then
class ItemsTests(unittest.TestCase):
    def test_creates_with_id(self):
        id = randstr()
        name = randstr()
        size = randint(0, 1000)
        resp = post("http://localhost:8080/api/v1/items", json={
            "id": id,
            "name": name,
            "size": size
        })
        self.assertEqual(resp.status_code, 200)
        self.assertEqual(resp.text, id)
        resp = get("http://localhost:8080/api/v1/items/"+id)
        self.assertEqual(resp.status_code, 200)
        json = resp.json()
        self.assertEqual(json, {
            "id": id,
            "name": name,
            "size": size
        })

    def test_creates_without_id(self):
        name = randstr()
        size = randint(0, 1000)
        resp = post("http://localhost:8080/api/v1/items", json={
            "name": name,
            "size": size
        })
        self.assertEqual(resp.status_code, 200)
        self.assertNotEqual(resp.text, None)
        id = resp.text
        resp = get("http://localhost:8080/api/v1/items/"+id)
        self.assertEqual(resp.status_code, 200)
        json = resp.json()
        self.assertEqual(json, {
            "id": id,
            "name": name,
            "size": size
        })

    def test_listing(self):
        id1 = randstr()
        name = randstr()
        size = randint(0, 1000)
        item1 = {
            "id": id1,
            "name": name,
            "size": size
        }
        resp = post("http://localhost:8080/api/v1/items", json=item1)
        self.assertEqual(resp.status_code, 200)
        name = randstr()
        size = randint(0, 1000)
        item2 = {
            "name": name,
            "size": size
        }
        resp = post("http://localhost:8080/api/v1/items", json=item2)
        self.assertEqual(resp.status_code, 200)
        item2["id"]=resp.text
        resp = get("http://localhost:8080/api/v1/items")
        result = resp.json()
        self.assertTrue(item1 in result)
        self.assertTrue(item2 in result)

if __name__=="__main__":
    unittest.main()