#! /usr/bin/env bash

apt-get update
apt-get install -f
apt-get install -y libatomic1 libpython2.7 libcurl4-openssl-dev
mkdir /opt/swift
cd /opt/swift
wget --no-check-certificate https://swift.org/builds/swift-4.1.2-release/ubuntu1404/swift-4.1.2-RELEASE/swift-4.1.2-RELEASE-ubuntu14.04.tar.gz
tar -xzf swift-4.1.2-RELEASE-ubuntu14.04.tar.gz
if ! grep -q "swift-4.1.2" ~/.profile; then echo "PATH=\"/opt/swift/swift-4.1.2-RELEASE-ubuntu14.04/usr/bin:$PATH\"" >> ~/.profile; fi;
chmod -R 755 /opt/swift/swift-4.1.2-RELEASE-ubuntu14.04/usr/lib/swift/
touch /etc/ld.so.conf.d/swift.conf
ls /opt/swift/swift-4.1.2-RELEASE-ubuntu14.04/usr/lib/swift/linux
if ! grep -q "/opt/swift/swift-4.1.2-RELEASE-ubuntu14.04/usr/lib/swift/linux" /etc/ld.so.conf.d/swift.conf; then echo "/opt/swift/swift-4.1.2-RELEASE-ubuntu14.04/usr/lib/swift/linux" >> /etc/ld.so.conf.d/swift.conf; fi;
ldconfig
cd -
