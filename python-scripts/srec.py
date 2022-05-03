import socket

ip =  socket.gethostbyname(socket.gethostname()) 

udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp.bind((ip, 5005))


while True:
    data, addr = udp.recvfrom(1024)
    print(data, " from: ", addr)
