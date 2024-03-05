"""
api tested:
    mc.events.pollBlockHits
    mc.events.pollChatPosts
    mc.events.clearAll
"""
import time
from math import (pi, sin, cos)
from mcpi.minecraft import Minecraft

mc = Minecraft.create()
pos = mc.player.getTilePos()


def test_blockHits():
    print("right click block using any sword in the next 20 seconds")
    time.sleep(20)
    hits = mc.events.pollBlockHits()
    for hit in hits:
        print(hit)


def test_chatPosts():
    print("post some chats in the next 20 seconds")
    time.sleep(20)
    posts = mc.events.pollChatPosts()
    for post in posts:
        print(post)


def test_clear():
    print("post some chats in the next 20 seconds")
    time.sleep(20)
    mc.events.clearAll()
    posts = mc.events.pollChatPosts()
    print("This should be 0: ", len(posts))


def main():
    test_blockHits()
    test_chatPosts()
    test_clear()
