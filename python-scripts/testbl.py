from bluetooth import *
import sys
def input_and_send():
    print("Type something: \n")
    while True:
        print("\nType: ")
        data = input()
        if len(data) == 0: break
        #encode data to bytes
        data = data.encode()
        sock.send(data)
        rx_and_echo()
    
def rx_and_echo():
    print("Waiting for data...: \n")
    while True:
        print(sock.recv(buf_size))

addrlist = ["00:21:09:00:7A:12", "00:21:09:00:79:0B", "00:21:09:00:81:D9"]

for addr in addrlist:
    service_matches = find_service( address = addr )
    buf_size = 1024
    if len(service_matches) != 0:
        print("couldn't find the SampleServer service =(")
        for s in range(len(service_matches)):
            print("\nservice_matches: [" + str(s) + "]:")
            print(service_matches[s])
        
        first_match = service_matches[0]
        port = first_match["port"]
        name = first_match["name"]
        host = first_match["host"]
        port=1
        print("connecting to \"%s\" on %s, port %s" % (name, host, port))
        sock=BluetoothSocket(RFCOMM)
        sock.connect((host, port))
        print("connected")
        # input_and_send()
        while True:
            print(sock.recv(buf_size))
        sock.close()
        print("\n--- bye ---\n")
