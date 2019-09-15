#www.stuffaboutcode.com
#Raspberry Pi, Minecraft - Create 3D Model from Obj file
# Version 2 - draws complete faces rather than wireframes and uses materials
import os
import io
import zipfile


class Vec3(list):
    idxmap = {'x': 0, 'y': 1, 'z': 2}
    def __init__(self, *arg):
        if len(arg) == 1:
            super().__init__(arg[0])
        elif len(arg) == 3:
            super().__init__(arg)
            #self = [arg[0], arg[1], arg[2]]
        else:
            raise ValueError
    def __getattr__(self, name):
        if name not in self.idxmap:
            raise KeyError
        return self[self.idxmap[name]]


# class to create 3d filled polygons
class MinecraftDrawing:
    def __init__(self, mc):
        self.mc = mc

    # draw point
    def drawPoint3d(self, x, y, z, blockType):
        self.mc.setBlock(x,y,z,blockType)
        #print "x = " + str(x) + ", y = " + str(y) + ", z = " + str(z)

    # draws a face, when passed a collection of vertices which make up a polyhedron
    def drawFace(self, vertices, blockType):
        
        # get the edges of the face
        edgesVertices = []
        # persist first vertex
        firstVertex = vertices[0]
        # loop through vertices and get edges
        vertexCount = 0
        for vertex in vertices:
            vertexCount+=1
            if vertexCount > 1:
                # got 2 vertices, get the points for the edge
                edgesVertices = edgesVertices + self.getLine(lastVertex.x, lastVertex.y, lastVertex.z, vertex.x, vertex.y, vertex.z)
                #print "x = " + str(lastVertex.x) + ", y = " + str(lastVertex.y) + ", z = " + str(lastVertex.z) + " x2 = " + str(vertex.x) + ", y2 = " + str(vertex.y) + ", z2 = " + str(vertex.z)
            # persist the last vertex found    
            lastVertex = vertex
        # get edge between the last and first vertices
        edgesVertices = edgesVertices + self.getLine(lastVertex.x, lastVertex.y, lastVertex.z, firstVertex.x, firstVertex.y, firstVertex.z)

        # sort edges vertices
        def keyX( point ): return point.x
        def keyY( point ): return point.y
        def keyZ( point ): return point.z
        edgesVertices.sort( key=keyZ )
        edgesVertices.sort( key=keyY )
        edgesVertices.sort( key=keyX )

        # not very performant but wont have gaps between in complex models
        for vertex in edgesVertices:
            vertexCount+=1
            # got 2 vertices, draw lines between them
            if (vertexCount > 1):
                self.drawLine(lastVertex.x, lastVertex.y, lastVertex.z, vertex.x, vertex.y, vertex.z, blockType)
                #print "x = " + str(lastVertex.x) + ", y = " + str(lastVertex.y) + ", z = " + str(lastVertex.z) + " x2 = " + str(vertex.x) + ", y2 = " + str(vertex.y) + ", z2 = " + str(vertex.z)
            # persist the last vertex found
            lastVertex = vertex
        
    # draw's all the points in a collection of vertices with a block
    def drawVertices(self, vertices, blockType):
        for vertex in vertices:
            self.drawPoint3d(vertex.x, vertex.y, vertex.z, blockType)

    # draw line
    def drawLine(self, x1, y1, z1, x2, y2, z2, blockType):
        self.drawVertices(self.getLine(x1, y1, z1, x2, y2, z2), blockType)
    
    # returns points on a line
    def getLine(self, x1, y1, z1, x2, y2, z2):

        # return maximum of 2 values
        def MAX(a,b):
            if a > b: return a
            else: return b

        # return step
        def ZSGN(a):
            if a < 0: return -1
            elif a > 0: return 1
            elif a == 0: return 0

        # list for vertices
        vertices = []

        # if the 2 points are the same, return single vertice
        if (x1 == x2 and y1 == y2 and z1 == z2):
            vertices.append(Vec3(x1, y1, z1))
                            
        # else get all points in edge
        else:
        
            dx = x2 - x1
            dy = y2 - y1
            dz = z2 - z1

            ax = abs(dx) << 1
            ay = abs(dy) << 1
            az = abs(dz) << 1

            sx = ZSGN(dx)
            sy = ZSGN(dy)
            sz = ZSGN(dz)

            x = x1
            y = y1
            z = z1

            # x dominant
            if (ax >= MAX(ay, az)):
                yd = ay - (ax >> 1)
                zd = az - (ax >> 1)
                loop = True
                while(loop):
                    vertices.append(Vec3(x, y, z))
                    if (x == x2):
                        loop = False
                    if (yd >= 0):
                        y += sy
                        yd -= ax
                    if (zd >= 0):
                        z += sz
                        zd -= ax
                    x += sx
                    yd += ay
                    zd += az
            # y dominant
            elif (ay >= MAX(ax, az)):
                xd = ax - (ay >> 1)
                zd = az - (ay >> 1)
                loop = True
                while(loop):
                    vertices.append(Vec3(x, y, z))
                    if (y == y2):
                        loop=False
                    if (xd >= 0):
                        x += sx
                        xd -= ay
                    if (zd >= 0):
                        z += sz
                        zd -= ay
                    y += sy
                    xd += ax
                    zd += az
            # z dominant
            elif(az >= MAX(ax, ay)):
                xd = ax - (az >> 1)
                yd = ay - (az >> 1)
                loop = True
                while(loop):
                    vertices.append(Vec3(x, y, z))
                    if (z == z2):
                        loop=False
                    if (xd >= 0):
                        x += sx
                        xd -= az
                    if (yd >= 0):
                        y += sy
                        yd -= az
                    z += sz
                    xd += ax
                    yd += ay
                    
        return vertices

def load_obj(filename, defaultBlock, materials) :
    V = [] #vertex
    T = [] #texcoords
    N = [] #normals
    F = [] #face indexies
    MF = [] #materials to faces

    currentMaterial = defaultBlock

    fn,fe = os.path.splitext(filename)
    if fe.lower() == ".zip":
        fh = io.TextIOWrapper(zipfile.ZipFile(filename).open(fn + ".obj"))
    else:
        fh = open(filename)

    for line in fh :
        if line[0] == '#' : continue
        line = line.strip().split(' ')
        if line[0] == 'v' : #vertex
            V.append(line[1:])
        elif line[0] == 'vt' : #tex-coord
            T.append(line[1:])
        elif line[0] == 'vn' : #normal vector
            N.append(line[1:])
        elif line[0] == 'f' : #face
            face = line[1:]
            for i in range(0, len(face)) :
                face[i] = face[i].split('/')
                # OBJ indexies are 1 based not 0 based hence the -1
                # convert indexies to integer
                for j in range(0, len(face[i])) :
                    if face[i][j] != "":
                        face[i][j] = int(face[i][j]) - 1
            #append the material currently in use to the face
            F.append(face)
            MF.append(currentMaterial)
    
        elif line[0] == 'usemtl': # material
            
            usemtl = line[1]
            if (usemtl in materials.keys()):
                currentMaterial = materials[usemtl]
            else:
                currentMaterial = defaultBlock
                print("Warning: Couldn't find '" + str(usemtl) + "' in materials using default")

    return V, T, N, F, MF

# strips the x,y,z co-ords from a vertex line, scales appropriately, rounds and converts to int
def getVertexXYZ(vertexLine, scale, startCoord, swapYZ):
    # convert, round and scale
    x = int((float(vertexLine[0]) * scale) + 0.5)
    y = int((float(vertexLine[1]) * scale) + 0.5)
    z = int((float(vertexLine[2]) * scale) + 0.5)
    # add startCoord to x,y,z
    x = x + startCoord.x
    y = y + startCoord.y
    z = z + startCoord.z
    # swap y and z coord if needed
    if swapYZ == True:
        swap = y
        y = z
        z = swap
    return x, y, z
