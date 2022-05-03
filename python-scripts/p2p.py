from flask import request, Flask, jsonify
import requests
import threading

app = Flask(__name__)

@app.route('/chat', methods=['POST'])
def recieve_message():
    data = request.json
    print(data)
    return ('', 204)


def start_server():
    app.run(host='0.0.0.0', port=10001)

def send_message():
    while True:
        message = input("What do you want to send: ")
        requests.post('http://158.39.194.165:10001/chat', json=message)

if __name__ == '__main__':
    while True:
        listenThread = threading.Thread(target=start_server)
        listenThread.start()
        sendThread = threading.Thread(target=send_message)
        sendThread.start()
        while True:
            listenThread.join(1)
            sendThread.join(1)

