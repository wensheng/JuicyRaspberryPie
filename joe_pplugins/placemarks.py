import json
import os

"""
[
    {
        "name": "favorite",
        "location": [-23, 0, 136]
    },
]
"""

class Placemarks():
    def __init__(self, pm_file='/tmp/mc_placemarks.json'):
        self._pm_file = pm_file
        self.location = {}
        if not os.path.exists(self._pm_file):
            with open(self._pm_file,'w') as fout:
                json.dump([{
                    'name': 'origin',
                    'location': [0, 0, 0]
                }], fout)
        with open(self._pm_file) as fin:
            self._marks = json.load(fin)
        for mark in self._marks:
            self.location[mark['name']] = mark['location']
    def add(self, name, location):
        self.location[name] = location
        return self
    def remove(self, name):
        try:
            del self.location[name]
        except KeyError:
            pass
        return self
    def save(self):
        with open(self._pm_file,'w') as fout:
            marks = [{
                'name': n,
                'location': l
            } for n, l in self.location.items()]
            json.dump(marks, fout)

if __name__=='__main__':
    print(Placemarks().location)

