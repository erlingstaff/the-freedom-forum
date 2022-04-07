import socket

PC2_IP = '0.0.0.0' # IP address is not valid, it is just for example. 
UDP_PORT = 5005

while True:
    message = input("What do want to say?\n")
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.sendto(message, (PC2_IP, UDP_PORT))
