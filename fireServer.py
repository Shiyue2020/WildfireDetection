import http.server
import socketserver

PORT = 8000

httpHandler = http.server.SimpleHTTPRequestHandler

#Add your IP inside " "
with socketserver.TCPServer((" ", PORT), httpHandler) as httpServer:
    print("serving at port", PORT)
    httpServer.serve_forever()

