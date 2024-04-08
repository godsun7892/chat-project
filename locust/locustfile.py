import json
import time
from locust import HttpUser, task, between, events
from websocket import create_connection
import stomper

class StompClient(object):
    def __init__(self, host, port, endpoint):
        self.ws_uri = f"ws://{host}:{port}/{endpoint}"
        self.ws = None

    def __del__(self):
        self.close()

    def close(self):
        if self.ws:
            print("disconnect...")
            self.ws.close()

    def connect(self):
        self.ws = create_connection(self.ws_uri)
        # connect_frame = stomper.connect("guest", "guest")
        # self.ws.send(connect_frame)

    def subscribe(self, destination):
        sub_frame = stomper.subscribe(destination, 1, ack='auto')
        self.ws.send(sub_frame)

    def send(self, body, destination):
        msg = stomper.send(destination, json.dumps(body))
        self.ws.send(msg)

class WebsocketUser(HttpUser):
    host = "localhost"
    port = 8080
    endpoint = "ws-stomp"

    @task()
    def chat_scenario(self):
        # WebSocket 연결 및 메시지 전송
        self.stompClient = StompClient(self.host, self.port, self.endpoint)
        self.stompClient.connect()
        response = self.client.get("http://localhost:8080/api/chat")
        time.sleep(1)  # 잠시 대기 후 메시지 전송
        self.stompClient.send({"roomId":1, "userId":"손님", "content":"ㅎㅇ"}, "/pub/chat/sendMessage")
        time.sleep(3)
        self.stompClient.send({"roomId":1, "userId":"손님2", "content":"ㅎㅇ2"}, "/pub/chat/sendMessage")
        time.sleep(3)
        
        # HTTP GET 요청 (Axios와 유사)
        print("GET /api/chat response status code:", response.status_code)

    def on_start(self):
        self.stompClient = StompClient(self.host, self.port, self.endpoint)
        self.stompClient.connect()
        # 예시: STOMP를 통해 "/sub/chat/room/1" 구독
        self.stompClient.subscribe("/sub/chat/room/1")
