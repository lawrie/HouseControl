cd ..
nohup java -Djava.library.path=/usr/lib/jni -cp "/usr/share/java/RXTXcomm.jar:lib/*:bin" net.geekgrandad.HouseControl $1 &
cd bin

