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
from traceback import format_exc
import re

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
    global mc_docs
    mc_functions = {}
    mc_docs = {}
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
                    fn = module.__dict__[item]
                    if isinstance(fn, types.FunctionType):
                        docs = fn.__doc__
                        if docs and docs.startswith("_mcp"):
                            print("registering command:", fn.__name__)
                            mc_functions[item] = wrap_exc_chat(fn)
                            mc_docs[item] = docs
            except (NameError, ImportError) as e:
                print(e)


def chat(msg="Whaaat?!"):
    mc = Minecraft.create()
    mc.postToChat(msg)

def wrap_exc_chat(fn):
    def wrapped(*args, **kw):
        try:
            fn(*args, **kw)
        except:
            for line in re.split(r'\n',format_exc())[:-1]:
                chat(line)
    return wrapped
            
class MyTCPHandler(socketserver.BaseRequestHandler):
    def post(self, s):
        self.request.sendall(s.encode('utf-8'))
        threading.Thread(target=chat, args=(s,), kwargs={}).start()
    def handle(self):
        global KEEP_RUNNING
        try:
            self.data = self.request.recv(1024)
            # firt 2 bytes are length info, from Java's writeUTF
            msg = self.data[2:].decode('utf-8')
            args = msg.split()
            cmd = args[0]
            if cmd == "man":
                name = args[1]
                docs = mc_docs[name]
                self.post('{}: {}'.format(name, docs))
                return
            if cmd == "list":
                sl = sorted(mc_functions)
                ls = ' '.join(sl)
                msg = 'commands: {}'.format(ls)
                self.post(msg)
                return
            if cmd == "help":
                s = 'JuicyRaspberryPie: put your Python files in pplugins, then "/p cmd" to call your function, "/p list" to see list of commands'
                self.post(s)
                return
            if cmd == "update":
                register_commands()
                s = 'found commands: ' + " ".join(mc_functions)
                self.post(s)
                return
            if cmd == "BYE":
                self.post("got shutdown request, signing off")
                KEEP_RUNNING = False
                return
            threading.Thread(target=mc_functions.get(cmd, chat), args=tuple(args[1:]), kwargs={}).start()
            self.request.sendall("ok".encode('utf-8'))
        except:
            for line in re.split(r'\n',format_exc())[:-1]:
                self.post(line)

register_commands()

if __name__ == "__main__":
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)
    def server_serve():
        while keep_running():
            server.handle_request()
        server.server_close()
    threading.Thread(target=server_serve).start()
