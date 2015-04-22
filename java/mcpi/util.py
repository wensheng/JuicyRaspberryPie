import collections
import functools

try:
    basestring
except NameError:
	basestring = str
	
def flatten(l):
    for e in l:
        if isinstance(e, collections.Iterable) and not isinstance(e, basestring):
            for ee in flatten(e): yield ee
        else: yield e

def flatten_parameters(l):
    # if isinstance(l, str):
    #    return l.encode('utf-8')
    return ",".join(map(str, flatten(l)))
