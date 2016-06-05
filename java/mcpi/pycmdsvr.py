# wenshengwang at gmail dot com
# BSD License
"""This is a TCP server. It is started by the JuicyRaspberryPie plugin and 
listen on localhost port 32123. 
When it start, it scan the "pplugins" directory for any python files and try
to load them as modules, in these modules, it search for any functions whose 
docstring starts with "_mcp" and register them as commands.
When the server receive a command, if it matches one in the registry, it will
be executed. If not, it will execute a dummy command.
"""

import os
import sys
import glob
import socketserver
import threading
import types
import importlib

plugin_dir = os.path.realpath(os.path.join(os.path.dirname(__file__), ".."))
sys.path.insert(0, plugin_dir)
from mcpi.minecraft import Minecraft

HOST = 'localhost'
# TODO: read port from config.yml
PORT = 32123

KEEP_RUNNING = True
def keep_running():
    return KEEP_RUNNING

def register_commands():
    global mc_functions
    mc_functions = {}
    pp_files = glob.glob(os.path.join(plugin_dir, "pplugins", "*.py"))
    # import all files and put minecraft function into the mc_functions dict
    for pp_file in pp_files:
        basename = os.path.basename(pp_file)
        if basename != "__init__.py":
            try:
                name = "pplugins." + basename[:-3]
                if name in sys.modules:
                    module = importlib.reload(sys.modules[name])
                else:
                    module = importlib.import_module(name)
                for item in dir(module):
                    if isinstance(module.__dict__[item], types.FunctionType):
                        docs = module.__dict__[item].__doc__
                        if docs and docs.startswith("_mcp"):
                            print("registering command:", module.__dict__[item].__name__)
                            mc_functions[item] = module.__dict__[item]
            except (NameError, ImportError) as e:
                print(e)


def chat(msg="Whaaat?!"):
    mc = Minecraft.create()
    mc.postToChat(msg)


class MyTCPHandler(socketserver.BaseRequestHandler):
    def handle(self):
        global KEEP_RUNNING
        self.data = self.request.recv(1024)
        # firt 2 bytes are length info, from Java's writeUTF
        args = self.data[2:].decode('utf-8').split()
        cmd = args[0]
        if cmd == "list":
            s = "Available commands: %s" % ( " ".join(list(mc_functions.keys())))
            self.request.sendall(s.encode('utf-8'))
            threading.Thread(target=chat, args=(s,), kwargs={}).start()
            return
        if cmd == "help":
            s = 'JuicyRaspberryPie: put your Python files in pplugins, then "/p cmd" to call your function, "/p list" to see list of commands'
            self.request.sendall(s.encode('utf-8'))
            threading.Thread(target=chat, args=(s,), kwargs={}).start()
            return
        if cmd == "update":
            register_commands()
            s = 'found commands: ' + " ".join(mc_functions)
            self.request.sendall(s.encode('utf-8'))
            threading.Thread(target=chat, args=(s,), kwargs={}).start()
            return
        if cmd == "BYE":
            print("got shutdown request, signing off")
            KEEP_RUNNING = False
            return
        threading.Thread(target=mc_functions.get(cmd, chat), args=tuple(args[1:]), kwargs={}).start()
        self.request.sendall("ok".encode('utf-8'))


register_commands()

if __name__ == "__main__":
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)
    def server_serve():
        while keep_running():
            server.handle_request()
    threading.Thread(target=server_serve).start()
