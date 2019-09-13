# JuicyRaspberryPie Lua API 

This is just a start and for demo purpose.  Most of API's have not been implemented.

## Install Lua and luorocks on Ubuntu Linux

Lua:

    sudo apt install lua5.3

Luarocks:

    wget https://luarocks.org/releases/luarocks-3.2.0.tar.gz
    tar xfp luarocks-3.2.0.tar.gz
    cd luarocks-3.2.0
    ./configure
    make
    sudo make install

Don't use `sudo make bootstrap` as recommended on its website.

Install luosocket:

    sudo luarocks install luasocket
