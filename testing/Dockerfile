FROM python:3.7.0
#todo better image

ADD ./requirements.txt .
ADD ./items_suite.py .

RUN ["python3", "-m", "pip", "install", "-r", "./requirements.txt"]

ENTRYPOINT ["python3", "./items_suite.py"]