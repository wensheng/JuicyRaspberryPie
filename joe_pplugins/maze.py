from __future__ import print_function
import numpy as np
import random



def maze(size):
    V = np.zeros((size,size),dtype=np.bool)
    W = np.zeros((size*2+1,size*2+1),dtype=np.bool)

    dxs = [ 0, 0, 1,-1 ]
    dys = [ 1,-1, 0, 0 ]

    x = size//2
    y = size//2

    stack = []

    while True:
        ds = list(range(4))
        random.shuffle(ds)
        dig = False
        i, j = 0, 0
        for d in ds:
            i = x + dxs[d]
            j = y + dys[d]
            in_bounds = i >= 0 and i < size and j >= 0 and j < size
            if not in_bounds:
                continue
            v = V[i,j]
            if not v:
                dig = True
                break
        if dig:
            V[i,j] = True
            W[x*2+dxs[d]+1,y*2+dys[d]+1] = True
            W[i*2+1,j*2+1] = True
            stack.append((i,j))
            x, y = i, j
        else:
            if len(stack) == 0:
                break
            x, y = stack.pop()
    return W

if __name__=='__main__':
    SIZE=13

    W = maze(SIZE)

    Wi = W.astype(np.int)
    show = ['@@','  ']
    for x in range(SIZE*2+1):
        print(''.join([show[v] for v in Wi[:,x]]))
