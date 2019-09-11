# wenshengwang at gmail dot com
# BSD License
"""This is a TCP server. It is started by the JuicyRaspberryPie plugin/mod and
listen on localhost port 4731(plugin) or 4732(mod).
When it start, it scan the "pplugins" directory for any python files and try
to load them as modules, in these modules, it search for any functions whose
docstring starts with "_mcpy" and register them as commands.
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
import time
import yaml

plugin_dir = os.path.realpath(os.path.join(os.path.dirname(__file__), ".."))
sys.path.insert(0, plugin_dir)

config = {}
with open(os.path.join(plugin_dir, "config.yml")) as f:
    try:
        config = yaml.safe_load(f)
    except yaml.YAMLError:
        pass

HOST = config.get("cmdsvr_host", "localhost")
PORT = config.get("cmdsvr_port", 4731)
# env variable 'JRP_CMDSVR_PORT' can overwrite config file
if 'JRP_CMDSVR_PORT' in os.environ:
    try:
        PORT = int(os.environ['JRP_CMDSVR_PORT'])
    except ValueError:
        pass


KEEP_RUNNING = True
exit_signal = threading.Event()


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
                        if docs and docs.startswith("_mcpy"):
                            print("registering command:", module.__dict__[item].__name__)
                            mc_functions[item] = module.__dict__[item]
            except (NameError, ImportError) as e:
                print(e)


class MyTCPHandler(socketserver.BaseRequestHandler):
    def handle(self):
        global KEEP_RUNNING
        self.data = self.request.recv(1024)
        # firt 2 bytes are length info, from Java's writeUTF
        args = self.data[2:].decode('utf-8').split()
        cmd = args[0]
        if cmd == "list":
            s = "Available commands: %s" % (" ".join(list(mc_functions.keys())))
            self.request.sendall(s.encode('utf-8'))
        elif cmd == "help":
            s = ('JuicyRaspberryPie: put your Python files in pplugins, '
                 'then "/p cmd" to call your function, "/p list" to see list of commands')
            self.request.sendall(s.encode('utf-8'))
        elif cmd == "update":
            register_commands()
            s = 'found commands: ' + " ".join(mc_functions)
            self.request.sendall(s.encode('utf-8'))
        elif cmd == "shutdownserver":
            print("got shutdown request, signing off")
            KEEP_RUNNING = False
            self.request.sendall("Command server received the request and will be shutdown".encode('utf-8'))
        elif cmd in mc_functions:
            threading.Thread(target=mc_functions[cmd], args=tuple(args[1:]), kwargs={}).start()
            self.request.sendall("ok".encode('utf-8'))
        else:
            self.request.sendall(("Unknown command: %s" % args).encode('utf-8'))


register_commands()

if __name__ == "__main__":
    socketserver.TCPServer.allow_reuse_address = True
    print("Command server started at %s:%d." % (HOST, PORT))
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)
    server.socket.settimeout(1)

    def server_serve():
        while keep_running():
            server.handle_request()

    thread = threading.Thread(target=server_serve)
    thread.daemon = True
    try:
        thread.start()
        while keep_running():
            time.sleep(0.5)
    except (KeyboardInterrupt, SystemExit):
        print("Exit...")
        KEEP_RUNNING = False

    thread.join()
