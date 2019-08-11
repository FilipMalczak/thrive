import unittest
from json import *
from random import randint
from requests import *
from uuid import uuid4
from os import environ
from time import sleep

randstr = lambda: str(uuid4())

CI_WAIT_TIME = 3 # in seconds
LOCAL_WAIT_TIME = 1 # in seconds

def is_circleci():
    try:
        environ["CIRCLECI"]
        return True
    except:
        return False

is_circleci = is_circleci()
wait_time = CI_WAIT_TIME if is_circleci else LOCAL_WAIT_TIME
print "Is CircleCI env?", is_circleci
print "Wait time is", wait_time

#todo document this
#fixme or rather remove this
allow_kafka_to_work = (lambda: sleep(wait_time))

#todo organize into given/when/then
class ItemsTests(unittest.TestCase):
    def test_creates_with_id(self):
        resp = get("http://localhost:8080/api/v1/items/size/statistics")
        self.assertEqual(resp.status_code, 200)
        previous_stats = resp.json()
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
        resp = get("http://localhost:8080/api/v1/items/" + id)
        self.assertEqual(resp.status_code, 200)
        json = resp.json()
        self.assertEqual(json, {
            "id": id,
            "name": name,
            "size": size
        })
        allow_kafka_to_work()
        resp = get("http://localhost:8080/api/v1/items/size/statistics")
        self.assertEqual(resp.status_code, 200)
        new_stats = resp.json()
        self.assertEqual(new_stats["total"], previous_stats["total"]+size)
        self.assertEqual(new_stats["count"], previous_stats["count"]+1)


    def test_creates_without_id(self):
        resp = get("http://localhost:8080/api/v1/items/size/statistics")
        self.assertEqual(resp.status_code, 200)
        previous_stats = resp.json()
        name = randstr()
        size = randint(0, 1000)
        resp = post("http://localhost:8080/api/v1/items", json={
            "name": name,
            "size": size
        })
        self.assertEqual(resp.status_code, 200)
        self.assertNotEqual(resp.text, None)
        id = resp.text
        resp = get("http://localhost:8080/api/v1/items/" + id)
        self.assertEqual(resp.status_code, 200)
        json = resp.json()
        self.assertEqual(json, {
            "id": id,
            "name": name,
            "size": size
        })
        allow_kafka_to_work()
        resp = get("http://localhost:8080/api/v1/items/size/statistics")
        self.assertEqual(resp.status_code, 200)
        new_stats = resp.json()
        self.assertEqual(new_stats["total"], previous_stats["total"]+size)
        self.assertEqual(new_stats["count"], previous_stats["count"]+1)

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
    allow_kafka_to_work()
    unittest.main()