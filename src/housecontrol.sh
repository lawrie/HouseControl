cd ..
rm nohup.out
mv log/app.log log/app.old
export DISPLAY=:0
nohup java -Djava.library.path=/usr/lib/jni -cp "/usr/share/java/RXTXcomm.jar:lib/*:bin" net.geekgrandad.HouseControl $1 &
[ "$#" -eq "2" ] && echo $! >$2
cd bin
